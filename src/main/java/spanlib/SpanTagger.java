package spanlib;

import java.util.List;

/**
 * Created by omishali on 04/09/2017.
 */
public interface SpanTagger {
    /**
     * Does the actual tagging, returns a list
     * of string tags, or null
     * if there is no tag to be added.
     * @param s
     * @return
     */
    public List<String> tag(Span s);

    /**
     * Whether the span is a candidate for tagging, e.g.,
     * some matchers may only consider spans having a specific length.
     * @param s
     * @return
     */
    public boolean isCandidate(Span s);
}
