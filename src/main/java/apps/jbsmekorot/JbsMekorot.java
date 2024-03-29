package apps.jbsmekorot;

import apps.jbsmekorot.manipulations.*;
import jngram.manipulations.MergeToMaximalNgrams;
import jngram.manipulations.RemoveNonEheviFuzzyMatches;
import org.apache.commons.lang3.time.StopWatch;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.io.*;
import jngram.manipulations.RemoveTagsInContainedNgrams;

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
    public static final int CERTAIN_NGRAM_SIZE = 4;
    public static final int MAXIMAL_DISTANCE_FROM_CERTAIN_NGRAM = 70;
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
        StopWatch timer = new StopWatch();
        timer.start();
        System.out.println("START " + timer.toString());
        doc.format(new JbsNgramFormatter());
        System.out.println("AFTER JbsNgramFormatter " + timer.toString());
        doc.add(new AddTextWithShemAdnut()); // should appear before PsukimTagger & after JbsNgramFormatter
        System.out.println("AFTER AddTextWithShemAdnut " + timer.toString());
        doc.add(new PsukimTagger());
        System.out.println("AFTER PsukimTagger " + timer.toString());

        // going bottom-up to maximal ngram matches:
        doc.add(new MergeToMaximalNgrams());
        System.out.println("AFTER MergeToMaximalNgrams " + timer.toString());
        doc.add(new RemoveTagsInContainedNgrams());
        System.out.println("AFTER RemoveTagsInContainedNgrams " + timer.toString());
        doc.add(new  ResolveOverlappingNgramsWithDifferentTags());
        System.out.println("AFTER ResolveOverlappingNgramsWithDifferentTags " + timer.toString());


        // cleaning operations. At first we applied them at the end
        // because of performance issues, but then we got incorrect results and they are applied here.
        doc.add(new RemoveNonSequentialTags());
        System.out.println("AFTER RemoveNonSequentialTags " + timer.toString());
//        we placed this block before since we found that we mark ngrams
//                with certain size and then we remove them, what causes false
//                proximity ngrams. but we have a significant performance issue here...
//        these are the heaviest methods, can we do something here????
        doc.add(new CalcAndFilterByEditDistance());
        System.out.println("AFTER CalcAndFilterByEditDistance " + timer.toString());
        doc.add(new PsukimRemoveNonEhevy());
        System.out.println("AFTER PsukimRemoveNonEhevy " + timer.toString());

        // Mark "certain" tags. We have different levels of certainty (see class Certain).
        doc.add(new MarkCertainBySize());
        System.out.println("AFTER MarkCertainBySize " + timer.toString());
        doc.add(new MarkCertainByProximity());
        System.out.println("AFTER MarkCertainByProximity " + timer.toString());
        doc.add(new MarkCertainByHintWords());
        System.out.println("AFTER MarkCertainByHintWords " + timer.toString());

        // Remove tags based on previous marks
        doc.add(new RemoveTagsBasedOnMarks());
        System.out.println("AFTER RemoveTagsBasedOnMarks " + timer.toString());
        timer.stop();
        System.out.println("TOTAL " + timer.toString());

        // DEBUG
        System.out.println("=== DEBUG INFO ===");
//        in proximity do we consider only certain size ngrams??
//       doc.getNgram(121, 124).printDebugInfo();
//        doc.getNgram(227, 229).printDebugInfo();
//        for (Ngram ng : doc.getAllNgramsWithTags())
//            ng.printDebugInfo();
        System.out.println("=== DEBUG INFO ===");
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

            System.out.println(outputJson.toString());
            System.out.println(subjects.get(0).getUri());
            System.out.println(subjects.get(0).getText());
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
