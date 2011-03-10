package cscopefinder.helpers;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.CscopeResult;


public class JumpHelper {

    public static void jumpToResult(final View view, final CscopeResult result) {
        new Jump(view, result.filename, result.query, result.line);
    }

    private static void sendAutoJump(final View view, final boolean starting) {
        EditPlugin p = jEdit.getPlugin("ise.plugin.nav.NavigatorPlugin",false);
        if (p != null) {
            AutoJumpHelper.autoJump(view, starting);
        }
    }

    public static class Jump implements Runnable {
        private Buffer buffer;
        private final String filename;
        private final View view;
        private final String target;
        private final int line;
        private boolean bufferLoaded;

        public Jump(final View view, final String filename, final String target, final int line) {
            this.view = view;
            this.filename = filename;
            this.target = target;
            this.line = line;
            bufferLoaded = false;

            ThreadUtilities.runInDispatchThread(this);
        }

        public void run() {
            EditBus.addToBus(this);

            buffer = jEdit.openFile(view, filename);

            if(buffer == null) {
                EditBus.removeFromBus(this);
                Log.log(Log.ERROR, CscopeFinderPlugin.class, "Jump failed! Could not open file");
                return;
            }

            sendAutoJump(view, true);

            view.goToBuffer(buffer);

            synchronized(this) {
                if (!bufferLoaded && buffer.isLoaded()) {
                    bufferLoaded = true;
                    bufferLoaded();
                }
            }
        }

        @EBHandler
		public void handleBufferUpdate(BufferUpdate msg) {
		    if (buffer == null)
		        return;

			if (msg.getWhat() == BufferUpdate.LOADED &&
                        msg.getBuffer() == buffer) {
			    EditBus.removeFromBus(this);
			    synchronized(this) {
			        if (!bufferLoaded) {
                        bufferLoaded = true;
                        bufferLoaded();
			        }
				}
			}
		}

		private void bufferLoaded() {
		    int start = 0;
            int end = 0;

            int lineNo = line - 1;
            if(lineNo >= 0 && lineNo < buffer.getLineCount())
            {
                String line = buffer.getLineText(lineNo);
                line.indexOf(target);

                start = line.indexOf(target) + buffer.getLineStartOffset(lineNo);
                end = start + target.length();
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

    }

}
