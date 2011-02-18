package cscopefinder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;

public class CscopeFinderOptionPane extends AbstractOptionPane {
    static public final String OPTION = CscopeFinderPlugin.OPTION;
    static public final String MESSAGE = CscopeFinderPlugin.MESSAGE;

    static public final String CSCOPE_PATH = OPTION + "cscope-path";
    static public final String CSCOPE_DB = OPTION + "cscope-db-path";

    static public final String CSCOPE_PATH_LABEL = MESSAGE + "cscope-path";
    static public final String CHECK_PATH_LABEL = MESSAGE + "check-cscope-path";
    static public final String PATH_OK_LABEL = MESSAGE + "bad-cscope-path";
    static public final String PATH_NOK_LABEL = MESSAGE + "good-cscope-path";

    JTextField cscopePath;
	JButton checkPath;

	public CscopeFinderOptionPane() {
	    super("CscopeFinder");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel cscopePanel = new JPanel();
		cscopePath = new JTextField(jEdit.getProperty(CSCOPE_PATH), 40);
		cscopePanel.add(cscopePath);
		checkPath = new JButton(jEdit.getProperty(CHECK_PATH_LABEL));
		cscopePanel.add(checkPath);
		checkPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				checkPath();
			}
		});

		addComponent(jEdit.getProperty(CSCOPE_PATH_LABEL), cscopePanel);
	}

	@Override
	public void _save() {
	    if (checkPath()) {
            jEdit.setProperty(CSCOPE_PATH, cscopePath.getText());
            EditBus.send(new PropertiesChanged(null));
        }
	}

	private boolean checkPath() {
		String path = cscopePath.getText();
		if (!CscopeFinderPlugin.verifyCscopePath(path)) {
		    JOptionPane.showMessageDialog(this, jEdit.getProperty(PATH_NOK_LABEL));
			return false;
		}
		JOptionPane.showMessageDialog(this, jEdit.getProperty(PATH_OK_LABEL));
		return true;
	}
}
