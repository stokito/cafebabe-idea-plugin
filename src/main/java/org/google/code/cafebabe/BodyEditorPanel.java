// BodyEditorPanel.java

package org.google.code.cafebabe;

import com.intellij.openapi.help.HelpManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowType;
import org.google.code.cafebabe.common.ToolWindowComponent;
import org.sf.cafebabe.Constants;
import org.sf.cafebabe.gadget.bodyeditor.BodyEditorActions;
import org.sf.cafebabe.gadget.bodyeditor.BodyEditorAware;
import org.sf.cafebabe.gadget.bodyeditor.BodyTable;
import org.sf.cafebabe.util.IconProducer;
import org.sf.classfile.ClassFile;
import org.sf.classfile.MethodEntry;

import javax.swing.*;
import java.awt.*;

/**
 * This is the toolwindow that holds Body Editor.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class BodyEditorPanel extends ToolWindowComponent implements BodyEditorAware {
  public static final String TOOL_WINDOW_ID = "CafeBabe - Body Editor";

  private BodyTable bodyTable;

  private BodyEditorActions actions;

  private JPanel parent;

  public BodyEditorPanel(Project project, JPanel parent) {
    super(project, TOOL_WINDOW_ID);

    this.parent = parent;

    actions = new BodyEditorActions(this) {
      protected void init() {
        ImageIcon editIcon = IconProducer.getImageIcon(Constants.ICONS_DIR + "edit_instr-16.png");
        ImageIcon addIcon = IconProducer.getImageIcon(Constants.ICONS_DIR + "add_instr-16.png");
        ImageIcon removeIcon = IconProducer.getImageIcon(Constants.ICONS_DIR + "remove_instr-16.png");
        ImageIcon helpIcon = IconProducer.getImageIcon(Constants.ICONS_DIR + "quest1-16.png");

        editAction = createEditAction("Edit instruction", editIcon);
        addAction = createAddAction("Add instruction", addIcon);
        removeAction = createRemoveAction("Remove instructions", removeIcon);
        helpAction = createHelpAction("Help", helpIcon);

        addActionToToolBar(editAction, toolBar, false, "Edit instruction", 0);
        addActionToToolBar(addAction, toolBar, false, "Add instruction", 0);
        addActionToToolBar(removeAction, toolBar, false, "Remove instruction", 0);

        JPanel emptyArea = new JPanel();
        toolBar.add(emptyArea);

        addActionToToolBar(helpAction, toolBar, false, "Help", 0);
      }
    };
  }

  public void open(JPanel parent, final ClassFile classFile, final MethodEntry methodEntry, int position) {
    bodyTable = new BodyTable(parent, methodEntry, classFile) {
      public void showTopic() {
        help();
      }
    };

//    if(position < bodyTable.getColumnCount()) {
    bodyTable.setRowSelectionInterval(position, position);
//    }

    if (isRegistered()) {
      release();
      disposeConsole();
      setRegistered(false);
    }

    init();

    initMainPanel();

    initContentPanel();

    JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.getViewport().setView(bodyTable);
    getContentPanel().add(scrollPane, BorderLayout.CENTER);

    ToolWindow toolWindow = openConsole();

    String methodName = methodEntry.resolve(classFile.getConstPool());
    toolWindow.setTitle("Method: " + methodName);

    setRegistered(true);
  }

  public void editInstruction() {
    bodyTable.editInstruction();
  }

  public void addInstruction() {
    bodyTable.addInstruction(null);
  }

  public void removeInstructions() {
    bodyTable.removeInstructions();
  }

  public void help() {
    String mnemonic = bodyTable.getCurrentMnemonic();

    if (mnemonic == null) {
      JOptionPane.showMessageDialog(parent,
          "Please select an instruction to be removed.\n",
          "Warning", JOptionPane.WARNING_MESSAGE);
    } else {
      int index = mnemonic.indexOf('_');
      if (index != -1) {
        mnemonic = mnemonic.substring(0, index + 1);
      } else if (mnemonic.startsWith("dcmp")) {
        mnemonic = "dcmp";
      } else if (mnemonic.startsWith("fcmp")) {
        mnemonic = "fcmp";
      }

      HelpManager helpManager = HelpManager.getInstance();
      helpManager.invokeHelp(mnemonic);
    }
  }

  protected ToolWindow createToolWindow() {
    Icon icon = IconLoader.getIcon(ClassFileViewerImpl.CAFEBABE_VERTICAL_ICON, ClassFileViewerImpl.class);

    ToolWindow toolWindow = ideaHelper.createsToolWindow(getProject(), getMainPanel(),
        TOOL_WINDOW_ID, ToolWindowAnchor.LEFT, icon);

    toolWindow.setType(ToolWindowType.DOCKED, null);

    return toolWindow;
  }

  protected JComponent createToolbar() {
    return actions.getToolBar();
  }

  public void close() {
    release();

    disposeConsole();

    setRegistered(false);
  }

}
