package apps.jbsmekorot;

import org.junit.Test;
import static org.junit.Assert.*;

import spanthera.SpannedDocument;
import spanthera.io.SpantheraIO;
import spanthera.io.Subject;
import spanthera.io.TaggerInput;

import java.util.List;

/**
 * Created by omishali on 14/12/2017.
 */
public class TestRecallPrecision {
    private static final String LABELED1 = "src/main/resources/labeledPsukimData/" + "tanach-midrashraba-1-labeled.json";
    private static final String LABELED2 = "src/main/resources/labeledPsukimData/" + "mesilatyesharim-labeled.json";
    @Test
    public void test1() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED2);
        assertNotNull(inputJson);
        List<Subject> subjects = inputJson.getSubjects();

        RecallPrecision calc = new RecallPrecision();

        // find psukim in first subject and calculate recall & precision
        SpannedDocument sd = JbsMekorot.findPsukimInSubject(subjects.get(11));
        System.out.println(calc.getRecall(sd));
        //System.out.println(calc.getPrecision(sd));


    }
}