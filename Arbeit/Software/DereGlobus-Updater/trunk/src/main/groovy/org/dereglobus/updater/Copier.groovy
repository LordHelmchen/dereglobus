package org.dereglobus.updater

import groovy.util.AntBuilder;

import java.io.File;

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

	Config config

	String rootPath

	List files

	public Copier(Config config) {
		this.config = config
	}

	protected void init(CheckNode root) {
		rootPath = root.userObject.getAbsolutePath()
		files = new Selector(config).getFiles(root)
	}

	public void copy(CheckNode root) {
		init(root)

		try {
			files.each { File sourceFile ->
				copyFile(sourceFile)
			}
		} finally {
			finish()
		}
	}


	protected boolean copyFile(File sourceFile) {
		String sourceFileName = sourceFile.getName()
		String relativePath = sourceFile.getParent() - rootPath
		File destDir = new File(new File(config.getDestPath()), relativePath)
		destDir.mkdirs()
		File destFile = new File(destDir, sourceFileName)
		config.log.append "Kopiere $relativePath$File.separator$sourceFileName\n"

		if (sourceFileName.endsWith(".kml")) {
			String fileText = sourceFile.getText("UTF-8")
			fileText = config.filter.filter(fileText)
			destFile.write(fileText, "UTF-8")
		} else {
			if (destFile.exists()) {
				destFile.delete()
			}
			destFile.createNewFile()

			sourceFile.withInputStream { inStream ->
				destFile.withOutputStream{ out -> out << inStream }
			}
		}
		return true
	}

	protected void finish() {
		// do nothing
	}
}
