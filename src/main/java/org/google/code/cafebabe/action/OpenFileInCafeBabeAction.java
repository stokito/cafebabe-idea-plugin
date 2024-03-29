package org.google.code.cafebabe.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import org.google.code.cafebabe.ClassFileViewerToolWindow;
import org.google.code.idea.common.IdeaAction;
import org.google.code.idea.common.IdeaHelper;
import org.sf.jlaunchpad.util.FileUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * This action opens up class file from files tree in CafeBabe plugin toolwindow.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class OpenFileInCafeBabeAction extends IdeaAction {
  private final IdeaHelper helper = IdeaHelper.getInstance();

  public void update(AnActionEvent event) {
    update(event, ClassFileViewerToolWindow.TOOL_WINDOW_ID);
  }

  protected boolean checkAdditionally(AnActionEvent event) {
    File file = helper.getFile(event.getDataContext());

    return file != null && "class".equals(FileUtil.getExtension(file));
  }

  public void actionPerformed(final AnActionEvent event) {
    final Project project = helper.getProject(event);

    Runnable runnable = new Runnable() {
      public void run() {
        if (project != null) {
          VirtualFile virtualFile = helper.getVirtualFile(event.getDataContext());

          if (virtualFile != null) {
            if ("class".equals(FileUtil.getExtension(virtualFile.getPresentableUrl()))) {
              Window window = WindowManager.getInstance().suggestParentWindow(project);

              Cursor cursor = window.getCursor();

              window.setCursor(new Cursor(Cursor.WAIT_CURSOR));

              ClassFileViewerToolWindow viewer = project.getComponent(ClassFileViewerToolWindow.class);

              try {
                viewer.openFile(virtualFile.getInputStream(), virtualFile.getPresentableName());
              } catch (IOException e) {
                e.printStackTrace();
              }

              window.setCursor(cursor);
            }
          }
        }
      }
    };

    actionPerformed(event, ClassFileViewerToolWindow.TOOL_WINDOW_ID, runnable);
  }

}
