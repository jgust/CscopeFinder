package cscopefinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import cscopefinder.commands.CscopeCommand;
import cscopefinder.helpers.ConfigHelper;

public class CscopeRunner {

    public CscopeCommand runCommand(CscopeCommand cmd, String cscopePath, String projectPath) {
        StringBuffer error = new StringBuffer();
        Process p = run(cmd, cscopePath, projectPath, error);
        try {
            if (p == null || p.waitFor() != 0) {
                Log.log(Log.ERROR, CscopeFinderPlugin.class, "Error running command!\n"
                        + "ERROR message: " + error.toString());
            }
        } catch (InterruptedException ie) {
		    if (p != null)
				p.destroy();
			Log.log(Log.ERROR, CscopeFinderPlugin.class, "", ie);
		}
        return cmd;
    }

    private boolean verifyCscopeDbDir(String dbDir) {
        boolean verified = true;

        if (!(new File(dbDir)).isDirectory())
            verified = false;

        if (verified && !(new File(dbDir, "cscope.out")).isFile())
            verified = false;

        if (verified && !(new File(dbDir, "cscope.in.out")).isFile())
            verified = false;

        if (verified && !(new File(dbDir, "cscope.po.out")).isFile())
            verified = false;

        if (!verified)
            Log.log(Log.ERROR, CscopeFinderPlugin.class, "No cscope database found in " + dbDir
                                        + ".");
        return verified;
    }

    private Process run(CscopeCommand cmd, String cscopePath,
                        String projectPath, StringBuffer error) {
        if (projectPath == null)
            return null;

        String cscopeDbDir = projectPath + File.separatorChar +
                ConfigHelper.getConfig(ConfigHelper.OPTION + "cscope-db-path");

        if (CscopeFinderPlugin.verifyCscopePath(cscopePath) && verifyCscopeDbDir(cscopeDbDir)) {
            Process p = execute(cscopePath, cmd, cscopeDbDir, error);
            return p;
        }
        return null;
    }

    private Process execute(String cscopePath, final CscopeCommand cmd,
                            String workingDir, final StringBuffer errorOutput) {
        Process p = null;
        StreamReaderThread outReader = null;
        StreamReaderThread errReader = null;
        try {

            p = Runtime.getRuntime().exec(cscopePath + " " + cmd.getArgs(),
                                            null, new File(workingDir));
            outReader = new StreamReaderThread(p.getInputStream(), new ReaderThreadCallback() {
                public void onLineRead(String line) {
                    cmd.parseOutput(line);
                }
                public void onReadComplete() {}
            });
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

    private interface ReaderThreadCallback {
        public void onLineRead(String line);
        public void onReadComplete();
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
