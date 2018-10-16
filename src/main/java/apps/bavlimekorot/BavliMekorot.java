package apps.bavlimekorot;

import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.manipulations.ResolveOverlappingNgramsWithDifferentTags;
import jngram.NgramDocument;
import jngram.io.TaggedSubject;
import jngram.manipulations.MergeToMaximalNgrams;
import jngram.manipulations.RemoveTagsInContainedNgrams;
import java.util.List;
import jngram.io.Tag;
import jngram.io.Subject;
import java.io.*;


public class BavliMekorot {
    public static final int MINIMAL_NGRAM_LENGTH = 3;
    public static int MAXIMAL_NGRAM_LENGTH = 100;
    private static final int MAXIMAL_HOLE_SIZE = 3;
    private static int quoteProbablyNoisyThreshold = 4;

    public BavliMekorot() {}

    public static List<Tag> findTextMekorot(String text, boolean printMatches) {
        quoteProbablyNoisyThreshold = Math.min(quoteProbablyNoisyThreshold, text.split(" ").length - 1);
        MAXIMAL_NGRAM_LENGTH = Math.max(MAXIMAL_NGRAM_LENGTH, text.split("\\s+").length / 10);
        NgramDocument doc = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
        primaryManipulations(doc, quoteProbablyNoisyThreshold, true);
        if(doc.getAllNgramsWithTags().isEmpty()) {
            doc = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
            primaryManipulations(doc, quoteProbablyNoisyThreshold * 4, false);
        }
        TaggedSubject taggedSubject = JbsMekorot.getTaggedSubject(doc, text);
        if(printMatches) {
            printTaggedSubject(taggedSubject);
        }
        return taggedSubject.getTags();
    }

    public static NgramDocument findSubjectMekorot(Subject s) {
        String text = s.getText();
        String uri = s.getUri();
        MAXIMAL_NGRAM_LENGTH = Math.max(MAXIMAL_NGRAM_LENGTH, text.split("\\s+").length / 10);
        if (text != null && uri != null) {
            NgramDocument document = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
            primaryManipulations(document, quoteProbablyNoisyThreshold, true);
            if(document.getAllNgramsWithTags().isEmpty()) {
                document = new NgramDocument(text, MINIMAL_NGRAM_LENGTH, MAXIMAL_NGRAM_LENGTH);
                primaryManipulations(document, quoteProbablyNoisyThreshold * 2, false);
            }
            return document;
        } else {
            System.out.println("Subject " + s.getUri() + " has not text or uri");
            return null;
        }
    }

    public static void main(String[] args) {
        if(args[0].equals("-text") && args.length == 2) {
            try {
                File file = new File(args[1]);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String text = "";
                String line;
                while ((line = br.readLine()) != null) {
                    text = text + " " + line;
                }
                findTextMekorot(text, true);
            }
            catch(FileNotFoundException ex) {
                System.out.println("File not found");
                System.exit(1);
            }
            catch(IOException ex) {
                System.out.println("Couldn't read file properly");
                System.exit(1);
            }
        }
        else {
            System.out.println("Wrong input. Program arguments should be in the form of -text \"text\"");
            System.exit(-1);
        }
        return;
    }

    public static void printTaggedSubject(TaggedSubject t) {
        List<Tag> allTags = t.getTags();
        int howManyMatches = allTags.size();
        System.out.println("Found " + howManyMatches + " matches:");
        String currSpan;
        String currUri;
        for(int i=1; i<howManyMatches+1; i++) {
            currSpan = allTags.get(i-1).getSpan();
            currUri = allTags.get(i-1).getUri();
            System.out.println("Match " + i + ": Span: " + currSpan + ", Uri: " + currUri);
        }
    }


    public static NgramDocument primaryManipulations(NgramDocument doc, int quoteNoisyThreshold,  boolean removeNonEheviMode) {
        //Oren, why does the replacements of Bet Shamai and Rabbi in the formatter don't work?
        doc.format(new BavliNgramFormatter());
        //Oren, why does it not recognize the % in a few examples?
        doc.add(new BavliTagger(MINIMAL_NGRAM_LENGTH));
        doc.add(new EliminateRashiTosafotRashbam());
        doc.add(new MergeToMaximalNgrams());
        doc.add(new RemoveTagsInContainedNgrams());
        doc.add(new FinalMergeTags(MAXIMAL_HOLE_SIZE));
        if(removeNonEheviMode) {
            doc.add(new BavliRemoveNonEhevi());
        }
        doc.add(new removeLowLengthMatches(quoteNoisyThreshold));
        doc.add(new removeMarginalLengthMatches(quoteNoisyThreshold * 2));
        doc.add(new removeMatchBlankMatchTags(MINIMAL_NGRAM_LENGTH));
        //Oren, the following manipulation doesn't work when starting with 3-grams.
//        doc.add(new ResolveOverlappingNgramsWithDifferentTags());
        return doc;
    }
}
