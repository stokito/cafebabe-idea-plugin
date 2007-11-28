package org.google.code.cafebabe.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindow;

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

  private ToolWindowManager toolWindowManager;

  private boolean isRegistered = false;

  private JPanel mainPanel;
  private JPanel contentPanel;

  private Project project;
  private String toolWindowId;

  public ToolWindowComponent(Project project, String toolWindowId) {
    this.project = project;
    this.toolWindowId = toolWindowId;

   toolWindowManager = ToolWindowManager.getInstance(project);
  }

 protected void init() {
    mainPanel = new JPanel(new BorderLayout());

    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(2, 2, 2, 2));

    mainPanel.add(contentPanel, BorderLayout.CENTER);

    ToolTipManager.sharedInstance().registerComponent(mainPanel);
  }

  protected void release() {
    if(mainPanel != null) {
      ToolTipManager.sharedInstance().unregisterComponent(mainPanel);

      mainPanel = null;
    }
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

 public ToolWindow openConsole() {
    ToolWindow toolWindow = null;

    if (toolWindowManager != null) {
      toolWindow = toolWindowManager.getToolWindow(toolWindowId);

      if (toolWindow == null) {
        toolWindow = createToolWindow();
      }

      customizeToolWindow(toolWindow);

      toolWindow.show(EMPTY_RUNNABLE);
    }

   return toolWindow;
 }

  protected abstract ToolWindow createToolWindow();  

  protected void customizeToolWindow(ToolWindow toolWindow) {}

  protected abstract JComponent createToolbar();

  public void disposeConsole() {
    if (toolWindowManager != null) {
      try {
        toolWindowManager.unregisterToolWindow(toolWindowId);
      }
      catch (IllegalArgumentException e) {
        // ignore - this can occur due to lazy initialization
      }
    }
  }

 /**
   * Hides the tool window.
   */
  public void hide() {
    ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowId);

    if (toolWindow != null) {
      if (isRegistered && toolWindow.isVisible()) {
        toolWindow.hide(EMPTY_RUNNABLE);
      }
    }
  }

  public ToolWindow getToolWindow() {
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
