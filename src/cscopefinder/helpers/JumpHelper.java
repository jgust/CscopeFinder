package cscopefinder.helpers;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.CscopeResult;


public class JumpHelper {

    public static void jumpToResult(final View view, final CscopeResult result) {
        final Buffer buffer = jEdit.openFile(view, result.filename);
        final String query = result.query;

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
