package apps.jbsmekorot;

import org.junit.Before;
import org.junit.Test;
import spanthera.SpannedDocument;
import spanthera.manipulations.MergeSiblingSpans;
import spanthera.manipulations.RemoveMatchesInContainedSpans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by omishali on 06/09/2017.
 */
public class TestFindPsukimRaba_39_9 {
    SpannedDocument doc;
    private String text = "אמר רבי לוי שתי פעמים כתיב לך לך, ואין אנו יודעים אי זו חביבה אם השניה אם הראשונה, ממה דכתיב (בראשית כב, ב): אל ארץ המוריה, הוי השניה חביבה מן הראשונה. אמר רבי יוחנן לך לך מארצך, מארפכי שלך. וממולדתך, זו שכונתך. ומבית אביך, זו בית אביך. אל הארץ אשר אראך, ולמה לא גלה לו, כדי לחבבה בעיניו ולתן לו שכר על כל פסיעה ופסיעה, הוא דעתיה דרבי יוחנן, דאמר רבי יוחנן (בראשית כב, ב): ויאמר קח נא את בנך את יחידך, אמר לו זה יחיד לאמו וזה יחיד לאמו. אמר לו אשר אהבת, אמר לו ואית תחומין במעיא. אמר לו את יצחק, ולמה לא גלה לו, כדי לחבבו בעיניו ולתן לו שכר על כל דבור ודבור, דאמר רב הונא משם רבי אליעזר בנו של רבי יוסי הגלילי, משהה הקדוש ברוך הוא ומתלא עיניהם של צדיקים, ואחר כך הוא מגלה להם טעמו של דבר. כך אל הארץ אשר אראך. על אחד ההרים אשר אמר אליך. (יונה ג, ב): וקרא אליה את הקריאה אשר אני דבר אליך. (יחזקאל ג, כב): קום צא אל הבקעה ושם אדבר אותך.";

    @Before
    public void before() {
        doc = new SpannedDocument(text, PsukimTagger.MINIMAL_PASUK_LENGTH, PsukimTagger.MAXIMAL_PASUK_LENGTH);
        doc.add(new PsukimTagger()).tag();
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(176, doc.getWords().size());
    }

    @Test
    public void testTagsSpansLength2() {
        assertTrue(doc.getSpan(23, 24).getSortedTags().contains("jbr:text-tanach-1-22-2"));
    }

    @Test
    public void testTagsAfterMerge() {
        doc.add(new MergeSiblingSpans()).manipulate();

        assertTrue(doc.getSpan(23, 24).getSortedTags().contains("jbr:text-tanach-1-22-2"));
        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(68, 76).getSortedTags());
    }

    @Test
    public void testFinal() {
        doc.add(new MergeSiblingSpans()).manipulate();
        doc.add(new RemoveMatchesInContainedSpans()).manipulate();
        doc.add(new FilterTagsFromSpansSize3(doc)).manipulate();
        doc.add(new FilterTagsFromSpansSize2(doc)).manipulate();

        // span2
        assertEquals(getList("jbr:text-tanach-1-12-1"), doc.getSpan(6, 7).getSortedTags());
        // note: word 25 is also from 1-22-2 but it contains ehevi
        assertEquals(getList("jbr:text-tanach-1-22-2"), doc.getSpan(23, 24).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-12-1"), doc.getSpan(42, 43).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-22-2"), doc.getSpan(92, 93).getSortedTags());
        assertEquals(getList("jbr:text-tanach-19-3-2"), doc.getSpan(164, 165).getSortedTags());
        // note: we currently miss this test, see the daf for details
        //assertEquals(getList("jbr:text-tanach-1-22-2"), doc.getSpan(101, 102).getSortedTags());

        // span3
        assertEquals(getList("jbr:text-tanach-1-12-1"), doc.getSpan(34, 36).getSortedTags());
        assertEquals(getEmptyList(), doc.getSpan(100, 102).getSortedTags());
        assertEquals(getEmptyList(), doc.getSpan(163, 165).getSortedTags());

        // span4
        assertEquals(getList("jbr:text-tanach-1-12-1"), doc.getSpan(145, 148).getSortedTags());
        assertEquals(getEmptyList(), doc.getSpan(162, 165).getSortedTags());

        // span5
        assertEquals(getList("jbr:text-tanach-1-12-1"), doc.getSpan(46, 50).getSortedTags());
        assertEquals(getList("jbr:text-tanach-19-3-2"), doc.getSpan(158, 162).getSortedTags());

        // span6
        assertEquals(getList("jbr:text-tanach-1-22-2"), doc.getSpan(149, 154).getSortedTags());

        // span7
        assertEquals(getList("jbr:text-tanach-1-22-2"), doc.getSpan(75, 81).getSortedTags());
        assertEquals(getList("jbr:text-tanach-14-3-22"), doc.getSpan(169, 175).getSortedTags());
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
