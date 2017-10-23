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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by omishali on 06/09/2017.
 */
public class FindPsukimRabaEster7_9 {
    SpannedDocument doc;
    private String text = "וירא המן כי אין מרדכי כרע ומשתחוה לו (אסתר ג, ה), אמר רבי איבו (תהלים סט, כד): תחשכנה עיניהם של רשעים מראות. לפי שמראית עיניהם של רשעים מורידות אותם לגיהנם, הדא הוא דכתיב (בראשית ו, ב): ויראו בני האלהים את בנות האדם. (בראשית ט, כב): וירא חם אבי כנען. (בראשית כח, ח): וירא עשו כי רעות בנות כנען. (במדבר כב, ב): וירא בלק בן צפור. (במדבר כד, א): וירא בלעם כי טוב בעיני ה' לברך את ישראל. וירא המן כי אין מרדכי כרע ומשתחוה לו. אבל מראית עיניהם של צדיקים תואר, לפי שמראית עיניהם של צדיקים מעלה אותם למעלה העליונה, הדא הוא דכתיב (בראשית יח, ב): וישא עיניו וירא והנה שלשה אנשים. (בראשית כב, יג): וירא והנה איל. (בראשית כט, ב): וירא והנה באר בשדה. (שמות ג, ב): וירא והנה הסנה. (במדבר כה, ז): וירא פינחס, לפיכך הם שמחים במראית עיניהם, שנאמר (תהלים קז, מב): יראו ישרים וישמחו.";

    @Before
    public void before() {
        // we create a document with spans of size 2-8
        doc = new SpannedDocument(text, PsukimTagger.MINIMAL_PASUK_LENGTH, PsukimTagger.MAXIMAL_PASUK_LENGTH);
        // we tag the uri "jbr:text-tanach-1-3-18" to spans of size 2 (that's how PsukimTagger is implemented)
        doc.add(new PsukimTagger()).tag();
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(148, doc.getWords().size());
    }

    @Test
    public void testTagsSpansLength2() {
        assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(68, 69).getSortedTags());
        assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(69, 70).getSortedTags());
        assertTrue(doc.getSpan(70, 71).getSortedTags().contains("jbr:text-tanach-4-24-1"));
        assertTrue(doc.getSpan(71, 72).getSortedTags().contains("jbr:text-tanach-4-24-1"));
        assertTrue(doc.getSpan(72, 73).getSortedTags().contains("jbr:text-tanach-4-24-1"));
        assertTrue(doc.getSpan(73, 74).getSortedTags().contains("jbr:text-tanach-4-24-1"));
        assertTrue(doc.getSpan(74, 75).getSortedTags().contains("jbr:text-tanach-4-24-1"));
        assertTrue(doc.getSpan(75, 76).getSortedTags().contains("jbr:text-tanach-4-24-1"));
    }

    @Test
    public void testTagsAfterMerge() {
        doc.add(new MergeSiblingSpans()).manipulate();

        assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(68, 76).getSortedTags());
    }

    @Test
    public void testFinal() {
        doc.add(new MergeSiblingSpans()).manipulate();
        doc.add(new RemoveMatchesInContainedSpans()).manipulate();
        doc.add(new FilterTagsFromSpansSize3(doc)).manipulate();
        doc.add(new FilterTagsFromSpansSize2(doc)).manipulate();

        // span3
        assertEquals(getList("jbr:text-tanach-1-22-13"), doc.getSpan(115, 117).getSortedTags());
        assertEquals(getList("jbr:text-tanach-2-3-2"), doc.getSpan(128, 130).getSortedTags());
        assertEquals(getList("jbr:text-tanach-27-107-42"), doc.getSpan(145, 147).getSortedTags());

        // span4
        assertEquals(getList("jbr:text-tanach-1-9-22"), doc.getSpan(45, 48).getSortedTags());
        assertEquals(getList("jbr:text-tanach-4-22-2"), doc.getSpan(61, 64).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-29-2"), doc.getSpan(121, 124).getSortedTags());

        // span6
        assertEquals(getList("jbr:text-tanach-1-6-2"), doc.getSpan(36, 41).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-28-8"), doc.getSpan(52, 57).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-18-2"), doc.getSpan(106, 111).getSortedTags());

        // span8
        assertEquals(getList("jbr:text-tanach-34-3-5"), doc.getSpan(0, 7).getSortedTags());
        assertEquals(getList("jbr:text-tanach-34-3-5"), doc.getSpan(77, 84).getSortedTags());

        // span9
        assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(68, 76).getSortedTags());
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
