package org.dereglobus.updater

import groovy.io.FileType;
import groovy.io.FileVisitResult;

import org.dereglobus.updater.tree.CheckNode

class Copier {

	static LOG

	public copy() {
		LOG = new StringBuilder()
	}

	public copy(def log) {
		LOG = log
	}

	public void copy(CheckNode root, def log) {
		def dirs = getFolders(root)
		dirs.each { CheckNode node ->
			node.userObject.traverse(
					type         : FileType.FILES,
					preDir       : {
						if (it.name == '.svn') return FileVisitResult.SKIP_SUBTREE
					},
					) { LOG.append "Kopiere $it\n" }
		}
	}

	private List getFolders(CheckNode dir) {
		def dirs = []
		if (dir.isSelected()) {
			LOG.append "Bereite $dir vor\n"
			dirs.add(dir)
		}

		dir.children()?.each {
			dirs.addAll(getFolders(it))
		}
		return dirs
	}
}
