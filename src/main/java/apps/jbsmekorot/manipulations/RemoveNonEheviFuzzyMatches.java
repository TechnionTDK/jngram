package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.HebrewUtils;
import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.JbsTanachIndex;
import spanthera.NgramDocument;
import spanthera.Ngram;
import spanthera.NgramDocumentManipulation;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

/**
 * Some of the fuzzy matches contain non-ehevi diffs. This manipulation removes
 * such matches.
 */
public class RemoveNonEheviFuzzyMatches implements NgramDocumentManipulation {
    @Override
    public void manipulate(NgramDocument doc) {
        for (Ngram s : doc.getAllNgrams()) {
            if (s.hasNoTags())
                continue;

            List<String> removedTags = new ArrayList<>();
            for (String tag : s.getTags()) {
                // get the text of the pasuk
                JbsTanachIndex index = new JbsTanachIndex();
                List<org.apache.lucene.document.Document> docs = index.searchExactInUri(tag);
                String pasuk = docs.get(0).get("text");

                String pasukSpan = getPasukSpanWithBestMatch(pasuk, s.getTextFormatted()); // note use of formatted text here.

                String adnutText = s.getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT);

                if (adnutText == null) {
                    if (!HebrewUtils.isEheviDiff(s.getTextFormatted(), pasukSpan)) {
                        removedTags.add(tag);
                        //System.out.println(s.text());
                    }
                } else {
                    if (!HebrewUtils.isEheviDiff(s.getTextFormatted(), pasukSpan) && !HebrewUtils.isEheviDiff(adnutText, pasukSpan)) {
                        removedTags.add(tag);
                    }
                }
            }
            s.removeTags(removedTags);
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
