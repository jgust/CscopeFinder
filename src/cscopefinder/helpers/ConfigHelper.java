package cscopefinder.helpers;

import java.awt.Font;
import java.io.File;

import org.gjt.sp.jedit.jEdit;

public class ConfigHelper
{
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

	public static Font getFontConfig(String fontName) {
	    Font defaultFont = new Font(jEdit.getProperty(fontName + DEFAULT),
                    jEdit.getIntegerProperty(fontName + "style" + DEFAULT),
                    jEdit.getIntegerProperty(fontName + "size" + DEFAULT));

	    return jEdit.getFontProperty(fontName, defaultFont);
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

    public static boolean createCscopeDbDir(String projectPath) {
        if (projectPath == null)
            return false;

        if (!(new File(projectPath)).isDirectory())
            return false;

        File dbDir = new File(getCscopeDbPath(projectPath));

        if (!(dbDir).isDirectory()) {
            return dbDir.mkdir();
        }

        return true;
    }

}
