package org.google.code.cafebabe.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import org.google.code.cafebabe.ClassFileViewerImpl;
import org.google.code.cafebabe.common.IdeaAction;
import org.sf.cafebabe.util.FileUtil;

import java.awt.*;

/**
 * This action opens up class file in CafeBabe plugin toolwindow.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class OpenFileAction extends IdeaAction {

  public void update(AnActionEvent event) {
    update(event, ClassFileViewerImpl.TOOL_WINDOW_ID);
  }

  public void actionPerformed(final AnActionEvent event) {
    final Project project = helper.getProject(event);

   Runnable runnable = new Runnable() {
      public void run() {
        if(project != null) {
          //FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
          FileChooserDescriptor descriptor = FileChooserDescriptorFactory.getFileChooserDescriptor("class file");
          VirtualFile[] chooseFiles = FileChooser.chooseFiles(project, descriptor);

          if(chooseFiles.length > 0) {
            VirtualFile virtualFile = chooseFiles[0];
       
             if (virtualFile != null) {
               if ("class".equals(FileUtil.getExtension(virtualFile.getPresentableUrl()))) {
                 Window window = WindowManager.getInstance().suggestParentWindow(project);

                 Cursor cursor = window.getCursor();

                 window.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                 ClassFileViewerImpl viewer = project.getComponent(ClassFileViewerImpl.class);

                 viewer.openFile(virtualFile);

                 window.setCursor(cursor);
               }
             }

          }
        }
      }
    };

    actionPerformed(event, ClassFileViewerImpl.TOOL_WINDOW_ID, runnable);
  }

}