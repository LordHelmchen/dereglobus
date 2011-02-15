application {
	title = 'DereGlobus Updater'
	startupGroups = ['dereglobus-updater']

	// Should Griffon exit when no Griffon created frames are showing?
	autoShutdown = true

	// If you want some non-standard application class, apply it here
	//frameClass = 'javax.swing.JFrame'
}
mvcGroups {
	// MVC Group for "dereglobus-updater"
	'dereglobus-updater' {
		model = 'org.dereglobus.updater.DereglobusUpdaterModel'
		controller = 'org.dereglobus.updater.DereglobusUpdaterController'
		view = 'org.dereglobus.updater.DereglobusUpdaterView'
	}
}
