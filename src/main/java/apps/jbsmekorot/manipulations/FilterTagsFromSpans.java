package apps.jbsmekorot.manipulations;

import org.apache.commons.lang3.StringUtils;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.manipulations.FilterTagsManipulationNgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omishali on 15/10/2017.
 */
public class FilterTagsFromSpans extends FilterTagsManipulationNgram {
    private static final int CERTAIN_LENGTH = 4;
    private static final int MAXIMAL_DISTANCE_FROM_CERTAIN_SPAN = 70;

    public FilterTagsFromSpans(NgramDocument doc) {
        doc.clearTagsNgramIndex();
        doc.createTagNgramIndex();
    }

    @Override
    protected boolean isCandidate(Ngram s) {
        return s.size() == 2 || s.size() == 3;
    }

    @Override
    protected void filterTags(NgramDocument doc, Ngram s) {
        // we used to check this, but we prefer not to be based on dots and other punctuation marks.
        // anyway, if you use this rule note that current impl. has bug: it doesn't filter out % that appear
        // in labeled data!
        // if the span contains the dot char "." IN THE MIDDLE of it, we clear all tags - we assume that a quotation may not include a dot
//        if (s.text().matches("\\D+\\.\\D+")) {
//            System.out.println(s);
//            s.clearTags();
//            return;
//        }

        List<String> tagsToBeRemoved = new ArrayList<>();

        // this rule caused many imprecisions. Of course its removal will also hurt recall but should
        // think of a better techniques to catch quotes of length 3.
        //if (s.getTags().size() <= 1) // we keep the tags in case of uniqueness
        //    return;
        // now we have tags.size() >= 2

        // keep a tag if it appears in a different span in the document
        for (String tag : s.getTags()) {
            List<Ngram> spansWithSameTag = doc.getNgrams(tag);

            // spansWithSameTag cannot be null since it contains at least s

            if (spansWithSameTag.size() == 1) {  // only s, no other spans with same tag => remove tag
                tagsToBeRemoved.add(tag); // if we use s.removeTag(tag) we get a java.util.ConcurrentModificationException
                continue;
            }

            // if all found tags are in length < CERTAIN_LENGTH => remove tag. Example: Raba_39_9 !!
            // In other words, we keep the tag only if it also appears in a CERTAIN span
            if (isInLongDistanceFromCertainSpan(s, spansWithSameTag))
                tagsToBeRemoved.add(tag);
        }

        //if (!isSurroundedWithQuotes(s)) // we use "hint". See e.g. test Mitzvot_1_1 span [20,22] that we miss otherwise.
        s.removeTags(tagsToBeRemoved);

        // here we may check whether s contains "many" tags (MANY_TAGS)
    }

    private boolean isInLongDistanceFromCertainSpan(Ngram thisNgram, List<Ngram> spansWithSameTag) {
        for (Ngram otherNgram : spansWithSameTag) {
            if (thisNgram.equals(otherNgram))
                continue;

            if (otherNgram.size() < CERTAIN_LENGTH)
                continue;

            int distance = 0;
            if (thisNgram.getStart() > otherNgram.getEnd())
                distance = thisNgram.getStart() - otherNgram.getEnd();
            else
                distance = otherNgram.getStart() - thisNgram.getEnd();

            if (distance <= MAXIMAL_DISTANCE_FROM_CERTAIN_SPAN + 1)
                return false;
        }

        return true;
    }

    private boolean hasTagInCertainLength(List<Ngram> spansWithSameTag) {
        for (Ngram s : spansWithSameTag)
            if (s.size() >= CERTAIN_LENGTH)
                return true;

        return false;
    }

    /**
     * Actually we currently do not check whether it is surrounded with quotes
     * but whether it contains two quotes (easier and may be enough).
     * @param s
     * @return
     */
    private boolean isSurroundedWithQuotes(Ngram s) {
        String text = s.getText();
        text = text.replace("''", "\""); // replace double quotes of torat emet '' with normal quotes

        // count the number of quotes
        int count = StringUtils.countMatches(text, "\"");

        if (count == 2)
            return true;
        else
            return false;
    }
}
