package apps.bavlimekorot;

import apps.jbsmekorot.RecallPrecision;
import apps.bavlimekorot.BavliMekorot;
import org.junit.Test;
import static org.junit.Assert.*;
import jngram.NgramDocument;
import jngram.io.SpantheraIO;
import jngram.io.Subject;
import jngram.io.TaggerInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jngram.io.Tag;


public class TestBavliMekorot {

    private static final String LABELED_MISHNE_TORAH = "src/main/resources/labeledPsukimData/" + "mishnetorah-2-labeled.json";
    private static final String LABELED_MESILAT_YESHARIM = "src/main/resources/labeledPsukimData/" + "mesilatyesharim-labeled-bavli.json";

    @Test
    public void testMishneTorahPerek() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED_MISHNE_TORAH);
        assertNotNull(inputJson);
        _testPerekAux(inputJson);
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
        _testPerekAux(inputJson);
    }

    @Test
    public void testMesilatYesharimAll() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED_MESILAT_YESHARIM);
        assertNotNull(inputJson);
        _testAllAux(inputJson);
    }

    public void _testPerekAux(TaggerInput inputJson) {
        List<Subject> subjects = inputJson.getSubjects();
        RecallPrecision calc = new RecallPrecision();
        // find psukim in first subject and calculate recall & precision
        NgramDocument sd = BavliMekorot.findSubjectMekorot(subjects.get(0));
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
            NgramDocument sd = BavliMekorot.findSubjectMekorot(subjects.get(i));
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
