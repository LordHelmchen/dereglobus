package org.dereglobus.updater

import java.util.prefs.Preferences

class Config {

	static final String nodeName = "/org/dereglobus/updater";

	static final String SOURCE_PATH = "SOURCE_PATH"
	static final String DEST_PATH = "DEST_PATH"
	static final String FTP_USER = "FTP_USER"
	static final String FTP_PASS = "FTP_PASS"

	Preferences prefs

	def log = new StringBuffer()

	Filter filter

	String ftpServer = "dereglobus.orkenspalter.com"

	//	String ftpPath = "dereglobus.orkenspalter.de/public/DereGlobus"
	String ftpPath = "dereglobus.orkenspalter.de/public/DereGlobusTest"

	public Config() {
		prefs = Preferences.userRoot().node(nodeName)
		filter = new SimpleServerUrlFilter("http://www.dereglobus.orkenspalter.com/svn/Release/", "http://www.dereglobus.orkenspalter.de/public/")
	}

	String getSourcePath() {
		return prefs.get(SOURCE_PATH, new File("").getAbsolutePath())
	}

	void setSourcePath(String path) {
		prefs.put(SOURCE_PATH, path)
	}

	String getDestPath() {
		return prefs.get(DEST_PATH, new File("").getAbsolutePath())
	}

	void setDestPath(String path) {
		prefs.put(DEST_PATH, path)
	}

	String getFtpUser() {
		return prefs.get(FTP_USER, "user")
	}

	void setFtpUser(String user) {
		prefs.put(FTP_USER, user)
	}

	String getFtpPass() {
		return prefs.get(FTP_PASS, "pass")
	}

	void setFtpPass(String pass) {
		prefs.put(FTP_PASS, pass)
	}
}
