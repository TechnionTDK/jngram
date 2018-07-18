package jngram;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * Created by omishali on 30/04/2017.
 */
public class NgramDocument {
    private Word[] words;
    private int minimalNgramSize = 1;
    private int maximalNgramSize = 1;
    private List<List<Ngram>> allNgrams = new ArrayList<List<Ngram>>(); // position 0 holds all ngrams of size minimalNgramSize, position 1 of size minimalNgramSize+1, etc.
    private List<List<Ngram>> ngramsByWords; // position 0 holds all ngrams that contain word 0, position 1 holds all ngrams that contain word 1, etc. Why? for efficient implementation of getNgrams(int wordIndex)
    private Map<String, List<Ngram>> tagNgramIndex = new HashMap<>(); // holds mappings from tag to ngrams. For boosting method getNgrams(tag). Note: is created by demand!
    private List<NgramTagger> taggers = new ArrayList<NgramTagger>();
    private List<NgramDocumentManipulation> manipulations = new ArrayList<NgramDocumentManipulation>();

    /**
     * This constructor creates all ngrams from minimalNgramSize size
     * to maximalNgramSize.
     * Max: O(M*N), M number of different ngram sizes
     * @param text textual content of the document
     */
    public NgramDocument(String text, int minimalNgramSize, int maximalNgramSize) {
        this.minimalNgramSize = minimalNgramSize;
        this.maximalNgramSize = maximalNgramSize;
        breakTextToWords(text);
        ngramsByWords = new ArrayList<>(words.length);
        createAllNgrams();
        createNgramsByWords();
    }


    /**
     * Returns the ngrams having the given tag.
     * ~O(1) - based on tagNgramIndex.
     * Note: createTagNgramIndex() should be called before.
     * @param tag
     * @return
     */
    public List<Ngram> getNgrams(String tag) {
        return tagNgramIndex.get(tag);
    }

    /**
     * Note: tagNgramIndex is created only once upon the first call to
     * this method. More importantly, this data structure is not further updated, i.e.,
     * if tags are removed from ngrams, it is not reflected in the data
     * structure. In case you want to make sure that tagNgramIndex is refreshed, you should call
     * clearTagNgramIndex and then createTagNgramIndex.
     */
    public void createTagNgramIndex() {
        if (!tagNgramIndex.isEmpty())
            return;

        // see https://stackoverflow.com/questions/3019376/shortcut-for-adding-to-list-in-a-hashmap
        for (Ngram s : getAllNgrams()) {
            for (String tag : s.getTags()) {
                tagNgramIndex.computeIfAbsent(tag, v -> new ArrayList<>()).add(s); // JAVA 8!
            }
        }
    }

    public void clearTagsNgramIndex() {
        tagNgramIndex.clear();
    }

    /**
     * Returns a pointer to the desired ngram
     * O(1)
     * @param start
     * @param end
     * @return
     */
    public Ngram getNgram(int start, int end) {
        checkNgramRangeAndSize(start, end);

        int ngramSize = end - start + 1;
        List<Ngram> ngramsOfThisSize = getNgrams(ngramSize);
        return ngramsOfThisSize.get(start);
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
     * Execute taggers on all ngrams
     */
    public NgramDocument tag() {
        for (Ngram s : getAllNgrams()) {
            for (NgramTagger m : taggers) {
                if (m.isCandidate(s)) {
                    List<String> result = m.tag(s);
                    if (result != null)
                        s.addTags(result);
                }
            }
        }
        return this;
    }

    public NgramDocument manipulate() {
        for (NgramDocumentManipulation manipulator : manipulations)
            manipulator.manipulate(this);

        return this;
    }

    public NgramDocument add(NgramTagger tagger) {
        taggers.add(tagger);
        return this;
    }

    public NgramDocument add(NgramDocumentManipulation manipulator) {
        manipulations.add(manipulator);
        return this;
    }

    /**
     * Returns all ngrams of length ngramSize
     * O(1)
     * @param ngramSize
     * @return
     */
    public List<Ngram> getNgrams(int ngramSize) {
        checkNgramSize(ngramSize);

        return allNgrams.get(ngramSize - minimalNgramSize);
    }

    /**
     * Returns all ngrams in the document. The ngrams are sorted by size.
     * @return
     */
    public List<Ngram> getAllNgrams() {
        List<Ngram> result = new ArrayList<Ngram>();

        for (List<Ngram> ngrams : allNgrams)
            result.addAll(ngrams);

        return result;
    }

    /**
     * Returns all ngrams in the document. The ngrams are sorted by size.
     * @return
     */
    public List<Ngram> getAllNgramsWithTags() {
        List<Ngram> result = new ArrayList<Ngram>();
        List<Ngram> all = getAllNgrams();
        for (Ngram ng : all)
            if (ng.hasTags())
                result.add(ng);

        return result;
    }

    public int getMinimalNgramSize() {
        return minimalNgramSize;
    }

    public int getMaximalNgramSize() {
        return maximalNgramSize;
    }

    private void breakTextToWords(String text) {
        String[] split = text.split("\\s+");
        words = new Word[split.length];
        int i=0;
        for (String s : split) {
            words[i++] = new Word().setText(s);
        }
    }

    private void createAllNgrams() {
        allNgrams = new ArrayList<List<Ngram>>();
        for(int i = minimalNgramSize; i <= maximalNgramSize; i++)
            allNgrams.add(createNgrams(i));
    }

    /**
     * Creates a new ngras
     * O(1)
     * @param start
     * @param end
     * @return
     */
    private Ngram createNgram(int start, int end) {
        checkNgramRangeAndSize(start, end);
        Ngram ngram = new Ngram(this, start, end);
        return ngram;
    }

    private void checkNgramRangeAndSize(int start, int end) {
        if (start < 0 || end >= length() || start > end)
            throw new DocumentException("Illegal ngram ranges " + "(" + start + "," + end + ")");

        int ngramSize = end - start + 1;
        checkNgramSize(ngramSize);
    }

    private void checkNgramSize(int size) {
        if (size < minimalNgramSize || size > maximalNgramSize)
            throw new DocumentException("Ngram size " + size + " out of defined range");
    }


    /**
     * Creates ngrams of length ngramLength
     * O(N)
     * @param ngramLength
     * @return
     */
    private List<Ngram> createNgrams(int ngramLength) {
        List<Ngram> result = new ArrayList<Ngram>();
        for (int i = 0; i < words.length - ngramLength + 1; i++) {
            result.add(createNgram(i, i + ngramLength - 1));
        }
        return result;
    }

    private void createNgramsByWords() {
        // first fill-in ngramsByWords with an empty list for each word
        for (Word w : getWords())
            ngramsByWords.add(new ArrayList<>());

        for (Ngram s : getAllNgrams()) {
            int curr = s.getStart();
            int end = s.getEnd();
            while (curr <= end) {
                ngramsByWords.get(curr).add(s);
                curr++;
            }
        }
    }

    /**
     * Return ngrams that contain s.
     * Note: Ngram s itself will not be in the returned set.
     * @param s
     * @return
     */
    public List<Ngram> getContainingNgrams(Ngram s) {
        List<Ngram> result = new ArrayList<>();
        int curr = s.getStart();
        int end = s.getEnd();

        result.addAll(ngramsByWords.get(curr));
        curr++;

        while (curr <= end) {
            result = (List<Ngram>) CollectionUtils.intersection(result, ngramsByWords.get(curr));
            curr++;
        }

        result.remove(s);
        return result;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (List<Ngram> ngrams : allNgrams) {
            if (ngrams.size() == 0) // for short text we may have List<Ngram> of size 0 so we must skip or we get an exception.
                continue;

            result.append("LENGTH: " + ngrams.get(0).size() + "\n");
            for (Ngram s : ngrams)
                if(s.getTags().size() > 0)
                    result.append(s.toString() + "\n");
        }
        return result.toString();
    }


    // this tag is a version that only tags the ngrams with size  @ngramSize
    // @param  ngramSize : the size of the ngrams that are going to be tagged. IE NgramDocument(4) will tag all the ngrams with size 4

   public NgramDocument tag(int ngramSize) {
        List<Ngram> ngrams = getNgrams(ngramSize);
        for (Ngram s : ngrams) {
            for (NgramTagger m : taggers) {
                if (m.isCandidate(s)) {
                    List<String> result = m.tag(s);
                    if (result != null  && result.size()!=0)
                        s.addTags(result);
                }
            }
        }
        return this;
    }

    /**
     * Format the text of each ngram using the provided formatter.
     * Formatted text will be available via Ngram.getTextFormatted.
     * @param formatter
     */
    public void format(NgramFormatter formatter) {
        for (Ngram s : getAllNgrams())
            s.setTextFormatted(formatter.format(s));
    }

    /**
     * Note: return overlapping ngrams that appear MARK_AFTER s in
     * the document, and besides s itself.
     * @param s
     * @return
     */
    public List<Ngram> getOverlappingNgrams(Ngram s) {
        List<Ngram> result = new ArrayList<>();
        int start = s.getStart();
        int end = s.getEnd();

        // we start with ngram (start, end+1)
        for (int i=start; i<=end; i++) {
            int last = start + getMaximalNgramSize();
            for (int j=end+1; j < last && j < length() ; j++) {
               result.add(getNgram(i, j));
            }
        }

        return result;
    }

    /**
     * Returns an ngram that appears right before ng.
     * @param size the size of the returned ngram
     *             (in case of an invalid size we return the closest result).
     * @return null when ng starts at 0 or when the result is out of legal range.
     */
    public Ngram getNgramBefore(Ngram ng, int size) {
        if (size > getMaximalNgramSize())
            size = getMaximalNgramSize();

        int resStart = ng.getStart() - size;
        int resEnd = ng.getStart() - 1;

        // null is returned when ng starts at 0
        if (resEnd < 0)
            return null;

        // "fix" values ("best effort" policy)
        if (resStart < 0) resStart = 0;

        // return null if invalid
        try {
            checkNgramRangeAndSize(resStart, resEnd);
        } catch(DocumentException e) {
            return null;
        }

        return getNgram(resStart, resEnd);
    }

    /**
     * Returns an ngram that appears right after ng.
     * @param size the size of the returned ngram.
     * @return null for an invalid request
     */
    public Ngram getNgramAfter(Ngram ng, int size) {
        if (size > getMaximalNgramSize())
            size = getMaximalNgramSize();

        int resStart = ng.getEnd() + 1;
        int resEnd = ng.getEnd() + size;

        // null is returned when ng ends at end of document
        if (resStart >= length())
            return null;
        // "fix" values ("best effort" policy)
        if (resEnd >= length()) resEnd = length() - 1;

        // return null if invalid
        try {
            checkNgramRangeAndSize(resStart, resEnd);
        } catch(DocumentException e) {
            return null;
        }

        return getNgram(resStart, resEnd);
    }

    public List<Ngram> getAdjacentNgrams(Ngram ng, int maxDistance) {
        return null;
    }
}
