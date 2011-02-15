package org.dereglobus.updater


import groovy.beans.Bindable

import javax.swing.tree.TreeModel

import org.dereglobus.updater.tree.FileSystemCheckModel



class DereglobusUpdaterModel {
	// @Bindable String propName

	TreeModel treeModel = new FileSystemCheckModel()
}