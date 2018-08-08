package apps.bavlimekorot.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;
import java.util.Iterator;

public class EliminateDuplicateNgrams extends NgramDocumentManipulation {

    public EliminateDuplicateNgrams() {}

    public void manipulate(NgramDocument doc) {
        HashSet<String> seenTags = new HashSet<>();
        List<String> correctTags;
        Iterator iter = doc.getAllNgrams().iterator();
        while(true) {
            Ngram ngram;
            do {
                if (!iter.hasNext()) {
                    return;
                }

                ngram = (Ngram)iter.next();
            } while(ngram.getTags().size() == 0);
            for(String tag : ngram.getTags()) {
                seenTags.add(tag);
            }
            correctTags = new ArrayList<>(seenTags);
            ngram.clearTags();
            ngram.addTags(correctTags);
            seenTags.clear();
        }
    }

}
