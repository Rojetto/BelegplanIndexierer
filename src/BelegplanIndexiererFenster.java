import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.VERTICAL_CENTER;
import static javax.swing.SpringLayout.WEST;

import java.awt.BorderLayout;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
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
	private JSpinner eintraege;
	
	public BelegplanIndexiererFenster(BelegplanIndexierer indexer) {
		super("Belegplan-Indexierer");
		this.indexer = indexer;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.out.println("System Look And Feel konnte nicht geladen werden. Benutze standard Java LnF");
		} catch (InstantiationException e) {
			System.out.println("System Look And Feel konnte nicht geladen werden. Benutze standard Java LnF");
		} catch (IllegalAccessException e) {
			System.out.println("System Look And Feel konnte nicht geladen werden. Benutze standard Java LnF");
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("System Look And Feel konnte nicht geladen werden. Benutze standard Java LnF");
		}
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel settingsPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		settingsPanel.setLayout(layout);
		
		sourceLabel = new JLabel("Quelldatei (HTML)");
		settingsPanel.add(sourceLabel);
		
		layout.putConstraint(WEST, sourceLabel, 5, WEST, settingsPanel);
		layout.putConstraint(NORTH, sourceLabel, 10, NORTH, settingsPanel);
		
		sourceField = new JTextField();
		settingsPanel.add(sourceField);
		
		layout.putConstraint(WEST, sourceField, 5, EAST, sourceLabel);
		layout.putConstraint(VERTICAL_CENTER, sourceField, 0, VERTICAL_CENTER, sourceLabel);
		
		sourceButton = new JButton("Durchsuchen");
		sourceButton.addActionListener(this);
		settingsPanel.add(sourceButton);
		
		layout.putConstraint(WEST, sourceButton, 5, EAST, sourceField);
		layout.putConstraint(VERTICAL_CENTER, sourceButton, 0, VERTICAL_CENTER, sourceField);
		layout.putConstraint(EAST, settingsPanel, 5, EAST, sourceButton);
		
		destinationLabel = new JLabel("Zielverzeichnis");
		settingsPanel.add(destinationLabel);
		
		layout.putConstraint(WEST, destinationLabel, 0, WEST, sourceLabel);
		layout.putConstraint(EAST, destinationLabel, 0, EAST, sourceLabel);
		layout.putConstraint(NORTH, destinationLabel, 10, SOUTH, sourceLabel);
		
		destinationField = new JTextField();
		settingsPanel.add(destinationField);
		
		layout.putConstraint(WEST, destinationField, 0, WEST, sourceField);
		layout.putConstraint(EAST, destinationField, 0, EAST, sourceField);
		layout.putConstraint(VERTICAL_CENTER, destinationField, 0, VERTICAL_CENTER, destinationLabel);
		
		destinationButton = new JButton("Durchsuchen");
		destinationButton.addActionListener(this);
		settingsPanel.add(destinationButton);
		
		layout.putConstraint(WEST, destinationButton, 0, WEST, sourceButton);
		layout.putConstraint(EAST, destinationButton, 0, EAST, sourceButton);
		layout.putConstraint(VERTICAL_CENTER, destinationButton, 0, VERTICAL_CENTER, destinationField);
		
		startButton = new JButton("Indexieren");
		startButton.addActionListener(this);
		settingsPanel.add(startButton);
		
		layout.putConstraint(WEST, startButton, 0, WEST, destinationButton);
		layout.putConstraint(EAST, startButton, 0, EAST, destinationButton);
		layout.putConstraint(NORTH, startButton, 5, SOUTH, destinationButton);
		
		layout.putConstraint(SOUTH, settingsPanel, 5, SOUTH, startButton);
		
		tutor = new JCheckBox("Tutor", true);
		settingsPanel.add(tutor);
		
		layout.putConstraint(WEST, tutor, 0, WEST, sourceLabel);
		layout.putConstraint(VERTICAL_CENTER, tutor, 0, VERTICAL_CENTER, startButton);
		
		schule = new JCheckBox("Schule", true);
		settingsPanel.add(schule);
		
		layout.putConstraint(WEST, schule, 5, EAST, tutor);
		layout.putConstraint(VERTICAL_CENTER, schule, 0, VERTICAL_CENTER, startButton);
		
		gueltig = new JCheckBox("gültig ab", true);
		settingsPanel.add(gueltig);
		
		layout.putConstraint(WEST, gueltig, 5, EAST, schule);
		layout.putConstraint(VERTICAL_CENTER, gueltig, 0, VERTICAL_CENTER, startButton);
		
		eintraege = new JSpinner(new SpinnerNumberModel(25, 1, 100, 1));
		eintraege.setName("Einträge");
		settingsPanel.add(eintraege);
		
		layout.putConstraint(WEST, eintraege, 5, EAST, gueltig);
		layout.putConstraint(VERTICAL_CENTER, eintraege, 0, VERTICAL_CENTER, startButton);
		
		JLabel eintraegeLabel = new JLabel("Einträge pro Seite");
		settingsPanel.add(eintraegeLabel);
		
		layout.putConstraint(WEST, eintraegeLabel, 5, EAST, eintraege);
		layout.putConstraint(VERTICAL_CENTER, eintraegeLabel, 0, VERTICAL_CENTER, startButton);
		
		this.add(settingsPanel, BorderLayout.NORTH);
		
		logArea = new JTextArea();
		logArea.setEditable(false);
		logArea.setFont(sourceField.getFont());
		System.setOut(getPrintStream());
		
		scrollPane = new JScrollPane(logArea);
		this.add(scrollPane, BorderLayout.CENTER);
		
		this.setPreferredSize(new Dimension(600, 400));
		this.setMinimumSize(new Dimension(500, 200));
		
		this.pack();
		this.setVisible(true);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
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
	
	public int getEintraege() {
		return (int) eintraege.getValue();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == sourceButton) {
			JFileChooser fileChooser = new JFileChooser();
			if (getSourceFile().exists())
				fileChooser.setCurrentDirectory(getSourceFile());
			FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("HTML Datei", "html", "htm");
			fileChooser.setFileFilter(fileFilter);
			
			int returnVal = fileChooser.showOpenDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				sourceField.setText(file.getAbsolutePath());
				if (destinationField.getText().equals(""))
					destinationField.setText(file.getParent());
			}
		} else if (event.getSource() == startButton) {
			indexer.start();
		} else if (event.getSource() == destinationButton) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (getDestinationFile().exists())
				fileChooser.setCurrentDirectory(getDestinationFile());
			
			int returnVal = fileChooser.showSaveDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				destinationField.setText(file.getAbsolutePath());
			}
		}
	}
}
