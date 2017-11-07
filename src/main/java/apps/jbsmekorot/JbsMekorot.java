package apps.jbsmekorot;

import spanthera.Span;
import spanthera.SpannedDocument;
import spanthera.io.*;
import spanthera.manipulations.MergeSiblingSpans;
import spanthera.manipulations.RemoveTagsInContainedSpans;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.exit;

/**
 * Created by omishali on 23/10/2017.
 */
public class JbsMekorot {
    public static final int MINIMAL_PASUK_LENGTH = 2;
    public static final int MAXIMAL_PASUK_LENGTH = 14;
    private static List<String> ignoreDirs = new ArrayList<>(Arrays.asList(new String[]{".git"}));
    /**
     *
     * @param args arg1: path to input directory, arg2: path to output directory.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong arguments, should provide 2 arguments.");
            exit(0);
        }
        String outputDirPath = args[1];

        File rootDir = new File(args[0]);
        for (File subDir : rootDir.listFiles()) {
            if (!subDir.isDirectory() || ignoreDirs.contains(subDir.getName()))
                continue;
            System.out.println(subDir.getName());
        }
    }

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
      * The output jsons go to outputDir/dirName
     * @param dirName
     */
    public static void findPsukimInDirectory(String dirName, String inputDir, String outputDir) {

        TaggerOutput outputJson = new TaggerOutput();
        String[] jsonNames = SpantheraIO.getJsonsInDir(inputDir + dirName);

        for (String name : jsonNames) {
            TaggerInput inputJson = SpantheraIO.readInputJson(inputDir + dirName + "/" + name);
            List<Subject> subjects = inputJson.getSubjects();

            // a subject denotes a specific text element within the json file
            for(Subject s : subjects) {
                TaggedSubject taggedSubject = new TaggedSubject();
                String text = s.getText();
                String uri = s.getUri();
                SpannedDocument sd = new SpannedDocument(text, MINIMAL_PASUK_LENGTH, MAXIMAL_PASUK_LENGTH);
                findPsukim(sd);
                //System.out.println("===== " + uri + " =====");
                //System.out.println(sd.toString());
                // now we should output the result to a file & directory...
                taggedSubject.setUri(uri);
                for (Span span : sd.getAllSpans()) {
                    if (span.getTags().size() == 0)
                        continue;
                    for (String tag : span.getTags()) {
                        Tag t = new Tag(span.getStart(), span.getEnd(), tag);
                        taggedSubject.addTag(t);
                    }
                }

                System.out.println(taggedSubject.getUri() + "...");
                outputJson.addTaggedSubject(taggedSubject);
            }

            System.out.println(outputJson.toString());

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
