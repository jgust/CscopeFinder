package cscopefinder.commands;

public abstract class CscopeQueryCommand extends CscopeCommand {

    public static final int FIND_SYMBOL    = 0;
    public static final int FIND_DEF       = 1;
    public static final int FIND_CALLING   = 2;
    public static final int FIND_CALLED_BY = 3;
    public static final int FIND_INCLUDE   = 4;

    protected String query;

    public CscopeQueryCommand(String query) {
        this.query = query;
    }

    @Override
    public String getArgs() {
        return args + query;
    }

    public String getQuery() {
        return query;
    }

    static public CscopeQueryCommand createCommand(int type, String query) {
        switch(type) {
        case FIND_SYMBOL:
            return new FindSymbolCommand(query);
        case FIND_DEF:
            return new FindDefinitionCommand(query);
        case FIND_CALLING:
            return new FindCallingCommand(query);
        case FIND_CALLED_BY:
            return new FindCalledByCommand(query);
        case FIND_INCLUDE:
            return new FindIncludingCommand(query);
        default:
            return null;
        }

    }
}

/*package*/ class FindSymbolCommand extends CscopeQueryCommand {
    public FindSymbolCommand(String query) {
        super(query);
        args = CscopeCommand.FIND_SYMBOL_ARGS;
    }
}

/*package*/ class FindDefinitionCommand extends CscopeQueryCommand {
    public FindDefinitionCommand(String query) {
        super(query);
        args = CscopeCommand.FIND_DEF_ARGS;
    }
}

/*package*/ class FindCallingCommand extends CscopeQueryCommand {
    public FindCallingCommand(String query) {
        super(query);
        args = CscopeCommand.FIND_CALLING_ARGS;
    }
}

/*package*/ class FindCalledByCommand extends CscopeQueryCommand {
    public FindCalledByCommand(String query) {
        super(query);
        args = CscopeCommand.FIND_CALLED_BY_ARGS;
    }
}

/*package*/ class FindIncludingCommand extends CscopeQueryCommand {
    public FindIncludingCommand(String query) {
        super(query);
        args = CscopeCommand.FIND_INCLUDE_ARGS;
    }
}




