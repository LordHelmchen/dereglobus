package org.dereglobus.updater

import groovy.io.FileType;
import groovy.io.FileVisitResult;

import java.util.List;

import org.dereglobus.updater.tree.CheckNode;

/**
 * Ein Selector stellt alle Dateien zusammen, die der Benutzer mit dem Anhaken von Verzeichnissen
 * ausgew�hlt hat. Dabei werden Dopplungen entfernt.
 * 
 * @author marcvonrenteln
 *
 */
class Selector {

	Config config

	public Selector(Config config) {
		this.config = config
	}

	/**
	 * Gibt alle Dateien zur�ck, die in Verzeichnissen sind, die "selected" sind.
	 */
	public List<File> getFiles(CheckNode root) {
		def files = []
		def dirs = getFolders(root)
		dirs.each { CheckNode node ->
			node.userObject.traverse(
					type         : FileType.FILES,
					preDir       : { if (it.name == '.svn') return FileVisitResult.SKIP_SUBTREE },
					) { files << it }
		}
		config.log.append "Alle Dateien in den selektierten Verzeichnissen wurden zusammengestellt.\n"
		return files
	}

	/**
	 * Gibt eine flache Liste von CheckNodes zur�ck, die "selected" sind.
	 * 
	 * Dabei wird der Umstand ausgenutzt, dass jeder CheckNode, der "aufgeklappt" wurde,
	 * die Child nodes enth�lt, da beim Caching der Verzeichnisse alle Unterverzeichnisse als
	 * CheckNodes an diesen angeh�ngt werden. So kann bis zu der Tiefe, in die sich der Benutzer
	 * bewegt hat, traversiert werden, ohne Verzeichnisse zu traversieren, die sich der Benutzer
	 * nie angeschaut hat, da diese in den Child nodes gar nicht enthalten sind (da die Childs ja
	 * erst bef�llt werden, wenn der Benutzer den Baum aufklappt).
	 * 
	 */
	public List<CheckNode> getFolders(CheckNode dir) {
		def dirs = []
		if (dir.isSelected()) {
			config.log.append "Bereite '$dir' vor.\n"
			dirs.add(dir)
			// Selektierte Unterverzeichnisse interessieren nun nicht mehr,
			// also nicht rekursiv in die Unterverzeichnisse verzweigen
		} else {
			// children() ist nur bef�llt, wenn sie gecached wurden
			dir.children()?.each { CheckNode child ->
				// jedes Verzeichnis rekursiv durchsuchen
				dirs.addAll(getFolders(child))
			}
		}
		return dirs
	}
}
