
public class BelegplanIndexer {
	private BelegplanIndexerFenster fenster;
	public static final String DATA_FILE_NAME = "BelegplanDaten.json";
	public static final String INDEX_FILE_NAME = "BelegplanIndex.html";
	public static final String DATA_DIRECTORY_NAME = "BelegplanDateien";
	
	public BelegplanIndexer() {
		fenster = new BelegplanIndexerFenster(this);
		System.out.println("Belegplan Indexer geladen.");
	}
	
	public void start() {
		Thread thread = new Thread(new IndexerThread(fenster));
		thread.start();
	}
	
	public static void main(String[] args) {
		new BelegplanIndexer();
	}
}
