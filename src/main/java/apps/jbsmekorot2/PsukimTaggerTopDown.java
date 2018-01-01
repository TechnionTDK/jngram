package apps.jbsmekorot2;

import apps.jbsmekorot.JbsTanachIndex;
import apps.jbsmekorot.JbsTanachMaleIndex;
import org.apache.lucene.document.Document;
import spanthera.Span;
import spanthera.SpanTagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static apps.jbsmekorot.JbsMekorot.format;

public class PsukimTaggerTopDown implements SpanTagger {


    private static final int MAXIMUM_PHRASE_LENGTH = 20 ;
    private static final int MIN_EDITED_WORD_LENGTH = 4 ;
    private static final int MAX_EDITS = 1 ;
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
        List<String> res;
        if(s.size()>=CERTAIN_LENGTH) res= tagBigSpan(s);
        else res=tagSmallSpan(s);
        return res;
    }

    private List<String> tagSmallSpan(Span s ) {
        //we check only 3 and 2 lengths
        if(s.size()<2) return null;
        //now we search in tanach and if we didn't find we search in tanachMale
        String text= format(s.text());
        List<Document> docs= tanach.searchExactInText(text);
        if(docs.size()==0)
            docs= tanachMale.searchExactInText(text);
        List<String> result = new ArrayList<>();
        for (Document doc : docs)
            result.add(doc.get("uri"));
        //intersecting spans will not be candidates .
        if(result.size() > 0 )
            markSpanSegment(s.getStart(),s.size());
        return result;
    }

    private List<String> tagBigSpan(Span s) {
        String text= format(s.text());
        List<String> res= new ArrayList<>();
        List<Document>  docs= new ArrayList<>();
        docs= tanach.searchExactInText(text);
//        if(docs.size()==0)
//        {
//            docs= tanachMale.searchExactInText(text);
//        }
        if(docs.size()==0) {
            //we search with Levinstein distance
            docs= tanach.searchFuzzyInTextRestriction(text, MAX_EDITS,MIN_EDITED_WORD_LENGTH);
       }
        for (Document doc : docs)
            res.add(doc.get("uri"));
        //intersecting spans will not be candidates .
        if(res.size() > 0 )
            markSpanSegment(s.getStart(),s.size());
        return res;
    }

    private int calculateNumOfSubPhrases(String text) {
        int length= text.length();
        double frac= ((double)length)/((double) MAXIMUM_PHRASE_LENGTH);
        return (int)Math.ceil(frac);
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

