package cscopefinder.presenters;

import java.util.Vector;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.CscopeResult;

public class PreviewPresenter implements ResultPresenter {
    public void process(final View view, Vector<CscopeResult> results) {
        // TODO:
        for (CscopeResult result : results) {
            Log.log(Log.DEBUG, CscopeFinderPlugin.class, "Preview: " +
                result.filename + " " + result.line + " " + result.preview);
        }

    }

    public void processDirect(final View view, CscopeResult result) { } // Not implemented
}
