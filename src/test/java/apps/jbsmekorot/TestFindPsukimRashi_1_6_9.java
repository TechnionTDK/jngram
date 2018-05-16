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
public class TestFindPsukimRashi_1_6_9 {
    NgramDocument doc;
    private String text = "רש\"י אלה תולדת נח נח איש צדיק. הואיל והזכירו, סיפר בשבחו, (א) שנאמר זכר צדיק לברכה (משלי י, ז.). דבר אחר, ללמדך שעיקר תולדותיהם (ב) של צדיקים מעשים טובים בדורותיו. יש מרבותינו דורשים אותו לשבח, כל שכן שאילו היה בדור צדיקים היה צדיק יותר, ויש שדורשים אותו לגנאי, (ג) לפי דורו היה צדיק, ואילו היה בדורו של אברהם לא היה נחשב לכלום (סנהדרין קח.): את האלהים התהלך נח. ובאברהם (ד) הוא אומר אשר התהלכתי לפניו (בראשית כד, מ.), נח היה צריך סעד לתומכו, אבל אברהם היה מתחזק ומהלך בצדקו מאליו: התהלך. לשון עבר, (ה) וזהו שמושו של ל' בלשון (ו) כבד משמשת להבא ולשעבר בלשון אחד, קום התהלך (שם יג, יז.), להבא. התהלך נח, לשעבר. התפלל בעד עבדיך (שמואל-א יב, יט.), להבא. ובא והתפלל אל הבית הזה (מלכים-א ח, מב.), לשון עבר, אלא שהוי\"ו שבראשו (ז) הופכו להבא:";

    @Before
    public void before() {
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(138, doc.getWords().size());
    }

    @Test
    public void testTagsSpansLength2() {
        doc.format(new JbsNgramFormatter());
        doc.add(new PsukimTagger()).tag();
        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getNgram(68, 69).getSortedTags());
    }

    @Test
    public void testTagsAfterMerge() {
        doc.format(new JbsNgramFormatter());
        doc.add(new PsukimTagger()).tag();
        doc.add(new MergeSiblingSpans()).manipulate();

        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getNgram(68, 76).getSortedTags());
    }

    @Test @Ignore
    public void testFinal() {
        JbsMekorot.findPsukim(doc);

        //span2
        assertEquals(getList("jbr:text-tanach-1-6-9"), doc.getNgram(112, 113).getSortedTags()); // tests FilterTagsFromSpansSize2!
        assertEquals(getEmptyList(), doc.getNgram(106, 107).getSortedTags());
        // span3
        assertEquals(getList("jbr:text-tanach-28-10-7"), doc.getNgram(13, 15).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-24-40"), doc.getNgram(72, 74).getSortedTags());
        assertEquals(getList("jbr:text-tanach-8-12-19"), doc.getNgram(115, 117).getSortedTags());

        // span4
        assertEquals(getList("jbr:text-tanach-1-6-9"), doc.getNgram(64, 67).getSortedTags());

        // span5
        assertEquals(getList("jbr:text-tanach-10-8-42"), doc.getNgram(122, 126).getSortedTags());

        // span6
        assertEquals(getList("jbr:text-tanach-10-8-42"), doc.getNgram(122, 126).getSortedTags());
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
