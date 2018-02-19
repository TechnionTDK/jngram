package apps.jbsmekorot;

import org.junit.Test;
import static org.junit.Assert.*;

import spanthera.SpannedDocument;
import spanthera.io.SpantheraIO;
import spanthera.io.Subject;
import spanthera.io.TaggerInput;

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
        SpannedDocument sd = JbsMekorot.findPsukimInSubject(subjects.get(11));

        RecallPrecision.RecallResult recallResult = calc.getRecall(sd);
        System.out.print("Recall: ");
        System.out.println(recallResult.getRecall());

        RecallPrecision.PrecisionlResult precisionResult = calc.getPrecision(sd);
        System.out.print("Precision: ");
        System.out.println(precisionResult.getPrecision());

        recallResult.printMissedSpans();
        precisionResult.printImpreciseSpans();
    }

    @Test
    public void testMesilatYesharimAll() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED2);
        assertNotNull(inputJson);
        List<Subject> subjects = inputJson.getSubjects();
        RecallPrecision calc = new RecallPrecision();

        List<SpannedDocument> sds = new ArrayList<>();
        for (int i=0; i<=12; i++) {
            SpannedDocument sd = JbsMekorot.findPsukimInSubject(subjects.get(i));
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
