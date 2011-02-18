package cscopefinder.helpers;

import ise.plugin.nav.AutoJump;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EditBus;

public class AutoJumpHelper
{
    public static void autoJump(View view, boolean starting) {
        Object toSend = starting ? AutoJump.STARTED : AutoJump.ENDED;
        AutoJump aj = new AutoJump(view, toSend);
        EditBus.send(aj);
    }

}
