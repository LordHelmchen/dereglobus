package org.dereglobus.updater.tree


import javax.swing.event.TreeModelEvent 
import javax.swing.event.TreeModelListener 
import javax.swing.tree.TreeModel 
import javax.swing.tree.TreePath 

class FileSystemCheckModel implements TreeModel {
  CheckNode root;
  
  def listeners = [];
  
  public FileSystemCheckModel(File rootDirectory) {
    root = createCheckNode(rootDirectory);
  }
  
  public Object getRoot() {
    return root;
  }
  
  public Object getChild(Object parent, int index) {
    if (parent.getChildCount() == 0) {
      return getChilds(parent)[index]
    } else {
      return parent.getChildAt(index)
    }
  }
  
  public int getChildCount(Object parent) {
    if (parent.userObject.isDirectory()) {
      return getChilds(parent).size() ?: 0
    }
    return 0;
  }
  
  public List getChilds(CheckNode parent) {
    List childs = []
    if (parent.getChildCount() == 0) {
      parent.userObject.eachDir { file ->
        if (file.getName() != ".svn") {
          def child = createCheckNode(file)
          childs.add(child)
          parent.add(child)
        }
      }
    } else {
      parent.children().each { childs.add(it) }
    }
    return childs
  }
  
  public boolean isLeaf(Object node) {
    // Wir stellen nur Verzeichnisse dar!
    return false;
  }
  
  public int getIndexOfChild(Object parent, Object child) {
    String[] children = parent.userObject.list();
    for (int i = 0; i < children.length; i++) {
      if (child.userObject.getName().equals(children[i])) {
        return i;
      }
    }
    return -1;
  }
  
  public void valueForPathChanged(TreePath path, Object value) {
    throw new IllegalAccessException()
  }
  
  private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
    TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
    listeners.each {
      it.treeNodesChanged(event);
    }
  }
  
  public void addTreeModelListener(TreeModelListener listener) {
    listeners.add(listener);
  }
  
  public void removeTreeModelListener(TreeModelListener listener) {
    listeners.remove(listener);
  }
  
  private CheckNode createCheckNode(File file) {
    return new CheckNode(file) {
      public String toString() {
        return userObject.getName()
      }
    }
  }
}
