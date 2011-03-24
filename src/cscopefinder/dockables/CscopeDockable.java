package cscopefinder.dockables;

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;

import cscopefinder.CscopeResult;

public abstract class CscopeDockable extends JPanel implements DefaultFocusComponent
{
    protected final View view;
    protected JList resultsList;
    protected DefaultListModel resultsModel;
    protected ResultListListener listener;

    public CscopeDockable(View view) {
        super(new BorderLayout());
        this.view = view;
        resultsModel = new DefaultListModel();
        resultsList = new JList(resultsModel);
        add(new JScrollPane(resultsList), BorderLayout.CENTER);
    }

    public void setResults(Vector<CscopeResult> results, ResultListListener listener) {
        this.listener = listener;
        populateModel(results);
    }

    public void focusOnDefaultComponent()
    {
        resultsList.requestFocus();
    }

    protected void populateModel(Vector<CscopeResult> results) {
        resultsModel.removeAllElements();
        if (results != null){
            for (CscopeResult result : results) {
                resultsModel.addElement(result);
            }
        }
    }

    protected void fireResultSelected(final ResultListListener listener,
                                                final CscopeResult result) {
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
}
