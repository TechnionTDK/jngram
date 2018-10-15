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
import java.util.List;
import jngram.io.Tag;


public class TestRecallPrecisionBavli {

    private static final String LABELED = "src/main/resources/labeledPsukimData/" + "mishnetorah-2-labeled.json";

//    @Test
//    public void exposeTaggerProblem() {
//        String quoteFromOneNineTwo = "יהושע אומר";
//        List<Tag> results = BavliMekorot.findTextMekorot(quoteFromOneNineTwo, false);
//        // In the next line the assertion comes out true. It's here to show that this method generally works and it does find matches
//        assertTrue(results.contains(new Tag("0-1", "jbr:text-bavli-5-32-2")));
//        // In the next line the assertion fails although the string is definitely a quote from 1-9-2.
//        assertTrue(results.contains(new Tag("0-1", "jbr:text-bavli-1-9-2")));
//        return;
//    }

    @Test
    public void testMishneTorahPerek() {
        TaggerInput inputJson = SpantheraIO.readInputJson(LABELED);
        assertNotNull(inputJson);
        List<Subject> subjects = inputJson.getSubjects();

        RecallPrecision calc = new RecallPrecision();

        // find psukim in first subject and calculate recall & precision
        NgramDocument sd = BavliMekorot.findSubjectMekorot(subjects.get(2));

        RecallPrecision.RecallResult recallResult = calc.getRecall(sd);
        recallResult.printReport();

        RecallPrecision.PrecisionlResult precisionResult = calc.getPrecision(sd);
        precisionResult.printReport();
    }

//    @Test
//    public void testMishneTorahAll() {
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
