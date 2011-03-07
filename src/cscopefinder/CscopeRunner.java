package cscopefinder;

import cscopefinder.helpers.ProjectHelper;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import cscopefinder.commands.CscopeCommand;
import cscopefinder.helpers.ConfigHelper;

public class CscopeRunner {

    Thread t = null;

    public void runCommand(View view, CscopeCommand cmd) {
        String projectPath = ProjectHelper.getProjectPath(view);
        String cscopePath = ConfigHelper.getConfig(ConfigHelper.OPTION + "cscope-path");

        if (cscopePath == null || cscopePath.isEmpty()) {
            JOptionPane.showMessageDialog(view, "The path to the cscope executable was empty!\n" +
                                "Please check your plugin options.");
            return;
        }

        if (projectPath == null || projectPath.isEmpty()) {
            JOptionPane.showMessageDialog(view, "The project associated with this Cscope command\n" +
                        "was empty! Check the activity log for details.");
            return;
        }

        cmd.setView(view);
        cmd.setCscopePath(cscopePath);
        cmd.setProjectPath(projectPath);

        runCommand(cmd, view);

    }

    public void runCommand(Runnable cmd, View view) {
        if (t == Thread.currentThread())
            return;

        if (t != null && t.isAlive()) {
            JOptionPane.showMessageDialog(view, "Cscope is busy. Try again later...");
            return;
        }

        Thread t = new Thread(cmd);
        t.start();
    }

    public void abortCurrentCommand() {
        if (t == null || !t.isAlive()) {
            Log.log(Log.WARNING, CscopeFinderPlugin.class, "abortCurrentCommand: No command running.");
        } else {
            Log.log(Log.MESSAGE, CscopeFinderPlugin.class, "abortCurrentCommand: Aborting command.");
            t.interrupt();
            Log.log(Log.MESSAGE, CscopeFinderPlugin.class, "abortCurrentCommand: Thread interrupted." +
                        " Joining...");
            try {
                t.join();
            } catch (InterruptedException ie) {
                Log.log(Log.ERROR, CscopeFinderPlugin.class, "Got interrupted while waiting on" +
                                " command to abort.  Giving up..");
            }
        }
        t = null;
    }

}
