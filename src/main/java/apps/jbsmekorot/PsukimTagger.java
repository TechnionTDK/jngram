package apps.jbsmekorot;

import org.apache.lucene.document.Document;
import spanthera.Span;
import spanthera.SpanTagger;

import java.util.*;

import static apps.jbsmekorot.JbsMekorot.format;

/**
 * Created by omishali on 10/09/2017.
 */
public class PsukimTagger implements SpanTagger {

    private JbsTanachIndex tanach;
    private JbsTanachMaleIndex tanachMale;

    public PsukimTagger() {
        tanach = new JbsTanachIndex();
        tanachMale = new JbsTanachMaleIndex();
    }

    public List<String> tag(Span s) {
        String text = format(s.text());

        // formatting may cause reduce the size of the span to 1, which causes the fuzzy search to fail.
        if (text.split("\\s+").length == 1)
            return new ArrayList<>();

        List<Document> docs1;

        if(s.text().length() <= 6)
            docs1 = tanach.searchExactInText(text);
        else
            docs1 = tanach.searchFuzzyInText(text, 1);
        //List<Document> docs1 = tanach.searchExactInText(text);
        //List<Document> docs2 = tanachMale.searchExactInText(text);
        //List<Document> docs2 = tanachMale.searchFuzzyInText(text, 2);

        Set<String> result = new HashSet<>();
        for (Document doc : docs1)
            result.add(doc.get("uri"));
        //for (Document doc : docs2)
        //    result.add(doc.get("uri"));

        return new ArrayList<>(result);
    }
    public boolean isCandidate(Span s) {
        return s.size() == 2;
    }
}