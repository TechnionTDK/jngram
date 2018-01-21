package apps.jbsmekorot2spark;

import org.apache.spark.sql.SparkSession;

public class jbsmekorotApp {
    public static void main(String[] args){
        SparkSession sparkSession = SparkSession.builder().appName("jbsmekorotApp").getOrCreate();
        JbsMekorot2 jbsMekorot2 = new JbsMekorot2(sparkSession);
        jbsMekorot2.main(args);
    }
}
