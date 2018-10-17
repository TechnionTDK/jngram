package apps.bavlimekorot;

import apps.jbsmekorot.JbsIndex;
import jngram.NgramDocumentManipulation;
import jngram.NgramTagger;
import jngram.manipulations.RemoveNonEheviFuzzyMatches;

public class BavliRemoveNonEhevi extends RemoveNonEheviFuzzyMatches {

    private JbsIndex index = new JbsBavliIndex();

    @Override
    protected JbsIndex getIndex() {
        return index;
    }

    // We don't want to remove tags from very long fuzzy matches because they are probably correct.
    // So we only remove short non Ehevy fuzzy matches
    @Override
    protected int getMaximalNgramSize() {
        return BavliMekorot.MINIMAL_NGRAM_LENGTH * 4;
    }

    @Override
    protected int getMinimalNgramSize() {
        return BavliMekorot.MINIMAL_NGRAM_LENGTH;
    }
}
