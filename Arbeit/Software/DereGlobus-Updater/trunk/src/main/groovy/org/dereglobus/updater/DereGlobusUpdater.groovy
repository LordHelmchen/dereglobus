package org.dereglobus.updater

import groovy.swing.SwingBuilder

import java.awt.Insets
import java.io.File

import javax.swing.*

import org.dereglobus.updater.tree.CheckRenderer
import org.dereglobus.updater.tree.FileSystemCheckModel
import org.dereglobus.updater.tree.JTextPaneOutputStream
import org.dereglobus.updater.tree.NodeSelectionListener

/**
 * Zentrale Start-Klasse des DereGlobus Updaters, die die GUI anzeigt und verwaltet.
 * 
 * @author marcvonrenteln
 *
 */
class DereGlobusUpdater {

	static String file

	static config

	static JTree filesTree

	static swing = new SwingBuilder()


	public static void main(String[] args) {
		config = new Config()

		def frame = swing.frame(title: 'DereGlobus Updater', defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE, size: [800, 600], show: true, locationRelativeTo: null) {
			lookAndFeel("system")
			menuBar() {
				menu(text: "Datei", mnemonic: 'D') {
					menuItem(text: "Beenden", mnemonic: 'B', actionPerformed: {dispose() })
				}
			}
			splitPane {
				scrollPane(constraints: "left", preferredSize: [160, -1]) {
					filesTree = tree(model: new FileSystemCheckModel(new File(config.getSourcePath())),
							cellRenderer: new CheckRenderer(),
							rowHeight: 18)
					filesTree.addMouseListener(new NodeSelectionListener(filesTree));
				}
				splitPane(orientation:JSplitPane.VERTICAL_SPLIT, dividerLocation:280) {
					scrollPane(constraints: "top") {
						hbox() {
							button(margin: new Insets(5, 10, 15, 20),
									action: action(name: 'Verzeichnis wechseln', closure: openFileChooser))
							button(margin: new Insets(5, 10, 15, 20),
									action: action(name: 'Kopieren!', closure: copy))
						}
					}
					scrollPane(constraints: "bottom") {
						config.log = textArea(editable: false)
					}
				}
			}
		}
		JTextPaneOutputStream.setSysout(config.log)
	}

	static openFileChooser = {
		def fc = swing.fileChooser(dialogTitle:"Gib das Wurzelverzeichnis von DereGlobus an",
				id:"openDialog", fileSelectionMode : JFileChooser.DIRECTORIES_ONLY)
		int returnVal = fc.showOpenDialog();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			filesTree.setModel (new FileSystemCheckModel(file))
			config.setSourcePath(file.getAbsolutePath())
		} else {
			config.log.append("Wechseln vom Verzeichnis vom Benutzer abgebrochen.\n");
		}
		config.log.setCaretPosition(config.log.getDocument().getLength());
	}

	static copy = {
		def filter = new SimpleServerUrlFilter("http://www.dereglobus.orkenspalter.com/svn/Release/", "http://www.dereglobus.orkenspalter.de/public/")
		new Copier(config.log, filter).copy(filesTree.getModel().getRoot())
	}
}
