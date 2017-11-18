package apps.jbsmekorot2;

import apps.jbsmekorot.JbsTanachIndex;
import apps.jbsmekorot.JbsTanachMaleIndex;
import org.apache.lucene.document.Document;
import spanthera.Span;
import spanthera.SpanTagger;

import java.util.*;

import static apps.jbsmekorot.JbsMekorot.format;

public class PsukimTaggerTopDown implements SpanTagger {


    private JbsTanachIndex tanach;
    private JbsTanachMaleIndex tanachMale;
    private Boolean[] textCoveredBySpans;
    private final int CERTAIN_LENGTH=4;

    public PsukimTaggerTopDown(int documentLength) {
        textCoveredBySpans = new Boolean[documentLength];
        Arrays.fill(textCoveredBySpans, false);
        tanach = new JbsTanachIndex();
        tanachMale = new JbsTanachMaleIndex();
    }
    //marks the indexes of the words from @spanStart and @spanSize indexes ahead in the text as 'tagged'
    //spans that intersect with these indexes are not candidates.
    public void markSpanSegment(int spanStart , int spanSize){
        for(int i = spanStart; i <= spanStart+ spanSize-1 ; i++){
            textCoveredBySpans[i] = true;
        }
    }

    @Override
    public List<String> tag(Span s) {
        List<String> resOfBigSpans= new ArrayList<>();
        List<String> resOfSmallSpans= new ArrayList<>();
        if(s.size()>=CERTAIN_LENGTH) tagBigSpan(s, resOfBigSpans);
        else tagSmallSpan(s, resOfSmallSpans);
        resOfBigSpans.addAll(resOfSmallSpans);
        return resOfBigSpans;
    }

    private void tagSmallSpan(Span s, List<String> resOfSmallSpans ) {
        //we check only 3 and 2 lengths
        if(s.size()<2) return;
        //now we search in tanach and if we didn't find we search in tanachMale
        String text= format(s.text());
        List<Document> docs= tanach.searchExactInText(text);
        if(docs.size()==0)
            docs= tanachMale.searchExactInText(text);
        Set<String> result = new HashSet<>();
        for (Document doc : docs)
            result.add(doc.get("uri"));
        //intersecting spans will not be candidates .
        if(result.size() > 0 )
            markSpanSegment(s.getStart(),s.size());
        resOfSmallSpans.addAll(result);

    }

    private void tagBigSpan(Span s,  List<String> resOfBigSpans) {
        String text= format(s.text());
        List<Document>  docs= tanach.searchExactInText(text);
        if(docs.size()==0)
        {
            docs= tanachMale.searchExactInText(text);
        }
        if(docs.size()==0) {
            //we search with Levinstein distance
            int maxEdits= (int) Math.ceil(0.1*s.size()); // span of size 1-10 1 edit allowed. more that size 10 : 2 edits allowed
            docs= tanach.searchFuzzyInText(format(s.text()), maxEdits);
        }
        Set<String> result = new HashSet<>();
        for (Document doc : docs)
            result.add(doc.get("uri"));
        //intersecting spans will not be candidates .
        if(result.size() > 0 )
            markSpanSegment(s.getStart(),s.size());
        resOfBigSpans.addAll(result);
    }

    @Override
    public boolean isCandidate(Span s) {
        // a span is a candidate iff its not intersecting with another already-marked span.
        //since we go from top to bottom there is no risk that current span is containing another span (size is decreasing)
        //so we only need to check if the first or last word is on an index that was already marked.
        // ** 'true'  in textCoveredBySpans  denotes "marked"
        return !(textCoveredBySpans[s.getStart()] || textCoveredBySpans[s.getEnd()] );
    }

}

