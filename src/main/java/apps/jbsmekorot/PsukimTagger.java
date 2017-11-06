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

    public String format(String s) {
        // note the difference between replace and replaceAll https://stackoverflow.com/questions/10827872/difference-between-string-replace-and-replaceall\// should use replaceAll for entire words!!
        // see https://stackoverflow.com/questions/3223791/how-to-replace-all-occurences-of-a-word-in-a-string-with-another-word-in-java
        String result = s.replace("\"", "").
                replace(";", "").
                replace(".", "").
                replace(",", "").
                replace(":", "").
                replace("-", "dash"). // in some texts dash appears as a whole "word" so we want to replace these cases with a special character. If we eliminate it it causes a 3-span to act like a 2-span.
                replaceAll("\\bdash\\b", "@"). // replace "dash words" with a @ character
                replace("dash", ""). // remove the rest occurrences.
                replace("׳", "tag").
                replace("'", "tag"). // we do this for using \b - word boundaries expectes only word chars and not symbols!
                replaceAll("\\bיי\\b", "יהוה").
                replaceAll("\\bויי\\b", "ויהוה").
                replaceAll("\\bהtag\\b", "יהוה").
                replaceAll("\\bדtag\\b", "יהוה").
                replaceAll("\\bוהtag\\b", "ויהוה").
                replaceAll("\\bודtag\\b", "ויהוה").
                replaceAll("\\bכהtag\\b", "כיהוה").
                replaceAll("\\bכדtag\\b", "כיהוה").
                replaceAll("\\bאלוקים\\b", "אלהים").
                replaceAll("\\bאלוקים\\b", "אלהים").
                replaceAll("\\bאלקים\\b", "אלהים").
                replaceAll("\\bואלקים\\b", "ואלהים").
                replaceAll("\\bואלוקים\\b", "ואלהים").
                replaceAll("\\bכאלוקים\\b", "כאלהים").
                replaceAll("\\bכאלקים\\b", "כאלהים").

                replace("tag", ""); // should be applied at the end since previous replacements dependes on ' (tag) char

        //System.out.println(result);
        return result;
    }
}