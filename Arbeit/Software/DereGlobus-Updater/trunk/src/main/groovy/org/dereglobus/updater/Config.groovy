package org.dereglobus.updater

import java.util.prefs.Preferences

class Config {

	static final String nodeName = "/org/dereglobus/updater";

	static final String SOURCE_PATH = "SOURCE_PATH"
	static final String DEST_PATH = "DEST_PATH"

	Preferences prefs

	String sourcePath

	String destPath

	def log

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
		return prefs.get(DEST_PATH, new File(new File("").getAbsolutePath()))
	}

	void setDestPath(String path) {
		prefs.put(DEST_PATH, path)
	}
}
