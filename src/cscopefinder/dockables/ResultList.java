package cscopefinder.dockables;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import javax.swing.JScrollPane;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;

import cscopefinder.CscopeResult;
import cscopefinder.helpers.ConfigHelper;
import cscopefinder.helpers.ProjectHelper;

public class ResultList extends JPanel implements DefaultFocusComponent
{
    View view;
    JList resultsList;
    DefaultListModel resultsModel;
    ResultListListener listener;

    public ResultList(View view) {
        super(new BorderLayout());
        this.view = view;
        resultsModel = new DefaultListModel();
		resultsList = new JList(resultsModel);
		add(new JScrollPane(resultsList), BorderLayout.CENTER);
		resultsList.setCellRenderer(new ResultCellRenderer());
		resultsList.addMouseListener(new MouseAdapter() {
		        public void mouseClicked(MouseEvent me)
		        {
		            if (listener != null) {
		                CscopeResult selectedResult = (CscopeResult)resultsModel
                                .getElementAt(resultsList.getSelectedIndex());
		                fireResultSelected(listener, selectedResult);
		            }
		        }
		});
		setResults(null, null);
    }

    public void focusOnDefaultComponent()
	{
		resultsList.requestFocus();
	}

    public void setResults(Vector<CscopeResult> results, ResultListListener listener) {
       this.listener = listener;
       populateModel(results);
    }

    private void populateModel(Vector<CscopeResult> results) {
        resultsModel.removeAllElements();
        if (results != null){
            for (CscopeResult result : results) {
                resultsModel.addElement(result);
            }
        }

    }

    private void fireResultSelected(final ResultListListener listener, final CscopeResult result) {
        Runnable r = new Runnable() {
			public void run() {
				listener.resultSelected(result);
			}
		};
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
			SwingUtilities.invokeLater(r);
    }

    private final class ResultCellRenderer extends DefaultListCellRenderer
    {
        public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
			CscopeResult element = (CscopeResult) resultsModel.getElementAt(index);
			l.setText(shortenFileName(element.filename)
                        + ":" + element.line + ": " + element.preview);
			l.setFont(ConfigHelper.getFontConfig(ConfigHelper.OPTION + "font"));
			l.setBorder(BorderFactory.createLoweredBevelBorder());
			return l;
        }

        public String shortenFileName(String filename) {
            String prj = ProjectHelper.getActiveProjectPath(view);
            if (prj == null)
                return filename;
            String shFile = filename.replace(prj, "");
            if (shFile.startsWith(String.valueOf(File.separatorChar)))
                shFile = shFile.substring(1);
            return shFile;
        }
    }
}
