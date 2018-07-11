package apps.jbsmekorot;

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
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED2);
        assertNotNull(inputJson);
        List<Subject> subjects = inputJson.getSubjects();

        RecallPrecision calc = new RecallPrecision();

        // find psukim in first subject and calculate recall & precision
        NgramDocument sd = JbsMekorot.findPsukimInSubject(subjects.get(20));

        RecallPrecision.RecallResult recallResult = calc.getRecall(sd);
        recallResult.printReport();

        RecallPrecision.PrecisionlResult precisionResult = calc.getPrecision(sd);
        System.out.print("Precision: ");
        System.out.println(precisionResult.getPrecision());


        precisionResult.printImpreciseSpans();
    }

    @Test
    public void testMesilatYesharimAll() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED2);
        assertNotNull(inputJson);
        List<Subject> subjects = inputJson.getSubjects();
        RecallPrecision calc = new RecallPrecision();

        List<NgramDocument> sds = new ArrayList<>();
        for (int i=0; i<=17; i++) {
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
}
