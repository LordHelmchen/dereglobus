package org.dereglobus.updater

import junit.framework.TestCase

class CopyTest extends TestCase {
  
  public void testCopy() {
    def basePath = new File(new File("").getAbsolutePath())
    println "base path: "+basePath.absolutePath
    assert basePath.exists() && basePath.isDirectory()
    def model = new FileSystemCheckModel(basePath)
    def childs = model.getChilds(model.getRoot())
    assert 3 == childs.size()
    model.getChilds(childs[1])
    
    def child = model.getChild(model.getRoot(), 1)
    assert "src" == child.userObject.getName()
    child.setSelected(true)
    assert child.isSelected()
    child.children().nextElement().setSelected(true)
    
    assert 2 == new Copier().doCopy(model.getRoot()).size()
  }
  
  public void testCopyUnselectedParent() {
    def basePath = new File(new File("").getAbsolutePath())
    println "base path: "+basePath.absolutePath
    assert basePath.exists() && basePath.isDirectory()
    def model = new FileSystemCheckModel(basePath)
    def childs = model.getChilds(model.getRoot())
    model.getChilds(childs[1])
    
    def child = model.getChild(model.getRoot(), 1)
    assert "src" == child.userObject.getName()
    child.children().nextElement().setSelected(true)
    
    assert 1 == new Copier().doCopy(model.getRoot()).size()
  }
  
  public void testCopyRoot() {
    def basePath = new File(new File("").getAbsolutePath())
    println "base path: "+basePath.absolutePath
    assert basePath.exists() && basePath.isDirectory()
    def model = new FileSystemCheckModel(basePath)
    def childs = model.getChilds(model.getRoot())
    
    model.getRoot().setSelected(true)
    
    assert 1 == new Copier().doCopy(model.getRoot()).size()
  }
}
