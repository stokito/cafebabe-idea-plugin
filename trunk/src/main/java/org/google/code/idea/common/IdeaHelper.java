// IdeaHelper.java

package org.google.code.idea.common;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.peer.PeerFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.content.Content;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains useful routines for working with IDEA API.
 *
 * @author Alexander Shvets
 * @version 1.0 11/30/2003
 */
public class IdeaHelper {
  private static IdeaHelper ourInstance = new IdeaHelper();

  public static IdeaHelper getInstance() {
    return ourInstance;
  }

  public IdeaHelper() {
  }

  /**
   * Gets the project.
   *
   * @param event the event
   * @return the project
   */
  public Project getProject(AnActionEvent event) {
    DataContext dataContext = event.getDataContext();

    return DataKeys.PROJECT.getData(dataContext);
  }

  /**
   * Gets the tool window.
   *
   * @param project      the project
   * @param toolWindowId ID for tool window
   * @return the tool window
   */
  public ToolWindow getToolWindow(Project project, String toolWindowId) {
    ToolWindowManager toolWindowManager =
        ToolWindowManager.getInstance(project);

    return toolWindowManager.getToolWindow(toolWindowId);
  }

  /**
   * Creates the tool window.
   *
   * @param project      the project
   * @param contentPanel   content panel
   * @param toolWindowId ID for tool window
   * @param position the position
   * @return the tool window
   */
  public ToolWindow createsToolWindow(Project project, JPanel contentPanel, String toolWindowId,
                                      ToolWindowAnchor position) {
    return createToolWindow( project, contentPanel, toolWindowId, position, null);
  }

  /**
   * Creates the tool window.
   *
   * @param project      the project
   * @param contentPanel   content panel
   * @param toolWindowId ID for tool window
   * @param position the position
   * @param icon the icon
   * @return the tool window
   */
  public ToolWindow createToolWindow(Project project, JPanel contentPanel, String toolWindowId,
                                      ToolWindowAnchor position, Icon icon) {
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

    ToolWindow toolWindow =
        toolWindowManager.registerToolWindow(toolWindowId, true, position);

    PeerFactory peerFactory = PeerFactory.getInstance();

    Content content = peerFactory.getContentFactory().createContent(contentPanel, toolWindowId, false);

    toolWindow.getContentManager().addContent(content);

    if(icon != null) {
      toolWindow.setIcon(icon);
    }

    return toolWindow;
  }

  /**
   * Gets the icon.
   *
   * @param name  the resource name
   * @param clazz the class name
   * @return the icon
   */
  public ImageIcon getIcon(String name, Class clazz) {
    URL url = clazz.getResource(name);

    return new ImageIcon(url);
  }

  /**
   * Gets the class name inside the project.
   *
   * @param fileName the file name
   * @param project  the project
   * @return the class name
   */
  public String getClassName(String fileName, Project project) {
    PsiManager psiManager = PsiManager.getInstance(project);
    LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

    String vfsPathName = fileName.replace(File.separatorChar, '/');

    VirtualFile virtualFile = localFileSystem.findFileByPath(vfsPathName);

    if (virtualFile == null) {
      return fileName;
    }

    PsiDirectory psiDir = psiManager.findDirectory(virtualFile);

    if (psiDir == null) {
      return fileName;
    }

    PsiPackage psiPackage = psiDir.getPackage();

    if (psiPackage == null) {
      return fileName;
    }

    return psiPackage.getQualifiedName() + "." +
        virtualFile.getNameWithoutExtension();
  }

  public List<VirtualFile> getVirtualFiles(DataContext dataContext) {
    return new ArrayList<VirtualFile>(Arrays.asList(DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext)));
  }

  public Project getProject(DataContext dataContext) {
    return DataKeys.PROJECT.getData(dataContext);
  }

  public Editor getEditor(DataContext dataContext) {
    return DataKeys.EDITOR.getData(dataContext);
  }

  public String getFileExtension(DataContext dataContext) {
    VirtualFile file = DataKeys.VIRTUAL_FILE.getData(dataContext);

    return file != null ? file.getExtension() : null;
  }

  public VirtualFile getVirtualFile(DataContext dataContext) {
    return  DataKeys.VIRTUAL_FILE.getData(dataContext);
  }

  public File getFile(DataContext dataContext) {
    VirtualFile virtualFile = getVirtualFile(dataContext);

    if(virtualFile != null) {
      return new File(virtualFile.getPresentableUrl());
    }

    return null;
  }

}
