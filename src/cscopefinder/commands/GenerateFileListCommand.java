package cscopefinder.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import cscopefinder.helpers.ConfigHelper;
import cscopefinder.helpers.GlobFilter;
import cscopefinder.helpers.ProjectHelper;
import cscopefinder.presenters.ProgressPresenter;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import cscopefinder.CscopeFinderPlugin;

public class GenerateFileListCommand implements Runnable {

    private Runnable runAfter;
    private View view;

    public GenerateFileListCommand(View view, Runnable runAfter) {
        this.runAfter = runAfter;
        this.view = view;
    }

    public void run() {
        if (!generateFileList()) {
            JOptionPane.showMessageDialog(view, "The project did not contain any files matching " +
                                "the specified filter.\n" +
                                "Please check your plugin options.");
            return;
        }

        if (runAfter != null)
            runAfter.run();
    }

    public boolean generateFileList() {
        ProgressPresenter presenter = new ProgressPresenter();
        VPTProject proj = ProjectHelper.findProject(view);

        if (proj == null)
            return false;

        String projPath = ProjectHelper.getProjectPath(proj);
        if (!ConfigHelper.createCscopeDbDir(projPath)) {
            Log.log(Log.ERROR, CscopeFinderPlugin.class,
                    "Could not create a DB directory in '" + projPath + "'.");
            return false;
        }

        try {
            FileWriter fw = new FileWriter(new File(projPath, "cscope.files"));
            BufferedWriter out = new BufferedWriter(fw);
            GlobFilter filter = new GlobFilter(
                    ConfigHelper.getConfig(ConfigHelper.OPTION + "fileglobs"));
            boolean foundOne = false;
            int counter = 0;
            int total = proj.getOpenableNodes().size();

            for (VPTNode node : proj.getOpenableNodes()) {
                String filePath = node.getNodePath();

                if (filter.accept(filePath)) {
                    // TODO: make line ending configurable
                    // For cygwin we probably want unix line endings on windows.
                    out.write(filePath);
                    out.newLine();
                    foundOne = true;
                }
                counter += 1;
                presenter.processProgress(view, counter, total);
            }

            presenter.finished(view);
            out.close();
            return foundOne;

        } catch (IOException ioe){
            Log.log(Log.ERROR, CscopeFinderPlugin.class,
                "Could not create file 'cscope.files' in '" + projPath + "'.", ioe);
            return false;
        }

    }
}
