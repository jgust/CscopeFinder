package cscopefinder.helpers;

import java.io.File;

import org.gjt.sp.jedit.jEdit;

public class ConfigHelper {
    static public final String OPTION = "options.CscopeFinder.";
    static public final String DEFAULT = ".default";
    static public final String TOOLTIP = ".tooltip";
	static public final String MESSAGE = "messages.CscopeFinder.";
	static public final String DOCKABLE = "dockables.CscopeFinder.";

	public static String getConfig(String propName) {
	    String defaultValue = jEdit.getProperty(propName + DEFAULT);
	    return jEdit.getProperty(propName, defaultValue);
	}

	public static int getIntegerConfig(String propName) {
	    int defaultValue = jEdit.getIntegerProperty(propName + DEFAULT);
	    return jEdit.getIntegerProperty(propName, defaultValue);
	}

	public static boolean getBooleanConfig(String propName) {
	    boolean defaultValue = jEdit.getBooleanProperty(propName + DEFAULT);
	    return jEdit.getBooleanProperty(propName, defaultValue);
	}

	public static String getCscopeDbPath(String projPath) {
	    return projPath + File.separatorChar + getConfig(ConfigHelper.OPTION + "cscope-db-path");
	}

	public static boolean verifyCscopePath(String path) {
        File f = new File(path);
		if ((! f.exists()) || (! f.canExecute())) {
		    return false;
		}
		return true;
    }

    public static boolean verifyCscopeDbDir(String dbDir) {
        boolean verified = true;

        if (!(new File(dbDir)).isDirectory())
            verified = false;

        if (verified && !(new File(dbDir, "cscope.out")).isFile())
            verified = false;

        if (verified && !(new File(dbDir, "cscope.in.out")).isFile())
            verified = false;

        if (verified && !(new File(dbDir, "cscope.po.out")).isFile())
            verified = false;

        return verified;
    }

}
