package org.google.code.cafebabe.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.google.code.cafebabe.ClassFileViewerToolWindow;
import org.google.code.idea.common.IdeaAction;

/**
 * This displays ClassHound toolwindow.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class OpenClassHoundAction extends IdeaAction {
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
   Runnable runnable = new Runnable() {
      public void run() {
        Project project = helper.getProject(event);

        ClassFileViewerToolWindow viewer = project.getComponent(ClassFileViewerToolWindow.class);

        viewer.openClassHound();
      }
    };

    actionPerformed(event, ClassFileViewerToolWindow.TOOL_WINDOW_ID, runnable);
  }

}