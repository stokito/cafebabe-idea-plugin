package org.google.code.cafebabe;

import com.intellij.openapi.project.Project;
import org.sf.cafebabe.gadget.classtree.PlainClassTree;
import org.sf.cafebabe.task.classfile.ClassTreeActions;
import org.sf.cafebabe.util.IconProducer;
import org.sf.classfile.ClassFile;
import org.sf.classfile.MethodEntry;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is main panel for displaying classfile in the form of tree.
 *
 * @author Alexander Shvets
 * @version 1.0 11/24/2007
 */
public class IdeaClassTree extends PlainClassTree {
  private ClassTreeActions actions;

  private BodyEditorToolWindow bodyEditor;

  private JPanel parent;
  private String name;

  public IdeaClassTree(Project project, JPanel parent, String name) {
    super();

    this.parent = parent;
    this.name = name;

    bodyEditor = new BodyEditorToolWindow(project, parent);

    actions = new ClassTreeActions(this) {
      protected void init() {
        ImageIcon collapseIcon = IconProducer.getImageIcon(ICONS_DIR + "tree1-16.png");
        ImageIcon expandIcon = IconProducer.getImageIcon(ICONS_DIR + "tree2-16.png");
        ImageIcon expandAllIcon = IconProducer.getImageIcon(ICONS_DIR + "tree3-16.png");
        //ImageIcon searchIcon = IconProducer.getImageIcon(ICONS_DIR + "eye-16.png");
        //ImageIcon integrityIcon = IconProducer.getImageIcon(ICONS_DIR + "integrity-16.png");
        ImageIcon tutorialOnIcon = IconProducer.getImageIcon(ICONS_DIR + "quest2-16.png");
        ImageIcon tutorialOffIcon = IconProducer.getImageIcon(ICONS_DIR + "quest1-16.png");

        collapceAction = createCollapceAction(COLLAPSE_TREE_STRING, collapseIcon);
        expandFirstLevelAction = createExpandFirstLevelAction(EXPAND_1ST_LEVEL_STRING, expandIcon);
        expandAllLevelsAction = createExpandAllLevelsAction(EXPAND_ALL_LEVELS_STRING, expandAllIcon);
        //searchAction           = createSearchAction(SEARCH_STRING, searchIcon);
        //integrityAction        = createIntegrityAction(INTEGRITY_STRING, integrityIcon);

        AbstractButton tutoButton = new JToggleButton();

        tutoButton.setSelectedIcon(tutorialOnIcon);

        tutorialAction = createTutorialAction(TUTORIAL_MODE_STRING, tutorialOffIcon, tutoButton);

        addActionToToolBar(collapceAction, toolBar, false, COLLAPSE_TREE_STRING, 0);
        addActionToToolBar(expandFirstLevelAction, toolBar, false,
            EXPAND_1ST_LEVEL_STRING, 0);
        addActionToToolBar(expandAllLevelsAction, toolBar, false,
            EXPAND_ALL_LEVELS_STRING, 0);
        //addActionToToolBar(searchAction, toolBar, false, SEARCH_STRING, 0);
        //addActionToToolBar(integrityAction, toolBar, false, INTEGRITY_STRING, 0);

        JPanel emptyArea = new JPanel();
        toolBar.add(emptyArea);

        addActionToToolBar(tutorialAction, toolBar, false, TUTORIAL_MODE_STRING, 0);
      }
    };
  }

  public void openMethodBodyEditor(MethodEntry methodEntry, int pos) {
    bodyEditor.open(parent, classFile, methodEntry, pos);
  }

  /**
   * Opens class file in internal window.
   *
   * @param is input stream
   * @throws IOException I/O exception
   */
  public void open(InputStream is) throws IOException {
    try {
      ClassFile classFile = new ClassFile();
      classFile.read(new DataInputStream(is));

      setClassFile(classFile);

      parent.removeAll();

      JPanel topPanel = new JPanel();
      topPanel.setLayout(new BorderLayout());
      parent.add(topPanel);

      topPanel.add(actions.getToolBar(), BorderLayout.NORTH);

      // Create a new class tree
      JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
          JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      topPanel.add(scrollPane, BorderLayout.CENTER);

      scrollPane.getViewport().add(this);

      topPanel.add(getHexEditor(), BorderLayout.SOUTH);

      //setTitle(dataSource.getName());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      is.close();
    }
  }

  /**
   * Saves the content of the editor as external file
   *
   * @param dataSource the data source
   * @throws Exception exception
   */
  public void saveAs(DataSource dataSource) throws Exception {
    File file;

    if (dataSource instanceof FileDataSource) {
      FileDataSource fileDataSource = (FileDataSource) dataSource;

      file = new File(fileDataSource.getFile().getPath());
    } else {
      file = new File(dataSource.getName());
    }

    saveAsClassFile(file);

    //setTitle(dataSource.getName());
  }

  public BodyEditorToolWindow getBodyEditor() {
    return bodyEditor;
  }

  public String getName() {
    return name;
  }

}
