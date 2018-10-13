package apps.bavlimekorot;

import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;

import java.util.Iterator;

public class removeMarginalLengthMatches extends NgramDocumentManipulation {

    private static int THRESHOLD;

    public removeMarginalLengthMatches(int threshold) {
        THRESHOLD = threshold;
    }

    /*
    This manipulation is used to eliminate matches of reasonable length, say of 5 or 6 words, that alghouth are long enough
    to be considered reasonable are still noisy. for example let's take "אמר רבי שמעון בן לקיש", which is a 5 word wuote
    with more than 30 matches. To handle such cases, we simply take all quotes of medium length that has more than 1 tag,
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
            if(ngram.size() <= THRESHOLD && ngram.getTags().size() >= 2) {
                ngram.clearTags();
            }
        }
    }

}
