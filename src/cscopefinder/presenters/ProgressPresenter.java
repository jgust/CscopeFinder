package cscopefinder.presenters;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;

public class ProgressPresenter {

    public void processProgress(final View view, int currentProgress, int total) {
        // TODO:
        Log.log(Log.DEBUG, CscopeFinderPlugin.class, "Progress: " +
                currentProgress + "/" + total);
    }

    public void finished(final View view) {
        // TODO:
        Log.log(Log.DEBUG, CscopeFinderPlugin.class, "Progress: Finished");
    }
}
