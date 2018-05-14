package spanthera;

/**
 * In case we want to format the text of all ngrams in the document,
 * we should implement this interface and then pass the object to
 * NgramDocument.format method. Formatted text will be avaliable via
 * Ngram.getTextFormatted().
 * Created by omishali on 07/01/2018.
 */
public interface NgramFormatter {
    public String format(Ngram s);
    public boolean isCandidate(Ngram s);
}
