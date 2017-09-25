package spanlib;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * Created by omishali on 30/04/2017.
 */
public class SpannedDocument {
    private Word[] words;
    private int minimalSpanSize = 1;
    private int maximalSpanSize = 1;
    private List<List<Span>> allSpans = new ArrayList<List<Span>>(); // position 0 holds all spans of size minimalSpanSize, position 1 of size minimalSpanSize+1, etc.
    private List<List<Span>> spansByWords; // position 0 holds all spans that contain word 0, position 1 holds all spans that contain word 1, etc. Why? for efficient implementation of getSpans(int wordIndex)
    private List<SpanTagger> taggers = new ArrayList<SpanTagger>();
    private List<SpanManipulation> manipulations = new ArrayList<SpanManipulation>();

    /**
     * This constructor creates all spans from minimalSpanSize size
     * to maximalSpanSize.
     * Max: O(M*N), M number of different span sizes
     * @param text textual content of the document
     */
    public SpannedDocument(String text, int minimalSpanSize, int maximalSpanSize) {
        this.minimalSpanSize = minimalSpanSize;
        this.maximalSpanSize = maximalSpanSize;
        breakTextToWords(text);
        spansByWords = new ArrayList<>(words.length);
        createAllSpans();
        createSpansByWords();
    }

    /**
     * Returns a pointer to the desired span
     * O(1)
     * @param start
     * @param end
     * @return
     */
    public Span getSpan(int start, int end) {
        checkSpanRangeAndSize(start, end);

        int spanSize = end - start + 1;
        List<Span> spansOfThisSize = getSpans(spanSize);
        return spansOfThisSize.get(start);
    }

    public int length() {
        return words.length;
    }


    public Word getWord(int i) {
        if (i < 0 || i >= length())
            throw new DocumentException("Word index " + i + " out of range");

        return words[i];
    }

    public List<Word> getWords() {
        return Arrays.asList(words);
    }

    /**
     * Execute taggers on all spans
     */
    public SpannedDocument tag() {
        for (Span s : getAllSpans()) {
            for (SpanTagger m : taggers) {
                if (m.isCandidate(s)) {
                    List<String> result = m.tag(s);
                    if (result != null)
                        s.addTags(result);
                }
            }
        }
        return this;
    }

    public SpannedDocument manipulate() {
        for (SpanManipulation manipulator : manipulations)
            manipulator.manipulate(this);

        return this;
    }

    public SpannedDocument add(SpanTagger tagger) {
        taggers.add(tagger);
        return this;
    }

    public SpannedDocument add(SpanManipulation manipulator) {
        manipulations.add(manipulator);
        return this;
    }

    /**
     * Returns all spans of length spanLength
     * O(1)
     * @param spanSize
     * @return
     */
    List<Span> getSpans(int spanSize) {
        checkSpanSize(spanSize);

        return allSpans.get(spanSize - minimalSpanSize);
    }

    /**
     * Returns all spans in the document. The spans are sorted by size.
     * @return
     */
    public List<Span> getAllSpans() {
        List<Span> result = new ArrayList<Span>();

        for (List<Span> spans : allSpans)
            result.addAll(spans);

        return result;
    }

    public int getMinimalSpanSize() {
        return minimalSpanSize;
    }

    public int getMaximalSpanSize() {
        return maximalSpanSize;
    }

    private void breakTextToWords(String text) {
        String[] split = text.split("\\s+");
        words = new Word[split.length];
        int i=0;
        for (String s : split) {
            words[i++] = new Word().setText(s);
        }
    }

    private void createAllSpans() {
        allSpans = new ArrayList<List<Span>>();
        for(int i = minimalSpanSize; i <= maximalSpanSize; i++)
            allSpans.add(createSpans(i));
    }

    /**
     * Creates a new span
     * O(1)
     * @param start
     * @param end
     * @return
     */
    private Span createSpan(int start, int end) {
        checkSpanRangeAndSize(start, end);
        Span span = new Span(this, start, end);
        return span;
    }

    private void checkSpanRangeAndSize(int start, int end) {
        if (start < 0 || end >= length() || start > end)
            throw new DocumentException("Illegal span ranges " + "(" + start + "," + end + ")");

        int spanSize = end - start + 1;
        checkSpanSize(spanSize);
    }

    private void checkSpanSize(int size) {
        if (size < minimalSpanSize || size > maximalSpanSize)
            throw new DocumentException("Span size " + size + " out of defined range");
    }


    /**
     * Creates spans of length spanLength
     * O(N)
     * @param spanLength
     * @return
     */
    private List<Span> createSpans(int spanLength) {
        List<Span> result = new ArrayList<Span>();
        for (int i = 0; i < words.length - spanLength + 1; i++) {
            result.add(createSpan(i, i + spanLength - 1));
        }
        return result;
    }

    private void createSpansByWords() {
        // first fill-in spansByWords with an empty list for each word
        for (Word w : getWords())
            spansByWords.add(new ArrayList<>());

        for (Span s : getAllSpans()) {
            int curr = s.getStart();
            int end = s.getEnd();
            while (curr <= end) {
                spansByWords.get(curr).add(s);
                curr++;
            }
        }
    }

    /**
     * Return spans that contain s.
     * Note: Span s itself will not be in the returned set.
     * @param s
     * @return
     */
    public List<Span> getContainingSpans(Span s) {
        List<Span> result = new ArrayList<>();
        int curr = s.getStart();
        int end = s.getEnd();

        result.addAll(spansByWords.get(curr));
        curr++;

        while (curr <= end) {
            result = (List<Span>) CollectionUtils.intersection(result, spansByWords.get(curr));
            curr++;
        }

        result.remove(s);
        return result;
    }
}
