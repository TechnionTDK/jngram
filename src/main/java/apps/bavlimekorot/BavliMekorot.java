package apps.bavlimekorot;

import jngram.NgramDocument;
import jngram.manipulations.MergeToMaximalNgrams;
import jngram.manipulations.RemoveTagsInContainedNgrams;
import jngram.io.Subject;


public class BavliMekorot {

    public static final int MINIMAL_NGRAM_LENGTH = 3;
    public static final int MAXIMAL_NGRAM_LENGTH = 100;
    private static final int MAXIMAL_HOLE_SIZE = 3;
    private static int MINIMAL_QUOTE_LENGTH = 4;

    public BavliMekorot() {}

    public static NgramDocument findBavliMekorot(String text) {
        // just in case the given text is smaller than MINIMAL_QUOTE_LENGTH...
        MINIMAL_QUOTE_LENGTH = Math.min(MINIMAL_QUOTE_LENGTH, text.split(" ").length - 1);

        NgramDocument doc = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
        primaryManipulations(doc, MINIMAL_QUOTE_LENGTH, true);

//        // strategy to handle differences in non-ehevi chars
//        if(doc.getAllNgramsWithTags().isEmpty()) {
//            doc = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
//            primaryManipulations(doc, MINIMAL_QUOTE_LENGTH * 2, false);
//        }
        return doc;
    }

    public static NgramDocument findBavliMekorot(Subject s) {
        String text = s.getText();
        String uri = s.getUri();
        if (text != null && uri != null) {
            return findBavliMekorot(text);
        } else {
            System.out.println("Subject " + s.getUri() + " has not text or uri");
            return null;
        }
    }


    public static NgramDocument primaryManipulations(NgramDocument doc, int minimalQuoteLength,  boolean removeNonEheviMode) {
        doc.format(new BavliNgramFormatter());
        doc.add(new BavliTagger(MINIMAL_NGRAM_LENGTH));
        doc.add(new EliminateRashiTosafotRashbam());
        doc.add(new MergeToMaximalNgrams());
        doc.add(new RemoveTagsInContainedNgrams());
        doc.add(new FinalMergeTags(MAXIMAL_HOLE_SIZE));
        if(removeNonEheviMode) {
            doc.add(new BavliRemoveNonEhevi());
        }
        doc.add(new RemoveLowLengthMatches(minimalQuoteLength));
        doc.add(new RemoveMarginalLengthTagsIfManyMatches(minimalQuoteLength * 2));
        doc.add(new RemoveMatchBlankMatchTags(MINIMAL_NGRAM_LENGTH));
        return doc;
    }
}
