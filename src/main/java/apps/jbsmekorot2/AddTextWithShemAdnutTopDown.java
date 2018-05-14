package apps.jbsmekorot2;

import spanthera.NgramDocument;
import spanthera.Ngram;
import spanthera.NgramDocumentManipulation;

public class AddTextWithShemAdnutTopDown implements NgramDocumentManipulation {
        public static final String ADNUT_TEXT = "adnut_text";
        @Override
        public void manipulate(NgramDocument doc) {
            for (Ngram s : doc.getAllNgrams()) {
                if (s.getTextFormatted().contains("יהוה"))
                    s.putExtra(ADNUT_TEXT, s.getTextFormatted().replace("יהוה", "אדני"));
            }
        }
    }
