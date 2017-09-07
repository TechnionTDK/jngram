package tdk.technion.spanlib;

import org.junit.Before;
import org.junit.Test;
import tdk.technion.spanlib.manipulations.MergeSiblingSpans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by omishali on 06/09/2017.
 */
public class FindPsukimTest {
    SpannedDocument doc;
    private String text1 = "קוץ ודרדר לא נאמר אלא קוץ ודרדר תצמיח לך ולא עוד אלא תצמיח לך ואכלת את עשב השדה";
    private String pasuk = "וקוץ ודרדר תצמיח לך ואכלת את עשב השדה"; // tanach-1-3-18
    private String uri = "jbr:tanach-1-3-18";

    @Before
    public void before() {
        // we create a document with spans of size 2-8
        doc = new SpannedDocument(text1, 2, 8);
        // we tag the uri "jbr:tanach-1-3-18" to spans of size 2 (that's how PsukimTagger is implemented)
        doc.add(new PsukimTagger()).tag();
    }

    @Test
    public void testThatSpansOfSizeTwoAreCorrectlyTagged() {
        // check that relevant spans of size 2 are indeed tagged
        assertEquals(getList(uri), doc.getSpan(0, 1).getTags());
        assertEquals(getList(uri), doc.getSpan(5, 6).getTags());
        assertEquals(getList(uri), doc.getSpan(6, 7).getTags());
        assertEquals(getList(uri), doc.getSpan(7, 8).getTags());
        assertEquals(getList(uri), doc.getSpan(12, 13).getTags());
        assertEquals(getList(uri), doc.getSpan(13, 14).getTags());
        assertEquals(getList(uri), doc.getSpan(14, 15).getTags());
        assertEquals(getList(uri), doc.getSpan(15, 16).getTags());
        assertEquals(getList(uri), doc.getSpan(16, 17).getTags());

        // check that irrelevant spans of size 2 are not tagged
        assertEquals(getEmptyList(), doc.getSpan(1, 2).getTags());
        assertEquals(getEmptyList(), doc.getSpan(2, 3).getTags());
        assertEquals(getEmptyList(), doc.getSpan(3, 4).getTags());
        assertEquals(getEmptyList(), doc.getSpan(4, 5).getTags());
        assertEquals(getEmptyList(), doc.getSpan(8, 9).getTags());
        assertEquals(getEmptyList(), doc.getSpan(9, 10).getTags());
        assertEquals(getEmptyList(), doc.getSpan(10, 11).getTags());
        assertEquals(getEmptyList(), doc.getSpan(11, 12).getTags());
    }

    @Test
    public void testPostMergeManipulationState() {
        doc.add(new MergeSiblingSpans()).manipulate();
        // only these spans should have a (single) tag:
        assertEquals(getList(uri), doc.getSpan(0,1).getTags());
        assertEquals(getList(uri), doc.getSpan(5,8).getTags());
        assertEquals(getList(uri), doc.getSpan(12,17).getTags());

        // all other spans should be empty! (MergeSiblingSpans should remove their tags if exist
        assertEquals(getEmptyList(), doc.getSpan(9,11).getTags());
        assertEquals(getEmptyList(), doc.getSpan(2,3).getTags());
        assertEquals(getEmptyList(), doc.getSpan(4,5).getTags());

        assertEquals(getEmptyList(), doc.getSpan(5,6).getTags());
        assertEquals(getEmptyList(), doc.getSpan(6,7).getTags());
        assertEquals(getEmptyList(), doc.getSpan(7,8).getTags());
        assertEquals(getEmptyList(), doc.getSpan(5,7).getTags());
        assertEquals(getEmptyList(), doc.getSpan(6,8).getTags());
        assertEquals(getEmptyList(), doc.getSpan(12,16).getTags());
        assertEquals(getEmptyList(), doc.getSpan(14,17).getTags());
    }

    @Test
    public void whatIsTheNextTest() {
        fail();
    }

    private List<String> getList(String... args) {
        return Arrays.asList(args);
    }
    private List<String> getEmptyList() {
        return new ArrayList<>();
    }
    public class PsukimTagger implements SpanTagger {
        public List<String> tag(Span s) {
            if (pasuk.contains(s.text()))
                return Arrays.asList(new String[]{uri});
            return null;
        }

        public boolean isCandidate(Span s) {
            return s.size() == 2;
        }
    }



}
