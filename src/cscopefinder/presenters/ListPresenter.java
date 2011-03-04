package cscopefinder.presenters;

import java.util.Vector;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.CscopeResult;
import cscopefinder.dockables.ResultList;
import cscopefinder.dockables.ResultListListener;
import cscopefinder.helpers.ConfigHelper;
import cscopefinder.helpers.JumpHelper;

public class ListPresenter implements ResultPresenter {

    static final String LIST_DOCKABLE = ConfigHelper.DOCKABLE + "results-list";

    public void process(final View view, Vector<CscopeResult> results) {

        if (results == null || results.isEmpty()) {
            Log.log(Log.DEBUG, CscopeFinderPlugin.class,
                "Results were empty!");
            return;
        }

        if (results.size() == 1) {
            JumpHelper.jumpToResult(view, results.firstElement());
            return;
        }

        view.getDockableWindowManager().showDockableWindow(LIST_DOCKABLE);
        ResultList rl = (ResultList)view.getDockableWindowManager().getDockable(LIST_DOCKABLE);
        rl.setResults(results, new ResultListListener() {
            public void resultSelected(CscopeResult result) {
                JumpHelper.jumpToResult(view, result);
            }
        });
    }
}
