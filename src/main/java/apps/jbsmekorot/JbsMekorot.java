package apps.jbsmekorot;

import apps.jbsmekorot.manipulations.*;
import org.apache.commons.lang3.time.StopWatch;
import spanthera.NgramDocument;
import spanthera.Ngram;
import spanthera.io.*;
import spanthera.manipulations.MergeSiblingSpans;
import spanthera.manipulations.RemoveTagsInContainedSpans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
    private static List<String> ignoreDirs = new ArrayList<>(Arrays.asList(new String[]{".git", "manual", "tanach"}));
    private static List<String> includeDirs = new ArrayList<>(Arrays.asList(new String[]{"likuteymoharan"})); // if non-empty, only these directories will be analyzed.
    /**
     *
     * @param args arg1: path to input directory, basically it should be a path to "jbs-text"
     *             arg2: path to output directory.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong arguments, should provide 2 arguments.");
            exit(0);
        }

        if (args[0].equals("-text")) {
            // here we expect a text in the second argument, where psukim
            // should be identified in this text (for Dor's project).
            // output json is sent to standard output.
            String text = args[1];
            NgramDocument sd = new NgramDocument(text, MINIMAL_PASUK_LENGTH, MAXIMAL_PASUK_LENGTH);
            findPsukim(sd);
            TaggedSubject taggedSubject = getTaggedSubject(sd, "http://sometext");
            TaggerOutput outputJson = new TaggerOutput();
            outputJson.addTaggedSubject(taggedSubject);
            System.out.println(outputJson.toString());
            exit(1);
        }

        // init timers to take time measurements
        StopWatch timerPerDir = new StopWatch();
        StopWatch timerTotal = new StopWatch();

        String rootDirPath = args[0];
        String outputDirPath = args[1];

        createFolderIfNotExists(outputDirPath);

        timerTotal.start();

        File rootDir = new File(rootDirPath);
        for (File subDir : rootDir.listFiles()) {
            if (!subDir.isDirectory() || ignoreDirs.contains(subDir.getName()))
                continue;
            if (includeDirs.size() != 0)
                if (!includeDirs.contains(subDir.getName()))
                    continue;

            // Now we have a directory that should be analyzed.

            // First, we create the appropriate output folder
            // In this folder we will output a single json file,
            // regardless of how many jsons the input folder contains.
            createFolderIfNotExists(outputDirPath + "/" + subDir.getName());

            System.out.println("Analyze " + subDir.getName() + "...");
            timerPerDir.reset(); timerPerDir.start();

            TaggerOutput output = findPsukimInDirectory(subDir.getName(), rootDirPath);

            timerPerDir.stop();
            System.out.println("TOTAL: " + timerPerDir.toString());

            // now we write the result to the output folder
            try {
                PrintWriter writer = new PrintWriter(outputDirPath + "/" + subDir.getName() + "/" + subDir.getName() + ".json");
                writer.println(output.toString());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // and eventually we print the total execution time
        System.out.println("TOTAL TIME: " + timerTotal.toString());
    }

    private static void createFolderIfNotExists(String outputDirPath) {
        File dir = new File(outputDirPath);
        if (dir.isDirectory())
            return;
        dir.mkdir();
    }

    public static void findPsukim(NgramDocument doc) {
        doc.format(new JbsNgramFormatter());
        doc.add(new AddTextWithShemAdnut()).manipulate(); // should appear before PsukimTagger & after JbsNgramFormatter
        doc.add(new PsukimTagger()).tag();
        doc.add(new MergeSiblingSpans()).manipulate();
        doc.add(new RemoveTagsInContainedSpans()).manipulate();
        doc.add(new RemoveTagsFromOverlappingSpans()).manipulate();
        doc.add(new FilterTagsFromSpans(doc)).manipulate();
        doc.add(new RemoveNonSequentialTags()).manipulate();
        doc.add(new CalcAndFilterByEditDistance()).manipulate();
        doc.add(new RemoveNonEheviFuzzyMatches()).manipulate();
    }

     /**
      * Find psukim in all json files under dirName.
      * dirName is assumed to be the name of a directory right under INPUT_DIR.
      * A single json result holds the entire result, no matter hoe many
      * different jsons the directory contains.
     * @param dirName
     */
    public static TaggerOutput findPsukimInDirectory(String dirName, String rootDir) {

        TaggerOutput outputJson = new TaggerOutput();
        String[] jsonNames = SpantheraIO.getJsonsInDir(rootDir + "/" + dirName);

        for (String name : jsonNames) {
            TaggerInput inputJson = SpantheraIO.readInputJson(rootDir + "/" + dirName + "/" + name);
            List<Subject> subjects = inputJson.getSubjects();

            // a subject denotes a specific text element within the json file
            for(Subject s : subjects) {
                NgramDocument sd = findPsukimInSubject(s);
                TaggedSubject taggedSubject = getTaggedSubject(sd, s.getUri());
                outputJson.addTaggedSubject(taggedSubject);
            }

            //System.out.println(outputJson.toString());
            //System.out.println(subjects.get(0).getUri());
            //System.out.println(subjects.get(0).getText());
        }
        return outputJson;
    }

    public static NgramDocument findPsukimInSubject(Subject s) {
        String text = s.getText();
        String uri = s.getUri();

        if (text == null || uri == null) {
            System.out.println("Subject " + s.getUri() + " has not text or uri");
            return null;
        }

        NgramDocument sd = new NgramDocument(text, MINIMAL_PASUK_LENGTH, MAXIMAL_PASUK_LENGTH);
        findPsukim(sd);

        return sd;
    }

    public static TaggedSubject getTaggedSubject(NgramDocument sd, String uri) {
        TaggedSubject taggedSubject = new TaggedSubject();
        taggedSubject.setUri(uri);
        for (Ngram ngram : sd.getAllNgrams()) {
            if (ngram.getTags().size() == 0)
                continue;
            for (String tag : ngram.getTags()) {
                Tag t = new Tag(ngram.getStart(), ngram.getEnd(), tag);
                taggedSubject.addTag(t);
            }
        }

        return taggedSubject;
    }


}
