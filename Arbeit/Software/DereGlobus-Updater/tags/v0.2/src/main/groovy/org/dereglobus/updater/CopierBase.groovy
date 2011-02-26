package org.dereglobus.updater

import java.io.File;
import java.util.List;

import org.dereglobus.updater.tree.CheckNode;

abstract class CopierBase {

	Config config

	String rootPath

	List files

	public CopierBase(Config config) {
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

	abstract protected boolean copyFile(File sourceFile);

	protected void finish() {
		config.log "Das Kopieren wurde erfolgreich beendet!"
	}
}
