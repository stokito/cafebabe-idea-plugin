package org.google.code.cafebabe.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import org.google.code.cafebabe.CafeBabeConfiguration;
import org.google.code.cafebabe.ClassFileViewerToolWindow;
import org.google.code.idea.common.IdeaAction;
import org.sf.cafebabe.util.FileUtil;

import java.awt.*;
import java.io.IOException;

/**
 * This action opens up class file in CafeBabe plugin toolwindow.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class OpenFileAction extends IdeaAction {

  public void update(AnActionEvent event) {
    update(event, ClassFileViewerToolWindow.TOOL_WINDOW_ID);
  }

  public void actionPerformed(final AnActionEvent event) {
    final Project project = helper.getProject(event);

    Runnable runnable = new Runnable() {
      public void run() {
        if (project != null) {
          //FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
          // FileChooserDescriptor descriptor = FileChooserDescriptorFactory.getFileChooserDescriptor("class file");

          FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, true, true, true, false) {
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
              boolean b = super.isFileVisible(file, showHiddenFiles);

              if (!file.isDirectory()) {
                b &= StdFileTypes.CLASS.equals(FileTypeManager.getInstance().getFileTypeByFile(file));
               // b &= file.getPath().endsWith(".jar");
              }

              return b;
            }
          };

          descriptor.setTitle("class file");

          CafeBabeConfiguration configuration = project.getComponent(CafeBabeConfiguration.class);
          LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

          String lastSelectedFile = configuration.getLastSelectedFile();
          //String vfsPathName = fileName.replace(File.separatorChar, '/');
          VirtualFile lastSelectedVirtualFile = null;

          if (lastSelectedFile != null) {
            lastSelectedVirtualFile = localFileSystem.findFileByPath(lastSelectedFile);
          }

          VirtualFile[] chooseFiles = FileChooser.chooseFiles(project, descriptor, lastSelectedVirtualFile);

          if (chooseFiles.length > 0) {
            VirtualFile virtualFile = chooseFiles[0];

            if (virtualFile != null) {
              if ("class".equals(FileUtil.getExtension(virtualFile.getPresentableUrl()))) {
                Window window = WindowManager.getInstance().suggestParentWindow(project);

                Cursor cursor = window.getCursor();

                window.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                ClassFileViewerToolWindow viewer = project.getComponent(ClassFileViewerToolWindow.class);

                configuration.setLastSelectedFile(virtualFile.getPath());

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
      }
    };

    actionPerformed(event, ClassFileViewerToolWindow.TOOL_WINDOW_ID, runnable);
  }

}
