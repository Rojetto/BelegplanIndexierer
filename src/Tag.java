
public class Tag {
	private Stunde[] stunden;

	public Tag() {
		stunden = new Stunde[11];
		for(int i = 0; i < stunden.length; i++)
			stunden[i] = new Stunde();
	}
	
	public Stunde getStunde(int index) {
		return stunden[index];
	}
}
