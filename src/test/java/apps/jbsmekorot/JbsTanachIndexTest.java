package apps.jbsmekorot;

import org.apache.lucene.document.Document;
import org.junit.*;

import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Created by omishali on 19/09/2017.
 */
public class JbsTanachIndexTest {
    private static JbsTanachIndex index;

    @BeforeClass
    public static void beforeClass() throws Exception {
        index = new JbsTanachIndex();
    }

    @Test
    public void testGetSourceJsons() throws Exception {
        String[] jsons = index.getSourceJsons();
        assertEquals(39, jsons.length);
    }

    @Test
    public void testSearchExact() throws IOException {
        index.searchExactInText("את ידו");
    }

    @Test
    public void testPasuk1() throws IOException {
        List<Document> result = index.searchExactInText("ואני קרבת אלהים לי טוב");
        assertEquals(1, result.size());
        assertEquals("jbr:text-tanach-27-73-28", result.get(0).get("uri"));
        assertEquals("ואני קרבת אלהים לי טוב שתי באדני יהוה מחסי לספר כל מלאכותיך", result.get(0).get("text"));
    }

    @Test
    public void testPasuk2() throws IOException {
        List<Document> result = index.searchExactInText("אחת שאלתי מאת יהוה אותה אבקש");
        assertEquals(1, result.size());
        assertEquals("jbr:text-tanach-27-27-4", result.get(0).get("uri"));
    }

    @Test
    public void testPasuk3() throws IOException {
        List<Document> result = index.searchExactInText("וקוץ ודרדר");
        index.printDocs(result);
        assertEquals(1, result.size());
        assertEquals("jbr:text-tanach-1-3-18", result.get(0).get("uri"));
    }
}
