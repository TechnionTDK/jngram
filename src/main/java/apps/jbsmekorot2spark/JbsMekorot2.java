package apps.jbsmekorot2spark;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import apps.jbsmekorot.JbsSpanFormatter;
import com.google.gson.Gson;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRow;
import spanthera.Span;
import spanthera.SpannedDocument;
import spanthera.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.exit;

/**
 * Created by orasraf on 12/11/2017.
 */
public class JbsMekorot2 extends JbsSparkMekorot{
    public JbsMekorot2(SparkSession sparkSession) {
        super(sparkSession);
    }



   /* public static void findPsukimTopDown(SpannedDocument doc){
        doc.format(new JbsSpanFormatter());
        doc.add(new AddTextWithShemAdnutTopDown()).manipulate();
        doc.add(new PsukimTaggerTopDown(doc.length()));
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
    }*/



   /* @Override
    public void findPsukim(SpannedDocument sd) {
        findPsukimTopDown(sd);
    }*/


}
