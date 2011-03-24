package cscopefinder.presenters;

import java.util.Vector;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.CscopeResult;
import cscopefinder.dockables.Preview;
import cscopefinder.dockables.ResultListListener;
import cscopefinder.helpers.ConfigHelper;
import cscopefinder.helpers.JumpHelper;

public class PreviewPresenter implements ResultPresenter {

    static final String PREVIEW_DOCKABLE = ConfigHelper.DOCKABLE + "preview";

    public void process(final View view, Vector<CscopeResult> results) {

        if (results == null || results.isEmpty()) {
            Log.log(Log.DEBUG, CscopeFinderPlugin.class,
                "Results were empty!");
            return;
        }

        view.getDockableWindowManager().showDockableWindow(PREVIEW_DOCKABLE);
        Preview p = (Preview)view.getDockableWindowManager().getDockable(PREVIEW_DOCKABLE);
        p.setResults(results, new ResultListListener() {
            public void resultSelected(CscopeResult result) {
                JumpHelper.jumpToResult(view, result);
            }
        });
    }
}
