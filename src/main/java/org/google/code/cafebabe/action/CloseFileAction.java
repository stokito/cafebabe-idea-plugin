package org.google.code.cafebabe.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.google.code.cafebabe.ClassFileViewerImpl;
import org.google.code.cafebabe.common.IdeaAction;

/**
 * This action closes up the class file.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class CloseFileAction extends IdeaAction {

  /**
   * @param event event
   */
  public void update(AnActionEvent event) {
    update(event, ClassFileViewerImpl.TOOL_WINDOW_ID);
  }

  protected boolean checkAdditionally(AnActionEvent event) {
    Project project = helper.getProject(event);

    ClassFileViewerImpl viewer = project.getComponent(ClassFileViewerImpl.class);

    return viewer.classTreeIsLoaded();
  }

  /**
   * @param event event
   */
  public void actionPerformed(final AnActionEvent event) {
    Runnable runnable = new Runnable() {
      public void run() {
        Project project = helper.getProject(event);

        ClassFileViewerImpl viewer = project.getComponent(ClassFileViewerImpl.class);

        viewer.closeFile();
      }
    };

    actionPerformed(event, ClassFileViewerImpl.TOOL_WINDOW_ID, runnable);
  }

}
