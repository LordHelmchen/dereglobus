package org.dereglobus.updater.tree

import junit.framework.TestCase

class FileSystemCheckModelTest extends TestCase {
  
  public void testTree() {
    def basePath = new File(new File("").getAbsolutePath())
    println "base path: "+basePath.absolutePath
    assert basePath.exists() && basePath.isDirectory()
    def model = new FileSystemCheckModel(basePath)
    assert 3 == model.getChildCount (model.getRoot())
    assert 3 == model.getChilds(model.getRoot()).size()
    
    // Test caching
    assert 3 == model.getChildCount (model.getRoot())
    assert 3 == model.getChilds(model.getRoot()).size()
  }
}
