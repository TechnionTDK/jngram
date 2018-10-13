package apps.bavlimekorot;

import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;
import java.util.Iterator;

public class removeLowLengthMatches extends NgramDocumentManipulation {

    private static int QUOTE_PROBABLY_NOISY_THRESHOLD;

    public removeLowLengthMatches(int threshold) {
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
