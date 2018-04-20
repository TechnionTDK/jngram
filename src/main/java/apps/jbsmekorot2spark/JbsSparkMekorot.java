package apps.jbsmekorot2spark;

import apps.jbsmekorot.JbsSpanFormatter;
import apps.jbsmekorot.JbsTanachIndex;
import org.apache.hadoop.fs.FileStatus;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.net.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import spanthera.Span;
import spanthera.SpannedDocument;
import spanthera.io.Subject;
import spanthera.io.Tag;
import spanthera.io.TaggedSubject;
import spanthera.io.TaggerOutput;

import javax.security.auth.login.Configuration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.exit;

public class JbsSparkMekorot {
    public static final int MINIMAL_PASUK_LENGTH = 2;
    public static final int MAXIMAL_PASUK_LENGTH = 14;
    public static final int SPAN_INDEX_IN_ROW = 2;
    public static final int URI_INDEX_IN_ROW = 1;
    public static final int SUBJECT_URI_INDEX_IN_ROW = 0;
    private SparkSession sparkSession;

    public JbsSparkMekorot(SparkSession sparkSession)
    {
        this.sparkSession= sparkSession;
    }

    /**
     * arg[0] is the directory we want to work with. notice that it should be a directory without sub directories. e.g sub directory of jbs-text (like mesilatyesharim)
     * arg[1] is the output directory that the user want to get the results in.
     * @param args
     */
     public void main(String[] args)
    {
        if (args.length != 3) {
            System.out.println("Wrong arguments, should provide 3 arguments.");
            exit(0);
        }

        String inputDirPath= "hdfs://tdkstdsparkmaster:54310"+ args[0];
        String indexPath= args[1];
        String outDir = args[2];
        createFolderIfNotExists(outDir);
        TaggerOutput output;


        File dir = new File(inputDirPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json.spark"));
        System.out.println("dir: " + inputDirPath);
        //System.out.println("files: " + files.toString());

        output = findPsukimInDirectoryAux(inputDirPath, indexPath);


        try {
            PrintWriter writer = new PrintWriter(outDir + "/" + "output.json");
            writer.println("output file was created");
            writer.println(output.toString());
            writer.close();
            createFolderIfNotExists(outDir + "/test_out_dir");
            PrintWriter test_writer = new PrintWriter(outDir + "/test_out_dir" + "/" + "test_output.json");
            writer.println(output.toString());
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static void createFolderIfNotExists(String outputDirPath) {
        File dir = new File(outputDirPath);
        if (dir.isDirectory())
            return;
        dir.mkdir();
    }


    public TaggerOutput findPsukimInDirectoryAux(String dirPath, String indexPath)  {

        TaggerOutput outputJson = new TaggerOutput();

        String signleFile= dirPath + "/*.json.spark";
        System.out.println(signleFile);
        for(int i = 0 ; i < 1 ; i++){

            JavaRDD<Row> javaRDD = this.sparkSession.read().json(signleFile).javaRDD();
            JavaRDD<List<Row>> matches = javaRDD.map(x->findPsukimInJson(x,indexPath));
            List<List<Row>> outPutJsonsList = matches.collect();
            List<List<Row>> outPutJsonsListNotEmpty = new ArrayList<>();
            for(List<Row> rowList : outPutJsonsList){
                if(rowList.size() > 0){
                    outPutJsonsListNotEmpty.add(rowList);
                }
            }
            HashMap<String,TaggedSubject> taggedSubjectsMap = new HashMap<String,TaggedSubject>();
            for(List<Row> rowList : outPutJsonsListNotEmpty){
                Row temp_row = rowList.get(0);
                String subjectURI = (String) temp_row.get(0);
                if(!taggedSubjectsMap.containsKey(subjectURI)){
                    TaggedSubject taggedSubject = new TaggedSubject();
                    taggedSubject.setUri((String) temp_row.get(SUBJECT_URI_INDEX_IN_ROW));
                    taggedSubjectsMap.put(subjectURI,taggedSubject);
                }
                TaggedSubject taggedSubject = taggedSubjectsMap.get(subjectURI);

                for(Row row : rowList){
                    taggedSubject.addTag(new Tag(
                                    (String) row.get(SPAN_INDEX_IN_ROW),
                                    (String) row.get(URI_INDEX_IN_ROW)
                            )
                    );
                }

            }
            for(TaggedSubject taggedSubject : taggedSubjectsMap.values()){
                outputJson.addTaggedSubject(taggedSubject);
            }
        }

        return outputJson;
    }

    public static List<Row> findPsukimInJson(Row jSonName, String indexPath) {
//        int TEXT_INDEX = 1;
//        int URI_INDEX = 2;
        List<Row> retList = new ArrayList<>();
        Subject subject = new Subject((String) jSonName.getAs("uri"), (String) jSonName.getAs("text"));

        // a subject denotes a specific text element within the json file

        TaggedSubject taggedSubject = new TaggedSubject();
        String text = subject.getText();
        String uri = subject.getUri();

        if (text == null || uri == null) {
            System.out.println("Subject " + uri + " has not text or uri " + "(" + jSonName + ")");
            return retList;
        }

        SpannedDocument sd = new SpannedDocument(text, MINIMAL_PASUK_LENGTH, MAXIMAL_PASUK_LENGTH);
        findPsukim(sd, indexPath);
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
//        private String uri;
//        private List<Tag> tags;
        for(Tag tag : taggedSubject.getTags()){
            Row row = RowFactory.create(taggedSubject.getUri(),tag.getUri(),tag.getSpan());
            retList.add(row);
        }
//        Row row = RowFactory.create(taggedSubject.getUri(),taggedSubject.getTags());
//        retList.add(row);
        return retList;
    }
    public  static  void findPsukim(SpannedDocument sd,  String indexPath){
         findPsukimTopDown(sd, indexPath);
    };

    public static void findPsukimTopDown(SpannedDocument doc, String indexPath){
        doc.format(new JbsSpanFormatter());
        doc.add(new AddTextWithShemAdnutTopDown()).manipulate();
        doc.add(new PsukimTaggerTopDown(doc.length(),indexPath));
        //StopWatch tag_timer = new StopWatch();
        //double tag_timer_total = 0;
        //int span_size = 0;
        // int[] res_candidates = { 0 };
        for(int spanSize = doc.getMaximalSpanSize() ; spanSize >= doc.getMinimalSpanSize(); spanSize-- ){
            //span_size=spanSize;
            //System.out.println(">> DEBUG: measuring time for spans of size: "+ spanSize  );
            //tag_timer.start();
            doc.tag(spanSize);
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
    }
}
