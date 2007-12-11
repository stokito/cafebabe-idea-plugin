package org.google.code.cafebabe.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.google.code.cafebabe.ClassFileViewerToolWindow;
import org.google.code.idea.common.IdeaAction;
import org.google.code.idea.common.ToolWindowComponent;

/**
 * This action displays initial layout of CafeBabe Bytecode Editor.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class DisplayCafeBabeAction extends IdeaAction {

 /**
   * @param event event
   */
  public void update(AnActionEvent event) {
    update(event, ClassFileViewerToolWindow.TOOL_WINDOW_ID);
  }

  /**
   * @param event event
   */
  public void actionPerformed(final AnActionEvent event) {
    final Project project = helper.getProject(event);

    Runnable runnable = new Runnable() {
      public void run() {
        ToolWindowComponent toolWindow = project.getComponent(ClassFileViewerToolWindow.class);

        toolWindow.setConsoleVisible(true);
      }
    };

    actionPerformed(event, ClassFileViewerToolWindow.TOOL_WINDOW_ID, runnable);
  }

}
