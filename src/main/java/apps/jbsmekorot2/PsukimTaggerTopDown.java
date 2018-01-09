package apps.jbsmekorot2;

import apps.jbsmekorot.JbsTanachIndex;
import apps.jbsmekorot.JbsTanachMaleIndex;
import org.apache.lucene.document.Document;
import org.jetbrains.annotations.NotNull;
import spanthera.Span;
import spanthera.SpanTagger;

import java.util.*;

public class PsukimTaggerTopDown implements SpanTagger {

    private ContextFinder contextFinder;
    private JbsTanachIndex tanach;
    private JbsTanachMaleIndex tanachMale;
    private Boolean[] textCoveredBySpans;

    public PsukimTaggerTopDown(int documentLength) {
        contextFinder = new ContextFinder();
        textCoveredBySpans = new Boolean[documentLength];
        Arrays.fill(textCoveredBySpans, false);
        tanach = new JbsTanachIndex();
        tanachMale = new JbsTanachMaleIndex();
    }

    @Override
    //region Description
    /**
     * a span is a candidate iff its not intersecting with another already-marked span.
     * since we go from top to bottom there is no risk that current span is containing another span (size is decreasing)
     * so we only need to check if the first or last word is on an index that was already marked.
     * ** 'true'  in textCoveredBySpans  denotes "marked"
     */
    //endregion
    public boolean isCandidate(Span s) {

        return !(textCoveredBySpans[s.getStart()] || textCoveredBySpans[s.getEnd()] );
    }

    @Override
    public List<String> tag(Span s){
        String text= s.getTextFormatted();
        List<String> results = null;
        if(s.size() <= Config.MAXIMAL_PASUK_LENGTH && s.size() >= Config.SPAN_SIZE_LAYER_1){
            results= HandleFirstLayerSpans(s, text);

        }
        if(s.size() <= Config.SPAN_SIZE_LAYER_1 - 1 && s.size() >= Config.SPAN_SIZE_LAYER_2){
            results= HandleSecondLayerSpans(s, text);

        }
        if(s.size() <= Config.SPAN_SIZE_LAYER_2 - 1 && s.size() >= Config.SPAN_SIZE_LAYER_3){
            results= HandleThirdLayerSpans(s, text);
        }
        if(results!=null)
        {
            contextFinder.insertTags(s, results);
            return results;
        }
        System.out.println("span size outside of range\n");
        return null;
    }

    //region privates
    /**
     * marks the indexes of the words from @spanStart and @spanSize indexes ahead in the text as 'tagged'
     * spans that intersect with these indexes are not candidates.
     */
    private void markSpanSegment(int spanStart , int spanSize){
        for(int i = spanStart; i <= spanStart+ spanSize-1 ; i++){
            textCoveredBySpans[i] = true;
        }
    }
    @NotNull
    private List<String> HandleThirdLayerSpans(Span s, String text) {
        //1. exact in Tanach
        List<Document>  docs= tanach.searchExactInText(text);
        if(docs.size()==0){
            //2. exact in Tanach Male
            docs= tanachMale.searchExactInText(text);
        }
        //filter out tags

        HashMap<String, Double> matches = contextFinder.getTagsInContext(s,docs);
        List<String> result = new ArrayList<>();

        if(!matches.isEmpty()){
            getBestKtags(matches, result, Config.NUMBER_OF_TAGS_TO_KEEP_L3);
        }
        //intersecting spans will not be candidates .
        if(result.size() > 0 )
            markSpanSegment(s.getStart(),s.size());
        return result;
    }

    @NotNull
    private List<String> HandleSecondLayerSpans(Span s, String text) {
        //1. exact in Tanach
        List<Document>  docs= tanach.searchExactInText(text);
        if(docs.size()==0){
            //2. exact in Tanach Male
            docs= tanachMale.searchExactInText(text);
            if(docs.size()==0) {
                //3. Fuzzy in Tanach
                docs= tanach.searchFuzzyInTextRestriction(s.getTextFormatted(), Config.MAX_EDITS , Config.MIN_WORD_LENGTH_FOR_FUZZY);
            }
        }
        //filter out tags
        //TODO: consult with Oren: when do we want to consider keeping more than 1 tag?
        // only helps decide which is better. but if not found in context - keep them anyway
        List<String> result = new ArrayList<>();
        if(docs.size() < 2) {
            docs.forEach(d -> result.add(d.get("uri")));
        } else {
            HashMap<String, Double> matches = contextFinder.getTagsInContext(s,docs);

            if(!matches.isEmpty()){
                getBestKtags(matches,result,Config.NUMBER_OF_TAGS_TO_KEEP_L2);
            } else {
                docs.forEach(d -> result.add(d.get("uri")));
            }
        }
        //intersecting spans will not be candidates .
        if(result.size() > 0 )
            markSpanSegment(s.getStart(),s.size());

        return result;
    }

    @NotNull
    private List<String> HandleFirstLayerSpans(Span s, String text) {
        //1. exact in Tanach
        List<Document>  docs= tanach.searchExactInText(text);
        if(docs.size()==0){
            //2. exact in Tanach Male
            docs= tanachMale.searchExactInText(text);
            if(docs.size()==0) {
                //3. Fuzzy in Tanach
                docs= tanach.searchFuzzyInTextRestriction(s.getTextFormatted() , Config.MAX_EDITS , Config.MIN_WORD_LENGTH_FOR_FUZZY);
            }
        }
        List<String> result = new ArrayList<>();
        for (Document doc : docs)
            result.add(doc.get("uri"));
        //intersecting spans will not be candidates .
        if(result.size() > 0 )
            markSpanSegment(s.getStart(),s.size());

        return result;
    }

    private void getBestKtags(HashMap<String, Double> matches, List<String> toKeep, int min) {
        for(int i = 0 ; i < min ; i++ ){
            String bestString = getBest(matches);
            if(matches.get(bestString) < Config.MINIMUM_GRADE){
                break;
            }
            toKeep.add(bestString);
            matches.remove(bestString);
        }
    }

    private String getBest(HashMap<String, Double> matches) {
        String bestTag = new String();
        Double bestScore = 0.0;
        for(Map.Entry<String , Double> ent : matches.entrySet()){
            if(ent.getValue() > bestScore){
                bestTag = ent.getKey();
                bestScore = ent.getValue();
            }
        }
        return bestTag;
    }
    //endregion



}

