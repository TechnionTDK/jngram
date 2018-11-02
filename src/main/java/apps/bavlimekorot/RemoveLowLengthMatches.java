package apps.bavlimekorot;

import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;
import java.util.Iterator;

/**
 * This manipulation deals with very short quotes (3 or 4 words long or so) that are probably noise.
 * The Bavli quotation recognition algorithm assumes that n-grams this short are simply too unreliable
 * to take into account. Thus, we simply remove all tags from n-grams with length of up to
 * and including QUOTE_PROBABLY_NOISY_THRESHOLD
 */
public class RemoveLowLengthMatches extends NgramDocumentManipulation {

    private static int QUOTE_PROBABLY_NOISY_THRESHOLD;

    public RemoveLowLengthMatches(int threshold) {
        QUOTE_PROBABLY_NOISY_THRESHOLD = threshold;
    }

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
            if(ngram.size() <= QUOTE_PROBABLY_NOISY_THRESHOLD) {
                ngram.clearTags();
            }
        }
    }
}
