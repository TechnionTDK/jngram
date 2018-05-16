package spanthera;

import java.util.List;

/**
 * Created by omishali on 04/09/2017.
 */
public interface NgramTagger {
    /**
     * Does the actual tagging, returns a list
     * of string tags, or null
     * if there is no tag to be added.
     * @param ng
     * @return
     */
    public List<String> tag(Ngram ng);

    /**
     * Whether the ngram is a candidate for tagging, e.g.,
     * some matchers may only consider ngrams having a specific length.
     * @param ng
     * @return
     */
    public boolean isCandidate(Ngram ng);
}
