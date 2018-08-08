package apps.bavlimekorot;

import jngram.DocumentException;
import org.apache.commons.collections4.CollectionUtils;
import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;
import java.util.Iterator;
import java.util.List;

public class FinalMergeTags extends NgramDocumentManipulation {

    private static int MAXIMUM_HOLE_SIZE;

    public FinalMergeTags(int maximumHoleSize) {
        MAXIMUM_HOLE_SIZE = maximumHoleSize;
    }

    public void manipulate(NgramDocument doc) {
        Iterator iter = doc.getAllNgrams().iterator();
        int mergedStart, mergedEnd;
        while(true) {
            Ngram ngram;
            do {
                if (!iter.hasNext()) {
                    return;
                }

                ngram = (Ngram)iter.next();
            } while(ngram.getTags().size() == 0);
            for(Ngram adjNg : doc.getAdjacentNgrams(ngram, MAXIMUM_HOLE_SIZE)) {
                List<String> sharedTags = (List)CollectionUtils.intersection(ngram.getTags(), adjNg.getTags());
                if(sharedTags.isEmpty()) {
                    continue;
                }
                mergedStart = Math.min(ngram.getStart(), adjNg.getStart());
                mergedEnd = Math.max(ngram.getEnd(), adjNg.getEnd());
                try {
                    Ngram mergedNg = doc.getNgram(mergedStart, mergedEnd);
                    mergedNg.addTags(sharedTags);
                }
                catch(DocumentException de) {
                    continue;
                }
            }
        }
    }
}
