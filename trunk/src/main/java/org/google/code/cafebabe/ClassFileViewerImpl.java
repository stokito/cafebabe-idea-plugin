package org.google.code.cafebabe;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.openapi.wm.ex.ToolWindowManagerAdapter;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import org.google.code.cafebabe.common.ToolWindowComponent;
import org.jetbrains.annotations.NotNull;
import org.sf.cafebabe.util.IconProducer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * The tool window for CafeBabe BytecodeEditor plugin.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class ClassFileViewerImpl extends ToolWindowComponent implements ProjectComponent {
  private static final Logger logger = Logger.getInstance(ClassFileViewerImpl.class.getName());

  public static final String COMPONENT_NAME = "cafeBabe.ToolWindow.Component";
  public static final String TOOL_WINDOW_ID = "CafeBabe";
  public static final String ACTION_GROUP_ID = "cafebabe.ToolWindow";
  public static final String CAFEBABE_VERTICAL_ICON = "/Icons/cafebabe-16-vert.png";
  public static final String CAFEBABE_HORIZONTAL_ICON = "/Icons/cafebabe-16.png";

  private ClassTreeComponent classTree;

  public ClassFileViewerImpl(Project project) {
    super(project, TOOL_WINDOW_ID);

    IconProducer.setClass(getClass());
  }

  /**
   * Method is called after plugin is already created and configured. Plugin can start to communicate with
   * other plugins only in this method.
   */
  public void initComponent() {
    init();
  }

  /**
   * This method is called on plugin disposal.
   */
  public void disposeComponent() {
    release();
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
    initMainPanel();

    initContentPanel();

    openConsole();

    setRegistered(true);
  }

  /**
   * Invoked when project is closed.
   */
  public void projectClosed() {
    disposeConsole();

    setRegistered(false);
  }

  protected ToolWindow createToolWindow() {
    Icon icon = IconLoader.getIcon(CAFEBABE_VERTICAL_ICON, ClassFileViewerImpl.class);

    ToolWindow toolWindow = ideaHelper.createsToolWindow(getProject(), getMainPanel(), TOOL_WINDOW_ID, ToolWindowAnchor.LEFT, icon);

    toolWindow.setType(ToolWindowType.DOCKED, null);

    return toolWindow;
  }

  protected void customizeToolWindow(final ToolWindow toolWindow) {
    ToolWindowManagerEx toolWindowManager = ToolWindowManagerEx.getInstanceEx(getProject());
    toolWindowManager.addToolWindowManagerListener(new ToolWindowManagerAdapter() {

      public void stateChanged() {
        if (toolWindow.isAvailable()) {
          try {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            Icon icon;

            if (anchor == ToolWindowAnchor.TOP || anchor == ToolWindowAnchor.BOTTOM) {
              icon = IconLoader.getIcon(CAFEBABE_HORIZONTAL_ICON, ClassFileViewerImpl.class);
            } else {
              icon = IconLoader.getIcon(CAFEBABE_VERTICAL_ICON, ClassFileViewerImpl.class);
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
    return  classTree != null;
  }

  public void openFile(final VirtualFile virtualFile) {
    JFrame mainFrame = (JFrame) getMainPanel().getTopLevelAncestor();
    Component glassPane = mainFrame.getGlassPane();
    glassPane.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
      }
    });

    glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));

    glassPane.setVisible(true);

    try {
      classTree = new ClassTreeComponent(getProject(), getContentPanel());

      classTree.open(virtualFile);

      ToolWindow toolWindow = getToolWindow();
      toolWindow.setTitle(virtualFile.getPresentableName());
    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
      glassPane.setVisible(false);
    }
  }

  public void closeFile() {
    if(classTree != null) {
      initContentPanel();

      BodyEditorPanel bodyEditor = classTree.getBodyEditor();

      bodyEditor.close();

      classTree = null;

      ToolWindow toolWindow = getToolWindow();
      toolWindow.setTitle("");
    }
  }

  public void saveFile() {
    Messages.showMessageDialog(getProject(), "Not Implemented Yet", "Warning", null);
  }

  public void openClassHound() {
    //ClassHoundFrame frame = new ClassHoundFrame();
    //frame.setVisible(true);

    // classHoundFrame = new ClassHoundFrame(mainFrame, mainFrame.getMDIDesktopPane(), PROGRAM_LAYER);

    // classHoundFrame.addInternalFrameListener(this);

    Messages.showMessageDialog(getProject(),  "Not Implemented Yet", "Warning", null);
  }

  public void displayCafeBabe() {
    openConsole();
  }
  
}
