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
        float numOfLabels = 0;
        float numOfHits = 0;
        RecallResult result = new RecallResult();

        for (Ngram ng : sd.getAllNgrams()) {
            if (!isLabeledNgram(ng))
                continue;

            if (isSingleLabeledNgram(ng)) {
                numOfLabels += 1;
                // ng should have at least 1 tag, if there are more, we ASSUME the correct is one of them.
                if (ng.getTags().size() >= 1)
                    numOfHits += 1;
                else { // we missed this span
                    numOfHits += ng.getTags().size(); // = 0 here
                    result.addMissedNgram(ng);
                }
            }

            if (isDoubleLabeledNgram(ng)) {
                numOfLabels += 2;
                // ng should have at least 2 tags
                if (ng.getTags().size() >= 2)
                    numOfHits += 2;
                else { // we missed this span
                    numOfHits += ng.getTags().size(); // < 2 here
                    result.addMissedNgram(ng);
                }
            }

            if (isTripleLabeledNgram(ng)) {
                numOfLabels += 3;
                // ng should have at least 3 tags
                if (ng.getTags().size() >= 3)
                    numOfHits += 3;
                else { // we missed this span
                    numOfHits += ng.getTags().size(); // < 3 here
                    result.addMissedNgram(ng);
                }
            }
        }
        result.setNumOfLabels(numOfLabels);
        result.setNumOfHits(numOfHits);

        return result;
    }

    private boolean isLabeledNgram(Ngram s) {
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
     * Ngram ng should be a labeled span.
     * @param ng
     * @return
     */
    private boolean isDoubleLabeledNgram(Ngram ng) {
        return !isTripleLabeledNgram(ng) && ng.getText().startsWith(DOUBLE_LABEL)
                && ng.getText().endsWith(DOUBLE_LABEL);
    }

    private boolean isTripleLabeledNgram(Ngram ng) {
        return ng.getText().startsWith(TRIPLE_LABEL) && ng.getText().endsWith(TRIPLE_LABEL);
    }

    /**
     * Ngram ng should be a labeled span.
     * @param ng
     * @return
     */
    private boolean isSingleLabeledNgram(Ngram ng) {
        return !isDoubleLabeledNgram(ng) && !isTripleLabeledNgram(ng)&& ng.getText().startsWith(SINGLE_LABEL)
                && ng.getText().endsWith(SINGLE_LABEL);
    }

    public PrecisionlResult getPrecision(NgramDocument sd) {
        float numOfLabels = 0;
        float numOfImpreciseTags = 0;
        PrecisionlResult result = new PrecisionlResult();

        for (Ngram ng : sd.getAllNgrams()) {
            if (!isLabeledNgram(ng)) {
                if (ng.getTags().size() > 0) {
                    // here we have problems with precision,
                    // since the ngram has tags however the ngram is not labeled.
                    numOfImpreciseTags += ng.getTags().size();
                    result.addImpreciseNgram(ng);
                }
                continue;
            }

            // now we deal with labeled ngrams.
            if (isSingleLabeledNgram(ng)) {
                numOfLabels += 1;
                // do we have more than one tag? if yes, the span is imprecise
                if (ng.getTags().size() > 1) {
                    numOfImpreciseTags += ng.getTags().size() - 1; // all but one tag are considered imprecise
                    result.addImpreciseNgram(ng);
                }
            }

            if (isDoubleLabeledNgram(ng)) {
                numOfLabels += 2;
                // do we have more than two tags? if yes, the span is imprecise
                if (ng.getTags().size() > 2) {
                    numOfImpreciseTags += ng.getTags().size() - 2; // all but two tags are considered imprecise
                    result.addImpreciseNgram(ng);
                }
            }

            if (isTripleLabeledNgram(ng)) {
                numOfLabels += 3;
                // do we have more than three tags? if yes, the span is imprecise
                if (ng.getTags().size() > 3) {
                    numOfImpreciseTags += ng.getTags().size() - 3; // all but three tags are considered imprecise
                    result.addImpreciseNgram(ng);
                }
            }
        }

        result.setNumOfLabels(numOfLabels);
        result.setNumOfImpreciseTags(numOfImpreciseTags);

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
        private float numOfLabels, numOfHits;

        public float getNumOfLabels() {
            return numOfLabels;
        }

        public void setNumOfLabels(float numOfLabels) {
            this.numOfLabels = numOfLabels;
        }

        public float getNumOfHits() {
            return numOfHits;
        }

        public void setNumOfHits(float numOfHits) {
            this.numOfHits = numOfHits;
        }

        private List<Ngram> missedNgrams = new ArrayList<>();

        public void addMissedNgram(Ngram s) {
            missedNgrams.add(s);
        }

        public void printReport() {
            System.out.println("Recall " + getRecall() + " (" + numOfHits + "/" + numOfLabels + ")");
            for (Ngram s : missedNgrams) {
                System.out.println("Missed ngram:");
                System.out.println(s);
            }
        }

        public float getRecall() {
            // in case of no labeled spans we return 100% recall
            if (numOfLabels == 0)
                return 1;
            else
                return numOfHits / numOfLabels;
        }
    }

    public class PrecisionlResult {
        private float numOfLabels, numOfImpreciseTags;

        public float getNumOfLabels() {
            return numOfLabels;
        }

        public void setNumOfLabels(float numOfLabels) {
            this.numOfLabels = numOfLabels;
        }

        public float getNumOfImpreciseTags() {
            return numOfImpreciseTags;
        }

        public void setNumOfImpreciseTags(float numOfImpreciseTags) {
            this.numOfImpreciseTags = numOfImpreciseTags;
        }

        private List<Ngram> impreciseNgrams = new ArrayList<>();

        public void addImpreciseNgram(Ngram s) {
            impreciseNgrams.add(s);
        }

        public void printReport() {
            System.out.println("Precision " + getPrecision() + " (" + numOfLabels + "/(" + numOfLabels + "+" + numOfImpreciseTags + "))");
            for (Ngram s : impreciseNgrams) {
                System.out.println("Imprecise span:");
                System.out.println(s);
            }
        }

        public float getPrecision() {
            return numOfLabels / (numOfLabels + numOfImpreciseTags);
        }
    }


}
