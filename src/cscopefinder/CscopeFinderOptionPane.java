package cscopefinder;

import java.awt.BorderLayout;
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
import org.gjt.sp.jedit.gui.FontSelector;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import cscopefinder.helpers.ConfigHelper;

public class CscopeFinderOptionPane extends AbstractOptionPane {
    static public final String OPTION = ConfigHelper.OPTION;
    static public final String MESSAGE = ConfigHelper.MESSAGE;

    static public final String CSCOPE_PATH = OPTION + "cscope-path";
    static public final String FONT = OPTION + "font";
    static public final String CSCOPE_DB = OPTION + "cscope-db-path";
    static public final String FILEGLOBS = OPTION + "fileglobs";
    static public final String INDEX_TIMEOUT = OPTION + "index-timeout";
    static public final String AUTO_INDEX = OPTION + "index-auto";

    static public final String CSCOPE_PATH_LABEL = MESSAGE + "cscope-path";
    static public final String CHECK_PATH_LABEL = MESSAGE + "check-cscope-path";
    static public final String PATH_OK_LABEL = MESSAGE + "bad-cscope-path";
    static public final String PATH_NOK_LABEL = MESSAGE + "good-cscope-path";
    static public final String FONT_LABEL = MESSAGE + "font";
    static public final String FILEGLOBS_LABEL = MESSAGE + "fileglobs";
    static public final String TIMEOUT_LABEL = MESSAGE + "index-timeout";
    static public final String TIMEOUT_UNITS = MESSAGE + "index-timeout.units";
    static public final String AUTO_INDEX_LABEL = MESSAGE + "index-auto";

    static public final String CSCOPE_PATH_TOOLTIP = CSCOPE_PATH_LABEL + ConfigHelper.TOOLTIP;
    static public final String FONT_TOOLTIP = FONT_LABEL + ConfigHelper.TOOLTIP;
    static public final String FILEGLOBS_TOOLTIP = FILEGLOBS_LABEL + ConfigHelper.TOOLTIP;
    static public final String TIMEOUT_TOOLTIP = TIMEOUT_LABEL + ConfigHelper.TOOLTIP;
    static public final String AUTO_INDEX_TOOLTIP = AUTO_INDEX_LABEL + ConfigHelper.TOOLTIP;


    private JTextField cscopePath;
    private FontSelector font;
	private JTextField fileglobs;
	private JSpinner timeoutSpinner;
	private JCheckBox autoIndex;


	public CscopeFinderOptionPane() {
	    super("CscopeFinder");
	}

	@Override
	public void _save() {
	    jEdit.setProperty(CSCOPE_PATH, cscopePath.getText());
        jEdit.setProperty(FILEGLOBS, fileglobs.getText());
        jEdit.setIntegerProperty(INDEX_TIMEOUT, (Integer)timeoutSpinner.getValue());
        jEdit.setBooleanProperty(AUTO_INDEX, autoIndex.isSelected());
        jEdit.setFontProperty(FONT, font.getFont());
        EditBus.send(new PropertiesChanged(null));
	}

	@Override
	protected void _init() {
	    setBorder(new EmptyBorder(5, 5, 5, 5));

	    addCscopePathOption();
	    addFontOption();
	    addFileGlobsOption();
	    addIndexingTimeoutOption();
	    addAutomaticIndexingOption();
	}

	private void addCscopePathOption() {
	    JPanel cscopePanel = new JPanel(new BorderLayout());
		cscopePath = new JTextField(ConfigHelper.getConfig(CSCOPE_PATH));
		cscopePath.setToolTipText(jEdit.getProperty(CSCOPE_PATH_TOOLTIP));
		cscopePanel.add(cscopePath);
		JButton checkPath = new JButton(jEdit.getProperty(CHECK_PATH_LABEL));
		cscopePanel.add(checkPath, BorderLayout.EAST);
		checkPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				checkPath();
			}
		});

		addComponent(jEdit.getProperty(CSCOPE_PATH_LABEL), cscopePanel);
	}

	private void addFontOption() {
	    font = new FontSelector(ConfigHelper.getFontConfig(FONT));
	    font.setToolTipText(jEdit.getProperty(FONT_TOOLTIP));
	    addComponent(jEdit.getProperty(FONT_LABEL), font);
	}

	private void addFileGlobsOption() {
	    fileglobs = new JTextField(ConfigHelper.getConfig(FILEGLOBS));
	    fileglobs.setToolTipText(jEdit.getProperty(FILEGLOBS_TOOLTIP));
	    addComponent(jEdit.getProperty(FILEGLOBS_LABEL), fileglobs);
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
	    autoIndex = new JCheckBox(jEdit.getProperty(AUTO_INDEX_LABEL));
	    autoIndex.setSelected(ConfigHelper.getBooleanConfig(AUTO_INDEX));
	    autoIndex.setToolTipText(AUTO_INDEX_TOOLTIP);
	    addComponent(autoIndex);
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
