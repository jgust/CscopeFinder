package cscopefinder.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.CscopeResult;
import cscopefinder.presenters.ListPresenter;
import cscopefinder.presenters.PreviewPresenter;
import cscopefinder.presenters.ResultPresenter;
import cscopefinder.helpers.ConfigHelper;
import cscopefinder.helpers.ProjectHelper;


public abstract class QueryCommand extends CscopeCommand
                                    implements CscopeCommand.ReaderThreadCallback{

    public static final int FIND_SYMBOL    = 0;
    public static final int FIND_DEF       = 1;
    public static final int FIND_CALLING   = 2;
    public static final int FIND_CALLED_BY = 3;
    public static final int FIND_INCLUDE   = 4;

    protected static final String FIND_SYMBOL_ARGS    = "-L -0";
    protected static final String FIND_DEF_ARGS       = "-L -1";
    protected static final String FIND_CALLED_BY_ARGS = "-L -2";
    protected static final String FIND_CALLING_ARGS   = "-L -3";
    protected static final String FIND_INCLUDE_ARGS   = "-L -8";
    protected static final String DEFAULT_PARSE_REGEXP  = "^(\\S+)\\s*(\\S+)?\\s*(\\d+)\\s*(.*)$";

    protected final String query;
    protected final Vector<CscopeResult> results;
    protected ResultPresenter presenter;

    public QueryCommand(String query) {
        this.query = query;
        results = new Vector();
        presenter = null;
        pattern = Pattern.compile(DEFAULT_PARSE_REGEXP);
    }

    @Override
    protected String getArgs() {
        String args = ConfigHelper.getBooleanConfig("index-auto") ? this.args : "-d " + this.args;
        return args + query;
    }

    @Override
    protected ReaderThreadCallback getIsCallback() {
        return this;
    }

    @Override
    protected boolean preVerify(StringBuilder error) {
        if (super.preVerify(error) && !ConfigHelper.verifyCscopeDbDir(getWorkingDir())) {
            error.append("No Cscope database found for project '" +
                        ProjectHelper.findProjectWithPath(projectPath) + "'.\n" +
                        "Hint: Run CscopeFinder->" +
                        jEdit.getProperty("cscopefinder-generate-index.label"));

            Log.log(Log.ERROR, CscopeFinderPlugin.class,
                    "No cscope database found in " + getWorkingDir() + ".");

            return false;
        }
        return true;
    }

    protected boolean usePreviewAsQuery() {
        return false;  /* don't use the preview */
    }

    public void onLineRead(String line) {
        Matcher m = pattern.matcher(line);
        if (m.matches()) {
            results.add(new CscopeResult(m.group(1), m.group(3),
                m.group(2), m.group(4), usePreviewAsQuery() ? m.group(4) : query));
        }
    }

    public void onReadComplete() {
        if (presenter == null) {
            Log.log(Log.ERROR, CscopeFinderPlugin.class,
                this.getClass().toString() + ": onReadComplete() presenter was null");
            return;
        }
        presenter.process(view, results);
    }

    static public QueryCommand createCommand(int type, String query, boolean preview) {
        ResultPresenter presenter = preview ? new PreviewPresenter() : new ListPresenter();

        switch(type) {
        case FIND_SYMBOL:
            return new FindSymbolCommand(query, presenter);
        case FIND_DEF:
            return new FindDefinitionCommand(query, presenter);
        case FIND_CALLING:
            return new FindCallingCommand(query, presenter);
        case FIND_CALLED_BY:
            return new FindCalledByCommand(query, presenter);
        case FIND_INCLUDE:
            return new FindIncludingCommand(query, presenter);
        default:
            return null;
        }

    }
}

/*package*/ class FindSymbolCommand extends QueryCommand {
    public FindSymbolCommand(String query, ResultPresenter p) {
        super(query);
        args = FIND_SYMBOL_ARGS;
        presenter = p;
    }
}

/*package*/ class FindDefinitionCommand extends QueryCommand {
    public FindDefinitionCommand(String query, ResultPresenter p) {
        super(query);
        args = FIND_DEF_ARGS;
        presenter = p;
    }
}

/*package*/ class FindCallingCommand extends QueryCommand {
    public FindCallingCommand(String query, ResultPresenter p) {
        super(query);
        args = FIND_CALLING_ARGS;
        presenter = p;
    }
}

/*package*/ class FindCalledByCommand extends QueryCommand {
    public FindCalledByCommand(String query, ResultPresenter p) {
        super(query);
        args = FIND_CALLED_BY_ARGS;
        presenter = p;
    }

    @Override
    protected boolean usePreviewAsQuery() {
        return true;
    }
}

/*package*/ class FindIncludingCommand extends QueryCommand {
    public FindIncludingCommand(String query, ResultPresenter p) {
        super(query);
        args = FIND_INCLUDE_ARGS;
        presenter = p;
    }

    @Override
    protected boolean usePreviewAsQuery() {
        return true;
    }
}




