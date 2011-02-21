package org.dereglobus.updater

import org.dereglobus.updater.tree.CheckNode

/**
 * 
 * Kopiert alle �bergebenen Dateien lokal in ein angegebenes Verzeichnis. 
 * Die Auswahl der Dateien werden durch einen Selector vorgenommen. Die
 * resultierenden Dateien werden durch einen �bergebenen Filter gefiltert,
 * bevor sie an ihr Ziel kopiert werden.
 * 
 * @author marcvonrenteln
 *
 */
class Copier {

	def LOG

	Filter filter

	public Copier(def log) {
		LOG = log
		filter = new NullFilter()
	}

	public Copier(def log, Filter filter) {
		LOG = log
		this.filter = filter
	}

	public void copy(CheckNode root) {
		String rootPath = root.userObject.getAbsolutePath()
		def destRoot = new File("<dest>")
		List files = new Selector(LOG).getFiles(root)

		files.each { File sourceFile ->
			String sourceFileName = sourceFile.getName()
			if (sourceFileName.endsWith(".kml")) {
				String relativePath = sourceFile.getParent() - rootPath
				File destDir = new File(destRoot, relativePath)
				destDir.mkdirs()
				File destFile = new File(destDir, sourceFileName)

				LOG.append "Kopiere $relativePath$File.separator$sourceFileName\n"
				String fileText = sourceFile.getText("UTF-8")
				destFile.write(filter.filter(fileText), "UTF-8")
			} else {
				// TODO Datei bin�r kopieren
			}
		}
	}
}
