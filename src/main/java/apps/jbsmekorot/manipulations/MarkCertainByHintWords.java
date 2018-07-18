package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsMekorot;
import jngram.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Here, for each candidate ngram we look at some words before
 * it and at some words after. If we detect words that may hint the
 * ngram is a real quotation we mark the ngram with MarkCertainByHintWords.MARK_BEFORE/MARK_AFTER = true.
 * We also save the list of hint words.
 */
public class MarkCertainByHintWords extends NgramManipulation {
    private List<String> HINT_WORDS_BEFORE = Arrays.asList("אומר", "שאמר", "באמרו", "ואומר", "שנאמר", "שאמרו", "ואמר", "אמר",
            "הכתוב", "אמרו", "שאמרה");
    private List<String> HINTS_WORDS_AFTER = Arrays.asList("וגו", "וכו");
    public static final String MARK_BEFORE = "mark_before";
    public static final String MARK_AFTER = "mark_after";

    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.hasTags() && ng.size() < JbsMekorot.CERTAIN_NGRAM_SIZE;
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        List<String> wordsBefore = getWordsBefore(doc, ng);
        List<String> wordsAfter = getWordsAfter(doc, ng);

        List<String> hintsFoundBefore = (List<String>) CollectionUtils.intersection(wordsBefore, HINT_WORDS_BEFORE);
        List<String> hintsFoundAfter = (List<String>) CollectionUtils.intersection(wordsAfter, HINTS_WORDS_AFTER);

        if (hintsFoundBefore.size() > 0) {
            ng.putExtra(MARK_BEFORE, true);
            ng.putExtra(MARK_BEFORE, wordsBefore);
        }
        if (hintsFoundAfter.size() > 0) {
            ng.putExtra(MARK_AFTER, true);
            ng.putExtra(MARK_AFTER, wordsAfter);
        }

//        System.out.println(ng);
//        System.out.println(wordsBefore);
//        System.out.println(wordsAfter);
    }

    private List<String> getWordsAfter(NgramDocument doc, Ngram ng) {
        Ngram ngAfter = doc.getNgramAfter(ng, doc.getMaximalNgramSize());
        // null means ng is the last ngram in the document
        if (ngAfter == null)
            return new ArrayList<>();;

        // note: ngAfter is not neccessarily in size MaximalNgramSize, it may be shorter (see getNgramAfter implementation).
        String text = ngAfter.getTextFormatted();
        text = removeReferences(text);

        List<String> result = Arrays.asList(text.split("\\s+"));
        // get 5 last words
        if (result.size() > 5)
            result = result.subList(0, 5);

        return result;
    }

    /**
     * Return the 5 words before the provided ngram. How it works?
     * We take N words before, where N is maximal ngram size in the document.
            * Then we take the formatted text and remove all "(*)", that is, any indication
     * of references. From the text left we return last 5 words.
     * @param doc
     * @param ng
     * @return
             */
    private List<String> getWordsBefore(NgramDocument doc, Ngram ng) {
        Ngram ngBefore = doc.getNgramBefore(ng, doc.getMaximalNgramSize());
        // null means ng is the first ngram in the document
        if (ngBefore == null)
            return new ArrayList<>();;

        // note: ngBefore is not neccessarily in size MaximalNgramSize, it may be shorter (see getNgramBefore implementation).
        String text = ngBefore.getTextFormatted();
        text = removeReferences(text);

        List<String> result = Arrays.asList(text.split("\\s+"));
        // get 5 last words
        if (result.size() > 5)
            result = result.subList(result.size() - 5, result.size());

//        System.out.println("ngram: " + ng);
//        System.out.println("ngram before: " + result);

        return result;
    }

    private String removeReferences(String text) {
        return text.replaceAll("\\(.*\\)", "");
    }
}
