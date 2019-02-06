package apps.bavlimekorot;

import apps.jbsmekorot.RecallPrecision;
import org.junit.Test;
import static org.junit.Assert.*;
import jngram.NgramDocument;
import jngram.io.SpantheraIO;
import jngram.io.Subject;
import jngram.io.TaggerInput;
import java.util.ArrayList;
import java.util.List;


public class TestBavliMekorot {

    private static final String LABELED_MISHNE_TORAH = "src/main/resources/labeledPsukimData/" + "mishnetorah-2-labeled.json";
    private static final String LABELED_MESILAT_YESHARIM = "src/main/resources/labeledPsukimData/" + "mesilatyesharim-labeled-bavli.json";

    @Test
    public void testMishneTorahPerek() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED_MISHNE_TORAH);
        assertNotNull(inputJson);
        _testPerekAux(inputJson, 0);
    }

    @Test
    public void testMishneTorahAll() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED_MISHNE_TORAH);
        assertNotNull(inputJson);
        _testAllAux(inputJson);
    }

    @Test
    public void testMesilatYesharimPerek() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED_MESILAT_YESHARIM);
        assertNotNull(inputJson);
        _testPerekAux(inputJson, 0);
    }

    @Test
    public void testMesilatYesharimAll() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED_MESILAT_YESHARIM);
        assertNotNull(inputJson);
        _testAllAux(inputJson);
    }

    public void _testPerekAux(TaggerInput inputJson, int whichText) {
        List<Subject> subjects = inputJson.getSubjects();
        RecallPrecision calc = new RecallPrecision();
        // find psukim in first subject and calculate recall & precision
        NgramDocument sd = BavliMekorot.findBavliMekorot(subjects.get(whichText));
        RecallPrecision.RecallResult recallResult = calc.getRecall(sd);
        recallResult.printReport();
        RecallPrecision.PrecisionlResult precisionResult = calc.getPrecision(sd);
        precisionResult.printReport();
    }

    public void _testAllAux(TaggerInput inputJson) {
        List<Subject> subjects = inputJson.getSubjects();
        RecallPrecision calc = new RecallPrecision();
        List<NgramDocument> sds = new ArrayList<>();
        for (int i=0; i<subjects.size(); i++) {
            NgramDocument sd = BavliMekorot.findBavliMekorot(subjects.get(i));
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
