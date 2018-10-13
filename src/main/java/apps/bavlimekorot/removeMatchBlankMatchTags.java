package apps.bavlimekorot;

import jngram.DocumentException;
import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;
import java.util.List;

/*
This manipulation deals with the following (pretty common) error of the algorihm:
Assume the 3 word quote: "aaa bbb ccc" appears in the Talmud Bavli.
Now assume a jewish source has a text that says: "aaa bbb ccc @#$#@$GDFBCGHXVvcxvxft%$#% aaa bbb ccc".
Our algirhm will recognize the 2 edges containing "aaa bbb ccc" as quotes from the Talmud, and then
connect them. We would like to avoid such errors, so we remove all tags from such matches
 */
public class removeMatchBlankMatchTags extends NgramDocumentManipulation {

    private static int MINIMAL_NGRAM_LENGTH;

    public removeMatchBlankMatchTags(int minimalNgramLength) {
        MINIMAL_NGRAM_LENGTH = minimalNgramLength;
    }

    public void manipulate(NgramDocument doc) {
        Ngram startNg, endNg;
        List<Ngram> taggedNgrams = doc.getAllNgramsWithTags();
        for(int i=MINIMAL_NGRAM_LENGTH; i<=MINIMAL_NGRAM_LENGTH+2; i++) {
            for(Ngram ngram : taggedNgrams) {
                try {
                    startNg = doc.getNgram(ngram.getStart(), ngram.getStart() + i - 1);
                    endNg = doc.getNgram(ngram.getEnd() - i + 1, ngram.getEnd());
                    if(startNg.getTextFormatted().equals(endNg.getTextFormatted())) {
                        ngram.clearTags();
                    }
                }
                catch(DocumentException de) {
                    continue;
                }
            }
        }
    }

}
