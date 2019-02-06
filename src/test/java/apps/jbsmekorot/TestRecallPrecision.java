package apps.jbsmekorot;

import jngram.Ngram;
import org.junit.Test;
import static org.junit.Assert.*;

import jngram.NgramDocument;
import jngram.io.SpantheraIO;
import jngram.io.Subject;
import jngram.io.TaggerInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omishali on 14/12/2017.
 */
public class TestRecallPrecision {
    private static final String LABELED1 = "src/main/resources/labeledPsukimData/" + "tanach-midrashraba-1-labeled.json";
    private static final String LABELED2 = "src/main/resources/labeledPsukimData/" + "mesilatyesharim-labeled.json";
    @Test
    public void testMesilatYesharimPerek() {
        List<Subject> subjects = getSubjects(LABELED2);

        RecallPrecision calc = new RecallPrecision();

        // find psukim in a specific subject and calculate recall & precision
        NgramDocument sd = JbsMekorot.findPsukimInSubject(subjects.get(11));

        RecallPrecision.RecallResult recallResult = calc.getRecall(sd);
        recallResult.printReport();

        RecallPrecision.PrecisionlResult precisionResult = calc.getPrecision(sd);
        precisionResult.printReport();
    }

    @Test
    public void testMidrashRabaUnit() {
        List<Subject> subjects = getSubjects(LABELED1);
        RecallPrecision calc = new RecallPrecision();
        // find psukim in first subject and calculate recall & precision
        NgramDocument sd = JbsMekorot.findPsukimInSubject(subjects.get(0));

        RecallPrecision.RecallResult recallResult = calc.getRecall(sd);
        recallResult.printReport();
        RecallPrecision.PrecisionlResult precisionResult = calc.getPrecision(sd);
        precisionResult.printReport();

        Ngram ng = sd.getNgram(72, 74);
        ng.printDebugInfo();
    }

    @Test
    public void testEntireJson() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED1);
        assertNotNull(inputJson);
        List<Subject> subjects = inputJson.getSubjects();
        RecallPrecision calc = new RecallPrecision();

        List<NgramDocument> sds = new ArrayList<>();
        for (int i=0; i<=14; i++) {
            NgramDocument sd = JbsMekorot.findPsukimInSubject(subjects.get(i));
            sds.add(sd);
        }

        RecallPrecision.MultPrecisionResult multPrecisionResult = calc.getPrecision(sds);
        RecallPrecision.MultRecallResult multRecallResult = calc.getRecall(sds);

        System.out.print("Average recall: ");
        System.out.println(multRecallResult.getAverageRecall());

        System.out.print("Average precision: ");
        System.out.println(multPrecisionResult.getAveragePrecision());
    }

    List<Subject> getSubjects(String jsonPath) {
        TaggerInput inputJson = SpantheraIO.readInputJson(jsonPath);
        return inputJson.getSubjects();
    }
}
