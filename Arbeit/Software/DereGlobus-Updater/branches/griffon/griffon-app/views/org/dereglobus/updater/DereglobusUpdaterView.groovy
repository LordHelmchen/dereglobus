package org.dereglobus.updater

import java.awt.Insets
import java.io.File;

import org.dereglobus.updater.tree.CheckRenderer
import org.dereglobus.updater.tree.FileSystemCheckModel
import org.dereglobus.updater.tree.NodeSelectionListener


def controller

String file

def filesTree

def log

actions {
	action(id: 'copyAction',
			name: 'Kopieren',
			closure: controller.copy)

	action(id: 'chooseFileAction',
			name: 'Verzeichnis wechseln',
			closure: openFileChooser)
}


application(title: 'DereGlobus Updater',
		size: [800, 600],
		//		pack: true,
		show: true,
		location: [50, 50],
		locationByPlatform:true,
		iconImage: imageIcon('/griffon-icon-48x48.png').image,
		iconImages: [
			imageIcon('/griffon-icon-48x48.png').image,
			imageIcon('/griffon-icon-32x32.png').image,
			imageIcon('/griffon-icon-16x16.png').image
		]) {
			splitPane {
				scrollPane(constraints: "left", preferredSize: [160, -1]) {
					filesTree = tree(model: new FileSystemCheckModel(new File(new File("").getAbsolutePath())),
							cellRenderer: new CheckRenderer(),
							rowHeight: 18)
					filesTree.addMouseListener(new NodeSelectionListener(filesTree));
				}
				splitPane(orientation:JSplitPane.VERTICAL_SPLIT, dividerLocation:280) {
					scrollPane(constraints: "top") {
						hbox() {
							button chooseFileAction
							button copyAction
						}
					}
					scrollPane(constraints: "bottom") {
						log = textArea(editable: false)
					}
				}
			}
		}


def openFileChooser = {
	def fc = swing.fileChooser(dialogTitle:"Gib das Wurzelverzeichnis von DereGlobus an",
			id:"openDialog", fileSelectionMode : JFileChooser.DIRECTORIES_ONLY)
	int returnVal = fc.showOpenDialog();

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		File chosenFile = fc.getSelectedFile();
		filesTree.setModel (new FileSystemCheckModel(chosenFile))
	} else {
		//		log.append("Wechseln vom Verzeichnis vom Benutzer abgebrochen.\n");
	}
	//	log.setCaretPosition(log.getDocument().getLength());
}