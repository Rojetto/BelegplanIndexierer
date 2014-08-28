import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.GsonBuilder;

public class IndexiererThread implements Runnable {
	BelegplanIndexiererFenster fenster;
	
	public IndexiererThread(BelegplanIndexiererFenster fenster) {
		this.fenster = fenster;
	}
	
	public boolean indexieren() {
		System.out.println("--------------------------------------------------------------------------------------------------------------");
		
		File sourceFile = fenster.getSourceFile();
		List<Belegplan> belegplaene = fromFile(sourceFile);
		
		if(belegplaene == null)
			return false;
		
		if(!fenster.getDestinationFile().isDirectory()) {
			System.out.println("Zielverzeichnis ist kein Ordner.");
			return false;
		}
		
		System.out.println("Entpacke notwendige Dateien in \"" + fenster.getDestinationFile() + "\"");
		
		try {
			extractFolder("BelegplanDateien", fenster.getDestinationFile());
			extractFile(BelegplanIndexierer.INDEX_FILE_NAME, fenster.getDestinationFile());
		} catch(URISyntaxException e) {
			System.out.println("Datei konnte nicht entpackt werden.");
			return false;
		} catch(ZipException e) {
			System.out.println("Datei konnte nicht entpackt werden.");
			return false;
		}
		System.out.println("Dateien entpackt.");
		
		File dataDirectory = new File(fenster.getDestinationFile(), BelegplanIndexierer.DATA_DIRECTORY_NAME);
		
		File belegplanFile = new File(fenster.getDestinationFile(), BelegplanIndexierer.INDEX_FILE_NAME);
		System.out.println("Schreibe Belegplan in \"" + belegplanFile.getAbsolutePath() + "\"");
		try {
			createIndexFile(new File(fenster.getDestinationFile(), BelegplanIndexierer.INDEX_FILE_NAME), belegplaene);
		} catch(IOException e1) {
			System.out.println("Belegplan Index konnte nicht geschrieben werden.");
			return false;
		}
		
		File belegplanJsonFile = new File(dataDirectory, BelegplanIndexierer.DATA_FILE_NAME);
		System.out.println("Schreibe Belegplan-Daten in \"" + belegplanJsonFile.getAbsolutePath() + "\"");
		
		String belegplanJson = new GsonBuilder().setPrettyPrinting().create().toJson(belegplaene);
		FileWriter writer;
		try {
			writer = new FileWriter(belegplanJsonFile);
			writer.write(belegplanJson);
			writer.close();
		} catch(IOException e) {
			System.out.println(BelegplanIndexierer.DATA_FILE_NAME + " konnte nicht geschrieben werden.");
		}
		
		File belegplaeneDirectory = new File(dataDirectory, "Belegplaene");
		belegplaeneDirectory.mkdir();
		System.out.println("Erstelle einzelne Belegplan-Dateien in \"" + belegplaeneDirectory.getAbsolutePath() + "\"");
		try {
			createBelegplaeneDirectory(belegplaeneDirectory, belegplaene);
		} catch(IOException e) {
			System.out.println("Belegplaene konnte nicht geschrieben werden.");
		}
		
		return true;
	}
	
	public List<Belegplan> fromFile(File sourceFile) {
		System.out.println("Starte Indexierung von \"" + sourceFile.getAbsolutePath() + "\"");
		
		if(!sourceFile.exists() || sourceFile.isDirectory()) {
			System.out.println("Datei konnte nicht gefunden werden oder ist ein Verzeichnis.");
			return null;
		}
		
		Document doc;
		
		try {
			doc = Jsoup.parse(sourceFile, null);
		} catch(IOException e) {
			System.out.println("Datei konnte nicht gelesen werden.");
			return null;
		}
		System.out.println("Datei eingelesen.");
		Elements pages = doc.getElementsByAttributeValueStarting("name", "PageN");
		
		if(pages.size() == 0) {
			System.out.println("Datei nicht als Stundenplan erkannt.");
			return null;
		}
		
		System.out.println("Datei als Stundenplan erkannt.");
		System.out.println(pages.size() + " einzelne Stundenplaene gefunden.");
		
		Elements tabellen = doc.getElementsByTag("table");
		List<Belegplan> belegplaene = new ArrayList<Belegplan>();
		
		for(Element tabelle : tabellen) {
			Belegplan plan = new Belegplan();
			String schueler = tabelle.getElementsByAttributeValue("class", "s0").first().text();
			String tutor = tabelle.getElementsByAttributeValue("class", "s1").get(0).text();
			String schule = tabelle.getElementsByAttributeValue("class", "s1").get(1).text();
			String gueltig = tabelle.getElementsByAttributeValue("class", "s1").get(2).text();
			
			Elements stundenElemente = tabelle.getElementsByClass("s0");
			stundenElemente.remove(0);
			int maxStunden = stundenElemente.size() / 15;
			
			for(int i = 0; i < maxStunden; i++) {
				for(int j = 0; j < 5; j++) {
					String kurs = stundenElemente.get(15 * i + j).text();
					if(!kurs.equals("\u00a0"))
						plan.getTag(j).getStunde(i).setKurs(kurs);
				}
				for(int j = 5; j < 10; j++) {
					String lehrer = stundenElemente.get(15 * i + j).text();
					if(!lehrer.equals("\u00a0"))
						plan.getTag(j - 5).getStunde(i).setLehrer(lehrer);
				}
				for(int j = 10; j < 15; j++) {
					String raum = stundenElemente.get(15 * i + j).text();
					if(!raum.equals("\u00a0"))
						plan.getTag(j - 10).getStunde(i).setRaum(raum);
				}
			}
			
			plan.setSchueler(schueler);
			plan.setTutor(tutor);
			plan.setSchule(schule);
			plan.setGueltig(gueltig);
			
			belegplaene.add(plan);
		}
		
		return belegplaene;
	}
	
	public void extractFolder(String name, File destination) throws URISyntaxException, ZipException {
		File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		ZipFile zip = new ZipFile(jarFile);
		@SuppressWarnings("unchecked")
		List<FileHeader> headers = zip.getFileHeaders();
		
		for(FileHeader header : headers) {
			if(header.getFileName().startsWith(name + "/")) {
				System.out.println("Entpacke " + header.getFileName());
				zip.extractFile(header, destination.getAbsolutePath());
			}
		}
	}
	
	public void extractFile(String name, File destination) throws URISyntaxException, ZipException {
		File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		ZipFile zip = new ZipFile(jarFile);
		
		System.out.println("Entpacke " + name);
		zip.extractFile(name, destination.getAbsolutePath());
	}
	
	public void createIndexFile(File htmlFile, List<Belegplan> belegplaene) throws IOException {
		Document doc = Jsoup.parse(htmlFile, null);
		Element table = doc.getElementById("belegplaene").getElementsByTag("tbody").first();
		
		Element headRow = doc.getElementById("belegplaene").getElementsByTag("thead").first().getElementsByTag("tr").first();
		
		if(fenster.getTutor())
			headRow.appendElement("td").text("Tutor");
		if(fenster.getSchule())
			headRow.appendElement("td").text("Schule");
		if(fenster.getGueltig())
			headRow.appendElement("td").text("gültig ab");
		
		for(Belegplan plan : belegplaene) {
			Element row = table.appendElement("tr");
			row.appendElement("td").appendElement("a").attr("href", "BelegplanDateien/Belegplaene/" + plan.getSchueler() + ".html").text(plan.getSchueler());
			if(fenster.getTutor())
				row.appendElement("td").text(plan.getTutor());
			if(fenster.getSchule())
				row.appendElement("td").text(plan.getSchule());
			if(fenster.getGueltig())
				row.appendElement("td").text(plan.getGueltig());
		}
		
		doc.html(doc.html().replaceAll("%DISPLAY_LENGTH%", "25"));
		
		FileWriter writer = new FileWriter(htmlFile);
		writer.write(doc.html());
		writer.close();
	}
	
	public void createBelegplaeneDirectory(File directory, List<Belegplan> belegplaene) throws IOException {
		for(Belegplan plan : belegplaene) {
			File file = new File(directory, plan.getSchueler() + ".html");
			System.out.println("Erstelle \"" + file.getAbsolutePath() + "\"");
			createBelegplan(file, plan);
		}
	}
	
	public void createBelegplan(File file, Belegplan plan) throws IOException {
		Document doc = Document.createShell(file.toURI().toString());
		doc.title(plan.getSchueler());
		doc.body().appendElement("h3").text(plan.getSchueler());
		Element table = doc.body().appendElement("table").attr("border", "1");
		Element head = table.appendElement("tr");
		head.appendElement("th").text("Stunde");
		head.appendElement("th").text("Montag").attr("style", "width: 100px");
		head.appendElement("th").text("Dienstag").attr("style", "width: 100px");
		head.appendElement("th").text("Mittwoch").attr("style", "width: 100px");
		head.appendElement("th").text("Donnerstag").attr("style", "width: 100px");
		head.appendElement("th").text("Freitag").attr("style", "width: 100px");
		
		for(int i = 1; i <= 11; i++) {
			Element row = table.appendElement("tr");
			row.appendElement("td").text(i + "");
			
			for(int j = 0; j < 5; j++) {
				Stunde stunde = plan.getTag(j).getStunde(i - 1);
				String kurs = stunde.getKurs() != null ? stunde.getKurs() : "";
				String lehrer = stunde.getLehrer() != null ? stunde.getLehrer() : "";
				String raum = stunde.getRaum() != null ? stunde.getRaum() : "";
				Element zelle = row.appendElement("td");
				zelle.appendText(kurs);
				zelle.appendElement("br");
				zelle.appendText(lehrer);
				zelle.appendElement("br");
				zelle.appendText(raum);
			}
		}
		FileWriter writer = new FileWriter(file);
		writer.write(doc.html());
		writer.close();
	}
	
	@Override
	public void run() {
		fenster.setButtonEnabled(false);
		
		if(indexieren())
			System.out.println("Indexierung erfolgreich abgeschlossen.");
		else
			System.out.println("Indexierung abgebrochen.");
		
		fenster.setButtonEnabled(true);
	}
}
