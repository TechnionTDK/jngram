package apps.jbsmekorot;

import spanthera.Span;
import spanthera.SpannedDocument;
import spanthera.manipulations.FilterTagsManipulation;

/**
 * - Removes tags from spans in length 2.
 * - Removes tags from spans in length 3 unless there is a single tag there.
 * Created by omishali on 15/10/2017.
 */
public class FilterTagsFromSmallSpans extends FilterTagsManipulation {
    @Override
    protected boolean isCandidate(Span s) {
        return s.size() == 2 || s.size() == 3;
    }

    @Override
    protected void filterTags(SpannedDocument doc, Span s) {
        // clear span2 tags, leave span3 tags
        if (s.size() == 2)
            s.clearTags();
    }
}
