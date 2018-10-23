package apps.bavlimekorot;

import jngram.NgramDocument;
import jngram.manipulations.MergeToMaximalNgrams;
import jngram.manipulations.RemoveTagsInContainedNgrams;
import jngram.io.Subject;


public class BavliMekorot {

    public static final int MINIMAL_NGRAM_LENGTH = 3;
    public static final int MAXIMAL_NGRAM_LENGTH = 50;
    private static final int MAXIMAL_HOLE_SIZE = 3;
    private static int QUOTE_PROBABLY_TOO_SHORT_VALUE = 4;
    private static final int NUM_MATCHES_THRESHOLD_FOR_MARGINAL_LENGTH_QUOTES = 4;


    public BavliMekorot() {}

    public static NgramDocument findSubjectMekorot(String text) {
        QUOTE_PROBABLY_TOO_SHORT_VALUE = Math.min(QUOTE_PROBABLY_TOO_SHORT_VALUE, text.split(" ").length - 1);
        NgramDocument doc = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
        primaryManipulations(doc, QUOTE_PROBABLY_TOO_SHORT_VALUE, true);
//        if(doc.getAllNgramsWithTags().isEmpty()) {
//            doc = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
//            primaryManipulations(doc, quoteProbablyNoisyThreshold * 2, false);
//        }
        return doc;
    }

    public static NgramDocument findSubjectMekorot(Subject s) {
        String text = s.getText();
        String uri = s.getUri();
        if (text != null && uri != null) {
            return findSubjectMekorot(text);
        } else {
            System.out.println("Subject " + s.getUri() + " has not text or uri");
            return null;
        }
    }


    public static NgramDocument primaryManipulations(NgramDocument doc, int quoteNoisyThreshold,  boolean removeNonEheviMode) {
        doc.format(new BavliNgramFormatter());
        doc.add(new BavliTagger(MINIMAL_NGRAM_LENGTH));
        doc.add(new EliminateRashiTosafotRashbam());
        doc.add(new MergeToMaximalNgrams());
        doc.add(new FinalMergeTags(MAXIMAL_HOLE_SIZE));
        doc.add(new RemoveTagsInContainedNgrams());
//        if(removeNonEheviMode) {
            // BavliRemoveNonEhevy should be moved here in case the mechanism of removeNonEhevyMode is being uncommented.
//        }
        doc.add(new BavliRemoveNonEhevi());
        doc.add(new removeLowLengthMatches(quoteNoisyThreshold));
        doc.add(new removeMarginalLengthTagsIfManyMatches(quoteNoisyThreshold * 2, NUM_MATCHES_THRESHOLD_FOR_MARGINAL_LENGTH_QUOTES));
        doc.add(new removeMatchBlankMatchTags(MINIMAL_NGRAM_LENGTH));
        return doc;
    }
}
