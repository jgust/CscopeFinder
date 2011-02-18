package cscopefinder.helpers;

import java.util.Vector;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.CscopeResult;
import cscopefinder.dockables.ResultList;
import cscopefinder.dockables.ResultListListener;

public class ResultHandler {

    static final String LIST_DOCKABLE = CscopeFinderPlugin.DOCKABLE + "results-list";

    public void process(final View view, final String query, Vector<CscopeResult> results) {

        if (results == null || results.isEmpty()) {
            Log.log(Log.DEBUG, CscopeFinderPlugin.class,
                "Results were empty for query: " + "'" + query + "'");
            return;
        }

        if (results.size() == 1) {
            jumpToResult(view, query, results.firstElement());
            return;
        }

        view.getDockableWindowManager().showDockableWindow(LIST_DOCKABLE);
        ResultList rl = (ResultList)view.getDockableWindowManager().getDockable(LIST_DOCKABLE);
        rl.setResults(results, new ResultListListener() {
            public void resultSelected(CscopeResult result) {
                jumpToResult(view, query, result);
            }
        });
    }

    public void processForPreview(final View view, final String query,
                                    Vector<CscopeResult> results) {

        for (CscopeResult result : results) {
            Log.log(Log.DEBUG, CscopeFinderPlugin.class,
                result.filename + " " + result.line + " " + result.preview);
        }

    }

    public void jumpToResult(final View view, final String query, final CscopeResult result) {
        final Buffer buffer = jEdit.openFile(view, result.filename);
        if(buffer == null)
            return;

        sendAutoJump(view, true);

        VFSManager.runInAWTThread(new Runnable() {
            public void run() {
                view.goToBuffer(buffer);

                int start = 0;
                int end = 0;

                int lineNo = result.line - 1;
                if(lineNo >= 0 && lineNo < buffer.getLineCount())
                {
                    String line = buffer.getLineText(lineNo);
                    line.indexOf(query);

                    start = line.indexOf(query) + buffer.getLineStartOffset(lineNo);
                    end = start + query.length();
                }

                Log.log(Log.DEBUG, CscopeFinderPlugin.class,
                    "jump line: " + lineNo + " start: " + start + " end: " + end);

                view.getTextArea().setSelection(
                    new Selection.Range(start,end));

                view.getTextArea().moveCaretPosition(end);

                if (!view.getTextArea().hasFocus())
                    view.getTextArea().requestFocus();

                sendAutoJump(view, false);
            }
        });
    }

    private static void sendAutoJump(final View view, final boolean starting) {
        EditPlugin p = jEdit.getPlugin("ise.plugin.nav.NavigatorPlugin",false);
        if (p != null) {
            AutoJumpHelper.autoJump(view, starting);
        }
    }

}
