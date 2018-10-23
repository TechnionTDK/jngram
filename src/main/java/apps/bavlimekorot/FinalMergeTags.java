package apps.bavlimekorot;

import jngram.DocumentException;
import org.apache.commons.collections4.CollectionUtils;
import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * In order to perform the Bavli quotation recognition task well, it is not enough to merge small overlapping
 * quotes into their maximal counterparts. We all need to merge quotes of the same Bavli chapter that has a few
 * spaces (irrelevant words) between them. So, for example, if the two quotes "a b c" and "d e f" appear in
 * Bavli-1-9-2, and in a jewish source the text "a b c *a few words* d e f" appears, we would like to tag the entire
 * text as a quote from Bavli-1-9-2. This manipulation does exactly that. It allows for MAXIMUM_HOLE_SIZE words
 * between two quotes and, if they have shared tags, merges them so the overarching n-gram contains the shared tags.
 */
public class FinalMergeTags extends NgramDocumentManipulation {

    private static int MAXIMUM_HOLE_SIZE;

    public FinalMergeTags(int maximumHoleSize) {
        MAXIMUM_HOLE_SIZE = maximumHoleSize;
    }

    public void manipulate(NgramDocument doc) {
        Iterator iter = doc.getAllNgrams().iterator();
        int mergedStart, mergedEnd;
        List<String> mergedNgTags, tagsToAdd;
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
                    mergedNgTags = mergedNg.getTags();
                    tagsToAdd = new ArrayList<>(sharedTags);
                    for(String tag : sharedTags) {
                        if(mergedNgTags.contains(tag)) {
                            tagsToAdd.remove(tag);
                        }
                    }
                    if(!tagsToAdd.isEmpty()) {
                        mergedNg.addTags(tagsToAdd);
                        ngram.removeTags(tagsToAdd);
                        adjNg.removeTags(tagsToAdd);
                    }
                }
                catch(DocumentException de) {
                    continue;
                }
            }
        }
    }
}
