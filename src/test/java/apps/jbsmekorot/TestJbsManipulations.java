package apps.jbsmekorot;

import apps.jbsmekorot.manipulations.AddTextWithShemAdnut;
import org.junit.*;
import spanthera.SpannedDocument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by omishali on 08/01/2018.
 */
public class TestJbsManipulations {
    SpannedDocument doc;

    @Test
    public void testShemAdnutManipulation() {
        String text = "כי מקרא מלא שהביאוהו זכרונם לברכה לראיתם (ישעיה ט, טז) על כן על בחוריו לא ישמח ה' וגו'";
        doc = new SpannedDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertEquals("ישמח אדני", doc.getSpan(15, 16).getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT));
        assertEquals(getList("jbr:text-tanach-12-9-16"), doc.getSpan(10, 16).getSortedTags());
        //test is failed since we put extra in span 2 and thus removenonsequential removes the tag!
    }

    @Test
    public void testFilterTagsFromSpansManipulation() {
        String text = "בראשית ברא אלקים את השמים ואת הארץ 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 בראשית ברא אלקים";
        doc = new SpannedDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        assertEquals(getList("jbr:text-tanach-1-1-1"), doc.getSpan(0, 6).getSortedTags());
        assertEquals(getList("jbr:text-tanach-1-1-1"), doc.getSpan(27, 29).getSortedTags());
    }

    @Test
    public void testRemoveNonSequentialTags() {
        String text = "להבדיל בין הטמא ובין הטהור";
        doc = new SpannedDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        // without the manipulation, we should detect here two psukim:
        // jbr:text-tanach-3-10-10, jbr:text-tanach-3-11-47
        // the manipulation should remove jbr:text-tanach-3-10-10
        assertEquals(getList("jbr:text-tanach-3-11-47"), doc.getSpan(0, 4).getSortedTags());
    }

    @Test
    public void testRemoveTagsFromOverlappingSpans() {
        String text = "ורם לבבך ושכחת את ה' אלקיך על ";
        doc = new SpannedDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);

        // without the manipulation, we should also detect a pasuk for (3,6)
        assertEquals(0, doc.getSpan(3, 6).getSortedTags().size());
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
