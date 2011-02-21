package org.dereglobus.updater

import org.dereglobus.updater.tree.CheckNode

/**
 * 
 * Kopiert alle übergebenen Dateien lokal in ein angegebenes Verzeichnis. 
 * Die Auswahl der Dateien werden durch einen Selector vorgenommen. Die
 * resultierenden Dateien werden durch einen übergebenen Filter gefiltert,
 * bevor sie an ihr Ziel kopiert werden.
 * 
 * @author marcvonrenteln
 *
 */
class Copier {

	def LOG

	public Copier(def log) {
		LOG = log
	}

	public void copy(CheckNode root) {
		def files = new Selector(LOG).getFiles(root)

		def dest = new File("<dest>")

		files.each { File file ->
			if (file.getName().endsWith(".kml")) {
				LOG.append "Kopiere $file\n"
				String fileText = file.getText()
				File destFile = new File(dest, file.getName())
				destFile.write(fileText)
			} else {
				// Datei binär kopieren
			}
		}
	}
}
