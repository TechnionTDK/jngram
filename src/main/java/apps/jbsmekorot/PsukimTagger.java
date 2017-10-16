package apps.jbsmekorot;

import org.apache.lucene.document.Document;
import spanthera.Span;
import spanthera.SpanTagger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omishali on 10/09/2017.
 */
public class PsukimTagger implements SpanTagger {
    public static final int MINIMAL_PASUK_LENGTH = 2;
    public static final int MAXIMAL_PASUK_LENGTH = 14;

    private JbsTanachIndex index;

    public PsukimTagger() {
        try {
            index = new JbsTanachIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> tag(Span s) {
        List<Document> documents = index.searchExactInText(format(s.text()));
        List<String> result = new ArrayList<>();
        for (Document doc : documents)
            result.add(doc.get("uri"));
        return result;
    }
    public boolean isCandidate(Span s) {
        return s.size() == 2;
    }

    private String format(String s) {
        String result = s.replace("\"", "").
                replace(";", "").
                replace(".", "").
                replace(",", "").
                replace(":", "").
                replace("ויי ", "ויהוה ").
                replace("ה'", "יהוה");

        //System.out.println(result);
        return result;
    }
}