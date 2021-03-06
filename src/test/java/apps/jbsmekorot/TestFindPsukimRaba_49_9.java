package apps.jbsmekorot;

import jngram.manipulations.MergeToMaximalNgrams;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import jngram.NgramDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by omishali on 06/09/2017.
 */
public class TestFindPsukimRaba_49_9 {
    NgramDocument doc;
    private String text = "חללה לך (בראשית יח, כה), אמר רבי יודן חללה הוא לך בריה הוא לך. אמר רבי אחא חללה חללה שתי פעמים, חלול שם שמים יש בדבר. אמר רבי אבא מעשת דבר אין כתיב כאן, אלא מעשת כדבר, לא היא ולא דכותה, ולא דפחותה מנה. אמר רבי לוי שני בני אדם אמרו דבר אחד, אברהם ואיוב, אברהם אמר חללה לך מעשת כדבר הזה להמית צדיק עם רשע. איוב אמר (איוב ט, כב): אחת היא על כן אמרתי תם ורשע הוא מכלה, אברהם נטל עליה שכר, איוב נענש עליה. אברהם אמר בשולה, איוב אמר פגה, אחת היא על כן אמרתי תם ורשע הוא מכלה. רבי חיא בר אבא אמר ערבובי שאלות יש כאן, אברהם אמר: חללה לך מעשת כדבר הזה להמית צדיק עם רשע, והקדוש ברוך הוא אומר: והיה כצדיק כרשע, יתלה לרשעים בשביל צדיקים...";

    @Before
    public void before() {
        doc = new NgramDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(133, doc.getWords().size());
    }


    @Test
    public void testTagsAfterMerge() {
        doc.format(new JbsNgramFormatter());
        doc.add(new PsukimTagger());
        doc.add(new MergeToMaximalNgrams());

        //assertTrue(doc.getNgram(23, 24).getSortedTags().contains("jbr:text-tanach-1-22-2"));
        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getNgram(68, 76).getSortedTags());
    }

    @Test @Ignore
    public void testFinal() {
        JbsMekorot.findPsukim(doc);

        // span2
        assertEquals(getList("jbr:text-tanach-1-18-25"), doc.getNgram(0, 1).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-18-25"), doc.getNgram(35, 36).getSortedTags());

        // span3
        assertEquals(getList("jbr:text-tanach-1-18-25"), doc.getNgram(126, 128).getSortedTags());

        // span9
        assertEquals(getList("jbr:text-tanach-1-18-25"), doc.getNgram(57, 65).getSortedTags());
        assertEquals(getList("jbr:text-tanach-29-9-22"), doc.getNgram(71, 79).getSortedTags());
        assertEquals(getList("jbr:text-tanach-29-9-22"), doc.getNgram(93, 101).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-18-25"), doc.getNgram(113, 121).getSortedTags());


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
