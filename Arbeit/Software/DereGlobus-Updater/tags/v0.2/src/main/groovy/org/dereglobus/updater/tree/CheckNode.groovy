package org.dereglobus.updater.tree


import javax.swing.tree.DefaultMutableTreeNode 

class CheckNode extends DefaultMutableTreeNode {
  
  protected boolean isSelected;
  
  public CheckNode() {
    this(null);
  }
  
  public CheckNode(Object userObject) {
    this(userObject, true, false);
  }
  
  public CheckNode(Object userObject, boolean allowsChildren,
  boolean isSelected) {
    super(userObject, allowsChildren);
    this.isSelected = isSelected;
  }
  
  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }
  
  public boolean isSelected() {
    return isSelected;
  }
  
  // If you want to change "isSelected" by CellEditor,
  /*
   public void setUserObject(Object obj) { if (obj instanceof Boolean) {
   * setSelected(((Boolean)obj).booleanValue()); } else {
   * super.setUserObject(obj); } }
   */
  
}
