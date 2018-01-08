package apps.jbsmekorot;

import org.junit.*;
import spanthera.Span;
import spanthera.SpanFormatter;
import spanthera.SpannedDocument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by omishali on 08/01/2018.
 */
public class TestManipulations {
    SpannedDocument doc;
    private String text = "כי מקרא מלא שהביאוהו זכרונם לברכה לראיתם (ישעיה ט, טז) על כן על בחוריו לא ישמח ה' וגו'";
    @Test
    public void testShemAdnutManipulation() {
        doc = new SpannedDocument(text, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);
        JbsMekorot.findPsukim(doc);
        assertEquals(getList("jbr:text-tanach-12-9-16"), doc.getSpan(10, 16).getSortedTags());
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
