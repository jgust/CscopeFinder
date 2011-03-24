package cscopefinder.dockables;


import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.gjt.sp.jedit.View;

import cscopefinder.CscopeResult;
import cscopefinder.helpers.ConfigHelper;
import cscopefinder.helpers.ProjectHelper;

public class ResultList extends CscopeDockable
{
    public ResultList(View view) {
        super(view);
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

    private final class ResultCellRenderer extends DefaultListCellRenderer
    {
        public Component getListCellRendererComponent(JList list, Object value,
                            int index, boolean isSelected, boolean cellHasFocus) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
			CscopeResult element = (CscopeResult) value;
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
