package apps.jbsmekorot;

import org.apache.commons.lang3.StringUtils;
import spanthera.Span;
import spanthera.SpannedDocument;

/**
 * Provides services to calculate recall & precision for psukim detection.
 * It goes like that: identify psukim using one of the labeled data files
 * found in resources/labeledPsukimData (of course you should ignore the labels, i.e.,
 * remove % chars). Then you should give the resulted SpannedDocument to this
 * class for calculating recall & precision.
 * Created by omishali on 14/12/2017.
 */
public class RecallPrecision {
    private static final String DOUBLE_LABEL = "%%";
    private static final String SINGLE_LABEL = "%";

    public float getRecall(SpannedDocument sd) {
        System.out.println("Calculating recall...");
        float totalLabeledSpans = 0;
        float totalHits = 0;

        for (Span s : sd.getAllSpans()) {
            if (!isLabeledSpan(s))
                continue;

            totalLabeledSpans++;

            if (isSingleLabeledSpan(s)) {
                // s should have at least 1 tag
                if (s.getTags().size() >= 1)
                    totalHits++;
                else { // we missed this span
                    System.out.println("Missed span:");
                    System.out.println(s);
                }
            }

            if (isDoubleLabeledSpan(s)) {
                // s should have at least 2 tags
                if (s.getTags().size() > 2)
                    totalHits++;
                else { // we missed this span
                    System.out.println("Missed span:");
                    System.out.println(s);
                }
            }
        }
        System.out.println("total labeled spans: " + totalLabeledSpans);
        System.out.println("total hits: " + totalHits);

        // in case of no labeled spans we return 100% recall
        if (totalLabeledSpans == 0)
            return 1;
        else
            return totalHits / totalLabeledSpans;
    }

    private boolean isLabeledSpan(Span s) {
        if (s.text().startsWith(DOUBLE_LABEL) && s.text().endsWith(DOUBLE_LABEL)) { // potential labeled span
            if (StringUtils.countMatches(s.text(), "%") == 4)
                return true; // no inner marks
            else
                return false;
        }
        if (s.text().startsWith(SINGLE_LABEL) && s.text().endsWith(SINGLE_LABEL)) { // potential labeled span
            if (StringUtils.countMatches(s.text(), "%") == 2)
                return true; // no inner marks
            else
                return false;
        }

        return false;
    }

    /**
     * Span s should be a labeled span.
     * @param s
     * @return
     */
    private boolean isDoubleLabeledSpan(Span s) {
        return s.text().startsWith(DOUBLE_LABEL);
    }

    /**
     * Span s should be a labeled span.
     * @param s
     * @return
     */
    private boolean isSingleLabeledSpan(Span s) {
        return !isDoubleLabeledSpan(s) && s.text().startsWith(SINGLE_LABEL);
    }

    public float getPrecision(SpannedDocument sd) {
        System.out.println("Calculating precision...");
        float totalLabeledTags = 0;
        float totalTags = 0;

        for (Span s : sd.getAllSpans()) {
            if (s.getTags().size() == 0)
                continue;

            totalTags += s.getTags().size();

            // here we have problems with precision,
            // since the span has tags however the span is not labeled.
            if (!isLabeledSpan(s)) {
                System.out.println("Imprecise span:");
                System.out.println(s);
                continue;
            }

            // here we deal with labeled spans.

            if (isSingleLabeledSpan(s))
                totalLabeledTags += 1;

            if (isDoubleLabeledSpan(s))
                totalLabeledTags += 2;
        }

        System.out.println("total labeled tags: " + totalLabeledTags);
        System.out.println("total tags: " + totalTags);

        // in case of no found tags we return 100% precision
        if (totalTags == 0)
            return 1;
        else
            return totalLabeledTags / totalTags;
    }


}
