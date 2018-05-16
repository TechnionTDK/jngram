package jngram.manipulations;

import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;

/**
 * Super class for manipulations that remove tags from spans
 * based on a certain criteria.
 */
public abstract class FilterTagsManipulationNgram implements NgramDocumentManipulation {
    @Override
    public void manipulate(NgramDocument doc) {
        for (Ngram s : doc.getAllNgrams()) {
            if (isCandidate(s))
                filterTags(doc, s);}
    }

    /**
     * Whether the span is a candidate for tag removal.
     * Only spans that answer "yes" be later be queried
     * using shouldRemoveTag.
     * @param s
     * @return
     */
    protected abstract boolean isCandidate(Ngram s);
    protected abstract void filterTags(NgramDocument doc, Ngram s);
}