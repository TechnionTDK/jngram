package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.JbsTanachIndex;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

/**
 * Created by omishali on 21/01/2018.
 * This manipulation filters tags based on their edit distance value.
 * First, it calculates edit distance for each tag. Then we
 * remove tags based on several related strategies.
 * The goal is to prevent edit distance to accumulate to a too big value.
 */
public class CalcAndFilterByEditDistance implements NgramDocumentManipulation {
    private static final String DISTANCE_KEY = "dist_key::";
    private static final double MAXIMAL_DISTANCE_LENGTH_RATIO = 0.15;

    @Override
    public void manipulate(NgramDocument doc) {
        for (Ngram s : doc.getAllNgrams()) {
            if (s.hasNoTags())
                continue;

            for (String tag : s.getTags()) {
                // get the text of the pasuk
                JbsTanachIndex index = new JbsTanachIndex();
                List<org.apache.lucene.document.Document> docs = index.searchExactInUri(tag);
                String pasuk = docs.get(0).get("text");

                int minDistance = getMinimalDistance(pasuk, s.getTextFormatted());
                s.putExtra(DISTANCE_KEY + tag, minDistance);
            }

            // now that we have added distance to all tags we apply filtering
            filterTagsBasedOnDistanceLengthRatio(s);
            filterTagsWithDistanceHigherThanMinimalDistance(s);
        }
    }

    private void filterTagsWithDistanceHigherThanMinimalDistance(Ngram s) {
        List<String> removedTags = new ArrayList<>();

        int min = getMinimalEditDistance(s);

        for (String tag : s.getTags())
            if (getDistance(s, tag) > min)
                removedTags.add(tag);

        s.removeTags(removedTags);
    }

    private int getMinimalEditDistance(Ngram s) {
        int min = 1000;
        for (String tag : s.getTags())
            if (getDistance(s, tag) < min)
                min = getDistance(s, tag);

        return min;
    }

    private void filterTagsBasedOnDistanceLengthRatio(Ngram s) {
        List<String> removedTags = new ArrayList<>();

        for (String tag : s.getTags()) {
            double distance = getDistance(s, tag);
            double length = s.getTextFormatted().length();
            if (distance / length > MAXIMAL_DISTANCE_LENGTH_RATIO)
                removedTags.add(tag);
        }
        s.removeTags(removedTags);
    }

    /**
     * We return the best match of text within the pasuk.
     * @param pasuk
     * @param text
     * @return
     */
    private int getMinimalDistance(String pasuk, String text) {
        NgramDocument sd = new NgramDocument(pasuk, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);

        int minDistance = 1000;
        for (Ngram s : sd.getAllNgrams()) {
            int currDist = getLevenshteinDistance(s.getText(), text);
            if (currDist < minDistance)
                minDistance = currDist;
        }

        return minDistance;
    }

    public static Integer getDistance(Ngram s, String tag) {
        return  s.getIntExtra(DISTANCE_KEY + tag);
    }
}
