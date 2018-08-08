package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.HebrewUtils;
import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.JbsTanachIndex;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;
import jngram.NgramManipulation;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

/**
 * Some of the fuzzy matches contain non-ehevi diffs. This manipulation removes
 * such matches.
 */
public class RemoveNonEheviFuzzyMatches extends NgramManipulation {
    private JbsTanachIndex index = JbsTanachIndex.instance();

    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.hasTags();
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        List<String> removedTags = new ArrayList<>();
        for (String tag : ng.getTags()) {
            // get the text of the pasuk
            List<org.apache.lucene.document.Document> docs = index.searchExactInUri(tag);
            String pasuk = docs.get(0).get("text");

            String pasukSpan = getPasukSpanWithBestMatch(pasuk, ng.getTextFormatted()); // note use of formatted text here.

            String adnutText = ng.getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT);

            if (adnutText == null) {
                if (!HebrewUtils.isEheviDiff(ng.getTextFormatted(), pasukSpan)) {
                    removedTags.add(tag);
                }
            } else {
                if (!HebrewUtils.isEheviDiff(ng.getTextFormatted(), pasukSpan) && !HebrewUtils.isEheviDiff(adnutText, pasukSpan)) {
                    removedTags.add(tag);
                }
            }
        }
        if (removedTags.size() > 0) {
            ng.addToHistory(getName(), "Removed some tags.");
            ng.removeTags(removedTags);
        }
    }

    /**
     * We return the span within the pasuk with the best
     * match w.r.t. given text.
     * @param pasuk
     * @param text
     * @return
     */
    private String getPasukSpanWithBestMatch(String pasuk, String text) {
        NgramDocument sd = new NgramDocument(pasuk, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);

        int minDistance = 1000;
        String bestMatch = null;
        for (Ngram s : sd.getAllNgrams()) {
            int currDist = getLevenshteinDistance(s.getText(), text);
            if (currDist < minDistance) {
                minDistance = currDist;
                bestMatch = s.getText();
            }
        }

        return bestMatch;
    }
}
