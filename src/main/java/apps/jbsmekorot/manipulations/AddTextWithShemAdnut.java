package apps.jbsmekorot.manipulations;

import spanthera.NgramDocument;
import spanthera.Ngram;
import spanthera.NgramDocumentManipulation;

/**
 * Created by omishali on 08/01/2018.
            */
    public class AddTextWithShemAdnut implements NgramDocumentManipulation {
        public static final String ADNUT_TEXT = "adnut_text";
        @Override
        public void manipulate(NgramDocument doc) {
            // we used to cover spans of size 2 since only these spans
            // are get tagged however the manipulation RemoveNonSequentialTags requires
            // larger spans as well.
            for (Ngram s : doc.getAllNgrams()) {
                if (s.getTextFormatted().contains("יהוה"))
                    s.putExtra(ADNUT_TEXT, s.getTextFormatted().replace("יהוה", "אדני"));
            }
        }
}
