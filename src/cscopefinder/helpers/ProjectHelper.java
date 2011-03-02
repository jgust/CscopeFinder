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
            String path = ((Buffer)view.getTextArea().getBuffer()).getPath();
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

    public static String getProjectPath(VPTProject prj) {
        if(prj == null)
            return null;
        return prj.getRootPath();
    }

    public static String getActiveProjectPath(View view) {
        return getProjectPath(ProjectViewer.getActiveProject(view));
    }
}
