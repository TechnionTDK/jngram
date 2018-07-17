package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsMekorot;
import jngram.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Here, for each candidate ngram we look at some words before
 * it and at some words after. If we detect words that may hint the
 * ngram is a real quotation we mark the ngram with MarkCertainByContext.VALUE = true.
 * We also save the list of hint words.
 */
public class MarkCertainByContext extends NgramManipulation {
    private List<String> HINT_WORDS_BEFORE = Arrays.asList("אומר", "שאמר", "באמרו", "ואומר", "שנאמר", "שאמרו", "ואמר", "אמר",
            "הכתוב", "אמרו", "שאמרה");
    private List<String> HINTS_WORDS_AFTER = Arrays.asList("וגו'", "וכו'");

    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.size() < JbsMekorot.CERTAIN_NGRAM_SIZE;
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        List<String> wordsBefore = getWordsBefore(doc, ng);
        List<String> wordsAfter = getWordsAfter(doc, ng);
        
    }

    private List<String> getWordsAfter(NgramDocument doc, Ngram ng) {
        return null;
    }

    private List<String> getWordsBefore(NgramDocument doc, Ngram ng) {
        return null;
    }
}
