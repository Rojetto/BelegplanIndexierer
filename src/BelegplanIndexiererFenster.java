import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BelegplanIndexiererFenster extends JFrame implements ActionListener {
	private static final long serialVersionUID = -7448075019740707535L;
	public static final int WIDTH = 600;
	public static final int HEIGHT = 345;
	
	private BelegplanIndexierer indexer;
	private JLabel sourceLabel;
	private JTextField sourceField;
	private JButton sourceButton;
	private JLabel destinationLabel;
	private JTextField destinationField;
	private JButton destinationButton;
	private JButton startButton;
	private JScrollPane scrollPane;
	private JTextArea logArea;
	private JCheckBox tutor;
	private JCheckBox schule;
	private JCheckBox gueltig;
	
	public BelegplanIndexiererFenster(BelegplanIndexierer indexer) {
		super("Belegplan-Indexierer");
		this.indexer = indexer;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException e) {
			System.out.println("System Look And Feel konnte nicht geladen werden. Benutze standard Java LnF");
		} catch(InstantiationException e) {
			System.out.println("System Look And Feel konnte nicht geladen werden. Benutze standard Java LnF");
		} catch(IllegalAccessException e) {
			System.out.println("System Look And Feel konnte nicht geladen werden. Benutze standard Java LnF");
		} catch(UnsupportedLookAndFeelException e) {
			System.out.println("System Look And Feel konnte nicht geladen werden. Benutze standard Java LnF");
		}
		
		this.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((int) (screenSize.getWidth() - WIDTH) / 2, (int) (screenSize.getHeight() - HEIGHT) / 2, WIDTH, HEIGHT);
		this.setResizable(false);
		
		sourceLabel = new JLabel("Quelldatei (HTML)");
		sourceLabel.setBounds(5, 5, 500, 30);
		this.add(sourceLabel);
		
		sourceField = new JTextField();
		sourceField.setBounds(105, 5, 360, 30);
		this.add(sourceField);
		
		sourceButton = new JButton("Durchsuchen");
		sourceButton.setBounds(470, 5, 120, 30);
		sourceButton.addActionListener(this);
		this.add(sourceButton);
		
		destinationLabel = new JLabel("Zielverzeichnis");
		destinationLabel.setBounds(5, 40, 500, 30);
		this.add(destinationLabel);
		
		destinationField = new JTextField();
		destinationField.setBounds(105, 40, 360, 30);
		this.add(destinationField);
		
		destinationButton = new JButton("Durchsuchen");
		destinationButton.setBounds(470, 40, 120, 30);
		destinationButton.addActionListener(this);
		this.add(destinationButton);
		
		startButton = new JButton("Indexieren");
		startButton.setBounds(470, 75, 120, 30);
		startButton.addActionListener(this);
		this.add(startButton);
		
		logArea = new JTextArea();
		logArea.setEditable(false);
		logArea.setFont(sourceField.getFont());
		System.setOut(getPrintStream());
		
		scrollPane = new JScrollPane(logArea);
		scrollPane.setBounds(5, 110, 585, 200);
		this.add(scrollPane);
		
		tutor = new JCheckBox("Tutor", true);
		tutor.setBounds(5, 75, 80, 30);
		this.add(tutor);
		
		schule = new JCheckBox("Schule", true);
		schule.setBounds(100, 75, 80, 30);
		this.add(schule);
		
		gueltig = new JCheckBox("gültig ab", true);
		gueltig.setBounds(200, 75, 80, 30);
		this.add(gueltig);
		
		this.setVisible(true);
	}

	public File getSourceFile() {
		return new File(sourceField.getText());
	}
	
	public File getDestinationFile() {
		return new File(destinationField.getText());
	}
	
	public PrintStream getPrintStream() {
		return new PrintStream(new TextAreaOutputStream(logArea));
	}
	
	public void setButtonEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
	}
	
	public boolean getTutor() {
		return tutor.isSelected();
	}
	
	public boolean getSchule() {
		return schule.isSelected();
	}
	
	public boolean getGueltig() {
		return gueltig.isSelected();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == sourceButton) {
			JFileChooser fileChooser = new JFileChooser();
			if(getSourceFile().exists())
				fileChooser.setCurrentDirectory(getSourceFile());
			FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("HTML Datei", "html", "htm");
			fileChooser.setFileFilter(fileFilter);
			
			int returnVal = fileChooser.showOpenDialog(this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				sourceField.setText(file.getAbsolutePath());
				if(destinationField.getText().equals(""))
					destinationField.setText(file.getParent());
			}
		} else if(event.getSource() == startButton) {
			indexer.start();
		} else if(event.getSource() == destinationButton) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(getDestinationFile().exists())
				fileChooser.setCurrentDirectory(getDestinationFile());
			
			int returnVal = fileChooser.showSaveDialog(this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				destinationField.setText(file.getAbsolutePath());
			}
		}
	}
}
