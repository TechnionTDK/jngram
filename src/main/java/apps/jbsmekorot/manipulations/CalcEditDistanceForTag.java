package apps.jbsmekorot.manipulations;

import apps.jbsmekorot.JbsIndex;
import apps.jbsmekorot.JbsMekorot;
import apps.jbsmekorot.JbsTanachIndex;
import org.apache.lucene.document.Document;
import spanthera.Span;
import spanthera.SpanManipulation;
import spanthera.SpannedDocument;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;

/**
 * Created by omishali on 21/01/2018.
 */
public class CalcEditDistanceForTag implements SpanManipulation {
    private static final String DISTANCE_KEY = "dist_key::";
    private static final double PERCENTAGE_DISTANCE_ALLOWED = 0.1;

    @Override
    public void manipulate(SpannedDocument doc) {
        for (Span s : doc.getAllSpans()) {
            if (s.hasNoTags())
                continue;

            for (String tag : s.getTags()) {
                // get the text of the pasuk
                JbsTanachIndex index = new JbsTanachIndex();
                List<Document> docs = index.searchExactInUri(tag);
                String pasuk = docs.get(0).get("text");

                int minDistance = getMinimalDistance(pasuk, s.text());
                s.putExtra(DISTANCE_KEY + tag, minDistance);
            }

            // now that we have added distance to all tags we apply filtering
            filterTags(s);
        }
    }

    private void filterTags(Span s) {
        List<String> removedTags = new ArrayList<>();

        // filter out tags with more than PERCENTAGE_DISTANCE_ALLOWED than the length of text.
        for (String tag : s.getTags()) {
            double distance = getDistance(s, tag);
            double length = s.text().length();
            //System.out.println(distance);
            //System.out.println(length);
            //System.out.println(distance / length);
//            if (distance / length > PERCENTAGE_DISTANCE_ALLOWED) {
//                removedTags.add(tag);
//                System.out.println("REMOVED:");
//                System.out.println(s);
//                System.out.println(tag);
//            }
        }

        s.removeTags(removedTags);
    }

    /**
     * We return the best match of text within the pasuk.
     * @param pasuk
     * @param text
     * @return
     */
    private int getMinimalDistance(String pasuk, String text) {
        SpannedDocument sd = new SpannedDocument(pasuk, JbsMekorot.MINIMAL_PASUK_LENGTH, JbsMekorot.MAXIMAL_PASUK_LENGTH);

        int minDistance = 1000;
        for (Span s : sd.getAllSpans()) {
            int currDist = getLevenshteinDistance(s.text(), text);
            if (currDist < minDistance)
                minDistance = currDist;
        }

        return minDistance;
    }

    public static Integer getDistance(Span s, String tag) {
        return  s.getIntExtra(DISTANCE_KEY + tag);
    }
}
