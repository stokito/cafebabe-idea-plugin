// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(1) braces fieldsfirst nonlb 
// Source File Name:   CollapseTreeAction.java

package org.google.code.cafebabe.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.google.code.cafebabe.ClassHoundToolWindow;
import org.google.code.idea.common.IdeaAction;
import org.google.code.idea.common.ToolWindowComponent;

import javax.swing.*;

/**
 * This is the action for closing classhound toolwindow.
 *
 * @author Alexander Shvets
 * @version 1.0 12/08/2007
 */
public class CloseClassHoundAction extends IdeaAction {

/*  private Project project;

  public CloseClassHoundAction(Project project) {
    this.project = project;

    Presentation presentation = getTemplatePresentation();

    Icon icon = IconLoader.getIcon("/actions/cancel.png");
    
    presentation.setIcon(icon);

    presentation.setText("Hide ClassHound");
    presentation.setDescription("Hide ClassHound");
  }
*/
  
  /**
   * @param event event
   */
  public void update(AnActionEvent event) {
    update(event, ClassHoundToolWindow.TOOL_WINDOW_ID);
  }

 /**
   * @param event event
   */
  public void actionPerformed(final AnActionEvent event) {
    Runnable runnable = new Runnable() {
      public void run() {
        Project project = helper.getProject(event);

        if(project != null) {
          ToolWindowComponent toolWindow = project.getComponent(ClassHoundToolWindow.class);

          toolWindow.closeConsole();
        }
      }
    };

    actionPerformed(event, ClassHoundToolWindow.TOOL_WINDOW_ID, runnable);
  }

}
