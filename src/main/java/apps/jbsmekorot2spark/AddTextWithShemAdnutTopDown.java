package apps.jbsmekorot2spark;

import spanthera.Span;
import spanthera.SpanManipulation;
import spanthera.SpannedDocument;

public class AddTextWithShemAdnutTopDown implements  SpanManipulation {
        public static final String ADNUT_TEXT = "adnut_text";
        @Override
        public void manipulate(SpannedDocument doc) {
            for (Span s : doc.getAllSpans()) {
                if (s.getTextFormatted().contains("יהוה"))
                    s.putExtra(ADNUT_TEXT, s.getTextFormatted().replace("יהוה", "אדני"));
            }
        }
    }
