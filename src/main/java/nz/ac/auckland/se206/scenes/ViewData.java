package nz.ac.auckland.se206.scenes;

import javafx.scene.Parent;

public class ViewData {
  private Parent uiRoot;
  private Object controller;

  public ViewData(Parent uiRoot, Object controller) {
    this.uiRoot = uiRoot;
    this.controller = controller;
  }

  public Parent getUiRoot() {
    return uiRoot;
  }

  public Object getController() {
    return controller;
  }
}
