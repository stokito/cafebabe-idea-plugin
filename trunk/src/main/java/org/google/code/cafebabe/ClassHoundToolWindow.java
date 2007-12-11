package org.google.code.cafebabe;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowType;
import org.gjt.jclasslib.browser.config.classpath.ClasspathArchiveEntry;
import org.gjt.jclasslib.browser.config.classpath.ClasspathBrowser;
import org.google.code.idea.common.ToolWindowComponent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.sf.cafebabe.MainChooser;
import org.sf.cafebabe.activation.ArchivedDataSource;
import org.sf.cafebabe.gadget.classtree.PlainClassTree;
import org.sf.cafebabe.task.classhound.CafeBabeParent;
import org.sf.cafebabe.task.classhound.ClassHoundPanel;
import org.sf.cafebabe.util.FileUtil;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * This is the toolwindow that holds ClassHound.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class ClassHoundToolWindow extends ToolWindowComponent implements ProjectComponent, CafeBabeParent {
  public static final String ACTION_GROUP_ID = "cafebabe.ClassHoundToolWindow";
  public static final String TOOL_WINDOW_ID = "ClassHound";
  public static final String COMPONENT_NAME = "cafebabe.ClassHoundToolWindow.Component";

  private MainChooser fileChooser;

  public ClassHoundToolWindow(Project project) {
    super(project, TOOL_WINDOW_ID);

    this.fileChooser = new MainChooser() {
      public File open(String title) {
        CafeBabeConfiguration configuration = getProject().getComponent(CafeBabeConfiguration.class);

        String lastSelectedEntry = configuration.getLastSelectedEntry();

        if (lastSelectedEntry != null && new File(lastSelectedEntry).exists()) {
          fileChooser.setSelectedFile(new File(lastSelectedEntry));
        }

        return super.open(title);
      }
    };

    create();
  }

  protected void customizeToolWindow(ToolWindow toolWindow) {
    toolWindow.setType(ToolWindowType.DOCKED, null);

    toolWindow.setIcon(IconLoader.getIcon(ClassFileViewerToolWindow.CAFEBABE_HORIZONTAL_ICON));

    toolWindow.setAnchor(ToolWindowAnchor.BOTTOM, EMPTY_RUNNABLE);
  }

  protected JComponent createToolbar() {
    ActionManager actionManager = ActionManager.getInstance();

    ActionGroup actionGroup =
        (ActionGroup) actionManager.getAction(ACTION_GROUP_ID);

    ActionToolbar toolBar =
        actionManager.createActionToolbar(TOOL_WINDOW_ID, actionGroup, true);

    return toolBar.getComponent();
  }

  protected void createContentPanel() {
    contentPanel = new ClassHoundPanel(this);
  }

  protected void initContentPanel() {
  }

  public void projectOpened() {
  }

  public void projectClosed() {
    CafeBabeConfiguration configuration = getProject().getComponent(CafeBabeConfiguration.class);

    File selectedFile = fileChooser.getSelectedFile();

    if (selectedFile != null) {
      configuration.setLastSelectedEntry(selectedFile.getPath());
    }
  }

  @NonNls
  @NotNull
  public String getComponentName() {
    return COMPONENT_NAME;
  }

  public void initComponent() {
    create();
  }

  public void disposeComponent() {
    dispose();
  }

  public void open(File file) {
    ClassFileViewerToolWindow classFileViewer = getProject().getComponent(ClassFileViewerToolWindow.class);

    try {
      DataSource dataSource = getDataSource(file);

      if (dataSource != null) {
        classFileViewer.openFile(dataSource.getInputStream(), dataSource.getName());
      } else {
        classFileViewer.openFile(new FileInputStream(file), file.getPath());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private DataSource getDataSource(File file) throws IOException {
    DataSource dataSource = null;

    if (file != null && !file.isDirectory()) {
      String extension = FileUtil.getExtension(file).toLowerCase();

      if (extension != null) {
        if (extension.equals("zip") || extension.equals("jar")) {
          ClasspathArchiveEntry entry = new ClasspathArchiveEntry();

          entry.setFileName(file.getPath());

          JFrame mainFrame = (JFrame) getMainPanel().getTopLevelAncestor();

          ClasspathBrowser jarBrowser =
              new ClasspathBrowser(mainFrame, null, "Classes in selected JAR file:", false);

          jarBrowser.clear();
          jarBrowser.setClasspathComponent(entry);
          jarBrowser.setVisible(true);

          String selectedClassName = jarBrowser.getSelectedClassName();

          if (selectedClassName != null) {
            dataSource = new ArchivedDataSource(new ZipFile(file.getPath()), selectedClassName + ".class");
          }
        } else {
          dataSource = new FileDataSource(file);
        }
      }
    }

    return dataSource;
  }

  public MainChooser getFileChooser() {
    return fileChooser;
  }

  public void open(DataSource dataSource) {
    ClassFileViewerToolWindow classFileViewer = getProject().getComponent(ClassFileViewerToolWindow.class);

    try {
      classFileViewer.openFile(dataSource.getInputStream(), dataSource.getName());
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void setFieldPosition(String selectedField) {
    ClassFileViewerToolWindow classFileViewer = getProject().getComponent(ClassFileViewerToolWindow.class);

    PlainClassTree classTree = classFileViewer.getClassTree();

    classTree.setFieldPosition(selectedField);
  }

  public void setMethodPosition(String selectedMethod) {
    ClassFileViewerToolWindow classFileViewer = getProject().getComponent(ClassFileViewerToolWindow.class);

    PlainClassTree classTree = classFileViewer.getClassTree();

    classTree.setMethodPosition(selectedMethod);
  }

}
