package apps.jbsmekorot;

import org.junit.Before;
import org.junit.Test;
import spanthera.SpannedDocument;
import spanthera.io.TaggedSubject;
import spanthera.manipulations.MergeSiblingSpans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by omishali on 06/09/2017.
 */
public class TestFindPsukimKeliYekar_1 {
    SpannedDocument doc;
    private String text = "כלי יקר בראשית ברא אלהים את השמים ואת הארץ. מה שהתחיל התורה בבי\"ת לפי שבכל ספר קהלת מדמה שלמה המלך התורה לשמש שאין מהלכה כי אם בשלש רוחות כצורת הבי\"ת הפתוחה לצפון, ונתינתם ע\"י משה שפניו כחמה. ואולי הטעם שלפי שהיצה\"ר המתנגד אל התורה בא מצפון והוא פורץ גדר התורה כמ\"ש (יואל ב.כ) ואת הצפוני ארחיק מעליכם, וכן הזהב אשר מצפון יאתה מתנגד אל התורה. ועל הרוב הם בורחים זה מזה כמ\"ש (תהלים קיט.עא) טוב לי כי עניתי למען אלמד חקך. ואין כאן מקומו לדבר מזה יותר. ויתכן לפרש עוד על דרך שנאמר (משלי ט.א) חכמות בנתה ביתה על כן התחיל חכמת התורה בבי\"ת כי היא גברת הבית הכללי. בראשית ברא אלהים. מן הראוי היה להתחיל התורה בשם אלהים עד שהוצרכו רז\"ל (מגילה ט.) לשנות לתלמי המלך ולכתוב אלהים ברא בראשית";

    @Before
    public void before() {
        doc = new SpannedDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(131, doc.getWords().size());
    }

    @Test
    public void testTagsSpansLength2() {
        doc.add(new PsukimTagger()).tag();

        //assertTrue(doc.getSpan(0, 1).getSortedTags().contains("jbr:text-tanach-1-12-1"));
    }

    @Test
    public void testTagsAfterMerge() {
        doc.add(new PsukimTagger()).tag();
        doc.add(new MergeSiblingSpans()).manipulate();

        //assertTrue(doc.getSpan(23, 24).getSortedTags().contains("jbr:text-tanach-1-22-2"));
        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(68, 76).getSortedTags());
    }

    @Test
    public void testFinal() {
        JbsMekorot.findPsukim(doc);
        TaggedSubject tagged = JbsMekorot.getTaggedSubject(doc, "keliyekar-1");

        assertEquals(getList("jbr:text-tanach-1-1-1"), doc.getSpan(2, 8).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-1-1"), doc.getSpan(109, 111).getSortedTags());
        System.out.println(tagged.toString());
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
