package apps.jbsmekorot.manipulations;

import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;
import jngram.NgramManipulation;

/**
 * In the texts where we search for quotations, "shem hashem" is mentioned a lot,
 * usually using abbreviations. The formatter replaces these abbreviations with
 * "shem havaya" (yod he vav he). However in some (quite rare) cases this is not correct
 * since in the Tanach the name that appears is "shem Adnut". Therefore, we run this
 * manipulation to create another text version for each span where "shem havaya" appears,
 * a version with "shem adnut". When we search for matchings, we consult this second version
 * too.
 * Created by omishali on 08/01/2018.
 *
 */
    public class AddTextWithShemAdnut extends NgramManipulation {
        public static final String ADNUT_TEXT = "adnut_text";

    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.getTextFormatted().contains("יהוה");
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        ng.putExtra(ADNUT_TEXT, ng.getTextFormatted().replace("יהוה", "אדני"));
    }
}
