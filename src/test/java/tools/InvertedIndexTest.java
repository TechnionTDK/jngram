package tools;

import org.junit.*;
import tools.InvertedIndex;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by omishali on 19/09/2017.
 */
public class InvertedIndexTest {
    private static InvertedIndex index;

    @BeforeClass
    public static void beforeClass() throws Exception {
        index = new InvertedIndex();
        //index.create();
    }

    @Test
    public void testGetSourceJsons() throws Exception {
        String[] jsons = index.getSourceJsons();
        assertEquals(39, jsons.length);
    }

    @Test
    public void testSearchExact() throws IOException {
        index.searchExact("את ידו");
    }

    @Test
    public void testSearchFuzzy() throws IOException {
        index.searchFuzzy("סנה בער");
    }

    @Test
    public void testPasuk1() throws IOException {
        List<InvertedIndex.Text> result = index.searchExact("ואני קרבת אלהים לי טוב");
        assertEquals(1, result.size());
        assertEquals("jbr:text-tanach-27-73-28", result.get(0).getUri());
        assertEquals("ואני קרבת אלהים לי טוב שתי באדני יהוה מחסי לספר כל מלאכותיך", result.get(0).getText());
    }

    @Test
    public void testPasuk2() throws IOException {
        List<InvertedIndex.Text> result = index.searchExact("אחת שאלתי מאת ה' אותה אבקש");
        assertEquals(1, result.size());
        assertEquals("jbr:text-tanach-27-27-4", result.get(0).getUri());
    }
}
