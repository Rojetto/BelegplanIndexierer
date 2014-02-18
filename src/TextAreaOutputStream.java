import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaOutputStream extends OutputStream {
	private JTextArea textArea;
	private StringBuilder sb = new StringBuilder();
	
	public TextAreaOutputStream(JTextArea textArea) {
		this.textArea = textArea;
	}
	
	@Override
	public void write(int b) throws IOException {
		if(b == '\r')
			return;
		
		if(b == '\n') {
			final String text = sb.toString() + "\n";
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textArea.append(text);
				}
			});
			sb.setLength(0);
			
			return;
		}
		
		sb.append((char) b);
	}
}
