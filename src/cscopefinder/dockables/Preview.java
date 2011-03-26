package cscopefinder.dockables;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListModel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.jedit.textarea.JEditEmbeddedTextArea;
import org.gjt.sp.util.Log;

import cscopefinder.CscopeFinderPlugin;
import cscopefinder.CscopeResult;
import cscopefinder.helpers.ConfigHelper;

public class Preview extends CscopeDockable
{
    static final String PREVIEW_DOCKABLE = ConfigHelper.DOCKABLE + "preview";

    public Preview(View view) {
        super(view);
        resultsList.setCellRenderer(new PreviewCellRenderer());
        resultsList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me)
                {
                    /* fire on double click */
                    if (me.getClickCount() == 2 && listener != null) {
                        CscopeResult selectedResult = (CscopeResult)resultsModel
                                .getElementAt(resultsList.getSelectedIndex());
                        fireResultSelected(listener, selectedResult);
                        close();
                        me.consume();
                    }
                }
        });
        resultsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    DockableWindowManager dockman = Preview.this.view.getDockableWindowManager();
                    if (!dockman.isDockableWindowDocked(PREVIEW_DOCKABLE)) {
                        dockman.hideDockableWindow(PREVIEW_DOCKABLE);
                    }
                    Preview.this.view.getTextArea().requestFocus();
                    e.consume();
                }
            }
        });
        setResults(null, null);
    }

    @Override
    public void setResults(Vector<CscopeResult> results, ResultListListener listener) {
        super.setResults(results, listener);
        PreviewCellRenderer renderer = (PreviewCellRenderer) resultsList.getCellRenderer();
        renderer.resultsChanged(resultsList.getModel());
    }

    public void close() {
        DockableWindowManager dockman = Preview.this.view.getDockableWindowManager();
        if (!dockman.isDockableWindowDocked(PREVIEW_DOCKABLE)) {
            dockman.hideDockableWindow(PREVIEW_DOCKABLE);
            setResults(null, null);
        }
    }

    public class PreviewCellRenderer extends DefaultListCellRenderer
    {
        private Vector<PreviewTextArea> previews;

        public PreviewCellRenderer() {
            previews = new Vector<PreviewTextArea>();
        }

        public Component getListCellRendererComponent(JList list, Object value,
                            int index, boolean isSelected, boolean cellHasFocus) {
            Log.log(Log.DEBUG, CscopeFinderPlugin.class, "getCellRenderer : " + index);
            PreviewTextArea previewCell = previews.elementAt(index);
            if (previewCell != null)
                return previewCell.renderCell(isSelected, cellHasFocus);
            return null;
        }

        public void resultsChanged(ListModel model) {
            Log.log(Log.DEBUG, CscopeFinderPlugin.class, "Preview Results new size: "
                                                                        + model.getSize());
            previews.setSize(model.getSize());
            for (int i = 0; i < model.getSize(); i++) {
                CscopeResult result = (CscopeResult)model.getElementAt(i);
                previews.insertElementAt(new PreviewTextArea(result), i);
            }
        }
    }

    public class PreviewTextArea extends JEditEmbeddedTextArea
    {
        private StringBuffer content;
        private Mode mode;
        private static final int NO_LINES = 11;
        private static final int CENTER_LINE = NO_LINES / 2;  // 5
        private static final int MARGIN = CENTER_LINE;        // 5
        private boolean firstRender = true;

        public PreviewTextArea(CscopeResult result) {
            File file = new File(result.filename);
            Buffer sourceBuffer = jEdit.openTemporary(view, file.getParent(), file.getName(), false);
            content = new StringBuffer();

            if (buffer == null)
                return;

            int begin = (result.line - 1) - MARGIN;
            int end = result.line + MARGIN;
            int counter = begin;

            do {
                if (counter >= 0)
                    content.append(sourceBuffer.getLineText(counter));
                if (counter < (end - 1))
                    content.append('\n');
                counter += 1;
            } while (counter < end);

            mode = ModeProvider.instance.getModeForFile(result.filename,
                                                                sourceBuffer.getLineText(0));
            if (mode == null)
                mode = ModeProvider.instance.getMode("text");

        }

        public PreviewTextArea renderCell(boolean isSelected, boolean cellHasFocus) {
            if (firstRender) {
                synchronized (this) {
                    firstRender = false;
                }
                getBuffer().setReadOnly(false);
                setText(content.toString());
                getBuffer().setMode(mode);
                getBuffer().setReadOnly(true);
                setCaretPosition(getBuffer().getLineStartOffset(CENTER_LINE));
                scrollTo(0, false);
                Log.log(Log.DEBUG, CscopeFinderPlugin.class, "Linecount: "
                    + getBuffer().getLineCount());
            }

            return this;
        }

        @Override
        public Dimension getPreferredSize()
        {
            Dimension dim = super.getPreferredSize();
            // plus 2 to get rid of scrollbar
            dim.height = painter.getFontMetrics().getHeight() * (NO_LINES + 2);
            return dim;
        }

    }
}
