package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsIndex;
import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.JbsTanachIndex;
import jngram.manipulations.RemoveNonEheviFuzzyMatches;

public class PsukimRemoveNonEhevy extends RemoveNonEheviFuzzyMatches {
    private JbsTanachIndex index = JbsTanachIndex.instance();

    @Override
    protected JbsIndex getIndex() {
        return index;
    }

    @Override
    protected int getMaximalNgramSize() {
        return JbsMekorot.MAXIMAL_PASUK_LENGTH;
    }

    @Override
    protected int getMinimalNgramSize() {
        return JbsMekorot.MINIMAL_PASUK_LENGTH;
    }
}
