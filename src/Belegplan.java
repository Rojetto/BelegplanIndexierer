
public class Belegplan {
	private String schueler;
	private String tutor;
	private String schule;
	private String gueltig;
	private Tag[] tage;
	
	public Belegplan() {
		tage = new Tag[5];
		for(int i = 0; i < tage.length; i++)
			tage[i] = new Tag();
	}
	
	public Tag getTag(int index) {
		return tage[index];
	}

	public String getSchueler() {
		return schueler;
	}
	
	public void setSchueler(String schueler) {
		this.schueler = schueler;
	}
	
	public String getTutor() {
		return tutor;
	}
	
	public void setTutor(String tutor) {
		this.tutor = tutor;
	}
	
	public String getSchule() {
		return schule;
	}
	
	public void setSchule(String schule) {
		this.schule = schule;
	}
	
	public String getGueltig() {
		return gueltig;
	}
	
	public void setGueltig(String gueltig) {
		this.gueltig = gueltig;
	}
	
	public String toString() {
		return "Schueler: " + schueler + "; Tutor: " + tutor + "; Schule: " + schule + "; gueltig ab: " + gueltig;
	}
}
