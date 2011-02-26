package org.dereglobus.updater

import org.dereglobus.updater.tree.FileSystemCheckModel;

import junit.framework.TestCase

class SelectorTest extends TestCase {

	def basePath

	def model

	def root

	def childs

	def selector

	public void setUp() {
		basePath = new File(new File("").getAbsolutePath())
		println "base path: "+basePath.absolutePath
		assert basePath.exists() && basePath.isDirectory()
		model = new FileSystemCheckModel(basePath)
		root = model.getRoot()
		childs = model.getChilds(root)
		selector = new Selector(new Config())
	}


	public void testActualDir() {
		def child = model.getChild(root, 1)
		assert "src" == child.userObject.getName()
		assert 3 == childs.size()
	}


	public void testModelSelected() {
		def child = model.getChild(root, 1)
		child.setSelected(true)
		assert child.isSelected()
	}


	public void testSelect() {
		model.getChild(root, 1).setSelected(true)
		model.getChild(root, 2).setSelected(true)

		assert 2 == selector.getFolders(root).size()
	}


	public void testSelectRedundant() {
		model.getChilds(childs[1])  // caching aktivieren

		def child = model.getChild(root, 1)
		child.setSelected(true)
		child.children().nextElement().setSelected(true)

		assert 1 == selector.getFolders(root).size()
	}


	public void testSelectUnselectedParent() {
		model.getChilds(childs[1])  // caching aktivieren

		childs[1].children().nextElement().setSelected(true)

		assert 1 == selector.getFolders(root).size()
	}


	public void testSelectRoot() {
		model.getRoot().setSelected(true)
		assert 1 == selector.getFolders(root).size()
	}
}
