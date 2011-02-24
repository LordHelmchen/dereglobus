package org.dereglobus.updater

import java.util.prefs.Preferences

import javax.swing.SwingUtilities

class Config {

	static final String nodeName = "/org/dereglobus/updater";

	static final String SOURCE_PATH = "SOURCE_PATH"
	static final String DEST_PATH = "DEST_PATH"
	static final String FTP_USER = "FTP_USER"
	static final String FTP_PASS = "FTP_PASS"
	static final String FTP_SERVER = "FTP_SERVER"
	static final String FTP_PATH = "FTP_PATH"
	static final String RELEASE_URL = "RELEASE_URL"
	static final String PUBLIC_URL = "PUBLIC_URL"

	Preferences prefs

	def log = new StringBuffer()

	public Config() {
		prefs = Preferences.userRoot().node(nodeName)
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

	String getFtpServer() {
		return prefs.get(FTP_SERVER, "dereglobus.orkenspalter.com")
	}

	void setFtpServer(String server) {
		prefs.put(FTP_SERVER, server)
	}

	String getFtpPath() {
		return prefs.get(FTP_PATH, "dereglobus.orkenspalter.de/public/DereGlobusTest")
	}

	void setFtpPath(String path) {
		prefs.put(FTP_PATH, path)
	}

	String getReleaseUrl() {
		return prefs.get(RELEASE_URL, "http://www.dereglobus.orkenspalter.com/svn/Release/")
	}

	void setReleaseUrl(String path) {
		prefs.put(RELEASE_URL, path)
	}

	String getPublicUrl() {
		return prefs.get(PUBLIC_URL, "http://www.dereglobus.orkenspalter.de/public/")
	}

	void setPublicUrl(String path) {
		prefs.put(PUBLIC_URL, path)
	}

	Filter getFilter() {
		return new SimpleServerUrlFilter(getReleaseUrl(), getPublicUrl())
	}

	void log(String logMessage) {
		SwingUtilities.invokeLater { log.append logMessage }
	}
}
