package apps.bavlimekorot;

import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EliminateRashiTosafotRashbam extends NgramDocumentManipulation {

    public EliminateRashiTosafotRashbam() {}

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

            ArrayList<String> tagsToRemove = new ArrayList<>();
            for(String tag : ngram.getTags()) {
                boolean isTagRashi = tag.startsWith("jbr:text-bavli-rashi");
                boolean isTagTosafot = tag.startsWith("jbr:text-bavli-tosafot");
                boolean isTagRashbam = tag.startsWith("jbr:text-bavli-rashbam");
                if(isTagRashi || isTagTosafot || isTagRashbam) {
                    tagsToRemove.add(tag);
                }
            }
            ngram.removeTags(tagsToRemove);
        }
    }

}
