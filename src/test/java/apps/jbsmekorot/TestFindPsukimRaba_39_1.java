package apps.jbsmekorot;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import jngram.NgramDocument;
import jngram.manipulations.MergeSiblingSpans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by omishali on 06/09/2017.
 */
public class TestFindPsukimRaba_39_1 {
    NgramDocument doc;
    private String text = "ויאמר ה' אל אברם לך לך מארצך וגו' (בראשית יב, א), רבי יצחק פתח (תהלים מה, יא): שמעי בת וראי והטי אזנך ושכחי עמך ובית אביך, אמר רבי יצחק משל לאחד שהיה עובר ממקום למקום, וראה בירה אחת דולקת, אמר תאמר שהבירה הזו בלא מנהיג, הציץ עליו בעל הבירה, אמר לו אני הוא בעל הבירה. כך לפי שהיה אבינו אברהם אומר תאמר שהעולם הזה בלא מנהיג, הציץ עליו הקדוש ברוך הוא ואמר לו אני הוא בעל העולם. (תהלים מה, יב): ויתאו המלך יפיך כי הוא אדניך. ויתאו המלך יפיך, ליפותך בעולם, (תהלים מה, יב): והשתחוי לו, הוי ויאמר ה' אל אברם.";

    @Before
    public void before() {
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(101, doc.getWords().size());
    }

    @Test
    public void testTagsSpansLength2() {
        doc.format(new JbsNgramFormatter());
        doc.add(new PsukimTagger()).tag();

        assertTrue(doc.getNgram(0, 1).getSortedTags().contains("jbr:text-tanach-1-12-1"));
        assertTrue(doc.getNgram(1, 2).getSortedTags().contains("jbr:text-tanach-1-12-1"));
        assertTrue(doc.getNgram(2, 3).getSortedTags().contains("jbr:text-tanach-1-12-1"));
        assertTrue(doc.getNgram(3, 4).getSortedTags().contains("jbr:text-tanach-1-12-1"));
        assertTrue(doc.getNgram(4, 5).getSortedTags().contains("jbr:text-tanach-1-12-1"));
        assertTrue(doc.getNgram(5, 6).getSortedTags().contains("jbr:text-tanach-1-12-1"));
    }

    @Test
    public void testTagsAfterMerge() {
        doc.format(new JbsNgramFormatter());
        doc.add(new PsukimTagger()).tag();
        doc.add(new MergeSiblingSpans()).manipulate();

        //assertTrue(doc.getNgram(23, 24).getSortedTags().contains("jbr:text-tanach-1-22-2"));
        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getNgram(68, 76).getSortedTags());
    }

    @Test @Ignore
    public void testFinal() {
        JbsMekorot.findPsukim(doc);

        // span2
        assertEquals(getList("jbr:text-tanach-27-45-12"), doc.getNgram(94, 95).getSortedTags());

        // span3
        assertEquals(getList("jbr:text-tanach-27-45-12"), doc.getNgram(86, 88).getSortedTags());

        // span4
        assertEquals(getList("jbr:text-tanach-1-12-1"), doc.getNgram(97, 100).getSortedTags());

        // span6
        assertEquals(getList("jbr:text-tanach-27-45-12"), doc.getNgram(80, 85).getSortedTags());

        // span7
        assertEquals(getList("jbr:text-tanach-1-12-1"), doc.getNgram(0, 6).getSortedTags());

        // span9
        assertEquals(getList("jbr:text-tanach-27-45-11"), doc.getNgram(17, 25).getSortedTags());
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
    private List<String> getEmptyList() {
        return new ArrayList<>();
    }




}
