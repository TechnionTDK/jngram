package apps.jbsmekorot2;
import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.RecallPrecision;
import org.jetbrains.annotations.NotNull;
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

    public class TestRecallPrecision2 {
        private static final String LABELED1 = "src/main/resources/labeledPsukimData/" + "tanach-midrashraba-1-labeled.json";
        private static final String LABELED2 = "src/main/resources/labeledPsukimData/" + "mesilatyesharim-labeled.json";
        private static final String ONE_SENTENCE = "src/main/resources/labeledPsukimData/" + "one_sentence.json";
        private static final int NUM_ITER = 1;

        @Before
        public void SetUpStream()
        {
            try {
                PrintStream out = new PrintStream( new FileOutputStream("PrecisionRecall.txt"));
                System.setOut(out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        @Test
        public void test1() {
            TaggerInput inputJson = SpantheraIO.readInputJson(LABELED1);
            assertNotNull(inputJson);
            List<Subject> subjects = inputJson.getSubjects();
            for(int i= 0; i<NUM_ITER; i++)
            {
                PrincRecallAndPrecison(subjects,i);
            }

        }

        private void PrincRecallAndPrecison(List<Subject> subjects, int index) {
            RecallPrecision calc = new RecallPrecision();

            // find psukim in first subject and calculate recall & precision
            NgramDocument sdTopDOwn = JbsMekorot2.findPsukimInSubject(subjects.get(index));
            NgramDocument sdBottomUp = JbsMekorot.findPsukimInSubject(subjects.get(index));

            PrintRecall(calc, sdTopDOwn, "TopDown");
            PrintRecall(calc, sdBottomUp, "BottomUp");

            PrintPrecision(calc, sdTopDOwn, "TopDown");
            PrintPrecision(calc, sdBottomUp, "BottomUp");
        }


        @Test
        public void test2() {
            TaggerInput inputJson = SpantheraIO.readInputJson(LABELED2);
            assertNotNull(inputJson);
            List<Subject> subjects = inputJson.getSubjects();

            RecallPrecision calc = new RecallPrecision();

            // find psukim in second subject and calculate recall & precision
           NgramDocument sdTopDOwn = JbsMekorot2.findPsukimInSubject(subjects.get(11));
            NgramDocument sdBottomUp = JbsMekorot.findPsukimInSubject(subjects.get(11));

            PrintRecall(calc, sdTopDOwn, "TopDown");
            PrintRecall(calc, sdBottomUp, "BottomUp");

            PrintPrecision(calc, sdTopDOwn, "TopDown");
            PrintPrecision(calc, sdBottomUp, "BottomUp");


        }
//        @Test
//        public void test3() {
//            TaggerInput inputJson = SpantheraIO.readInputJson(ONE_SENTENCE);
//            assertNotNull(inputJson);
//            List<Subject> subjects = inputJson.getSubjects();
//
//            RecallPrecision calc = new RecallPrecision();
//
//            // find psukim in second subject and calculate recall & precision
//            NgramDocument sdTopDOwn = JbsMekorot2.findPsukimInSubject(subjects.get(0));
//            NgramDocument sdBottomUp = JbsMekorot.findPsukimInSubject(subjects.get(0));
//
//            PrintRecall(calc, sdTopDOwn, "TopDown");
//            PrintRecall(calc, sdBottomUp, "BottomUp");
//
//            PrintPrecision(calc, sdTopDOwn, "TopDown");
//            PrintPrecision(calc, sdBottomUp, "BottomUp");
//
//
//        }

        public static void PrintPrecision(RecallPrecision calc, NgramDocument sdButtomUp, String method) {
            RecallPrecision.PrecisionlResult precisionResult = calc.getPrecision(sdButtomUp);
            System.out.print("Precision "+ method+ " : ");
            System.out.println(precisionResult.getPrecision());
            System.out.println(method + " imprecise: ");
            precisionResult.printImpreciseSpans();
        }

        @NotNull
        public static void PrintRecall(RecallPrecision calc, NgramDocument sd, String method) {
            RecallPrecision.RecallResult recallResult = calc.getRecall(sd);
            System.out.print("Recall "+ method +" : ");
            System.out.println(recallResult.getRecall());
            System.out.println(method + " misses  ");
            recallResult.printReport();

        }
    }
