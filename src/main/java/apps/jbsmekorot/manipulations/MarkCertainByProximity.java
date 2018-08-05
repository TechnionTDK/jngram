package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsMekorot;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramManipulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Here we mark short ngrams (size 2,3) with MarkCertainByProximity.MARK = true if there are
 * other ngrams "nearby" that are marked with MarkCertainBySize and
 * have the same tags. In addition, a list of tags to be keep is also added to the extras
 * (list is available via TAGS_TO_KEEP key).
 */
public class MarkCertainByProximity extends NgramManipulation {
    public static final String MARK = "certain_proximity";
    public static final String TAGS_TO_KEEP = "to_keep";

    @Override
    public void manipulate(NgramDocument doc) {
        doc.clearTagsNgramIndex();
        doc.createTagNgramIndex();
        super.manipulate(doc);
    }

    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.size() == 2 || ng.size() == 3;
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        // previous techniques that we tried and were proved quite unhelpful:
        // (1) check for a dot in the middle of a text (don't want to rely on that).
        // (2) keep the tag in case of a unique (one) tag.
        // (3) look for hints: ngrams surrounded with quotes (don't want to rely on that).

        // for a given ngram, we save for keep all tags that
        // are contained in certain_size ngrams nearby
        List<String> tagsToKeep = new ArrayList<>();

        for (String tag : ng.getTags()) {
            // ngramsWithSameTag cannot be null since it contains at least ng
            List<Ngram> ngramsWithSameTag = doc.getNgrams(tag);

            if (ngramsWithSameTag.size() == 1) // only ng, no other ngrams with same tag, nothing to mark.
                continue;

            // we keep the tag only if it also appears in a CERTAIN_SIZE ngram
            if (nearbyCertainNgram(ng, ngramsWithSameTag)) {
                tagsToKeep.add(tag);
            }
        }

        if (tagsToKeep.size() > 0) {
            ng.addToHistory(getName(), "Mark certain by proximity");
            ng.putExtra(MARK, true);
            ng.putExtra(TAGS_TO_KEEP, tagsToKeep);
        }
    }

    private boolean nearbyCertainNgram(Ngram thisNgram, List<Ngram> ngramsWithSameTag) {
        for (Ngram otherNgram : ngramsWithSameTag) {
            if (thisNgram.equals(otherNgram))
                continue;

            if (otherNgram.size() < JbsMekorot.CERTAIN_NGRAM_SIZE)
                continue;

            int distance = 0;
            if (thisNgram.getStart() > otherNgram.getEnd())
                distance = thisNgram.getStart() - otherNgram.getEnd();
            else
                distance = otherNgram.getStart() - thisNgram.getEnd();

            if (distance <= JbsMekorot.MAXIMAL_DISTANCE_FROM_CERTAIN_NGRAM + 1) {
                return true;
            }
        }

        return false;
    }
}
