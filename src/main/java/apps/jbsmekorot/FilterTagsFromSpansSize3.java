package apps.jbsmekorot;

import org.apache.commons.lang3.StringUtils;
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
public class FilterTagsFromSpansSize3 extends FilterTagsManipulation {

    public FilterTagsFromSpansSize3(SpannedDocument doc) {
        doc.clearTagsSpanIndex();
        doc.createTagSpanIndex();
    }

    @Override
    protected boolean isCandidate(Span s) {
        return s.size() == 3;
    }

    @Override
    protected void filterTags(SpannedDocument doc, Span s) {
        List<String> tagsToBeRemoved = new ArrayList<>();

        if (s.getTags().size() <= 1) // we keep the tags in case of uniqueness
            return;

        // now we have tags.size() >= 2

        // keep a tag if it appears in a different span in the document
        for (String tag : s.getTags()) {
            List<Span> spansWithSameTag = doc.getSpans(tag);

            // spansWithSameTag cannot be null since it contains at least s

            if (spansWithSameTag.size() == 1)  // only s, no other spans with same tag => remove tag
                tagsToBeRemoved.add(tag); // if we use s.removeTag(tag) we get a java.util.ConcurrentModificationException
        }

        if (!isSurroundedWithQuotes(s)) // we use "hint". See e.g. test Mitzvot_1_1 span [20,22] that we miss otherwise.
            s.removeTags(tagsToBeRemoved);

        // here we may check whether s contains "many" tags (MANY_TAGS)
    }

    /**
     * Actually we currently do not check whether it is surrounded with quotes
     * but whether it contains two quotes (easier and may be enough).
     * @param s
     * @return
     */
    private boolean isSurroundedWithQuotes(Span s) {
        String text = s.text();
        text = text.replace("''", "\""); // replace double quotes of torat emet '' with normal quotes

        // count the number of quotes
        int count = StringUtils.countMatches(text, "\"");

        if (count == 2)
            return true;
        else
            return false;
    }
}
