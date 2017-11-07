package apps.jbsmekorot;

import spanthera.SpannedDocument;
import spanthera.io.SpantheraIO;
import spanthera.io.Subject;
import spanthera.io.TaggerInput;
import spanthera.manipulations.MergeSiblingSpans;
import spanthera.manipulations.RemoveTagsInContainedSpans;

import java.util.List;

/**
 * Created by omishali on 23/10/2017.
 */
public class JbsMekorot {
    public static final String INPUT_DIR = "jbs-text/";
    public static final String OUTPUT_DIR = "jbs-text-tagged/";
    public static final int MINIMAL_PASUK_LENGTH = 2;
    public static final int MAXIMAL_PASUK_LENGTH = 14;

    public static void findPsukim(SpannedDocument doc) {
        doc.add(new PsukimTagger()).tag();
        doc.add(new MergeSiblingSpans()).manipulate();
        doc.add(new RemoveTagsInContainedSpans()).manipulate();

        doc.add(new FilterTagsFromSpansSize3(doc)).manipulate();
        doc.add(new FilterTagsFromSpansSize2(doc)).manipulate();
    }

    /**
     * Find psukim in all json files under dirName.
     * dirName is assumed to be the name of a directory right under INPUT_DIR.
     * The output jsons go to OUTPUT_DIR/dirName
     * @param dirName
     */
    public static void findPsukimInDirectory(String dirName) {

        String[] jsonNames = SpantheraIO.getJsonsInDir(INPUT_DIR + dirName);

        for (String name : jsonNames) {
            TaggerInput in = SpantheraIO.readInputJson(INPUT_DIR + dirName + "/" + name);
            List<Subject> subjects = in.getSubjects();

            // a subject denotes a specific text element
            for(Subject s : subjects) {
                String text = s.getText();
                String uri = s.getUri();
                SpannedDocument sd = new SpannedDocument(text, MINIMAL_PASUK_LENGTH, MAXIMAL_PASUK_LENGTH);
                findPsukim(sd);
                System.out.println("===== " + uri + " =====");
                System.out.println(sd.toString());
                // now we should output the result to a file & directory...
            }

            //System.out.println(subjects.get(0).getUri());
            //System.out.println(subjects.get(0).getText());
        }
    }

    public static String format(String s) {
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
                replaceAll("\\bכאלוקים\\b", "כאלהים").
                replaceAll("\\bכאלקים\\b", "כאלהים").
                replaceAll("\\bאלקיכם\\b", "אלהיכם").

                replace("tag", ""); // should be applied at the end since previous replacements dependes on ' (tag) char

        //System.out.println(result);
        return result;
    }


}
