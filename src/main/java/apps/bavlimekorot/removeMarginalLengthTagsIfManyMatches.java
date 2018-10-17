package apps.bavlimekorot;

import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;

import java.util.Iterator;

public class removeMarginalLengthTagsIfManyMatches extends NgramDocumentManipulation {

    private static int LENGTH_THRESHOLD;
    private static final int MATCHES_THRESHOLD = 2;

    public removeMarginalLengthTagsIfManyMatches(int threshold) {
        LENGTH_THRESHOLD = threshold;
    }

    /*
    This manipulation is used to eliminate matches of reasonable length, say of 5 to 8 words, that although are long enough
    to be considered reasonable are still noisy. for example let's take "אמר רבי שמעון בן לקיש", which is a 5 word quote
    with more than 30 matches. To handle such cases, we simply take all quotes of medium length that has more than MATCHES_THRESHOLD tags,
    and remove all its tags. the 1 correct tag probably was merged already into a longer ngram.
     */
    public void manipulate(NgramDocument doc) {
        Iterator iter = doc.getAllNgrams().iterator();
        while(true) {
            Ngram ngram;
            do {
                if (!iter.hasNext()) {
                    return;
                }

                ngram = (Ngram)iter.next();
            } while(ngram.getTags().size() == 0);
            if(ngram.size() <= LENGTH_THRESHOLD && ngram.getTags().size() >= MATCHES_THRESHOLD) {
                ngram.clearTags();
            }
        }
    }

}
