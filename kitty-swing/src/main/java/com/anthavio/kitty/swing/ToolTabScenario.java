/**
 * 
 */
package com.anthavio.kitty.swing;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.anthavio.io.FileUtils;

/**
 * @author vanek
 * 
 */
public class ToolTabScenario extends ToolTab implements HyperlinkListener {

	private final RSyntaxTextArea rsTextArea;

	private final RTextScrollPane rsSrollPane;

	public ToolTabScenario(ToolFrame toolFrame, File file) throws IOException {
		super(toolFrame, file);

		rsTextArea = createTextArea();
		rsSrollPane = new RTextScrollPane(rsTextArea);
		Gutter gutter = rsSrollPane.getGutter();
		gutter.setBookmarkingEnabled(true);
		URL url = getClass().getClassLoader().getResource("bookmark.png");
		gutter.setBookmarkIcon(new ImageIcon(url));

		String text = FileUtils.readFile(file);
		rsTextArea.setText(text);
		rsTextArea.setEditable(true);
		rsTextArea.setCaretPosition(0);

		// addTab(tabbedPane, file.getName(), rsSrollPane);
	}

	private RSyntaxTextArea createTextArea() {
		final RSyntaxTextArea rsTextArea = new RSyntaxTextArea();
		rsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		rsTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		rsTextArea.setEditable(true);
		rsTextArea.setMarkOccurrences(true);
		// rsTextArea.setTextAntiAliasHint(null);
		rsTextArea.addHyperlinkListener(this);
		rsTextArea.setCaretPosition(0);
		rsTextArea.requestFocusInWindow();

		rsTextArea.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent ce) {
				int line = rsTextArea.getCaretLineNumber();
				int pos = rsTextArea.getCaretOffsetFromLineStart();
				// takhle lze stopovat kouzor
				// System.out.println("Caret is [" + line + "," + pos + "]");
			}
		});

		return rsTextArea;

	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			URL url = e.getURL();
			if (url == null) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			} else {
				JOptionPane.showMessageDialog(super.toolFrame,
						"URL clicked:\n" + url.toString());
			}
		}
	}

	@Override
	public Component getContent() {
		return rsSrollPane;
	}

}
