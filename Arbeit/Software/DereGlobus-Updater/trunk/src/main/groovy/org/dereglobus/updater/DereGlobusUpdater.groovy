package org.dereglobus.updater

import groovy.beans.Bindable;
import groovy.swing.SwingBuilder

import java.io.File

import javax.swing.*

import org.dereglobus.updater.tree.CheckRenderer
import org.dereglobus.updater.tree.FileSystemCheckModel
import org.dereglobus.updater.tree.JTextPaneOutputStream
import org.dereglobus.updater.tree.NodeSelectionListener

import com.jgoodies.forms.factories.Borders
import com.jgoodies.forms.factories.DefaultComponentFactory
import com.jgoodies.forms.layout.CellConstraints
import com.jgoodies.forms.layout.FormLayout

/**
 * Zentrale Start-Klasse des DereGlobus Updaters, die die GUI anzeigt und verwaltet.
 * 
 * @author marcvonrenteln
 *
 */
class DereGlobusUpdater {

	static config

	static swing

	String file

	JTree filesTree

	def userField

	def passwordField

	def destField


	public static void main(String[] args) {
		config = new Config()
		swing = new SwingBuilder()
		new DereGlobusUpdater().initComponents()
	}

	private void initComponents() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, 100dlu, 3dlu, min", // columns
				"p, 3dlu, p, 9dlu, p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");  // rows
		CellConstraints contraints = new CellConstraints();
		def compFactory = DefaultComponentFactory.getInstance()

		// -----------------------------------------------------------------------------

		def frame = swing.frame(title: 'DereGlobus Updater', defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE, size: [800, 600], show: true, locationRelativeTo: null) {
			lookAndFeel(UIManager.systemLookAndFeelClassName)
			menuBar() {
				menu(text: "Datei", mnemonic: 'D') {
					menuItem(text: "Beenden", mnemonic: 'B', actionPerformed: { dispose() })
				}
			}
			splitPane {
				scrollPane(constraints: "left", preferredSize: [160, -1]) {
					filesTree = tree(model: new FileSystemCheckModel(new File(config.getSourcePath())),
							cellRenderer: new CheckRenderer(),
							rowHeight: 18)
					filesTree.addMouseListener(new NodeSelectionListener(filesTree));
				}
				splitPane(orientation:JSplitPane.VERTICAL_SPLIT, dividerLocation:320) {
					scrollPane(constraints: "top") {
						panel( layout: layout, border: Borders.DIALOG_BORDER) {
							widget( widget: compFactory.createSeparator('Grundeinstellungen'), constraints: contraints.xyw(1, 1, 5))
							button(constraints: contraints.xyw (1, 3, 2, "left, default"),
									action: action(name: 'Quellverzeichnis wechseln', closure: openSourceChooser))

							widget( widget: compFactory.createSeparator('Lokales Kopieren'), constraints: contraints.xyw(1, 5, 5))
							label("Zielverzeichnis",constraints: contraints.xy (1, 7))
							destField = textField(text: config.destPath, constraints: contraints.xy (3, 7), editable: false)
							button(constraints: contraints.xy (5, 7),
									action: action(name: '...', closure: openDestChooser))
							button(constraints: contraints.xyw (3, 9, 3, "right, default"),
									action: action(name: 'Kopieren!', closure: copy))

							widget( widget: compFactory.createSeparator('Kopieren auf FTP-Server'), constraints: contraints.xyw(1, 11, 5))
							label("Benutzername",constraints: contraints.xy (1, 13))
							userField = textField(text: config.ftpUser,	constraints: contraints.xyw (3, 13, 3))
							label("Passwort",constraints: contraints.xy (1, 15))
							passwordField = passwordField(text: config.ftpPass,	constraints: contraints.xyw (3, 15, 3))
							button(constraints: contraints.xyw (3, 17, 3, "right, default"),
									action: action(name: 'Kopieren!', closure: copyFtp))
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

	def openSourceChooser = {
		def fc = swing.fileChooser(dialogTitle:"Gib das Wurzelverzeichnis von DereGlobus an",
				id:"openDialog", fileSelectionMode : JFileChooser.DIRECTORIES_ONLY)
		int returnVal = fc.showOpenDialog();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			filesTree.setModel (new FileSystemCheckModel(file))
			config.setSourcePath(file.getAbsolutePath())
		}
	}

	def openDestChooser = {
		def fc = swing.fileChooser(dialogTitle:"Gib das Zielverzeichnis an",
				id:"saveDialog", fileSelectionMode : JFileChooser.DIRECTORIES_ONLY)
		int returnVal = fc.showOpenDialog();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			destField.text = file.getAbsolutePath()
			config.setDestPath(file.getAbsolutePath())
		}
	}

	def copy = {
		new Copier(config).copy(filesTree.getModel().getRoot())
	}

	def copyFtp = {
		config.setFtpUser(userField.text)
		config.setFtpPass(passwordField.text)
		new Copier(config).copy(filesTree.getModel().getRoot())
	}
}
