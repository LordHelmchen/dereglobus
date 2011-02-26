package org.dereglobus.updater

import groovy.swing.SwingBuilder

import java.io.File

import javax.swing.*

import org.dereglobus.updater.tree.CheckRenderer
import org.dereglobus.updater.tree.FileSystemCheckModel
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

	def invokeOutside = {
		SwingUtilities.isEventDispatchThread() ? Thread.start(it) : it()
	}

	static config

	static swing

	String file

	JTree filesTree

	def userField

	def passwordField

	def destField

	def sourceField

	def releaseUrlField

	def publicUrlField

	def serverField

	def pathField


	public static void main(String[] args) {
		config = new Config()
		swing = new SwingBuilder()
		new DereGlobusUpdater().initComponents()
	}

	private void initComponents() {
		FormLayout layout = new FormLayout(
				"right:pref, 3dlu, 200dlu, 3dlu, min", // columns
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");  // rows
		CellConstraints contraints = new CellConstraints();
		def compFactory = DefaultComponentFactory.getInstance()

		// -----------------------------------------------------------------------------

		def frame = swing.frame(title: 'DereGlobus Updater', defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE, size: [1024, 700], show: true, locationRelativeTo: null) {
			lookAndFeel(UIManager.systemLookAndFeelClassName)
			menuBar() {
				menu(text: "Datei", mnemonic: 'D') {
					menuItem(text: "Beenden", mnemonic: 'B', actionPerformed: { dispose() })
				}
			}
			splitPane {
				scrollPane(constraints: "left", preferredSize: [300, -1]) {
					filesTree = tree(model: new FileSystemCheckModel(new File(config.getSourcePath())),
							cellRenderer: new CheckRenderer(),
							rowHeight: getRowHight())
					filesTree.addMouseListener(new NodeSelectionListener(filesTree));
				}
				splitPane(orientation:JSplitPane.VERTICAL_SPLIT, dividerLocation:450) {
					scrollPane(constraints: "top") {
						panel( layout: layout, border: Borders.DIALOG_BORDER) {
							widget( widget: compFactory.createSeparator('Grundeinstellungen'), constraints: contraints.xyw(1, 1, 5))
							label("Quellverzeichnis",constraints: contraints.xy (1, 3))
							sourceField = textField(text: config.sourcePath, constraints: contraints.xy (3, 3), editable: false)
							withIcon(button(constraints: contraints.xy (5, 3, "left, default"),
									action: action(closure: openSourceChooser)), "046.png")
							label("Release-URL",constraints: contraints.xy (1, 5))
							releaseUrlField = textField(text: config.releaseUrl,	constraints: contraints.xyw (3, 5, 3))
							label("Public-URL",constraints: contraints.xy (1, 7))
							publicUrlField = textField(text: config.publicUrl,	constraints: contraints.xyw (3, 7, 3))

							widget( widget: compFactory.createSeparator('Lokales Kopieren'), constraints: contraints.xyw(1, 9, 5))
							label("Zielverzeichnis",constraints: contraints.xy (1, 11))
							destField = textField(text: config.destPath, constraints: contraints.xy (3, 11), editable: false)
							withIcon(button(constraints: contraints.xy (5, 11),
									action: action(closure: openDestChooser)), "046.png")
							withIcon(button(constraints: contraints.xyw (3, 13, 3, "right, default"),
									action: action(name: 'Kopieren', closure: copy)), "095.png")

							widget( widget: compFactory.createSeparator('Kopieren auf FTP-Server'), constraints: contraints.xyw(1, 15, 5))
							label("FTP-Server",constraints: contraints.xy (1, 17))
							serverField = textField(text: config.ftpServer,	constraints: contraints.xyw (3, 17, 3))
							label("FTP-Server-Pfad",constraints: contraints.xy (1, 19))
							pathField = textField(text: config.ftpPath,	constraints: contraints.xyw (3, 19, 3))
							label("Benutzername",constraints: contraints.xy (1, 21))
							userField = textField(text: config.ftpUser,	constraints: contraints.xyw (3, 21, 3))
							label("Passwort",constraints: contraints.xy (1, 23))
							passwordField = passwordField(text: config.ftpPass,	constraints: contraints.xyw (3, 23, 3))
							withIcon(button(constraints: contraints.xyw (3, 25, 3, "right, default"),
									action: action(name: 'Hochladen', closure: copyFtp)), "094.png")
						}
					}
					scrollPane(constraints: "bottom") {
						config.log = textArea(editable: false)
					}
				}
			}
		}
		config.log "Dieses Programm verwendet das IconSet Diagona.\nCopyright (C) 2007 Yusuke Kamiyamane (http://www.pinvoke.com/)."
	}

	private int getRowHight() {
		switch (System.getProperty("os.name").toLowerCase()) {
			case ~/.*win.*/:
				return 18
			case ~/.*mac.*/:
				return 20
			default:
				return 18
		}
	}

	private JButton withIcon(JButton button, String name) {
		button.icon = new ImageIcon(getClass().getResource(name))
		return button
	}

	def openSourceChooser = {
		def fc = swing.fileChooser(dialogTitle:"Gib das Wurzelverzeichnis von DereGlobus an",
				id:"openDialog", fileSelectionMode : JFileChooser.DIRECTORIES_ONLY)
		int returnVal = fc.showOpenDialog();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			filesTree.setModel (new FileSystemCheckModel(file))
			sourceField.text = file.getAbsolutePath()
			saveConfig()
		}
	}

	def openDestChooser = {
		def fc = swing.fileChooser(dialogTitle:"Gib das Zielverzeichnis an",
				id:"saveDialog", fileSelectionMode : JFileChooser.DIRECTORIES_ONLY)
		int returnVal = fc.showOpenDialog();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			destField.text = file.getAbsolutePath()
			saveConfig()
		}
	}

	def copy = {
		saveConfig()
		invokeOutside {
			new LocalCopier(config).copy(filesTree.getModel().getRoot())
		}
	}

	def copyFtp = {
		saveConfig()
		invokeOutside {
			new FtpCopier(config).copy(filesTree.getModel().getRoot())
		}
	}

	private void saveConfig() {
		config.setSourcePath(sourceField.text)
		config.setReleaseUrl(releaseUrlField.text)
		config.setPublicUrl(publicUrlField.text)
		config.setDestPath(destField.text)
		config.setFtpServer(serverField.text)
		config.setFtpPath(pathField.text)
		config.setFtpUser(userField.text)
		config.setFtpPass(passwordField.text)
	}
}
