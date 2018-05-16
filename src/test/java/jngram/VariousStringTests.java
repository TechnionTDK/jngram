package jngram;

import apps.jbsmekorot.PsukimTagger;
import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import static org.junit.Assert.*;


/**
 * Created by omishali on 17/10/2017.
 */
public class VariousStringTests {
    @Test
    public void test() {
        String text = "בבויי ויי אלהים אמת";
        assertEquals("בבויי ויהוה אלהים אמת", text.replaceAll("\\bויי\\b", "ויהוה"));

        // it tooks us time to find out that \b separates word chars only (and not symbols)
        text = "אנכי ה' אלהיך";
        assertEquals("אנכי יהוה אלהיך", text.replace("'", "tag").replaceAll("\\bהtag\\b", "יהוה"));
    }

    @Test
    public void testCountChars() {
        String text = "''hello''";
        text = text.replace("''", "\"");
        assertEquals(2, StringUtils.countMatches(text, "\""));
    }
}
