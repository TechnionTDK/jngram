package apps.jbsmekorot;

import apps.jbsmekorot.manipulations.AddTextWithShemAdnut;
import apps.jbsmekorot.manipulations.CalcAndFilterByEditDistance;
import apps.jbsmekorot.manipulations.MarkCertainByHintWords;
import jngram.NgramManipulation;
import org.junit.*;
import jngram.NgramDocument;
import jngram.Ngram;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by omishali on 08/01/2018.
 */
public class TestJbsManipulations {
    NgramDocument doc;

    @Test
    public void testShemAdnutManipulation() {
        String text = "כי מקרא מלא שהביאוהו זכרונם לברכה לראיתם (ישעיה ט, טז) על כן על בחוריו לא ישמח ה' וגו'";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertEquals("ישמח אדני", doc.getNgram(15, 16).getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT));
        assertEquals(getList("jbr:text-tanach-12-9-16"), doc.getNgram(10, 16).getSortedTags());
        //test is failed since we put extra in span 2 and thus removenonsequential removes the tag!
    }

    @Test
    public void testFilterTagsFromSpansManipulation() {
        String text = "בראשית ברא אלקים את השמים ואת הארץ 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 בראשית ברא אלקים";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertEquals(getList("jbr:text-tanach-1-1-1"), doc.getNgram(0, 6).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-1-1"), doc.getNgram(27, 29).getSortedTags());
    }

    @Test
    public void testRemoveNonSequentialTags() {
        String text = "להבדיל בין הטמא ובין הטהור";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        // without the manipulation, we should detect here two psukim:
        // jbr:text-tanach-3-10-10, jbr:text-tanach-3-11-47
        // the manipulation should remove jbr:text-tanach-3-10-10
        assertEquals(getList("jbr:text-tanach-3-11-47"), doc.getNgram(0, 4).getSortedTags());
    }

    @Test
    public void testRemoveTagsFromOverlappingSpans() {
        String text = "ורם לבבך ושכחת את ה' אלקיך על ";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        // without the manipulation, we should also detect a pasuk for (3,6)
        assertEquals(0, doc.getNgram(3, 6).getSortedTags().size());
    }

    @Test
    public void testCalcAndFilterByEditDistance1() {
        String text = "כל העולם על המעלות";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertEquals(getList(), doc.getNgram(0, 3).getSortedTags());

        Ngram s = doc.getNgram(0, 3);
        assertEquals(new Integer(6), CalcAndFilterByEditDistance.getDistance(s, "jbr:text-tanach-13-1-10"));

        text = "ואת אשת רעהו לא טימא";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertEquals(getList("jbr:text-tanach-14-18-6"), doc.getNgram(0, 4).getSortedTags());

        s = doc.getNgram(0, 4);
        assertEquals(new Integer(1), CalcAndFilterByEditDistance.getDistance(s, "jbr:text-tanach-14-18-6"));
        assertEquals(new Integer(2), CalcAndFilterByEditDistance.getDistance(s, "jbr:text-tanach-14-18-15"));
    }

    @Test
    public void testCalcAndFilterByEditDistance2() {
        String text = "%לא מעבר לים היא% באותם שהולכים %מעבר לים% בסחורה";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertEquals(getList("jbr:text-tanach-5-30-13"), doc.getNgram(0, 3).getSortedTags());
        assertEquals(getList("jbr:text-tanach-5-30-13"), doc.getNgram(6, 7).getSortedTags());

        Ngram s = doc.getNgram(0, 3);
        assertEquals(new Integer(2), CalcAndFilterByEditDistance.getDistance(s, "jbr:text-tanach-5-30-13"));
        s = doc.getNgram(6, 7);
        assertEquals(new Integer(0), CalcAndFilterByEditDistance.getDistance(s, "jbr:text-tanach-5-30-13"));
    }

    @Test
    public void testCalcAndFilterByEditDistance3() {
        String text = "%ואת אשת רעהו לא טימא%";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        // without the manipulation we should get also "jbr:text-tanach-14-18-15"
        // however it should be filtered since its distance is higher than "jbr:text-tanach-14-18-6"
        assertEquals(getList("jbr:text-tanach-14-18-6"), doc.getNgram(0, 4).getSortedTags());

        text = "%ולא תשא עליו חטא.%";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);
        assertEquals(getList("jbr:text-tanach-3-19-17"), doc.getNgram(0, 3).getSortedTags());

        text = "לא תקרבו לגלות ערוה.";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);
        assertEquals(getList("jbr:text-tanach-3-18-6"), doc.getNgram(0, 3).getSortedTags());
        assertEquals(getList("jbr:text-tanach-3-18-6"), doc.getNgram(0, 3).getSortedTags());
    }

    @Test
    public void testFilterTagsFromSpans() {
        // we expect the last bigram to be found but it doesn't.
        String text = "ולפני עור לא תתן מכשול,% לפני סומא בדבר. אמר לך, בת פלוני מהי לכהונה? אל תאמר לו, כשרה, והיא אינה אלא פסולה. היה נוטל בך עצה, אל תתן לו עצה שאינה הוגנת לו וכו', ואל תאמר לו מכור שדך וקח לך חמור, ואתה עוקף עליו ונוטלה ממנו. שמא תאמר, עצה יפה אני נותן לו, הרי הדבר מסור ללב, שנאמר, %ויראת מאלקיך.%";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertEquals(getList("jbr:text-tanach-3-19-14"), doc.getNgram(0, 4).getSortedTags());

        Ngram s = doc.getNgram(59, 60);
        assertEquals("%ויראת מאלקיך.%", s.getText());
        assertEquals("ויראת מאלהיך", s.getTextFormatted());
        assertEquals(getList("jbr:text-tanach-3-19-14"), doc.getNgram(59, 60).getSortedTags());

        text = "המצות המוטלות על כל ישראל כבר ידועות";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);
        assertEquals(getList(), doc.getNgram(2, 4).getSortedTags());
    }

    @Test
    public void testRemoveNonEheviFuzzyMatches() {
        String text = "הבל ואין בו מועיל";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        // this pasuk contains "bam" instead of "bo", so we should filter it out since
        // the letter "vav" is replaced with "mem"
        assertTrue(!doc.getNgram(0,3).getTags().contains("jbr:text-tanach-13-16-19"));

        text = "אם קטן ואם גדול";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        // this pasuk contains "im" instead of "et", so we should filter it out since
        // the letter "mem" is replaced with "tav"
        assertTrue(!doc.getNgram(0,3).getTags().contains("jbr:text-tanach-10-22-31"));


        text = "על כל איש ישראל";
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        // this pasuk contains "al" instead of "el", so we should filter it out since
        // the letter "ayin" is replaced with "alef"
        assertTrue(!doc.getNgram(0,3).getTags().contains("jbr:text-tanach-5-27-14"));
        assertTrue(!doc.getNgram(0,3).getTags().contains("jbr:text-tanach-6-10-24"));
    }

    @Test
    public void testMarkCertainByHintWords() {
        MarkCertainByHintWords m = new MarkCertainByHintWords();
        String before = "וכן אמרו זכרונם לברכה (אבות ו, ג) אין כבוד אלא תורה, שנאמר (משלי ג, לה)";
        assertEquals("וכן אמרו זכרונם לברכה  אין כבוד אלא תורה, שנאמר ", m.removeReferences(before));

        before = "ויאמרו על כל זה (פסחים נ, ב), יש זריז ונשכר";
        assertEquals("ויאמרו על כל זה , יש זריז ונשכר", m.removeReferences(before));
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
