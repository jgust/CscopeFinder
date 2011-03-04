package cscopefinder.presenters;

import java.util.Vector;

import org.gjt.sp.jedit.View;

import cscopefinder.CscopeResult;

public interface ResultPresenter {
    public void process(final View view, Vector<CscopeResult> results);
}
