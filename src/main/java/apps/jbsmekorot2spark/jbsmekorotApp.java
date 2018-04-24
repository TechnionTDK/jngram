package apps.jbsmekorot2spark;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

public class jbsmekorotApp {
    public static void main(String[] args){
        SparkSession sparkSession = SparkSession.builder().appName("jbsmekorotApp").getOrCreate();
        sparkSession.conf().set("spark.shuffle.service.enabled", "false");
        sparkSession.conf().set("spark.dynamicAllocation.enabled", "false");
        sparkSession.conf().set("spark.io.compression.codec", "snappy");
        sparkSession.conf().set("spark.rdd.compress", "true");
        sparkSession.conf();
        JbsSparkMekorot jbsMekorot = new JbsMekorot2(sparkSession);
        jbsMekorot.main(args);
    }

    /*public void main(String[] args)
    {
        if (args.length != 2) {
            System.out.println("Wrong arguments, should provide 2 arguments.");
            exit(0);
        }
        String dirPath= "hdfs://tdkstdsparkmaster:54310/"+ args[0];
        String dirName= new File(dirPath).getParentFile().getName();
        //String dirPath= "hdfs://tdkstdsparkmaster:54310/user/svitak/jbs-text/mesilatyesharim/mesilatyesharim.json.spark";
        String outDir = args[1];
        createFolderIfNotExists(outDir);
        TaggerOutput output = findPsukimInDirectoryAux(dirPath);
        try {
            PrintWriter writer = new PrintWriter(outDir + "/" + dirName+".json");
            writer.println("output file was created");
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
    }*/


}
