package org.google.code.cafebabe;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowType;
import org.google.code.idea.common.ToolWindowComponent;
import org.jetbrains.annotations.NotNull;
import org.sf.cafebabe.Constants;
import org.sf.cafebabe.MainChooser;
import org.sf.cafebabe.gadget.classtree.PlainClassTree;
import org.sf.cafebabe.task.classhound.ClassHoundPanel;
import org.sf.cafebabe.util.IconProducer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * The tool window for CafeBabe Bytecode Editor plugin.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class ClassFileViewerToolWindow extends ToolWindowComponent
    implements ProjectComponent {
  public static final String COMPONENT_NAME = "cafebabe.ClassFileViewerToolWindow.Component";
  public static final String TOOL_WINDOW_ID = "CafeBabe";
  public static final String ACTION_GROUP_ID = "cafebabe.ClassFileViewerToolWindow";
  public static final String CAFEBABE_VERTICAL_ICON = "/Icons/cafebabe-16-vert.png";
  public static final String CAFEBABE_HORIZONTAL_ICON = "/Icons/cafebabe-16.png";

  private MainChooser fileChooser = new MainChooser();

  private IdeaClassTree classTree;
  private ClassHoundToolWindow classHoundToolWindow;

  public ClassFileViewerToolWindow(Project project) {
    super(project, TOOL_WINDOW_ID);

    IconProducer.setClass(getClass());
  }

  /**
   * Method is called after plugin is already created and configured. Plugin can start to communicate with
   * other plugins only in this method.
   */
  public void initComponent() {
    create();
  }

  /**
   * This method is called on plugin disposal.
   */
  public void disposeComponent() {
    dispose();
  }

  protected JComponent createToolbar() {
    ActionManager actionManager = ActionManager.getInstance();

    ActionGroup actionGroup =
        (ActionGroup) actionManager.getAction(ACTION_GROUP_ID);

    ActionToolbar toolBar =
        actionManager.createActionToolbar(TOOL_WINDOW_ID, actionGroup, true);

    return toolBar.getComponent();
  }

  /**
   * Invoked when project is opened.
   */
  public void projectOpened() {
    createConsole();

    //setConsoleVisible(true);
  }

  /**
   * Invoked when project is closed.
   */
  public void projectClosed() {
    closeConsole();
  }

  protected void customizeToolWindow(final ToolWindow toolWindow) {
    toolWindow.setType(ToolWindowType.DOCKED, null);

    toolWindow.setIcon(IconLoader.getIcon(ClassFileViewerToolWindow.CAFEBABE_HORIZONTAL_ICON));

    /*   Icon icon = IconLoader.getIcon(CAFEBABE_VERTICAL_ICON, ClassFileViewerToolWindow.class);
    toolWindow.setIcon(icon);

    ToolWindowManagerEx toolWindowManager = ToolWindowManagerEx.getInstanceEx(getProject());
    toolWindowManager.addToolWindowManagerListener(new ToolWindowManagerAdapter() {

      public void stateChanged() {
        if (toolWindow.isAvailable()) {
          try {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            Icon icon;

            if (anchor == ToolWindowAnchor.TOP || anchor == ToolWindowAnchor.BOTTOM) {
              icon = IconLoader.getIcon(CAFEBABE_HORIZONTAL_ICON, ClassFileViewerToolWindow.class);
            } else {
              icon = IconLoader.getIcon(CAFEBABE_VERTICAL_ICON, ClassFileViewerToolWindow.class);
            }

            toolWindow.setIcon(icon);
          }
          catch (IllegalStateException e) {
            //noinspection UnnecessarySemicolon
            ;
          }
        }
      }
    });
    */

  }

  /**
   * Returns the name of component
   *
   * @return String representing component name. Use plugin_name.component_name notation.
   */
  @NotNull
  public String getComponentName() {
    return COMPONENT_NAME;
  }

  public boolean classTreeIsLoaded() {
    return classTree != null;
  }

  public PlainClassTree getClassTree() {
    return classTree;
  }

  public void openFile(InputStream is, String name) {
    JFrame mainFrame = (JFrame) getMainPanel().getTopLevelAncestor();
    Component glassPane = mainFrame.getGlassPane();
    glassPane.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
      }
    });

    glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));

    glassPane.setVisible(true);

    try {
      classTree = new IdeaClassTree(getProject(), getContentPanel(), name);

      classTree.open(is);

      ToolWindow toolWindow = getToolWindow();
      toolWindow.setTitle(name);
    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
      glassPane.setVisible(false);
    }
  }

  public void closeFile() {
    if (classTree != null) {
      if (classTree.isAnyChange()) {
        int value = JOptionPane.showConfirmDialog(classTree,
            Constants.SAVE_FILE_QUESTION + " " +
                classTree.getName() + "?",
            Constants.CONFIRMATION_STRING,
            JOptionPane.YES_NO_CANCEL_OPTION);

        if (value == JOptionPane.NO_OPTION) {
          closeBodyEditor();
        }
        if (value == JOptionPane.YES_OPTION) {
          try {
            saveFile();
            closeBodyEditor();
          }
          catch (Exception e) {
            ;
          }
        }
      } else {
        closeBodyEditor();
      }
    }
  }

  private void closeBodyEditor() {
    initContentPanel();

    BodyEditorToolWindow bodyEditor = classTree.getBodyEditor();

    bodyEditor.closeConsole();

    classTree = null;

    ToolWindow toolWindow = getToolWindow();
    toolWindow.setTitle("");
    //To change body of created methods use File | Settings | File Templates.
  }

  public void saveFile() {
    JFrame mainFrame = (JFrame) getMainPanel().getTopLevelAncestor();

      int returnVal = fileChooser.showDialog(mainFrame, "Save file as");

      if(returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      File file = fileChooser.getSelectedFile();

      if(file != null) {
        try {
          classTree.saveAsClassFile(file);
        }
        catch(Exception e) {
          ;
        }
      }

     // setTitle(dataSource.getName());
  }

  public void openClassHound() {
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(getProject());

    ToolWindow toolWindow = toolWindowManager.getToolWindow(ClassHoundToolWindow.TOOL_WINDOW_ID);

    if (toolWindow == null) {
      classHoundToolWindow = new ClassHoundToolWindow(getProject());

      classHoundToolWindow.createConsole();
      ((ClassHoundPanel) classHoundToolWindow.getContentPanel()).startThread();
    }

    classHoundToolWindow.setConsoleVisible(true);
  }

}
