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

/**
 * Created by omishali on 06/09/2017.
 */
public class TestFindPsukimMitzvot_1_1 {
    SpannedDocument doc;
    private String text = "היא הצווי אשר צונו בהאמנת האלהות, והוא שנאמין שיש שם עלה וסבה הוא פועל לכל הנמצאים, והוא אמרו (שמות כ-ב) ''אנכי ה' אלהיך''. ובסוף גמרא מכות (גמרא מכות כג-ב) אמרו תרי''ג מצות נאמרו למשה בסיני, מאי קראה (דברים לג-ד) ''תורה צוה לנו משה'', ר''ל מנין תור''ה. והקשו על זה ואמרו תורת בגימטריא תרי''א הוי, והיה המענה אנכי ולא יהיה מפי הגבורה שמענום. הנה נתבאר לך שאנכי ה' מכלל תרי''ג מצות, והוא צווי באמונת האלהות כמו שבארנו.";

    @Before
    public void before() {
        doc = new SpannedDocument(text, PsukimTagger.MINIMAL_PASUK_LENGTH, PsukimTagger.MAXIMAL_PASUK_LENGTH);
        doc.add(new PsukimTagger()).tag();
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(76, doc.getWords().size());
    }

    @Test
    public void testTagsSpansLength2() {
        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(68, 69).getSortedTags());
    }

    @Test
    public void testTagsAfterMerge() {
        doc.add(new MergeSiblingSpans()).manipulate();

        //assertEquals(getList("jbr:text-tanach-4-24-1"), doc.getSpan(68, 76).getSortedTags());
    }

    @Test
    public void testFinal() {
        doc.add(new MergeSiblingSpans()).manipulate();
        doc.add(new RemoveMatchesInContainedSpans()).manipulate();
        doc.add(new FilterTagsFromSpansSize3(doc)).manipulate();
        doc.add(new FilterTagsFromSpansSize2(doc)).manipulate();

        // span3
        assertEquals(getList("jbr:text-tanach-27-81-11", "jbr:text-tanach-2-20-2", "jbr:text-tanach-5-5-6"), doc.getSpan(20, 22).getSortedTags());

        // span4
        assertEquals(getList("jbr:text-tanach-5-33-4"), doc.getSpan(39, 42).getSortedTags());
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
