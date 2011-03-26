package cscopefinder.helpers;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import projectviewer.ProjectManager;
import projectviewer.ProjectViewer;
import projectviewer.event.ProjectUpdate;
import projectviewer.event.ViewerUpdate;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import cscopefinder.CscopeFinderPlugin;

public class ProjectHelper
{
    static final String AUTO_UPDATE = ConfigHelper.OPTION + "index-auto";

    private ProjectWatcher watcher;

    public ProjectHelper() {
        watcher = new ProjectWatcher();
    }

    public void startWatcher() {
        watcher.start();
    }

    public void stopWatcher() {
        watcher.stop();
    }

    public class ProjectWatcher
    {
        public void start() {
            EditBus.addToBus(this);
        }

        public void stop() {
            EditBus.removeFromBus(this);
        }

        @EBHandler
        public void handleProjectUpdate(ProjectUpdate pu) {
            if (!ConfigHelper.getBooleanConfig(AUTO_UPDATE))
                return;

            if (pu.getType() != ProjectUpdate.Type.FILES_CHANGED)
                return;

            VPTProject prj = pu.getProject();
            if (prj == null || prj.getOpenableNodes().isEmpty())
                return;

            Log.log(Log.MESSAGE, CscopeFinderPlugin.class, "Project: " + prj.getName()
                                    + " updated. Re-generating Cscope DB..");

            CscopeFinderPlugin.generateDb(jEdit.getActiveView(), prj);
        }

        @EBHandler
        public void handleViewerUpdate(ViewerUpdate vu) {
            if (!ConfigHelper.getBooleanConfig(AUTO_UPDATE))
                return;

            if (vu.getType() != ViewerUpdate.Type.PROJECT_LOADED)
                return;

            View view = jEdit.getActiveView();

            VPTProject prj = (VPTProject)vu.getNode();
            if (prj == null)
                return;

            String prjPath = ConfigHelper.getCscopeDbPath(prj.getRootPath());
            if (!ConfigHelper.verifyCscopeDbDir(prjPath) && !prj.getOpenableNodes().isEmpty()) {
                Log.log(Log.MESSAGE, CscopeFinderPlugin.class, "Project: " + prj.getName()
                                    + " loaded. Generating new Cscope DB..");

                CscopeFinderPlugin.generateDb(view, prj);
            }
        }


    }

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

}
