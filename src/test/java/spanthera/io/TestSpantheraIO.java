package spanthera.io;


import org.junit.*;

import static org.junit.Assert.*;


/**
 * Created by omishali on 01/11/2017.
 */
public class TestSpantheraIO {

    @Test
    public void testReadSingleInputJson() {
        TaggerInput in = SpantheraIO.readInputJson("jbs-text/mesilatyesharim/mesilatyesharim.json");
        assertEquals(28, in.getSubjects().size());
    }

    @Test
    public void testReadAllInputJsonsFromFolder() {
        String[] in = SpantheraIO.getJsonsInDir("jbs-text/mishnetorah");
        assertEquals(14, in.length);
        assertEquals("mishnetorah-1.json", in[0]);
    }
}
