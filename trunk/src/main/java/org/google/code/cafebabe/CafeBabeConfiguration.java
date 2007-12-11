package org.google.code.cafebabe;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents configuration component for Maven Archetype Tool Window.
 *
 * @author Alexander Shvets
 * @version 1.0 12/07/2007
 */
@State(
    name = CafeBabeConfiguration.COMPONENT_NAME,
    storages = {@Storage(id = "cafebabe", file = "$PROJECT_FILE$")}
)
public final class CafeBabeConfiguration
    implements ProjectComponent, PersistentStateComponent<CafeBabeConfiguration> {

  public final static String COMPONENT_NAME = "CafeBabe.Configuration";

  // properties to persist

  private String lastSelectedFile;
  private String lastSelectedEntry;

  // implements ProjectComponent

  public void projectOpened() {
  }

  public void projectClosed() {
  }

  @NotNull
  public String getComponentName() {
    return COMPONENT_NAME;
  }

  public void initComponent() {
  }

  public void disposeComponent() {
  }

  // implements PersistentStateComponent<CafeBabeConfiguration>

  public CafeBabeConfiguration getState() {
    return this;
  }

  public void loadState(CafeBabeConfiguration state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  // getters/setters

  public String getLastSelectedFile() {
    return lastSelectedFile;
  }

  public void setLastSelectedFile(String lastSelectedFile) {
    this.lastSelectedFile = lastSelectedFile;
  }

  public String getLastSelectedEntry() {
    return lastSelectedEntry;
  }

  public void setLastSelectedEntry(String lastSelectedEntry) {
    this.lastSelectedEntry = lastSelectedEntry;
  }

}
