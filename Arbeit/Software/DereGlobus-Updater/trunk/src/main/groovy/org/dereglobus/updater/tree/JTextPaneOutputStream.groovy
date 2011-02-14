package org.dereglobus.updater.tree

import javax.swing.JTextArea;
import javax.swing.JTextPane
import javax.swing.text.Document

class JTextPaneOutputStream extends OutputStream {
	JTextArea tp;
	Document doc;
	def originalOutput = System.out

	public JTextPaneOutputStream(def t) {
		super();
		tp = t;
		doc = tp.getDocument();
	}

	public void write(int i) {
		//*** Encoding Problem Here ??
		String s = Character.toString((char)i);
		tp.append s
		if (s == "\n") {
			tp.setCaretPosition(doc.length -1)
		}
	}

	public static void setSysout(JTextArea text) {
		System.setOut(new PrintStream(new BufferedOutputStream(new JTextPaneOutputStream(text), 32000), true, "UTF-8"))
	}
}

