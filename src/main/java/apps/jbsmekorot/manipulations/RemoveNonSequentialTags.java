package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.PsukimTagger;
import spanthera.Span;
import spanthera.SpanManipulation;
import spanthera.SpannedDocument;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

/**
 * Created by omishali on 08/01/2018.
 * Since we work bottom-up, sometimes we tag spans whose text is
 * made of different parts of the pasuk.
 * Here we detect and remove such tags.
 */
    public class RemoveNonSequentialTags implements SpanManipulation {
        @Override
        public void manipulate(SpannedDocument doc) {
            for (Span s : doc.getAllSpans()) {
                if (s.size() == 2 || s.getTags().size() == 0)
                    continue;

                PsukimTagger tagger = new PsukimTagger();
                List<String> foundTags = tagger.tag(s);
                List<String> tagsToBeRemoved = new ArrayList<>();

                // for each current tag, if it does not
                // exist in foundTags - we remove it.
                for (String currTag : s.getTags())
                    if (!foundTags.contains(currTag))
                        tagsToBeRemoved.add(currTag);

                // if we use s.removeTag(tag) we get a java.util.ConcurrentModificationException
                s.removeTags(tagsToBeRemoved);
            }
        }
}
