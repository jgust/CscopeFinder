package cscopefinder.commands;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.helpers.ConfigHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.View;


import org.gjt.sp.util.Log;

public abstract class CscopeCommand implements Runnable {

    protected String args;
    protected Pattern pattern;
    protected View view;
    protected String projectPath;
    protected String cscopePath;

    public CscopeCommand() {
        args = "";
        projectPath = "";
        cscopePath = "";
        view = null;
        pattern = null;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setProjectPath(String path) {
        projectPath = path;
    }

    public void setCscopePath(String path) {
        cscopePath = path;
    }

    public void run() {
        StringBuilder error = new StringBuilder();

        if (!preVerify(error)) {
            JOptionPane.showMessageDialog(view, error.toString());
            return;
        }

        Process p = execute(cscopePath, getArgs(), getWorkingDir(), error);

        try {
            if (p == null || p.waitFor() != 0) {
                Log.log(Log.ERROR, CscopeFinderPlugin.class, "Error running command!\n"
                        + "ERROR message: " + error.toString());
            }
        } catch (InterruptedException ie) {
            Log.log(Log.MESSAGE, CscopeFinderPlugin.class, getClass().toString() +
                        ": got interrupted. Killing process.");
		    if (p != null)
				p.destroy();
		}

		postCleanup();

    }

    protected interface ReaderThreadCallback {
        public void onLineRead(String line);
        public void onReadComplete();
    }

    protected abstract ReaderThreadCallback getIsCallback();

    protected boolean preVerify(StringBuilder error) {
        if (!ConfigHelper.verifyCscopePath(cscopePath)) {
            error.append("Cscope executable not found!\n" +
                                "Please check your plugin options.");
            return false;
        }
        return true;
    }

    protected void postCleanup() {/*No specific cleanup needed*/}

    protected String getArgs() {
        return args;
    }

    protected String getWorkingDir() {
        return projectPath + File.separatorChar +
                ConfigHelper.getConfig(ConfigHelper.OPTION + "cscope-db-path");
    }

    private Process execute(String cscopePath, String args, String workingDir,
                            final StringBuilder errorOutput) {
        Process p = null;
        StreamReaderThread outReader = null;
        StreamReaderThread errReader = null;
        try {

            p = Runtime.getRuntime().exec(cscopePath + " " + args, null, new File(workingDir));

            outReader = new StreamReaderThread(p.getInputStream(), getIsCallback());
            outReader.start();
            errReader = new StreamReaderThread(p.getErrorStream(), new ReaderThreadCallback() {
                public void onLineRead(String line) {
                    errorOutput.append(line);
                    errorOutput.append('\n');
                }
                public void onReadComplete() {}
            });
            errReader.start();
            return p;
        } catch (IOException ioe) {
            Log.log(Log.ERROR, CscopeFinderPlugin.class, "Starting process caused "
				    + "an exception!", ioe);
		}
		return null;
    }

    private class StreamReaderThread extends Thread {

        private InputStream is;
        private ReaderThreadCallback cb;

        public StreamReaderThread(InputStream stream, ReaderThreadCallback callback) {
            is = stream;
            cb = callback;
        }

        public void run() {
            try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;
				do {
					line = br.readLine();
					if (line != null)
						cb.onLineRead(line);
				}
				while (line != null);
				is.close();
			}
			catch (IOException ioe) {
				Log.log(Log.ERROR, CscopeFinderPlugin.class, "Reading from process caused "
				    + "an exception!", ioe);
			}
			finally {
			    cb.onReadComplete();
			}
        }
    }

}
