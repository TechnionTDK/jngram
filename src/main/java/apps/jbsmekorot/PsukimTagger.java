package apps.jbsmekorot;

import apps.jbsmekorot.manipulations.AddTextWithShemAdnut;
import org.apache.lucene.document.Document;
import jngram.Ngram;
import jngram.NgramTagger;

import java.util.*;

/**
 * Here we iterate all ngrams in size 2, and for each we ask the
 * Tanach index for candidate psukim (added as tags to the ngram).
 * Created by omishali on 10/09/2017.
 */
public class PsukimTagger implements NgramTagger {

    private JbsTanachIndex tanach;

    public PsukimTagger() {
        tanach = new JbsTanachIndex();
    }

    public List<String> tag(Ngram ng) {
        String text = ng.getTextFormatted();

        // formatting may reduce the size of the ngram to 1, which causes the fuzzy search to fail.
        if (text.split("\\s+").length == 1)
            return new ArrayList<>();

        List<Integer> maxEdits = getMaxEdits(ng);

        List<Document> docs1;
        List<Document> docs2 = new ArrayList<>(); // for ADNUT_TEXT

        docs1 = tanach.searchFuzzyInText(text, maxEdits);
        if (ng.getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT) != null)
            docs2 = tanach.searchFuzzyInText(ng.getStringExtra(AddTextWithShemAdnut.ADNUT_TEXT), maxEdits);

        Set<String> result = new HashSet<>();
        for (Document doc : docs1)
            result.add(doc.get("uri"));
        for (Document doc : docs2)
            result.add(doc.get("uri"));

        return new ArrayList<>(result);
    }

    /**
     * For each word in the ngram we return the
     * number of maxEdits. Basically, the shorter the word the smaller its maxEdit.
     * @param ng
     * @return
     */
     List<Integer> getMaxEdits(Ngram ng) {
        List<Integer> maxEdits = new ArrayList<>();
        String[] words = ng.getTextFormatted().split("\\s+");
        for (int i=0; i<words.length; i++) {
            if (words[i].length() <= 4)
                maxEdits.add(1);
            else
                maxEdits.add(2);
        }

        return maxEdits;
    }

    public boolean isCandidate(Ngram ng) {
        return ng.size() == 2;
    }
}