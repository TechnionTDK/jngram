package apps.jbsmekorot;

import org.junit.Test;
import jngram.NgramDocument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * Created by omishali on 08/01/2018.
 */
public class TestPsukimTagger {
    NgramDocument doc;

    @Test
    public void testFuzzyBug() {
        // we have detected pasuk jbr:text-tanach-14-22-24 for this text
        // and it is wrong (result of fuzzy search) although we expect
        // the fuzzy search not to detect this pasuk.
        String text = "בגן עדן. אמר לו,";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertThat(doc.getNgram(0,3).getTags(), not(contains("jbr:text-tanach-14-22-24")));

        // Fixed: bug was in max edit: original text was used
        // instead of formatted text.
    }

    @Test
    public void test_getMaxEdits() {
        // we have detected pasuk jbr:text-tanach-14-22-24 for this text
        // and it is wrong (result of fuzzy search).
        String text = "אאא אאאא. אאאאא לו,";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);
        PsukimTagger tagger = new PsukimTagger();
        // max edits should use formatted text and not original text.
        assertThat(tagger.getMaxEdits(doc.getNgram(0,3)), contains(1, 1, 2, 1));
    }

    /**
     * Returns a list, sorted.
     * @param args
     * @return
     */
    private List<String> getList(String... args) {
        List<String> result = Arrays.asList(args);
        Collections.sort(result);
        return result;
    }
}
