package spanthera.manipulations;

import spanthera.Span;
import spanthera.SpanManipulation;
import spanthera.SpannedDocument;

/**
 * Super class for manipulations that remove tags from spans
 * based on a certain criteria.
 */
public abstract class FilterTagsManipulation implements SpanManipulation {
    @Override
    public void manipulate(SpannedDocument doc) {
        for (Span s : doc.getAllSpans()) {
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
    protected abstract boolean isCandidate(Span s);
    protected abstract void filterTags(SpannedDocument doc, Span s);
}
