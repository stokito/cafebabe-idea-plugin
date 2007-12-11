package org.google.code.idea.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * This class represents common behavior of tool window component.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public abstract class ToolWindowComponent {
  public static final Runnable EMPTY_RUNNABLE = new Runnable() {
    public void run() {
    }
  };

  protected IdeaHelper ideaHelper = new IdeaHelper();

  private boolean isRegistered = false;

  protected JPanel mainPanel;
  protected JPanel contentPanel;

  private Project project;
  private String toolWindowId;

  public ToolWindowComponent(Project project, String toolWindowId) {
    this.project = project;
    this.toolWindowId = toolWindowId;
  }

  protected void create() {
    createMainPanel();

    createContentPanel();

    mainPanel.add(contentPanel, BorderLayout.CENTER);

    ToolTipManager.sharedInstance().registerComponent(mainPanel);
  }

  protected void createMainPanel() {
    mainPanel = new JPanel(new BorderLayout());
  }

  protected void createContentPanel() {
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
  }

  protected void dispose() {
    if (mainPanel != null) {
      ToolTipManager.sharedInstance().unregisterComponent(mainPanel);
    }

    contentPanel = null;
    mainPanel = null;
  }

  protected void initMainPanel() {
    mainPanel.add(createToolbar(), BorderLayout.NORTH);
  }

  protected void initContentPanel() {
    contentPanel.removeAll();

    JScrollPane scrollPane = new JScrollPane();

    contentPanel.add(scrollPane, BorderLayout.CENTER);

    contentPanel.repaint();
  }

  public void createConsole() {
    createToolWindow();

    initMainPanel();

    initContentPanel();

    setRegistered(true);
  }

  public void closeConsole() {
    setConsoleVisible(false);

    dispose();

    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

    try {
      toolWindowManager.unregisterToolWindow(toolWindowId);
    }
    catch (IllegalArgumentException e) {
      // ignore - this can occur due to lazy initialization
    }

    setRegistered(false);
  }

  public void setConsoleVisible(boolean isVisible) {
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

    ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowId);

    if (toolWindow != null && isRegistered) {
      if (isVisible) {
        toolWindow.show(EMPTY_RUNNABLE);
      } else if (toolWindow.isVisible()) {
        toolWindow.hide(EMPTY_RUNNABLE);
      }
    }
  }

  protected ToolWindow createToolWindow() {
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

    ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowId);

    if (toolWindow == null) {
      toolWindow = ideaHelper.createToolWindow(project, mainPanel, toolWindowId, ToolWindowAnchor.LEFT, null);

      customizeToolWindow(toolWindow);
    }

    return toolWindow;
  }

  protected void customizeToolWindow(ToolWindow toolWindow) {
  }

  protected abstract JComponent createToolbar();

  public ToolWindow getToolWindow() {
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

    return toolWindowManager.getToolWindow(toolWindowId);
  }

  public boolean isRegistered() {
    return isRegistered;
  }

  public void setRegistered(boolean registered) {
    isRegistered = registered;
  }

  public Project getProject() {
    return project;
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public JPanel getContentPanel() {
    return contentPanel;
  }

}
