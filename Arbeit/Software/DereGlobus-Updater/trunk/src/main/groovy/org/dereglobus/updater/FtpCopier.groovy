package org.dereglobus.updater

import java.io.ByteArrayInputStream;
import java.io.File;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.dereglobus.updater.tree.CheckNode;

class FtpCopier extends CopierBase {

	static final int NO_SUCH_FILE = 550
	static final int TRANSFER_COMPLETE = 226

	FTPClient ftp

	String actualPath = ""

	public FtpCopier(Config config) {
		super(config)
	}

	protected void init(CheckNode root) {
		super.init(root)
		ftp = new FTPClient()
		ftp.with {
			connect config.ftpServer

			if(!FTPReply.isPositiveCompletion(replyCode)) {
				finish()
				config.log("FTP-Server Verbindung fehlgeschlagen ($replyString) !")
			}

			enterLocalPassiveMode()
			config.log(replyString)
			login config.ftpUser, config.ftpPass
			config.log(replyString)
			cd("")
			fileType = FTPClient.BINARY_FILE_TYPE
		}
	}


	/**
	 * Wechselt das Verzeichnis in das Angegebene. 
	 * Wenn das Verzeichnis nicht existiert, wird es rekursiv angelegt.
	 * 
	 * @param dir immer relativ zum basePath der Anwendung!
	 */
	private boolean cd(String dir) {
		String newPath = config.ftpPath + "/" + dir

		if (actualPath == newPath) {
			return false
		}

		config.log "Wechsle in Verzeichnis $newPath."
		def relativePath
		if (newPath.startsWith(actualPath)) {
			relativePath = (newPath - actualPath).tokenize("/")
		} else {
			def actualPathDirs = actualPath.tokenize("/")
			def newPathDirs = newPath.tokenize("/")

			int dirsUp = 0
			int i = 0
			for (; i < actualPathDirs.size(); i++) {
				String actDir = actualPathDirs[i]
				if (newPathDirs.size() <= i || newPathDirs[i] != actualPathDirs[i]) {
					dirsUp = actualPathDirs.size() - i
					break;
				}
			}

			dirsUp.times{  ftp.changeToParentDirectory() }
			relativePath = newPathDirs[i..-1]
		}
		actualPath = newPath
		relativePath.each{
			if (!ftp.changeWorkingDirectory (it)) {
				createPath(it)
				ftp.changeWorkingDirectory (it)
			}
		}
		return true
	}

	protected boolean copyFile(File sourceFile) {
		ftp.with {
			def relativePath = sourceFile.getAbsolutePath() - (config.sourcePath + File.separator)
			relativePath = relativePath.replace("\\", "/")
			config.log "Kopiere $relativePath nach ${ftp.printWorkingDirectory()}/$relativePath"
			boolean success
			// in 90% der Fälle ist die Verzeichnisstruktur bereits vorhanden, also erstmal versuchen
			success = copyFile(sourceFile, relativePath)
			if (replyCode == NO_SUCH_FILE) {
				// Verzeichnisse fehlen, also anlegen und nochmal versuchen
				createPath relativePath[0..(relativePath.size()-sourceFile.name.size()-2)]
				success = copyFile(sourceFile, relativePath)
			}
			if (replyCode != TRANSFER_COMPLETE) {
				config.log("Kopieren von $relativePath fehlgeschlagen ($replyString) !")
				success = false
			}
			return success
		}
	}

	private boolean copyFile(File sourceFile, String relativePath) {
		boolean success
		if (relativePath.endsWith(".kml")) {
			String fileText = sourceFile.getText("UTF-8")
			fileText = config.filter.filter(fileText)
			InputStream inStream = new ByteArrayInputStream(fileText.getBytes("UTF-8"))
			inStream.withStream { instream -> success = ftp.storeFile (relativePath, instream) }
		} else {
			sourceFile.withInputStream { instream -> success = ftp.storeFile (relativePath, instream) }
		}
		return success
	}

	protected boolean createPath(String relativePath) {
		String tokenizedPath = ""
		relativePath.tokenize("/").each { String dirName ->
			if (!dirName.isEmpty()) {
				tokenizedPath += dirName + "/"
				ftp.makeDirectory(tokenizedPath)
			}
		}
	}

	protected void finish() {
		if(ftp.isConnected()) {
			try {
				ftp.disconnect();
			} catch(IOException ioe) {
				config.log "Unterbrechen der FTP-Verbindung fehlgeschlagen!\n  > ($ioe.message)"
			}
		}
		super.finish()
	}
}
