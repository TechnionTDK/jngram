package apps.jbsmekorot;

import org.junit.Before;
import org.junit.Test;
import spanthera.SpannedDocument;
import spanthera.manipulations.MergeSiblingSpans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by omishali on 06/09/2017.
 */
public class TestFindPsukimGevurot_8 {
    SpannedDocument doc;
    private String text = "...והסכימו הכל שאברהם לא יפה עשה במה שאמר במה אדע, כי היו רואים שאם נפרש במה אדע באיזה זכות יירש את הארץ, אין התשובה קח לי עגלה משולשת ועז משולשת ואיל משולש יתפרש לפי פשוטו על זאת השאלה, שהוקשה להם למה יירשו את הארץ בזכות הקרבנות והלא לא על דבר זבח צויתי את אבותיכם. ועוד לא מצאנו זה בכתוב שתהא הארץ לישראל כי אם בזכות האבות והשבועה שנשבע להם. ומפני זה פירש רש\"י במה אדע כי אירשנה באיזה זכות יתקיימו ולא יגלו ממנה. אלא שהקשו עליו כי לא יסבול זה לשון הכתוב במה אדע כי אירשנה, דהלשון משמע לשון ירושה דהיינו תחלת הירושה לא הקיום בה: ובפרק בני העיר (מגילה ל\"א ע\"ב) אמר רבי יוסי אלמלא מעמדות לא נתקיימו שמים וארץ, שנאמר ויאמר ה' אלקים במה אדע כי אירשנה אמר אברהם לפני הקדוש ברוך הוא רבונו של עולם שמא בני יהיו חוטאים ואתה עושה להם כאנשי דור המבול ודור הפלגה אמר לו לאו, במה אדע אמר לו קח נא לי עגלה משולשת.";

    @Before
    public void before() {
        doc = new SpannedDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(162, doc.getWords().size());
    }

    @Test
    public void testTagsSpansLength2() {
        doc.format(new JbsSpanFormatter());
        doc.add(new PsukimTagger()).tag();

        // we have difficulties with the commented lines, see daf
        //assertTrue(doc.getSpan(122, 123).getSortedTags().contains("jbr:text-tanach-1-15-8"));
        //assertTrue(doc.getSpan(124, 125).getSortedTags().contains("jbr:text-tanach-1-15-8"));
        assertTrue(doc.getSpan(125, 126).getSortedTags().contains("jbr:text-tanach-1-15-8"));
        assertTrue(doc.getSpan(126, 127).getSortedTags().contains("jbr:text-tanach-1-15-8"));
        assertTrue(doc.getSpan(127, 128).getSortedTags().contains("jbr:text-tanach-1-15-8"));
    }

    @Test
    public void testTagsAfterMerge() {
        doc.format(new JbsSpanFormatter());
        doc.add(new PsukimTagger()).tag();
        doc.add(new MergeSiblingSpans()).manipulate();

        //assertTrue(doc.getSpan(23, 24).getSortedTags().contains("jbr:text-tanach-1-22-2"));
        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(68, 76).getSortedTags());
    }

    @Test
    public void testFinal() {
        JbsMekorot.findPsukim(doc);

        // span2
        assertEquals(getList("jbr:text-tanach-1-15-8"), doc.getSpan(15, 16).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-15-8"), doc.getSpan(153, 154).getSortedTags());

        // span3
        // actually word "ki" does not belong to the context (but belongs to the pasuk...)
        assertEquals(getList("jbr:text-tanach-1-15-8"), doc.getSpan(8, 10).getSortedTags());

        // span4
        assertEquals(getList("jbr:text-tanach-1-15-8"), doc.getSpan(73, 76).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-15-8"), doc.getSpan(92, 95).getSortedTags());
        // here we do not catch the prefix of the pasuk, see daf for details
        assertEquals(getList("jbr:text-tanach-1-15-8"), doc.getSpan(125, 128).getSortedTags());

        // we have more problems here with otiyot ehevi, hoping to provide a solution soon.

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
