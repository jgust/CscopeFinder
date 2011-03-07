package cscopefinder.helpers;

import java.io.File;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;

import cscopefinder.CscopeFinderPlugin;

/* based on a class from ProjectViewer with same name */
public class GlobFilter {
    private Pattern file_positive;
    private Pattern file_negative;

    public GlobFilter(String fileGlobs) {
        StringTokenizer globs = new StringTokenizer(fileGlobs);
        StringBuilder fPos = new StringBuilder();
        StringBuilder fNeg = new StringBuilder();

        while (globs.hasMoreTokens()) {
            String token = globs.nextToken();
            if (token.startsWith("!")) {
                fNeg.append(StandardUtilities.globToRE(token.substring(1)));
                fNeg.append('|');
            } else {
                fPos.append(StandardUtilities.globToRE(token));
                fPos.append('|');
            }
        }
        if (fNeg.length() > 0)
            fNeg.setLength(fNeg.length() - 1);
        if (fPos.length() > 0)
            fPos.setLength(fPos.length() - 1);

        try {
            file_positive = Pattern.compile(fPos.toString());
            file_negative = Pattern.compile(fNeg.toString());
        } catch (PatternSyntaxException re) {
            Log.log(Log.ERROR, CscopeFinderPlugin.class, re);
        }
    }

    public boolean accept(String filePath) {
        if (file_positive == null) {
            Log.log(Log.ERROR, CscopeFinderPlugin.class, "File glob regexp was null!");
            return false;
        }

        String fileName = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);

        return file_positive.matcher(fileName).matches()
               && !file_negative.matcher(fileName).matches();
    }
}

