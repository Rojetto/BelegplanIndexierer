
public class BelegplanIndexierer {
	private BelegplanIndexiererFenster fenster;
	public static final String DATA_FILE_NAME = "BelegplanDaten.json";
	public static final String INDEX_FILE_NAME = "BelegplanIndex.html";
	public static final String DATA_DIRECTORY_NAME = "BelegplanDateien";
	
	public BelegplanIndexierer() {
		fenster = new BelegplanIndexiererFenster(this);
		System.out.println("Belegplan-Indexierer geladen.");
	}
	
	public void start() {
		Thread thread = new Thread(new IndexiererThread(fenster));
		thread.start();
	}
	
	public static void main(String[] args) {
		new BelegplanIndexierer();
	}
}
