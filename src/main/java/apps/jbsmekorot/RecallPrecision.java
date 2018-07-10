package apps.jbsmekorot;

import org.apache.commons.lang3.StringUtils;
import jngram.NgramDocument;
import jngram.Ngram;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides services to calculate recall & precision for psukim detection.
 * It goes like that: identify psukim using one of the labeled data files
 * found in resources/labeledPsukimData (of course you should ignore the labels, i.e.,
 * remove % chars). Then you should give the resulted NgramDocument to this
 * class for calculating recall & precision.
 * Created by omishali on 14/12/2017.
 */
public class RecallPrecision {
    private static final String TRIPLE_LABEL = "%%%";
    private static final String DOUBLE_LABEL = "%%";
    private static final String SINGLE_LABEL = "%";

    public RecallResult getRecall(NgramDocument sd) {
        float totalLabeledSpans = 0;
        float totalHits = 0;
        RecallResult result = new RecallResult();

        for (Ngram s : sd.getAllNgrams()) {
            if (!isLabeledSpan(s))
                continue;

            totalLabeledSpans++;

            if (isSingleLabeledSpan(s)) {
                // s should have at least 1 tag
                if (s.getTags().size() >= 1)
                    totalHits++;
                else { // we missed this span
                    result.addMissedSpan(s);
                }
            }

            if (isDoubleLabeledSpan(s)) {
                // s should have at least 2 tags
                if (s.getTags().size() >= 2)
                    totalHits++;
                else { // we missed this span
                    result.addMissedSpan(s);
                }
            }
        }
        result.setTotalLabeldSpans(totalLabeledSpans);
        result.setTotalHits(totalHits);

        return result;
    }

    private boolean isLabeledSpan(Ngram s) {
        if (s.getText().startsWith(TRIPLE_LABEL) && s.getText().endsWith(TRIPLE_LABEL)) { // potential labeled span
            if (StringUtils.countMatches(s.getText(), "%") == 6)
                return true; // no inner marks
            else
                return false;
        }
        if (s.getText().startsWith(DOUBLE_LABEL) && s.getText().endsWith(DOUBLE_LABEL)) { // potential labeled span
            if (StringUtils.countMatches(s.getText(), "%") == 4)
                return true; // no inner marks
            else
                return false;
        }
        if (s.getText().startsWith(SINGLE_LABEL) && s.getText().endsWith(SINGLE_LABEL)) { // potential labeled span
            if (StringUtils.countMatches(s.getText(), "%") == 2)
                return true; // no inner marks
            else
                return false;
        }

        return false;
    }

    /**
     * Ngram s should be a labeled span.
     * @param s
     * @return
     */
    private boolean isDoubleLabeledSpan(Ngram s) {
        return s.getText().startsWith(DOUBLE_LABEL);
    }

    /**
     * Ngram s should be a labeled span.
     * @param s
     * @return
     */
    private boolean isSingleLabeledSpan(Ngram s) {
        return !isDoubleLabeledSpan(s) && s.getText().startsWith(SINGLE_LABEL);
    }

    public PrecisionlResult getPrecision(NgramDocument sd) {
        float totalLabeledTags = 0;
        float totalTags = 0;
        PrecisionlResult result = new PrecisionlResult();

        for (Ngram s : sd.getAllNgrams()) {
            if (s.getTags().size() == 0)
                continue;

            totalTags += s.getTags().size();

            // here we have problems with precision,
            // since the span has tags however the span is not labeled.
            if (!isLabeledSpan(s)) {
                result.addImpreciseSpan(s);
                continue;
            }

            // here we deal with labeled spans.

            if (isSingleLabeledSpan(s)) {
                totalLabeledTags += 1;
                // do we have more than one tag? if yes, the span is imprecise
                if (s.getTags().size() != 1)
                    result.addImpreciseSpan(s);
            }

            if (isDoubleLabeledSpan(s)) {
                totalLabeledTags += 2;
                // do we have more or less than two tags? if yes, the span is imprecise
                if (s.getTags().size() != 2)
                    result.addImpreciseSpan(s);
            }
        }

        result.setTotalLabeldTags(totalLabeledTags);
        result.setTotalTags(totalTags);

        return result;
    }

    public MultPrecisionResult getPrecision(List<NgramDocument> sds) {
        MultPrecisionResult result = new MultPrecisionResult();
        for (NgramDocument sd : sds)
            result.add(getPrecision(sd));

        return result;
    }

    public MultRecallResult getRecall(List<NgramDocument> sds) {
        MultRecallResult result = new MultRecallResult();
        for (NgramDocument sd : sds)
            result.add(getRecall(sd));

        return result;
    }

    public class MultPrecisionResult {
        private List<PrecisionlResult> results = new ArrayList<>();

        public void add(PrecisionlResult result) {
            results.add(result);
        }

        public float getAveragePrecision() {
            float total = 0;
            for (PrecisionlResult res : results)
                total += res.getPrecision();

            return total / results.size();
        }
    }

    public class MultRecallResult {
        private List<RecallResult> results = new ArrayList<>();

        public void add(RecallResult result) {
            results.add(result);
        }

        public float getAverageRecall() {
            float total = 0;
            for (RecallResult res : results)
                total += res.getRecall();

            return total / results.size();
        }
    }


    public class RecallResult {
        private float totalLabeledSpans, totalHits;

        public float getTotalLabeldSpans() {
            return totalLabeledSpans;
        }

        public void setTotalLabeldSpans(float totalLabeldSpans) {
            this.totalLabeledSpans = totalLabeldSpans;
        }

        public float getTotalHits() {
            return totalHits;
        }

        public void setTotalHits(float totalHits) {
            this.totalHits = totalHits;
        }

        private List<Ngram> missedNgrams = new ArrayList<>();

        public void addMissedSpan(Ngram s) {
            missedNgrams.add(s);
        }

        public void printMissedSpans() {
            for (Ngram s : missedNgrams) {
                System.out.println("Missed span:");
                System.out.println(s);
            }
        }

        public float getRecall() {
            // in case of no labeled spans we return 100% recall
            if (totalLabeledSpans == 0)
                return 1;
            else
                return totalHits / totalLabeledSpans;
        }
    }

    public class PrecisionlResult {
        private float totalLabeledTags, totalTags;

        public float getTotalLabeldTags() {
            return totalLabeledTags;
        }

        public void setTotalLabeldTags(float totalLabeldTags) {
            this.totalLabeledTags = totalLabeldTags;
        }

        public float getTotalTags() {
            return totalTags;
        }

        public void setTotalTags(float totalTags) {
            this.totalTags = totalTags;
        }

        private List<Ngram> impreciseNgrams = new ArrayList<>();

        public void addImpreciseSpan(Ngram s) {
            impreciseNgrams.add(s);
        }

        public void printImpreciseSpans() {
            for (Ngram s : impreciseNgrams) {
                System.out.println("Imprecise span:");
                System.out.println(s);
            }
        }

        public float getPrecision() {
            // in case of no found tags we return 100% precision
            if (totalTags == 0)
                return 1;
            else
                return totalLabeledTags / totalTags;
        }
    }


}
