package org.google.code.cafebabe.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;

import org.google.code.idea.common.IdeaAction;
import org.sf.cafebabe.util.IconProducer;
import org.sf.cafebabe.Constants;

/**
 * This action displays information about the plugin.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class AboutAction extends IdeaAction {

  public void actionPerformed(AnActionEvent event) {
    Project project = helper.getProject(event);

    StringBuffer message = new StringBuffer();

    message.append(Constants.MAIN_FRAME_TITLE).append('\n');
    message.append("Author: " + Constants.AUTHOR).append('\n');
    message.append("e-mail: <" + Constants.EMAIL + ">").append('\n');

    StringBuffer title = new StringBuffer();
    title.append("About ").append("Cafe Babe").append(" plugin...");

    final ImageIcon icon = IconProducer.getImageIcon(Constants.ICON_FACE1);    

    Messages.showMessageDialog(project, message.toString(), "About CafeBabe", icon);
  }
}