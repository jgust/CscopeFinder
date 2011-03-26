package cscopefinder.commands;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.helpers.ConfigHelper;
import cscopefinder.helpers.ProjectHelper;
import cscopefinder.presenters.ProgressPresenter;

public class UpdateDbCommand extends CscopeCommand
                                implements CscopeCommand.ReaderThreadCallback {

    protected static final String OPTION_INDEX_TIMEOUT = "index-timeout";
    protected static final String UPDATE_DB_ARGS = "-b -q -k -v";
    protected static final String PROGRESS_RE = "^> Building symbol database (\\d+) of (\\d+)$";

    protected ProgressPresenter presenter;
    protected Timer timer;
    protected WatchdogTask watchdog;


    public UpdateDbCommand() {
        args = UPDATE_DB_ARGS;
        presenter = new ProgressPresenter(
                    jEdit.getProperty(ConfigHelper.MESSAGE + "progress.updating-index"));
        pattern = Pattern.compile(PROGRESS_RE);
        timer = new Timer();
    }

    @Override
    protected ReaderThreadCallback getIsCallback() {
        return this;
    }

    @Override
    protected boolean preVerify(StringBuilder error) {
        if (verifyDbDir(error)) {
            reScheduleWatchdog();
            return true;
        }
        return false;
    }

    @Override
    protected void postCleanup() {
        timer.cancel();
    }

    public void onLineRead(String line) {
        Log.log(Log.MESSAGE, CscopeFinderPlugin.class, getClass().toString() +
                    ": Line: " + line);
        Matcher m = pattern.matcher(line);
        if (m.matches()) {
            presenter.processProgress(view, Integer.parseInt(m.group(1)),
                                            Integer.parseInt(m.group(2)));
        }
        reScheduleWatchdog();
    }

    public void onReadComplete() {
        presenter.finished(view);
        if (timer != null)
            timer.cancel();
    }

    protected void reScheduleWatchdog() {
        if (timer == null)
            return;

        WatchdogTask old = watchdog;
        int timeout = ConfigHelper.getIntegerConfig(ConfigHelper.OPTION + OPTION_INDEX_TIMEOUT);
        timeout = timeout * 60 * 1000;
        watchdog = new WatchdogTask(Thread.currentThread());
        try {
            timer.schedule(watchdog, timeout);
        } catch (IllegalStateException ise) {
            timer = null;
            Log.log(Log.MESSAGE, CscopeFinderPlugin.class, getClass().toString() +
                            ": Timer was dead. Watchdog will not be scheduled.");
        }

        if (old != null)
            old.cancel();
    }

    protected boolean verifyDbDir(StringBuilder error) {
        if (!ConfigHelper.createCscopeDbDir(projectPath)) {
            error.append("Could not create a DB directory in '" + projectPath + "'");
            return false;
        }

        String dbPath = ConfigHelper.getCscopeDbPath(projectPath);

        if (!(new File(dbPath, "cscope.files")).isFile()) {
            error.append("cscope.files does not exist for project '"
                    + ProjectHelper.findProjectWithPath(projectPath) + "'.\n"
                    + "Hint: Run CscopeFinder->"
                    + jEdit.getProperty("cscopefinder-generate-index.label"));
            return false;
        }
        Log.log(Log.DEBUG, CscopeFinderPlugin.class, "Updating index for " +
            ProjectHelper.findProjectWithPath(projectPath));
        return true;
    }

    public class WatchdogTask extends TimerTask {
        private final Thread thread;

        public WatchdogTask(Thread t) {
            thread = t;
        }

        public void run() {
            try {
                if (thread != null && thread.isAlive()) {
                    Log.log(Log.MESSAGE, CscopeFinderPlugin.class, getClass().toString() +
                            ": Interrupting process");
                    thread.interrupt();
                    thread.join();
                }
            }  catch (InterruptedException ie) {
                Log.log(Log.ERROR, CscopeFinderPlugin.class, getClass().toString() +
                    ": got interrupted while joining thread. Giving up...");
			}
        }
    }

}
