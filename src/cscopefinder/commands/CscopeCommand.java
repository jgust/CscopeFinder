package cscopefinder.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;

import cscopefinder.CscopeResult;

public abstract class CscopeCommand {

    public static final String FIND_SYMBOL_ARGS    = "-d -L -0";
    public static final String FIND_DEF_ARGS       = "-d -L -1";
    public static final String FIND_CALLED_BY_ARGS = "-d -L -2";
    public static final String FIND_CALLING_ARGS   = "-d -L -3";
    public static final String FIND_INCLUDE_ARGS   = "-d -L -8";
    public static final String DEFAULT_PARSE_REGEXP  = "^(\\S+)\\s*(\\S+)?\\s*(\\d+)\\s*(.*)$";

    protected final Vector<CscopeResult> results;
    protected String args;
    protected Pattern pattern;

    public CscopeCommand() {
        results = new Vector();
        pattern = Pattern.compile(DEFAULT_PARSE_REGEXP);
    }

    public String getArgs() {
        return args;
    }

    public Vector<CscopeResult> getResults() {
        return results;
    }

    public void parseOutput(String output){
        Matcher m = pattern.matcher(output);
        if (m.matches()) {
            results.add(new CscopeResult(m.group(1), m.group(3), m.group(2), m.group(4)));
        }
    }

}
