package apps.jbsmekorot2spark;
import org.apache.spark.sql.SparkSession;

public class jbsmekorotApp {
    public static void main(String[] args){
        SparkSession sparkSession = SparkSession.builder().appName("jbsmekorotApp").getOrCreate();
        JbsSparkMekorot jbsMekorot = new JbsMekorot2(sparkSession);
        jbsMekorot.main(args);
    }

}
