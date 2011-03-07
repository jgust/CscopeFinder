package cscopefinder;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;

import org.gjt.sp.util.Log;

import cscopefinder.helpers.ProjectHelper;
import cscopefinder.commands.UpdateDbCommand;
import cscopefinder.commands.GenerateFileListCommand;

import static cscopefinder.commands.QueryCommand.FIND_SYMBOL;
import static cscopefinder.commands.QueryCommand.FIND_DEF;
import static cscopefinder.commands.QueryCommand.FIND_CALLING;
import static cscopefinder.commands.QueryCommand.FIND_CALLED_BY;
import static cscopefinder.commands.QueryCommand.FIND_INCLUDE;
import static cscopefinder.commands.QueryCommand.createCommand;


public class CscopeFinderPlugin extends EditPlugin
{
	static private CscopeRunner runner;
	static private ProjectHelper projHelper;

    public void start() {
        runner = new CscopeRunner();
        projHelper = new ProjectHelper();
    }

    public void stop() {
        runner = null;
        projHelper = null;
    }

    public static void findSymbol(View view) {
        runQuery(view, FIND_SYMBOL);
    }

    public static void findCalling(View view) {
        runQuery(view, FIND_CALLING);
    }

    public static void findDefinition(View view, boolean preview) {
        runQuery(view, FIND_DEF, preview);
    }

    public static void findIncludedBy(View view) {
        runQuery(view, FIND_INCLUDE);
    }

    public static void findCalledBy(View view) {
        runQuery(view, FIND_CALLED_BY);
    }

    public static void generateDb(final View view) {

        Runnable runAfter = new Runnable() {
            public void run() {
                updateDb(view);
            }
        };
        runner.runCommand(new GenerateFileListCommand(view, runAfter), view);
    }

    public static void updateDb(View view) {
        runner.runCommand(view, new UpdateDbCommand());
    }

    public static void abortCurrentCommand(View view) {
        runner.abortCurrentCommand();
    }

    private static void runQuery(View view, int type) {
        runQuery(view, type, false);
    }

    private static void runQuery(View view, int type, boolean preview) {
        String query = getQuery(view);
        if (query == null || query.isEmpty()) {
            Log.log(Log.WARNING, CscopeFinderPlugin.class, "Query was empty, doing nothing");
        }

        runner.runCommand(view, createCommand(type, query, preview));
    }

    private static String getQuery(View view) {
        String query = view.getTextArea().getSelectedText();
        if (query == null || query.isEmpty()) {
            view.getTextArea().selectWord();
            query = view.getTextArea().getSelectedText();
            view.getTextArea().selectNone();
        }
        query = query.trim();
        if (query.contains(" "))
            query = '"' + query + '"';
        return query;
    }
}
