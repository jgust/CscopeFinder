package cscopefinder;

import java.io.File;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import cscopefinder.commands.CscopeCommand;
import cscopefinder.commands.CscopeQueryCommand;

import cscopefinder.helpers.ProjectHelper;
import cscopefinder.helpers.ResultHandler;

import static cscopefinder.commands.CscopeQueryCommand.FIND_SYMBOL;
import static cscopefinder.commands.CscopeQueryCommand.FIND_DEF;
import static cscopefinder.commands.CscopeQueryCommand.FIND_CALLING;
import static cscopefinder.commands.CscopeQueryCommand.FIND_CALLED_BY;
import static cscopefinder.commands.CscopeQueryCommand.FIND_INCLUDE;
import static cscopefinder.commands.CscopeQueryCommand.createCommand;



public class CscopeFinderPlugin extends EditPlugin
{
    static public final String OPTION = "options.CscopeFinder.";
	static public final String MESSAGE = "messages.CscopeFinder.";
	static public final String DOCKABLE = "dockables.CscopeFinder.";

	static private CscopeRunner runner;
	static private ProjectHelper projHelper;
	static private ResultHandler resultHandler;

    public void start() {
        runner = new CscopeRunner();
        projHelper = new ProjectHelper();
        resultHandler = new ResultHandler();
    }

    public void stop() {
        runner = null;
        projHelper = null;
        resultHandler = null;
    }

    public static boolean verifyCscopePath(String path) {
        File f = new File(path);
		if ((! f.exists()) || (! f.canExecute())) {
		    return false;
		}
		return true;
    }

    public static void findSymbol(View view) {
        CscopeQueryCommand cmd = (CscopeQueryCommand)runQuery(view, FIND_SYMBOL);
        resultHandler.process(view, cmd.getQuery(), cmd.getResults());
    }

    public static void findCalling(View view) {
        CscopeQueryCommand cmd = (CscopeQueryCommand)runQuery(view, FIND_CALLING);
        resultHandler.process(view, cmd.getQuery(), cmd.getResults());
    }

    public static void findDefinition(View view, boolean preview) {
        CscopeQueryCommand cmd = (CscopeQueryCommand)runQuery(view, FIND_DEF);
        if (preview)
            resultHandler.processForPreview(view, cmd.getQuery(), cmd.getResults());
        else
            resultHandler.process(view, cmd.getQuery(), cmd.getResults());
    }

    public static void findIncludedBy(View view) {
        CscopeQueryCommand cmd = (CscopeQueryCommand)runQuery(view, FIND_INCLUDE);
        resultHandler.process(view, cmd.getQuery(), cmd.getResults());
    }

    public static void findCalledBy(View view) {
        CscopeQueryCommand cmd = (CscopeQueryCommand)runQuery(view, FIND_CALLED_BY);
        resultHandler.process(view, cmd.getQuery(), cmd.getResults());
    }

    private static CscopeCommand runQuery(View view, int type) {
        String query = getQuery(view);
        if (query == null || query.isEmpty()) {
            Log.log(Log.WARNING, CscopeFinderPlugin.class, "Query was empty, doing nothing");
        }

        String projectPath = projHelper.getProjectPath(view);
        String cscopePath = jEdit.getProperty(OPTION + "cscope-path");
        return runner.runCommand(createCommand(type, query),
                                            cscopePath, projectPath);
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
