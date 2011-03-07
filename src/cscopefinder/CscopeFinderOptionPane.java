package cscopefinder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import cscopefinder.helpers.ConfigHelper;

public class CscopeFinderOptionPane extends AbstractOptionPane {
    static public final String OPTION = ConfigHelper.OPTION;
    static public final String MESSAGE = ConfigHelper.MESSAGE;

    static public final String CSCOPE_PATH = OPTION + "cscope-path";
    static public final String CSCOPE_DB = OPTION + "cscope-db-path";
    static public final String FILEGLOBS = OPTION + "fileglobs";
    static public final String INDEX_TIMEOUT = OPTION + "index-timeout";
    static public final String AUTO_INDEX = OPTION + "index-auto";

    static public final String CSCOPE_PATH_LABEL = MESSAGE + "cscope-path";
    static public final String CHECK_PATH_LABEL = MESSAGE + "check-cscope-path";
    static public final String PATH_OK_LABEL = MESSAGE + "bad-cscope-path";
    static public final String PATH_NOK_LABEL = MESSAGE + "good-cscope-path";
    static public final String FILEGLOBS_LABEL = MESSAGE + "fileglobs";
    static public final String TIMEOUT_LABEL = MESSAGE + "index-timeout";
    static public final String TIMEOUT_UNITS = MESSAGE + "index-timeout.units";
    static public final String AUTO_INDEX_LABEL = MESSAGE + "index-auto";

    static public final String CSCOPE_PATH_TOOLTIP = CSCOPE_PATH_LABEL + ConfigHelper.TOOLTIP;
    static public final String FILEGLOBS_TOOLTIP = FILEGLOBS_LABEL + ConfigHelper.TOOLTIP;
    static public final String TIMEOUT_TOOLTIP = TIMEOUT_LABEL + ConfigHelper.TOOLTIP;
    static public final String AUTO_INDEX_TOOLTIP = AUTO_INDEX_LABEL + ConfigHelper.TOOLTIP;


    JTextField cscopePath;
	JButton checkPath;
	JTextField fileglobs;
	JSpinner timeoutSpinner;
	JCheckBox autoIndex;


	public CscopeFinderOptionPane() {
	    super("CscopeFinder");
	}

	@Override
	public void _save() {
	    jEdit.setProperty(CSCOPE_PATH, cscopePath.getText());
        jEdit.setProperty(FILEGLOBS, fileglobs.getText());
        jEdit.setIntegerProperty(INDEX_TIMEOUT, (Integer)timeoutSpinner.getValue());
        jEdit.setBooleanProperty(AUTO_INDEX, autoIndex.isSelected());
        EditBus.send(new PropertiesChanged(null));
	}

	@Override
	protected void _init() {
	    setBorder(new EmptyBorder(5, 5, 5, 5));

	    addCscopePathOption();
	    addFileGlobsOption();
	    addIndexingTimeoutOption();
	    addAutomaticIndexingOption();
	}

	private void addCscopePathOption() {
	    JPanel cscopePanel = new JPanel();
		cscopePath = new JTextField(ConfigHelper.getConfig(CSCOPE_PATH), 80);
		cscopePath.setToolTipText(jEdit.getProperty(CSCOPE_PATH_TOOLTIP));
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

	private void addFileGlobsOption() {
	    JPanel fileGlobPanel = new JPanel();
	    fileglobs = new JTextField(ConfigHelper.getConfig(FILEGLOBS), 80);
	    fileglobs.setToolTipText(jEdit.getProperty(FILEGLOBS_TOOLTIP));
	    fileGlobPanel.add(fileglobs);
	    addComponent(jEdit.getProperty(FILEGLOBS_LABEL), fileGlobPanel);
	}

	private void addIndexingTimeoutOption() {
	    SpinnerNumberModel model = new SpinnerNumberModel(
                        ConfigHelper.getIntegerConfig(INDEX_TIMEOUT), 1, 15, 1);
        timeoutSpinner = new JSpinner(model);
        JLabel units = new JLabel(jEdit.getProperty(TIMEOUT_UNITS));
        JPanel panel = new JPanel();
        timeoutSpinner.setToolTipText(TIMEOUT_TOOLTIP);
        panel.add(timeoutSpinner);
        panel.add(units);
        addComponent(jEdit.getProperty(TIMEOUT_LABEL), panel);
	}

	private void addAutomaticIndexingOption() {
	    JPanel panel = new JPanel();
	    autoIndex = new JCheckBox();
	    autoIndex.setSelected(ConfigHelper.getBooleanConfig(AUTO_INDEX));
	    autoIndex.setToolTipText(AUTO_INDEX_TOOLTIP);
	    panel.add(autoIndex);
	    addComponent(jEdit.getProperty(AUTO_INDEX_LABEL), panel);
	}

	private boolean checkPath() {
		String path = cscopePath.getText();
		if (!ConfigHelper.verifyCscopePath(path)) {
		    JOptionPane.showMessageDialog(this, jEdit.getProperty(PATH_NOK_LABEL));
			return false;
		}
		JOptionPane.showMessageDialog(this, jEdit.getProperty(PATH_OK_LABEL));
		return true;
	}
}
