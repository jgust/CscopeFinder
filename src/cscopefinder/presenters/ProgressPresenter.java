package cscopefinder.presenters;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;

public class ProgressPresenter {

    private final String prefix;

    public void ProgressPresenter(String prefix) {
        this.prefix = prefix;
    }
    public void processProgress(final View view, int currentProgress, int total) {
        int percent = (currentProgress/total) * 100;
        updateProgress(view, percent);
    }

    public void finished(final View view) {
        updateProgress(view, 100);
    }

    private void updateProgress(final View view, final int percent) {
        Runnable r = new Runnable() {
            public void run() {
                String msg = prefix + ' ' + precent + '%';
                view.getStatus().setMessageAndClear(msg);
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException ie) {
				// not gonna happen
				Log.log(Log.ERROR, PVActions.class, ie);
			} catch (java.lang.reflect.InvocationTargetException ite) {
				// not gonna happen
				Log.log(Log.ERROR, PVActions.class, ite);
			}
		}
    }

}
