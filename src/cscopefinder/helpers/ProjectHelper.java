package cscopefinder.helpers;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

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

    public static void generateFileList(View view) {
        Log.log(Log.WARNING, CscopeFinderPlugin.class, "generateFileList not implemented yet!");
    }

}
