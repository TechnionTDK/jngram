package tdk.technion.spanlib;

import org.junit.Before;
import org.junit.Test;
import tdk.technion.spanlib.manipulations.MergeSiblingSpans;
import tdk.technion.spanlib.manipulations.RemoveMatchesInContainedSpans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by omishali on 06/09/2017.
 */
public class FindPsukimText2Test {
    SpannedDocument doc;
    private String text = "האמת. הנשמה נבראת ממקום רוח הקודש, שנאמר (בראשית ב ד): \"ויפח באפיו נשמת חיים\"; ונחצבה ממקום טהרה, ונבראת מזוהר העליון מכסא הכבוד. ואין למעלה במקום קודשי הקודשים שקר, אלא הכל אמת, שנאמר (ירמיהו י י): \"ויי אלהים אמת\". ומצאתי כי כתיב: \"אהיה אשר אהיה\" (שמות ג יד), וכתיב: \"ויי אלהים אמת, הוא אלהים חיים ומלך עולם\" (ירמיהו שם). ועתה יש להודיעך שהקדוש ברוך הוא אלהים אמת: כי תמצא עשרים ואחת פעמים \"אהיה\" שהוא בגימטריא \"אמת\", וגם כן \"אהיה\" בגימטריא עשרים ואחת.";

    @Before
    public void before() {
        // we create a document with spans of size 2-8
        doc = new SpannedDocument(text, 2, 8);
        // we tag the uri "jbr:tanach-1-3-18" to spans of size 2 (that's how PsukimTagger is implemented)
        doc.add(new PsukimTagger()).tag();
    }

    @Test
    public void testNumberOfWords() {
        assertEquals(81, doc.getWords().size());
    }

    @Test
    public void testTagsofSpans2() {
        assertEquals(getList("jbr:tanach-1-2-7"), doc.getSpan(10, 11).getTags());
        assertEquals(getList("jbr:tanach-1-2-7"), doc.getSpan(11, 12).getTags());
        assertEquals(getList("jbr:tanach-1-2-7"), doc.getSpan(12, 13).getTags());

        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(35, 36).getTags());
        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(36, 37).getTags());

        assertEquals(getList("jbr:tanach-2-3-14"), doc.getSpan(41, 42).getTags());
        assertEquals(getList("jbr:tanach-2-3-14"), doc.getSpan(42, 43).getTags());

        assertEquals(getList("jbr:tanach-13-10-10", "jbr:tanach-13-23-36", "jbr:tanach-5-5-25", "jbr:tanach-8-17-26", "jbr:tanach-8-17-36"),
                doc.getSpan(52, 53).getTags());

        assertEquals(getList("jbr:tanach-31-2-20", "jbr:tanach-4-22-12"),
                doc.getSpan(62, 63).getTags());


        assertEquals(getList("jbr:tanach-13-10-10", "jbr:tanach-27-100-3", "jbr:tanach-39-20-6", "jbr:tanach-6-2-11"),
                doc.getSpan(63, 64).getTags());
        assertEquals(getList("jbr:tanach-13-10-10"),
                doc.getSpan(64, 65).getTags());

        assertEquals(getList("jbr:tanach-9-16-19"), doc.getSpan(76, 77).getTags());
    }

    @Test
    public void testTagsAfterMerge() {
        doc.add(new MergeSiblingSpans()).manipulate();

        assertEquals(getList("jbr:tanach-1-2-7"), doc.getSpan(10, 13).getTags());
        assertEquals(getEmptyList(), doc.getSpan(10, 11).getTags());
        assertEquals(getEmptyList(), doc.getSpan(11, 12).getTags());
        assertEquals(getEmptyList(), doc.getSpan(12, 13).getTags());
        assertEquals(getEmptyList(), doc.getSpan(10, 12).getTags());
        assertEquals(getEmptyList(), doc.getSpan(11, 13).getTags());

        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(35, 37).getTags());
        assertEquals(getEmptyList(), doc.getSpan(35, 36).getTags());
        assertEquals(getEmptyList(), doc.getSpan(36, 37).getTags());

        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(48, 55).getTags());
        assertEquals(getEmptyList(), doc.getSpan(48, 49).getTags());
        assertEquals(getEmptyList(), doc.getSpan(49, 50).getTags());
        assertEquals(getEmptyList(), doc.getSpan(50, 51).getTags());
        assertEquals(getList("jbr:tanach-27-100-3", "jbr:tanach-39-20-6", "jbr:tanach-6-2-11"), doc.getSpan(51, 52).getTags());
        assertEquals(getList("jbr:tanach-13-23-36", "jbr:tanach-5-5-25", "jbr:tanach-8-17-26", "jbr:tanach-8-17-36"), doc.getSpan(52, 53).getTags());
        assertEquals(getEmptyList(), doc.getSpan(53, 54).getTags());
        assertEquals(getEmptyList(), doc.getSpan(54, 55).getTags());

        assertEquals(getList("jbr:tanach-36-10-2"), doc.getSpan(58, 59).getTags());

        assertEquals(getList("jbr:tanach-31-2-20", "jbr:tanach-4-22-12"), doc.getSpan(62, 63).getTags());

        assertEquals(getList("jbr:tanach-27-100-3", "jbr:tanach-39-20-6", "jbr:tanach-6-2-11"), doc.getSpan(63, 64).getTags());
        assertEquals(getEmptyList(), doc.getSpan(64, 65).getTags());
        // note: this span is not really a quote from the pasuk 13-10-10!
        // but an interesting case of applying the merge since both spans of size 2 are from the pasuk but not subsequent.
        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(63, 65).getTags());

        assertEquals(getList("jbr:tanach-11-4-29"), doc.getSpan(66, 67).getTags());

        assertEquals(getList("jbr:tanach-9-16-19"), doc.getSpan(76, 77).getTags());
    }

    @Test
    public void testRemoveMatchesInContainedSpans() {
        doc.add(new MergeSiblingSpans()).manipulate();
        doc.add(new RemoveMatchesInContainedSpans()).manipulate();

        // after applying the Remove manipulation we expext the URIs matched
        // for span [51,52] [52,53] to be totally removed!
        assertEquals(getEmptyList(), doc.getSpan(51, 52).getTags());
        assertEquals(getEmptyList(), doc.getSpan(52, 53).getTags());
        // matches in containing span should be preserved:
        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(48, 55).getTags());

        // now we simply perform the previous assertions to make sure nothing was broken:
        assertEquals(getList("jbr:tanach-1-2-7"), doc.getSpan(10, 13).getTags());
        assertEquals(getEmptyList(), doc.getSpan(10, 11).getTags());
        assertEquals(getEmptyList(), doc.getSpan(11, 12).getTags());
        assertEquals(getEmptyList(), doc.getSpan(12, 13).getTags());
        assertEquals(getEmptyList(), doc.getSpan(10, 12).getTags());
        assertEquals(getEmptyList(), doc.getSpan(11, 13).getTags());

        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(35, 37).getTags());
        assertEquals(getEmptyList(), doc.getSpan(35, 36).getTags());
        assertEquals(getEmptyList(), doc.getSpan(36, 37).getTags());

        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(48, 55).getTags());
        assertEquals(getEmptyList(), doc.getSpan(48, 49).getTags());
        assertEquals(getEmptyList(), doc.getSpan(49, 50).getTags());
        assertEquals(getEmptyList(), doc.getSpan(50, 51).getTags());
        //assertEquals(getList("jbr:tanach-27-100-3", "jbr:tanach-39-20-6", "jbr:tanach-6-2-11"), doc.getSpan(51, 52).getTags());
        //assertEquals(getList("jbr:tanach-13-23-36", "jbr:tanach-5-5-25", "jbr:tanach-8-17-26", "jbr:tanach-8-17-36"), doc.getSpan(52, 53).getTags());
        assertEquals(getEmptyList(), doc.getSpan(53, 54).getTags());
        assertEquals(getEmptyList(), doc.getSpan(54, 55).getTags());

        assertEquals(getList("jbr:tanach-36-10-2"), doc.getSpan(58, 59).getTags());

        assertEquals(getList("jbr:tanach-31-2-20", "jbr:tanach-4-22-12"), doc.getSpan(62, 63).getTags());

        assertEquals(getEmptyList(), doc.getSpan(63, 64).getTags());
        assertEquals(getEmptyList(), doc.getSpan(64, 65).getTags());
        // note: this span is not really a quote from the pasuk 13-10-10!
        // but an interesting case of applying the merge since both spans of size 2 are from the pasuk but not subsequent.
        // idea: at the end perform validation of the identified psukim against the Index
        assertEquals(getList("jbr:tanach-13-10-10"), doc.getSpan(63, 65).getTags());

        assertEquals(getList("jbr:tanach-11-4-29"), doc.getSpan(66, 67).getTags());

        assertEquals(getList("jbr:tanach-9-16-19"), doc.getSpan(76, 77).getTags());
    }

    @Test
    public void testNext() {
        // next we should execute another manipulation that should add a
        // Marker "certain" = true for each span with size > X = 4.
    }
    
    private List<String> getList(String... args) {
        return Arrays.asList(args);
    }
    private List<String> getEmptyList() {
        return new ArrayList<>();
    }




}
