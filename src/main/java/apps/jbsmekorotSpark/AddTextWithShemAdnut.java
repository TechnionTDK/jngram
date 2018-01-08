package apps.jbsmekorotSpark;

import spanthera.Span;
import spanthera.SpanManipulation;
import spanthera.SpannedDocument;

/**
 * Created by omishali on 08/01/2018.
            */
    public class AddTextWithShemAdnut implements SpanManipulation {
        public static final String ADNUT_TEXT = "adnut_text";
        @Override
        public void manipulate(SpannedDocument doc) {
            for (Span s : doc.getSpans(2)) {
                if (s.getTextFormatted().contains("יהוה"))
                    s.putExtra(ADNUT_TEXT, s.getTextFormatted().replace("יהוה", "אדני"));
            }
        }
}
