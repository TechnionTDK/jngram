package apps.bavlimekorot;

import jngram.NgramDocument;
import jngram.manipulations.MergeToMaximalNgrams;
import jngram.manipulations.RemoveTagsInContainedNgrams;
import jngram.io.Subject;


public class BavliMekorot {

    public static final int MINIMAL_NGRAM_LENGTH = 3;
    public static int MAXIMAL_NGRAM_LENGTH = 100;
    private static final int MAXIMAL_HOLE_SIZE = 3;
    private static int quoteProbablyNoisyThreshold = 4;

    public BavliMekorot() {}

    public static NgramDocument findTextMekorot(String text) {
        quoteProbablyNoisyThreshold = Math.min(quoteProbablyNoisyThreshold, text.split(" ").length - 1);
        NgramDocument doc = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
        primaryManipulations(doc, quoteProbablyNoisyThreshold, true);
        if(doc.getAllNgramsWithTags().isEmpty()) {
            doc = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
            primaryManipulations(doc, quoteProbablyNoisyThreshold * 2, false);
        }
        return doc;
    }

    public static NgramDocument findSubjectMekorot(Subject s) {
        String text = s.getText();
        String uri = s.getUri();
        if (text != null && uri != null) {
            return findTextMekorot(text);
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
        doc.add(new RemoveTagsInContainedNgrams());
        doc.add(new FinalMergeTags(MAXIMAL_HOLE_SIZE));
        if(removeNonEheviMode) {
            doc.add(new BavliRemoveNonEhevi());
        }
        doc.add(new removeLowLengthMatches(quoteNoisyThreshold));
        doc.add(new removeMarginalLengthTagsIfManyMatches(quoteNoisyThreshold * 2));
        doc.add(new removeMatchBlankMatchTags(MINIMAL_NGRAM_LENGTH));
        return doc;
    }
}
