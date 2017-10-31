package apps.jbsmekorot;

import spanthera.Span;
import spanthera.SpannedDocument;
import spanthera.manipulations.FilterTagsManipulation;

import java.util.ArrayList;
import java.util.List;

/**
 * - Removes tags from spans in length 2.
 * - Removes tags from spans in length 3 unless there is a single tag there.
 * Created by omishali on 15/10/2017.
 */
public class FilterTagsFromSpansSize2 extends FilterTagsManipulation {
    private static final int MANY_TAGS = 2;
    private static final int CERTAIN_LENGTH = 4;

    public FilterTagsFromSpansSize2(SpannedDocument doc) {
        doc.clearTagsSpanIndex();
        doc.createTagSpanIndex();
    }

    @Override
    protected boolean isCandidate(Span s) {
        return s.size() == 2;
    }

    @Override
    protected void filterTags(SpannedDocument doc, Span s) {
        List<String> tagsToBeRemoved = new ArrayList<>();

        // keep a tag if it appears in a different span in the document
        for (String tag : s.getTags()) {
            List<Span> spansWithSameTag = doc.getSpans(tag);

            // spansWithSameTag cannot be null since it contains at least s

            if (spansWithSameTag.size() == 1)  // only s, no other spans with same tag => remove tag
                tagsToBeRemoved.add(tag); // if we use s.removeTag(tag) we get a java.util.ConcurrentModificationException
            else if (!hasTagInCertainLength(spansWithSameTag)) // if all found tags are in length < CERTAIN_LENGTH => remove tag. Example: Raba_39_9 !!
                tagsToBeRemoved.add(tag);
        }

        s.removeTags(tagsToBeRemoved);

        // here we may check whether s contains "many" tags (MANY_TAGS)
    }

    private boolean hasTagInCertainLength(List<Span> spansWithSameTag) {
        for (Span s : spansWithSameTag)
            if (s.size() >= CERTAIN_LENGTH)
                return true;

        return false;
    }
}
