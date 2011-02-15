package org.dereglobus.updater


class DereglobusUpdaterController {
	// these will be injected by Griffon
	def model
	def view

	// void mvcGroupInit(Map args) {
	//    // this method is called after model and view are injected
	// }

	// void mvcGroupDestroy() {
	//    // this method is called when the group is destroyed
	// }

	def copy = { evt = null ->
		new Copier().copy(model.treeModel.getRoot())
	}
}
