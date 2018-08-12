package jngram.manipulations;

import apps.jbsmekorot.HebrewUtils;
import apps.jbsmekorot.JbsIndex;
import apps.jbsmekorot.manipulations.AddTextWithShemAdnut;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramManipulation;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

/**
 * Some of the fuzzy matches contain non-ehevi diffs. This manipulation removes
 * such matches.
 */
public abstract class RemoveNonEheviFuzzyMatches extends NgramManipulation {

    protected abstract JbsIndex getIndex();
    protected abstract int getMaximalNgramSize();
    protected abstract int getMinimalNgramSize();

    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.hasTags();
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        List<String> removedTags = new ArrayList<>();
        for (String tag : ng.getTags()) {
            // get the text of the URI
            List<org.apache.lucene.document.Document> docs = getIndex().searchExactInUri(tag);
            String documentText = docs.get(0).get("text");

            String text = getBestMatchText(documentText, ng.getTextFormatted()); // note use of formatted text here.
            if (!HebrewUtils.isEheviDiff(ng.getTextFormatted(), text)) {
                    removedTags.add(tag);
            }


            // originally we treated also this case of ADNUT_TEXT.
            // but we currently ignore it in the generic version hoping
            // it will not affect precision so much.
//            String adnutText = ng.getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT);
//
//            if (adnutText == null) {
//                if (!HebrewUtils.isEheviDiff(ng.getTextFormatted(), text)) {
//                    removedTags.add(tag);
//                }
//            } else {
//                if (!HebrewUtils.isEheviDiff(ng.getTextFormatted(), text) && !HebrewUtils.isEheviDiff(adnutText, text)) {
//                    removedTags.add(tag);
//                }
//            }
        }
        if (removedTags.size() > 0) {
            ng.addToHistory(getName(), "Removed some tags.");
            ng.removeTags(removedTags);
        }
    }

    /**
     * We return the ngram within the text with the best
     * match w.r.t. given textPortion.
     * @param text
     * @param textPortion
     * @return
     */
    private String getBestMatchText(String text, String textPortion) {
        NgramDocument sd = new NgramDocument(text, getMinimalNgramSize(), getMaximalNgramSize());

        int minDistance = 1000;
        String bestMatch = null;
        for (Ngram ng : sd.getAllNgrams()) {
            int currDist = getLevenshteinDistance(ng.getText(), textPortion);
            if (currDist < minDistance) {
                minDistance = currDist;
                bestMatch = ng.getText();
            }
        }

        return bestMatch;
    }
}
