package apps.jbsmekorot2;

import apps.jbsmekorot.JbsNgramFormatter;
import apps.jbsmekorot.RecallPrecision;
import jngram.NgramTagger;
import org.apache.commons.lang3.time.StopWatch;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.io.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.exit;

/**
 * Created by orasraf on 12/11/2017.
 */
public class JbsMekorot2 {
    public static final int MINIMAL_PASUK_LENGTH = 2;
    public static final int MAXIMAL_PASUK_LENGTH = 14;
    private static List<String> ignoreDirs = new ArrayList<>(Arrays.asList(new String[]{".git", "manual", "tanach"}));
    private static List<String> includeDirs = new ArrayList<>(Arrays.asList(new String[]{})); // if non-empty, only these directories will be analyzed.

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

            //System.out.println("Analyze " + subDir.getName() + "...");
            timerPerDir.reset(); timerPerDir.start();

            TaggerOutput output = findPsukimInDirectory(subDir.getName(), rootDirPath);

            timerPerDir.stop();
            //System.out.println("TOTAL: " + timerPerDir.toString());

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
       // System.out.println("TOTAL TIME: " + timerTotal.toString());
    }

    private static void createFolderIfNotExists(String outputDirPath) {
        File dir = new File(outputDirPath);
        if (dir.isDirectory())
            return;
        dir.mkdir();
    }

    public static NgramDocument findPsukimInSubject(Subject s) {
        String text = s.getText();
        String uri = s.getUri();

        if (text == null || uri == null) {
            System.out.println("Subject " + uri + " has not text or uri");
            return null;
        }

        NgramDocument sd = new NgramDocument(text, MINIMAL_PASUK_LENGTH, MAXIMAL_PASUK_LENGTH);
        System.out.println("\nNgramDocument name :" + uri);
        findPsukimTopDown(sd);

        return sd;
    }
    public static void findPsukimTopDown(NgramDocument doc){
        doc.format(new JbsNgramFormatter());
        doc.add(new AddTextWithShemAdnutTopDown());
        NgramTagger tagger = new PsukimTaggerTopDown(doc.length());
        StopWatch tag_timer = new StopWatch();
        double tag_timer_total = 0;
        int span_size = 0;
       // int[] res_candidates = { 0 };
        for(int spanSize = doc.getMaximalNgramSize(); spanSize >= doc.getMinimalNgramSize(); spanSize-- ){
            //span_size=spanSize;
            //System.out.println(">> DEBUG: measuring time for spans of size: "+ spanSize  );
            //tag_timer.start();
            doc.add(tagger, spanSize);
            //System.out.println(">> DEBUG: result for spans of size: "+ spanSize + "is : "   + tag_timer.getNanoTime());
            //tag_timer_total = tag_timer.getNanoTime()/Math.pow(10,9);
            //DecimalFormat df = new DecimalFormat("#.##");
            //String time_s = df.format(tag_timer_total);
            //tag_timer.reset();
//            System.out.println(">> Performance Test: avarage time to tag span sized     " + span_size + ":  "
//                    + tag_timer_total/res_candidates[0] +"  #spans:     "
//                    +res_candidates[0] + ",total:   "+time_s  +"    , (maxEdits = "+ 2 +" per word)" );
//            res_candidates[0]=0;
        }



        //doc.add(new MarkCertainByProximity(doc)).manipulate();
        //doc.add(new FilterTagsFromSpansSize2(doc)).manipulate();
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
        List<NgramDocument> docs= new ArrayList<>();
        StopWatch watch= new StopWatch();
        watch.start();
        for (String name : jsonNames) {
            TaggerInput inputJson = SpantheraIO.readInputJson(rootDir + "/" + dirName + "/" + name);
            List<Subject> subjects = inputJson.getSubjects();

            // a subject denotes a specific text element within the json file
            for(Subject s : subjects) {
                TaggedSubject taggedSubject = new TaggedSubject();
                String text = s.getText();
                String uri = s.getUri();

                if (text == null || uri == null) {
                    System.out.println("Subject " + s.getUri() + " has not text or uri " + "(" + name + ")");
                    continue;
                }

                NgramDocument sd = new NgramDocument(text, MINIMAL_PASUK_LENGTH, MAXIMAL_PASUK_LENGTH);
                findPsukimTopDown(sd);
                System.out.println("===== " + uri + " =====");
                System.out.println(sd.toString());
                // now we should output the result to a file & directory...
                taggedSubject.setUri(uri);
                for (Ngram ngram : sd.getAllNgrams()) {
                    if (ngram.getTags().size() == 0)
                        continue;
                    for (String tag : ngram.getTags()) {
                        Tag t = new Tag(ngram.getStart(), ngram.getEnd(), tag);
                        taggedSubject.addTag(t);
                    }
                }
                docs.add(sd);
                System.out.println(taggedSubject.getUri() + "...");
                outputJson.addTaggedSubject(taggedSubject);
            }

            //System.out.println(outputJson.toString());
            //System.out.println(subjects.get(0).getUri());
            //System.out.println(subjects.get(0).getText());
        }
        watch.stop();
        PtintRecallAndRes(docs, watch.toString());
        return outputJson;
    }

    public static void PtintRecallAndRes(List<NgramDocument> docs, String time)
    {

        RecallPrecision madad= new RecallPrecision();
        PrintStream stdout= System.out;
        try {
            PrintStream out = new PrintStream( new FileOutputStream("C:\\Users\\vkopilov\\Pictures\\OutSpanthera\\outBottomUpMesilatYesharimLabled_afterchange.txt"));
            System.setOut(out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        RecallPrecision.MultRecallResult recallRes= madad.getRecall(docs);
        RecallPrecision.MultPrecisionResult presRes= madad.getPrecision(docs);
        System.out.println("Total time: " +time);
        System.out.println("Avarge Recall is: "+recallRes.getAverageRecall()+ "\n Average Precision is: " + presRes.getAveragePrecision());
        for (NgramDocument doc: docs) {

            RecallPrecision.RecallResult recallOneRes= madad.getRecall(doc);
            RecallPrecision.PrecisionlResult presOneRes= madad.getPrecision(doc);
            System.out.println("Recall is: "+recallOneRes.getRecall() +"\nPrecision is: " + presOneRes.getPrecision());
            recallOneRes.printReport();
            presOneRes.printReport();
        }
        System.setOut(stdout);
    }


}
