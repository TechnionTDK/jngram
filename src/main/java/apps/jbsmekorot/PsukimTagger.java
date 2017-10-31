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

    public String format(String s) {
        // note the difference between replace and replaceAll https://stackoverflow.com/questions/10827872/difference-between-string-replace-and-replaceall\// should use replaceAll for entire words!!
        // see https://stackoverflow.com/questions/3223791/how-to-replace-all-occurences-of-a-word-in-a-string-with-another-word-in-java
        String result = s.replace("\"", "").
                replace(";", "").
                replace(".", "").
                replace(",", "").
                replace(":", "").
                replace("-", "").
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

    /**
     * method format is also based on this method taken from mekorot.lucene project
     * @param text
     * @return
     */
    private String cleanText(String text) {
        //text = text.replaceAll("'", "' ");
        text = text.replaceAll(" אדני | אדוני", " יהוה "); // not correct.
        text = text.replaceAll(" ואדני | ואדוני", " ויהוה "); // not correct
        text = text.replaceAll(" כאדני | כאדוני", " כיהוה "); // not correct
        text = text.replaceAll("אלוהים", " אלהים "); // not sure
        text = text.replaceAll("ואלוהים", " ואלהים "); // not sure
        text = text.replaceAll("כאלוהים", " כאלהים "); // not sure
        text = text.replaceAll("[.]", " * ");
        text = text.replaceAll("[()]", " & ");
        text = text.replaceAll("''", "");

        text = text.replaceAll("[^&\\*\\p{InHebrew}\\s]+", " ");
        text = text.replaceAll("\\p{M}", ""); // Removes Nikkud
        text = text.trim().replaceAll(" +", " "); // Removes spaces

    //        text = text.replaceAll("\\s*\\([^\\)]*\\)\\s*", "*");// Remove parentheses and their content
        return text;
    }
}