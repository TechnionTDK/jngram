package apps.jbsmekorot;

import org.junit.Test;
import static org.junit.Assert.*;

public class HebrewUtilsTest {
    @Test
    public void test_getEheviDiff() {
        assertTrue(HebrewUtils.isEheviDiff("אברהם", "אברם"));
        assertTrue(HebrewUtils.isEheviDiff("ארן", "אורן"));
        assertTrue(HebrewUtils.isEheviDiff("בראשית ברא", "ברשית בר"));
        assertTrue(HebrewUtils.isEheviDiff("בראשית ברא", "ברשת ברווה"));
        assertTrue(HebrewUtils.isEheviDiff("וחי", "חיה"));

        assertTrue(HebrewUtils.isEheviDiff("לא תשיג גבול רעך", "לא תסיג גבול רעך"));
        assertTrue(HebrewUtils.isEheviDiff("במה מדליקים", "במה מדליקין"));
        assertTrue(HebrewUtils.isEheviDiff("ותענינה הנשים המשחקות ותאמרן הכה שאול", "ותענינה הנשים המשחקות ותאמרנה הכה שאול"));

        assertFalse(HebrewUtils.isEheviDiff("אם קטן ואם גדול", "את קטן ואת גדול"));
        assertFalse(HebrewUtils.isEheviDiff("הבל ואין בו מועיל", "הבל ואין בם מועיל"));
    }
}
