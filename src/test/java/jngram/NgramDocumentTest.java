package jngram;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


/**
 * Created by omishali on 30/04/2017.
 */
public class NgramDocumentTest {
    private NgramDocument doc;
    private String text = "ויאמר משה אל רבי יהודה הנשיא";
    private String textMitzva_1_1 = "היא הצווי אשר צונו בהאמנת האלהות, והוא שנאמין שיש שם עלה וסבה הוא פועל לכל הנמצאים, והוא אמרו (שמות כ-ב) ''אנכי ה' אלהיך''. ובסוף גמרא מכות (גמרא מכות כג-ב) אמרו תרי''ג מצות נאמרו למשה בסיני, מאי קראה (דברים לג-ד) ''תורה צוה לנו משה'', ר''ל מנין תור''ה. והקשו על זה ואמרו תורת בגימטריא תרי''א הוי, והיה המענה אנכי ולא יהיה מפי הגבורה שמענום. הנה נתבאר לך שאנכי ה' מכלל תרי''ג מצות, והוא צווי באמונת האלהות כמו שבארנו.";

    @Before
    public void before() {
        doc = new NgramDocument(text, 1, 6);
    }

    @Test
    public void testLength() {
        assertEquals(6, doc.length());
    }

    @Test
    public void testGetWord() {
        assertEquals("יהודה", doc.getWord(4).getText());
    }

    @Test
    public void testSpan() {
        Ngram ngram = doc.getNgram(3, 5);
        assertEquals(3, ngram.size());
        assertEquals("רבי יהודה הנשיא", ngram.getText());
        assertEquals("רבי", ngram.getWord(0).getText());
        assertEquals("יהודה", ngram.getWord(1).getText());
        assertEquals("הנשיא", ngram.getWord(2).getText());
    }

    /**
     * Phase 1: match between getNgrams in the document to URIs.
     * Each span may point to a set of URIs.
     */

    @Test
    public void testSpans() {
        // iterate all getNgrams of 1 word length
        List<Ngram> ngrams = doc.getNgrams(1);
        assertEquals(6, ngrams.size());
        assertEquals("ויאמר", ngrams.get(0).getText());

        // iterate all getNgrams of 2 word length
        ngrams = doc.getNgrams(2);
        assertEquals(5, ngrams.size());
        assertEquals("ויאמר משה", ngrams.get(0).getText());
        assertEquals("אל רבי", ngrams.get(2).getText());

        // iterate all getNgrams of 3 word length
        ngrams = doc.getNgrams(3);
        assertEquals(4, ngrams.size());
        assertEquals("ויאמר משה אל", ngrams.get(0).getText());

        // iterate all getNgrams of 5 word length
        ngrams = doc.getNgrams(5);
        assertEquals(2, ngrams.size());
        assertEquals("ויאמר משה אל רבי יהודה", ngrams.get(0).getText());

        // iterate all getNgrams of 6 word length
        ngrams = doc.getNgrams(6);
        assertEquals(1, ngrams.size());
        assertEquals("ויאמר משה אל רבי יהודה הנשיא", ngrams.get(0).getText());
    }

    @Test
    public void testSpansOfSize4() {
        // we create a document with spans of one size: 4
        NgramDocument doc = new NgramDocument(text, 4, 4);
        assertEquals(3, doc.getAllNgrams().size());
        assertEquals("אל רבי יהודה הנשיא", doc.getNgram(2,5).getText());
    }

    @Test
    public void testGetAllSpans() {
        NgramDocument sd = new NgramDocument(text, 1, 4);
        assertEquals(18, sd.getAllNgrams().size());

        sd = new NgramDocument(text, 1, 6);
        assertEquals(21, sd.getAllNgrams().size());

        sd = new NgramDocument(text, 4, 6);
        assertEquals(6, sd.getAllNgrams().size());
    }

    @Test
    public void testAddMatchers() {
        NgramDocument doc = new NgramDocument(text, 1, 3);

        doc.add(new NgramTagger() {
            public List<String> tag(Ngram s) {
                List<String> result = new ArrayList<String>();
                if (s.getText().equals("משה"))
                    result.add("jbr:person-moshe");
                else if (s.getText().equals("רבי"))
                    result.add("jbr:person-rabbi");

                return result;
            }

            // matching
            public boolean isCandidate(Ngram s) {
                return s.size() == 1;
            }
        })
        .add(new NgramTagger() {
            public List<String> tag(Ngram s) {
                List<String> result = new ArrayList<String>();
                if (s.getText().equals("רבי יהודה הנשיא"))
                    result.add("jbr:person-rabbi");

                return result;
            }

            // matching
            public boolean isCandidate(Ngram s) {
                return s.size() == 3;
            }
        });

        Ngram s = doc.getNgram(1, 1);
        assertEquals(getList("jbr:person-moshe"), s.getTags());
        s = doc.getNgram(3, 3);
        assertEquals(getList("jbr:person-rabbi"), s.getTags());
        s = doc.getNgram(3, 5);
        assertEquals(getList("jbr:person-rabbi"), s.getTags());
    }

    @Test
    public void testGetContainingSpans() {
        Ngram s = doc.getNgram(0, 1);
        assertEquals(4, doc.getContainingNgrams(s).size());

        //System.out.println(doc.getContainingNgrams(s));

        s = doc.getNgram(1, 2);
        assertEquals(7, doc.getContainingNgrams(s).size());
    }

    @Test
    public void testSpanSeparators() {
        // do we separate ngrams also by \n and not only spaces?
        String text1 = "ויאמר משה אל\nרבי יהודה הנשיא";
        doc = new NgramDocument(text1, 1, 3);

        List<Ngram> ngrams = doc.getNgrams(1);
        assertEquals(6, ngrams.size());
    }

    @Test
    public void testFilterMethod() {
        NgramFormatter formatter = new NgramFormatter() {
            @Override
            public String format(Ngram s) {
                String formatted = s.getText().replace("ה", "ש");
                return formatted;
            }

            @Override
            public boolean isCandidate(Ngram s) {
                return true;
            }
        };

        doc.format(formatter);
        Ngram s = doc.getNgram(0, 1);
        assertEquals("ויאמר משש", s.getTextFormatted());
        s = doc.getNgram(3, 5);
        assertEquals("רבי ישודש שנשיא", s.getTextFormatted());
    }

    @Test
    public void test_getOverlappingSpans() {
        text.length();// reference for convenience...
        Ngram s = doc.getNgram(0, 1);
        assertEquals("ויאמר משה", s.getText());

        List<Ngram> ngrams = doc.getOverlappingNgrams(s);
        // we expect all overlapping ngrams besides s itself
        assertThat(ngrams.size(), equalTo(8));

        s = doc.getNgram(1, 2);
        assertEquals("משה אל", s.getText());
        ngrams = doc.getOverlappingNgrams(s);
        // we do not expect ngrams MARK_BEFORE s, only MARK_AFTER
        assertThat(ngrams.size(), equalTo(6));

        s = doc.getNgram(4, 5);
        assertEquals("יהודה הנשיא", s.getText());
        ngrams = doc.getOverlappingNgrams(s);
        // we do not expect ngrams MARK_BEFORE s, only MARK_AFTER
        assertThat(ngrams.size(), equalTo(0));

        s = doc.getNgram(0, 4);
        assertEquals("ויאמר משה אל רבי יהודה", s.getText());
        ngrams = doc.getOverlappingNgrams(s);
        assertThat(ngrams.size(), equalTo(5));
    }

    @Test
    public void test_getNgramBefore() {
        NgramDocument doc = new NgramDocument(text, 1, 5);
        Ngram ng = doc.getNgram(5, 5);
        assertEquals("הנשיא", ng.getText());

        // test all valid before options
        Ngram ngBefore = doc.getNgramBefore(ng, 1);
        assertEquals("יהודה", ngBefore.getText());

        ngBefore = doc.getNgramBefore(ng, 2);
        assertEquals("רבי יהודה", ngBefore.getText());

        ngBefore = doc.getNgramBefore(ng, 3);
        assertEquals("אל רבי יהודה", ngBefore.getText());

        ngBefore = doc.getNgramBefore(ng, 4);
        assertEquals("משה אל רבי יהודה", ngBefore.getText());

        // we do our best effort to provide a result, and truncate if needed
        ngBefore = doc.getNgramBefore(ng, 6); // invalid size, too big
        // we return the closest result
        assertEquals("ויאמר משה אל רבי יהודה", ngBefore.getText());

        ng = doc.getNgram(1, 2);
        assertEquals("משה אל", ng.getText());
        ngBefore = doc.getNgramBefore(ng, 1);
        assertEquals("ויאמר", ngBefore.getText());
        ngBefore = doc.getNgramBefore(ng, 2);
        assertEquals("ויאמר", ngBefore.getText());

        // test some invalid options
        ng = doc.getNgram(0, 0);
        assertEquals("ויאמר", ng.getText());
        ngBefore = doc.getNgramBefore(ng, 1);
        assertNull(ngBefore);

        doc = new NgramDocument(text, 1, 3);
        ng = doc.getNgram(4, 5);
        assertEquals("יהודה הנשיא", ng.getText());
        ngBefore = doc.getNgramBefore(ng, 4);
        assertEquals("משה אל רבי", ngBefore.getText());
    }

    @Test
    public void test_getNgramAfter() {
        NgramDocument doc = new NgramDocument(text, 1, 5);
        Ngram ng = doc.getNgram(0, 0);
        assertEquals("ויאמר", ng.getText());

        // test all valid after options
        Ngram ngAfter = doc.getNgramAfter(ng, 1);
        assertEquals("משה", ngAfter.getText());

        ngAfter = doc.getNgramAfter(ng, 2);
        assertEquals("משה אל", ngAfter.getText());

        ngAfter = doc.getNgramAfter(ng, 3);
        assertEquals("משה אל רבי", ngAfter.getText());

        ngAfter = doc.getNgramAfter(ng, 4);
        assertEquals("משה אל רבי יהודה", ngAfter.getText());

        ngAfter = doc.getNgramAfter(ng, 5);
        assertEquals("משה אל רבי יהודה הנשיא", ngAfter.getText());

        // more extreme options, some are invalid
        ngAfter = doc.getNgramAfter(ng, 6);
        assertEquals("משה אל רבי יהודה הנשיא", ngAfter.getText());
        ng = doc.getNgram(3, 4);
        assertEquals("רבי יהודה", ng.getText());
        ngAfter = doc.getNgramAfter(ng, 1);
        assertNotNull(ngAfter);
        ngAfter = doc.getNgramAfter(ng, 2);
        assertNotNull(ngAfter);
    }
    private List<String> getList(String... args) {
        return Arrays.asList(args);
    }


}
