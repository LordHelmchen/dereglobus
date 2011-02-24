package org.dereglobus.updater

import java.io.File;

import junit.framework.TestCase

import org.dereglobus.updater.tree.CheckNode

class FtpCopierTest extends TestCase {

	def ftp

	def config

	public void setUp() {
		config = new Config()
		def sourcePath = new File(new File("").getAbsolutePath())
		ftp = new FtpCopier(config)
		ftp.init(new CheckNode(sourcePath))
	}

	public void testChangeDirs() {
		println ftp.ftp.listFiles()
		assert ftp.cd("test/subtest/subsubtest")
		println ftp.ftp.listFiles()
		assert ftp.cd("test2/test2sub")
		println ftp.ftp.listFiles()
	}

	public void testCopyFile() {
		File file = new File(config.sourcePath+"/Chroniken/kml/Chroniken.kml")
		assert file.exists()
		assert ftp.copyFile(file)
	}

	public void tearDown() {
		ftp.finish()
	}
}
