package net.anthavio.kitty.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.anthavio.kitty.Kitty;
import net.anthavio.kitty.scenario.ScenarioFileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;


public class ToolFrame extends JFrame {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	static {
		try {
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (UnsupportedLookAndFeelException ulfx) {
			ulfx.printStackTrace();
		}
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
	}

	private static final long serialVersionUID = 1L;

	private final JTextField tfFilename = new JTextField();

	private final JButton btnBrowse = new JButton("Browse");

	private final JButton btnExecute = new JButton("Execute");

	private final JButton btnValidate = new JButton("Validate");

	private final JButton btnSave = new JButton("Save");

	private final JPanel panelCenter = new JPanel();

	private final JTabbedPane tabbedPane;
	/*

		private final RSyntaxTextArea rsTextArea;

		private final RTextScrollPane rsSrollPane;
	*/
	private ErrorDialog errorDialog;

	protected Kitty tool;

	private Map<File, ToolTab> tabs = new HashMap<File, ToolTab>();

	JFileChooser fileChooser = new JFileChooser();

	public ToolFrame() {
		setTitle("Kitty Gui");
		Container contentPane = getContentPane();

		tfFilename.setEditable(false);

		btnBrowse.addActionListener(new OpenAction());

		//btnExecute.addActionListener(new ExecuteAction());
		//btnValidate.addActionListener(new ValidateAction());
		//btnSave.addActionListener(new SaveAction());
		btnSave.setEnabled(false);

		JPanel panelPath = new JPanel();

		panelPath.setLayout(new BorderLayout());

		panelPath.add(tfFilename, BorderLayout.CENTER);
		panelPath.add(btnBrowse, BorderLayout.EAST);
		contentPane.add(panelPath, BorderLayout.NORTH);

		tabbedPane = new JTabbedPane();
		//addTab(tabbedPane, "Untitled", rsSrollPane);

		panelCenter.setLayout(new BorderLayout());
		panelCenter.add(tabbedPane);
		contentPane.add(panelCenter, BorderLayout.CENTER);

		JPanel panelButons = new JPanel();

		panelButons.add(btnExecute);
		panelButons.add(btnValidate);
		panelButons.add(btnSave);
		contentPane.add(panelButons, BorderLayout.SOUTH);

		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		this.setLocation(screenWidth / 4, (screenHeight / 6));
		this.setSize(screenWidth / 2, (int) (screenHeight / 1.5));
	}

	private JMenuBar createMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		JMenuItem item;

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		item = new JMenuItem(new OpenAction());
		KeyStroke ctrlOkey = KeyStroke.getKeyStroke("control O");
		item.setAccelerator(ctrlOkey);
		fileMenu.add(item);

		//item = new JMenuItem(new SaveAction());
		item.setEnabled(false);
		KeyStroke ctrlSkey = KeyStroke.getKeyStroke("control S");
		item.setAccelerator(ctrlSkey);
		fileMenu.add(item);

		fileMenu.addSeparator();

		item = new JMenuItem(new ExitAction());
		fileMenu.add(item);

		menuBar.add(fileMenu);

		/*
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		JCheckBoxMenuItem cbItem = new JCheckBoxMenuItem(new MonospacedFontAction());
		cbItem.setSelected(true);
		viewMenu.add(cbItem);

		cbItem = new JCheckBoxMenuItem(new ViewLineHighlightAction());
		cbItem.setSelected(true);
		viewMenu.add(cbItem);

		cbItem = new JCheckBoxMenuItem(new ViewLineNumbersAction());
		cbItem.setSelected(true);
		viewMenu.add(cbItem);

		cbItem = new JCheckBoxMenuItem(new AnimateBracketMatchingAction());
		cbItem.setSelected(true);
		viewMenu.add(cbItem);

		cbItem = new JCheckBoxMenuItem(new BookmarksAction());
		cbItem.setSelected(true);
		viewMenu.add(cbItem);

		cbItem = new JCheckBoxMenuItem(new MarkOccurrencesAction());
		cbItem.setSelected(true);
		viewMenu.add(cbItem);

		cbItem = new JCheckBoxMenuItem(new WordWrapAction());
		viewMenu.add(cbItem);

		cbItem = new JCheckBoxMenuItem(new ToggleAntiAliasingAction());
		viewMenu.add(cbItem);

		menuBar.add(viewMenu);
		*/

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		item = new JMenuItem(new AboutAction());
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		helpMenu.add(item);
		menuBar.add(helpMenu);

		return menuBar;
	}

	public void init(final Kitty tool, final File file) {
		this.tool = tool;
		tfFilename.setText(file.getAbsolutePath());

		//nutne spustit pres invokeAndWait protoze 
		//event dispatch thread jeste nemusi stihnout inicializovat gui
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					fileChooser.setFileFilter(new ScenarioFileFilter(tool.getOptions().getScenarioPrefix(), true));
					if (file.isDirectory()) {
						fileChooser.setCurrentDirectory(file);
					} else {
						fileChooser.setCurrentDirectory(file.getParentFile());
					}
					fileChooser.setSelectedFile(file);

					errorDialog = new ErrorDialog(ToolFrame.this, true);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	protected void openFile(File file) {
		try {
			tfFilename.setText(file.getAbsolutePath());
			ToolTab toolTab;
			if (file.isDirectory()) {
				toolTab = new ToolTabDirectory(this, file);
			} else {
				toolTab = new ToolTabScenario(this, file);
			}
			addTab(tabbedPane, toolTab.getTitle(), toolTab.getContent());
		} catch (Exception x) {
			exception(x);
		}
	}

	/**
	 * Helper metoda pridavajici novy tab s krizkem pro jeho zavirani
	 */
	private static void addTab(final JTabbedPane tabbedPane, String title, final Component content) {
		JLabel tabLabel = new JLabel(title);

		JButton tabClose = new TabButton();
		tabClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int closeTabNumber = tabbedPane.indexOfComponent(content);
				tabbedPane.removeTabAt(closeTabNumber);
			}
		});

		JPanel tab = new JPanel();
		tab.setOpaque(false);
		tab.setLayout(new BorderLayout(0, 0));
		tab.add(tabLabel, BorderLayout.WEST);
		tab.add(tabClose, BorderLayout.EAST);

		//pridame obsahovou komponentu
		tabbedPane.addTab(null, content);
		//nastavime ji pacicku s jmenem a krizkem
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tab);
		//vybereme si ji pro zobrazeni
		tabbedPane.setSelectedComponent(content);
	}

	private void exception(Throwable t) {
		String stackTrace = stackTrace(t);
		errorDialog.textArea.setText(stackTrace);
		errorDialog.setVisible(true);
	}

	private void setActionsState(boolean b) {
		btnExecute.setEnabled(b);
		btnValidate.setEnabled(b);
		btnSave.setEnabled(b);
	}

	private String stackTrace(Throwable t) {
		logger.error("Oups!", t);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	class NewAction extends AbstractAction implements ActionListener {

		private static final long serialVersionUID = 1L;

		public NewAction() {
			putValue(NAME, "New");
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}
	}

	class OpenAction extends AbstractAction implements ActionListener {

		private static final long serialVersionUID = 1L;

		public OpenAction() {
			putValue(NAME, "Open...");
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			int rVal = fileChooser.showOpenDialog(ToolFrame.this);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				openFile(fileChooser.getSelectedFile());
			}
			if (rVal == JFileChooser.CANCEL_OPTION) {
				//tfFilename.setText(fileChooser.getSelectedFile().getAbsolutePath());
				//tfFilename.setText("You pressed cancel");
			}
		}
	}

	class ExitAction extends AbstractAction implements ActionListener {

		private static final long serialVersionUID = 1L;

		public ExitAction() {
			putValue(NAME, "Exit");
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private class AboutAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AboutAction() {
			putValue(NAME, "About Kitty...");
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(ToolFrame.this,// 
					"<html><b>Kitty</b> - Komix Integration Test Tool Y"//
							+ "<br>Version 1.5.1" + "<br>Built on RSyntaxTextArea",// 
					"About Kitty", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	class ErrorDialog extends JDialog {

		private static final long serialVersionUID = 1L;

		JScrollPane scrollPane;
		JTextArea textArea = new JTextArea();

		public ErrorDialog(Frame owner, boolean modal) {
			super(owner, modal);
			init();
		}

		private void init() {
			this.setTitle("Error");
			this.setLayout(new GridLayout(1, 1));

			textArea.setEditable(false);
			textArea.setLineWrap(false);
			scrollPane = new JScrollPane(textArea);
			this.add(scrollPane);

			//Close on Esc
			KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
			Action actionListener = new AbstractAction() {
				public void actionPerformed(ActionEvent actionEvent) {
					setVisible(false);
				}
			};
			InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(stroke, "ESCAPE");
			rootPane.getActionMap().put("ESCAPE", actionListener);

			//Set cool Size and Location
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension screenSize = tk.getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			this.setLocation(screenWidth / 4, screenHeight / 6);
			this.setSize(screenWidth / 2, (int) (screenHeight / 1.5));
		}

	}
	/*
		class SaveAction extends AbstractAction implements ActionListener {

			private static final long serialVersionUID = 1L;

			public SaveAction() {
				putValue(NAME, "Save");
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				ValidateCmd cmd = new ValidateCmd();
				cmd.setTool(tool);
				try {
					cmd.validate(rsTextArea.getText());
					if (file.isFile()) {
						FileUtils.writeStringToFile(file, rsTextArea.getText(), "utf-8");
					}
				} catch (Exception x) {
					exception(x);
				}
			}
		}

		class SaveAsAction extends AbstractAction implements ActionListener {

			private static final long serialVersionUID = 1L;

			public SaveAsAction() {
				putValue(NAME, "Save As...");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				ValidateCmd cmd = new ValidateCmd();
				cmd.setTool(tool);
				try {
					cmd.validate(rsTextArea.getText());
					int rVal = fileChooser.showOpenDialog(ToolFrame.this);
					if (rVal == JFileChooser.APPROVE_OPTION) {
						//openFile(fileChooser.getSelectedFile());
					}
				} catch (Exception x) {
					exception(x);
				}
			}
		}

		class ExecuteAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				ExecuteCmd cmd = new ExecuteCmd();
				cmd.setTool(tool);
				try {
					if (file.isDirectory()) {
						cmd.execute(file);
					} else {
						cmd.executeScenario(rsTextArea.getText());
					}
				} catch (Throwable x) {
					exception(x);
				}
			}
		}

		class ValidateAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				ValidateCmd cmd = new ValidateCmd();
				cmd.setTool(tool);
				try {
					if (file.isDirectory()) {
						cmd.execute(file);
					} else {
						cmd.validate(rsTextArea.getText());
					}
				} catch (Throwable x) {
					exception(x);
				}
			}
		}

		private class AnimateBracketMatchingAction extends AbstractAction {

			private static final long serialVersionUID = 1L;

			public AnimateBracketMatchingAction() {
				putValue(NAME, "Animate Bracket Matching");
			}

			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<tabbedPane.getTabCount();++i) {
					Component component = tabbedPane.getComponentAt(i);
				}
				
				rsTextArea.setAnimateBracketMatching(!rsTextArea.getAnimateBracketMatching());
			}

		}

		private class BookmarksAction extends AbstractAction {

			private static final long serialVersionUID = 1L;

			public BookmarksAction() {
				putValue(NAME, "Bookmarks");
			}

			public void actionPerformed(ActionEvent e) {
				rsSrollPane.setIconRowHeaderEnabled(!rsSrollPane.isIconRowHeaderEnabled());
			}

		}

		private class MarkOccurrencesAction extends AbstractAction {

			private static final long serialVersionUID = 1L;

			public MarkOccurrencesAction() {
				putValue(NAME, "Mark Occurrences");
			}

			public void actionPerformed(ActionEvent e) {
				rsTextArea.setMarkOccurrences(!rsTextArea.getMarkOccurrences());
			}

		}

		private class MonospacedFontAction extends AbstractAction {

			private static final long serialVersionUID = 1L;

			private boolean selected;

			public MonospacedFontAction() {
				putValue(NAME, "Monospaced Font");
				selected = true;
			}

			public void actionPerformed(ActionEvent e) {
				selected = !selected;
				if (selected) {
					rsTextArea.setFont(RSyntaxTextArea.getDefaultFont());
				} else {
					//Font font = new Font("Dialog", Font.PLAIN, 13);
					rsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
				}
			}

		}

		private class ToggleAntiAliasingAction extends AbstractAction {

			private static final long serialVersionUID = 1L;

			private boolean selected;

			public ToggleAntiAliasingAction() {
				putValue(NAME, "Anti-Aliasing");
			}

			public void actionPerformed(ActionEvent e) {
				selected = !selected;
				String hint = selected ? "VALUE_TEXT_ANTIALIAS_ON" : null;
				rsTextArea.setTextAntiAliasHint(hint);
			}

		}

		private class ViewLineHighlightAction extends AbstractAction {

			private static final long serialVersionUID = 1L;

			public ViewLineHighlightAction() {
				putValue(NAME, "Current Line Highlight");
			}

			public void actionPerformed(ActionEvent e) {
				rsTextArea.setHighlightCurrentLine(!rsTextArea.getHighlightCurrentLine());
			}

		}

		private class ViewLineNumbersAction extends AbstractAction {

			private static final long serialVersionUID = 1L;

			public ViewLineNumbersAction() {
				putValue(NAME, "Line Numbers");
			}

			public void actionPerformed(ActionEvent e) {
				rsSrollPane.setLineNumbersEnabled(!rsSrollPane.getLineNumbersEnabled());
			}

		}

		private class WordWrapAction extends AbstractAction {

			private static final long serialVersionUID = 1L;

			public WordWrapAction() {
				putValue(NAME, "Word Wrap");
			}

			public void actionPerformed(ActionEvent e) {
				rsTextArea.setLineWrap(!rsTextArea.getLineWrap());
			}

		}
	*/
}
