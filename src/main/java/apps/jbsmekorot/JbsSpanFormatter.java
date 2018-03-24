package apps.jbsmekorot;

import spanthera.Span;
import spanthera.SpanFormatter;

/**
 * Created by omishali on 08/01/2018.
 */
public class JbsSpanFormatter implements SpanFormatter {

    @Override
    public String format(Span s) {
        return format(s.text());
    }

    @Override
    public boolean isCandidate(Span s) {
        return s.size() == 2;
    }

    private String format(String s) {
        // note the difference between replace and replaceAll https://stackoverflow.com/questions/10827872/difference-between-string-replace-and-replaceall\// should use replaceAll for entire words!!
        // see https://stackoverflow.com/questions/3223791/how-to-replace-all-occurences-of-a-word-in-a-string-with-another-word-in-java
        String result = s.replace("\"", "").
                replace("%", ""). // special char we use for labeling the data
                replace(";", "").
                replace(".", "").
                replace(",", "").
                replace(":", "").
                replace("?", "").
                replace("-", "dash"). // in some texts dash appears as a whole "word" so we want to replace these cases with a special character. If we eliminate it it causes a 3-span to act like a 2-span.
                replaceAll("\\bdash\\b", "@"). // replace "dash words" with a @ character
                replace("dash", ""). // remove the rest occurrences.
                replace("׳", "tag").
                replace("'", "tag"). // we do this for using \b - word boundaries expectes only word chars and not symbols!
                replaceAll("\\bיי\\b", "יהוה").
                replaceAll("\\bבהtag\\b", "ביהוה").
                replaceAll("\\bויי\\b", "ויהוה").
                replaceAll("\\bהtag\\b", "יהוה").
                replaceAll("\\bדtag\\b", "יהוה").
                replaceAll("\\bוהtag\\b", "ויהוה").
                replaceAll("\\bודtag\\b", "ויהוה").
                replaceAll("\\bכהtag\\b", "כיהוה").
                replaceAll("\\bלהtag\\b", "ליהוה").
                replaceAll("\\bכדtag\\b", "כיהוה").
                replaceAll("\\bאלוקים\\b", "אלהים").
                replaceAll("\\bאלוקים\\b", "אלהים").
                replaceAll("\\bאלקים\\b", "אלהים").
                replaceAll("\\bואלקים\\b", "ואלהים").
                replaceAll("\\bואלוקים\\b", "ואלהים").
                replaceAll("\\bאלקיך\\b", "אלהיך").
                replaceAll("\\bמאלקיך\\b", "מאלהיך").
                replaceAll("\\bכאלוקים\\b", "כאלהים").
                replaceAll("\\bכאלקים\\b", "כאלהים").
                replaceAll("\\bלאלקים\\b", "לאלהים").
                replaceAll("\\bאלקיכם\\b", "אלהיכם").

                replace("tag", ""); // should be applied at the end since previous replacements dependes on ' (tag) char

        //System.out.println(result);
        return result;
    }
}
