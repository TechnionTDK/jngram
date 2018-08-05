package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.JbsNgramFormatter;
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
            "הכתוב", "אמרו", "שאמרה", "כתיב", "ויאמרו", "נאמר", "התורה", "נאמרו", "ומקרא");
    private List<String> HINTS_WORDS_AFTER = HINT_WORDS_BEFORE;
    public static final String MARK_BEFORE = "mark_before";
    public static final String MARK_AFTER = "mark_after";
    private static final int NUM_WORDS_TO_LOOK_BEFORE = 8;
    private static final int NUM_WORDS_TO_LOOK_AFTER = 5;

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
            ng.addToHistory(getName(), "Mark certain by hint words (before)");
            ng.putExtra(MARK_BEFORE, true);
            ng.putExtra(MARK_BEFORE, wordsBefore);
        }
        if (hintsFoundAfter.size() > 0) {
            ng.addToHistory(getName(), "Mark certain by hint words (after)");
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
        if (result.size() > NUM_WORDS_TO_LOOK_AFTER)
            result = result.subList(0, NUM_WORDS_TO_LOOK_AFTER);

        return result;
    }

    /**
     * Return the NUM_WORDS_TO_LOOK_BEFORE words before the provided ngram. How it works?
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
        String text = ngBefore.getText(); // not text formatted yet since we want the parentheses for reference removal.
        text = removeReferences(text);
        text = new JbsNgramFormatter().format(text);

        List<String> result = Arrays.asList(text.split("\\s+"));
        // get 5 last words
        if (result.size() > NUM_WORDS_TO_LOOK_BEFORE)
            result = result.subList(result.size() - NUM_WORDS_TO_LOOK_BEFORE, result.size());

//        System.out.println(ng);
//        System.out.println(result);

        return result;
    }

    public String removeReferences(String text) {
        return text.replaceAll("\\([^()]*\\)", "");
    }
}
