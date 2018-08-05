package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.PsukimTagger;
import jngram.NgramDocument;
import jngram.Ngram;
import jngram.NgramDocumentManipulation;
import jngram.NgramManipulation;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

/**
 * Created by omishali on 08/01/2018.
 * Since we work bottom-up, sometimes we tag ngrams whose text is
 * made of different parts of the pasuk.
 * Here we detect and remove such tags.
 */
    public class RemoveNonSequentialTags extends NgramManipulation {
    @Override
    protected boolean isCandidate(Ngram ng) {
        return ng.size() > 2 && ng.hasTags();
    }

    @Override
    protected void manipulate(NgramDocument doc, Ngram ng) {
        PsukimTagger tagger = new PsukimTagger();
        List<String> foundTags = tagger.tag(ng);
        List<String> tagsToBeRemoved = new ArrayList<>();

        // for each current tag, if it does not
        // exist in foundTags - we remove it.
        for (String currTag : ng.getTags())
            if (!foundTags.contains(currTag))
                tagsToBeRemoved.add(currTag);

        // if we use s.removeTag(tag) we get a java.util.ConcurrentModificationException
        ng.addToHistory(getName(), "Removed some tags.");
        ng.removeTags(tagsToBeRemoved);
    }
}
