package apps.jbsmekorot;

import apps.jbsmekorot.manipulations.AddTextWithShemAdnut;
import org.apache.lucene.document.Document;
import jngram.Ngram;
import jngram.NgramTagger;

import java.util.*;

/**
 * Created by omishali on 10/09/2017.
 */
public class PsukimTagger implements NgramTagger {

    private JbsTanachIndex tanach;
    //private JbsTanachMaleIndex tanachMale;

    public PsukimTagger() {
        tanach = new JbsTanachIndex();
        //tanachMale = new JbsTanachMaleIndex();
    }

    public List<String> tag(Ngram s) {
        String text = s.getTextFormatted();

        // formatting may reduce the size of the span to 1, which causes the fuzzy search to fail.
        if (text.split("\\s+").length == 1)
            return new ArrayList<>();

        List<Integer> maxEdits = getMaxEdits(s);

        List<Document> docs1;
        List<Document> docs2 = new ArrayList<>(); // for ADNUT_TEXT

        docs1 = tanach.searchFuzzyInText(text, maxEdits);
        //List<NgramDocument> docs1 = tanach.searchExactInText(text);
        //List<NgramDocument> docs2 = tanachMale.searchExactInText(text);
        //List<NgramDocument> docs2 = tanachMale.searchFuzzyInText(text, 2);
        if (s.getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT) != null)
            docs2 = tanach.searchFuzzyInText(s.getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT), maxEdits);

        Set<String> result = new HashSet<>();
        for (Document doc : docs1)
            result.add(doc.get("uri"));
        for (Document doc : docs2)
            result.add(doc.get("uri"));

        return new ArrayList<>(result);
    }

    /**
     * For each word in the span we return the
     * number of maxEdits. Basically, the shorter the word the smaller its maxEdit.
     * @param s
     * @return
     */
     List<Integer> getMaxEdits(Ngram s) {
        List<Integer> maxEdits = new ArrayList<>();
        String[] words = s.getTextFormatted().split("\\s+");
        for (int i=0; i<words.length; i++) {
            if (words[i].length() <= 4)
                maxEdits.add(1);
            else
                maxEdits.add(2);
        }

        return maxEdits;
    }

    public boolean isCandidate(Ngram s) {
        return s.size() == 2;
    }
}