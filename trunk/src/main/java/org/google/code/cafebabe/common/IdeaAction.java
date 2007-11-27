package org.google.code.cafebabe.common;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

/**
 * This is base actopn class for the Tool Window.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public abstract class IdeaAction extends AnAction {
  protected IdeaHelper helper = IdeaHelper.getInstance();

  protected void update(AnActionEvent event, String toolWindowId) {
    Presentation presentation = event.getPresentation();

    Project project = helper.getProject(event);

    if (project != null) {
      ToolWindow toolWindow = helper.getToolWindow(project, toolWindowId);

      if (toolWindow != null) { // tool window isn't registered
        if (checkAdditionally(event)) {
          presentation.setEnabled(toolWindow.isAvailable());
          presentation.setVisible(true);
        }
        else {
          presentation.setEnabled(false);
        }

        return;
      }
    }

    presentation.setEnabled(false);
    //presentation.setVisible(false);
  }

  protected boolean checkAdditionally(AnActionEvent event) {
    return true;
  }

  protected void actionPerformed(AnActionEvent event, String toolWindowId, Runnable runnable) {
    Project project = helper.getProject(event);

    if (project != null) {
      ToolWindow toolWindow = helper.getToolWindow(project, toolWindowId);

      if (toolWindow != null) {
        toolWindow.activate(runnable);
      }
    }
  }

}
