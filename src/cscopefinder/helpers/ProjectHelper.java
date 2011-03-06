package cscopefinder.helpers;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import cscopefinder.CscopeFinderPlugin;

public class ProjectHelper {


    public static VPTProject findProject(View view) {
        VPTProject prj = ProjectViewer.getActiveProject(view);
        if (prj != null)
            return prj;

        String path = ((Buffer)view.getTextArea().getBuffer()).getPath();
        prj = findProjectForFile(path);

        if (prj == null) {
            Log.log(Log.ERROR, CscopeFinderPlugin.class, "Could not find a project for file: "
                + path);
        }

        return prj;
    }

    public static VPTProject findProjectForFile(String path) {
        VPTProject prj = null;
        ProjectManager pm = ProjectManager.getInstance();
        for(VPTProject p : pm.getProjects()) {
            VPTNode node = p.getChildNode(path);
            if (node != null) {
                prj = p;
                break;
            }
        }
        return prj;
    }

    public static String findProjectWithPath(String path) {
        String prjName = "";
        ProjectManager pm = ProjectManager.getInstance();
        for(VPTProject p : pm.getProjects()) {
            if (path.equals(p.getRootPath())) {
                prjName = p.getName();
                break;
            }
        }
        return prjName;
    }

    public static String getProjectPath(VPTProject prj) {
        if(prj == null)
            return null;
        return prj.getRootPath();
    }

    public static String getProjectPath(View view) {
        return getProjectPath(findProject(view));
    }

    public static String getActiveProjectPath(View view) {
        return getProjectPath(ProjectViewer.getActiveProject(view));
    }

    public static boolean generateFileList(View view) {
        VPTProject proj = findProject(view);

        if (proj == null)
            return false;

        String projPath = getProjectPath(proj);
        if (!ConfigHelper.createCscopeDbDir(projPath)) {
            Log.log(Log.ERROR, CscopeFinderPlugin.class,
                    "Could not create a DB directory in '" + projPath + "'.");
            return false;
        }

        try {
            FileWriter fw = new FileWriter(new File(projPath, "cscope.files"));
            BufferedWriter out = new BufferedWriter(fw);
            GlobFilter filter = new GlobFilter(
                    ConfigHelper.getConfig(ConfigHelper.OPTION + "fileglobs"));
            boolean foundOne = false;
            for (VPTNode node : proj.getOpenableNodes()) {
                File file = new File(node.getNodePath());
                 Log.log(Log.DEBUG, CscopeFinderPlugin.class,
                    "Processing File '" + file.getPath() + "'.");

                if (filter.accept(file)) {
                    // TODO: make line ending configurable
                    // For cygwin we probably want unix line endings on windows.
                    out.write(file.getPath());
                    out.newLine();
                    foundOne = true;
                    Log.log(Log.DEBUG, CscopeFinderPlugin.class,
                    "File '" + node.getNodePath() + "' matched filter.");
                }
            }

            out.close();
            return foundOne;

        } catch (IOException ioe){
            Log.log(Log.ERROR, CscopeFinderPlugin.class,
                "Could not create file 'cscope.files' in '" + projPath + "'.", ioe);
            return false;
        }

    }

    /* based on a class from ProjectViewer with same name */
    private static class GlobFilter {
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

        public boolean accept(File file) {
            if (file_positive == null) {
                Log.log(Log.ERROR, CscopeFinderPlugin.class, "File glob regexp was null!");
                return false;
            }

            return file_positive.matcher(file.getName()).matches()
				   && !file_negative.matcher(file.getName()).matches();
        }
    }

}
