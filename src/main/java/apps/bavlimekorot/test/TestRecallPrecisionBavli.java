package apps.bavlimekorot.test;

import apps.jbsmekorot.RecallPrecision;
import apps.bavlimekorot.main.BavliMekorot;
import org.junit.Test;
import static org.junit.Assert.*;
import jngram.NgramDocument;
import jngram.io.SpantheraIO;
import jngram.io.Subject;
import jngram.io.TaggerInput;
import java.util.ArrayList;
import java.util.List;


public class TestRecallPrecisionBavli {

    private static final String LABELED = "src/main/resources/labeledPsukimData/" + "mesilatyesharim-labeled-bavli.json";

    @Test
    public void testMesilatYesharimPerek() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED);
        assertNotNull(inputJson);
        List<Subject> subjects = inputJson.getSubjects();

        RecallPrecision calc = new RecallPrecision();

        // find psukim in first subject and calculate recall & precision
        NgramDocument sd = BavliMekorot.findSubjectMekorot(subjects.get(0));

        RecallPrecision.RecallResult recallResult = calc.getRecall(sd);
        recallResult.printReport();

        RecallPrecision.PrecisionlResult precisionResult = calc.getPrecision(sd);
        precisionResult.printReport();
    }

//    @Test
//    public void testMesilatYesharimAll() {
//        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED);
//        assertNotNull(inputJson);
//        List<Subject> subjects = inputJson.getSubjects();
//        RecallPrecision calc = new RecallPrecision();
//
//        List<NgramDocument> sds = new ArrayList<>();
//        for (int i=0; i<subjects.size(); i++) {
//            NgramDocument sd = BavliMekorot.findSubjectMekorot(subjects.get(i));
//            sds.add(sd);
//        }
//
//        RecallPrecision.MultPrecisionResult multPrecisionResult = calc.getPrecision(sds);
//        RecallPrecision.MultRecallResult multRecallResult = calc.getRecall(sds);
//
//        System.out.print("Average recall: ");
//        System.out.println(multRecallResult.getAverageRecall());
//
//        System.out.print("Average precision: ");
//        System.out.println(multPrecisionResult.getAveragePrecision());
//    }

}
