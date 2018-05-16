package apps.jbsmekorot2;

import apps.jbsmekorot.RecallPrecision;
import org.junit.Before;
import org.junit.Test;
import jngram.NgramDocument;
import jngram.io.SpantheraIO;
import jngram.io.Subject;
import jngram.io.TaggerInput;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class PsukimTaggerTopDownTests {
    private static final String LABELED1 = "src/main/resources/labeledPsukimData/" + "tanach-midrashraba-1-labeled.json";
    private static final String LABELED2 = "src/main/resources/labeledPsukimData/" + "mesilatyesharim-labeled.json";

    private RecallPrecision madad;
    @Before
    public void Init(){
        madad= new RecallPrecision();
        try {
            PrintStream out = new PrintStream( new FileOutputStream("outTopDown.txt"));
            System.setOut(out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void Test1()
    {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED2);
        assertNotNull(inputJson);
        List<Subject> subjects = inputJson.getSubjects();
        NgramDocument doc= JbsMekorot2.findPsukimInSubject(subjects.get(0));
        System.out.println("Recall is: "+ madad.getRecall(doc).getRecall()+ "\n Precision is: " + madad.getPrecision(doc).getPrecision());
        System.out.println(doc.toString());
        TestRecallPrecision2.PrintRecall(madad,doc, "");
        TestRecallPrecision2.PrintPrecision(madad,doc, "");
    }
}
