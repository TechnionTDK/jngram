package apps.jbsmekorot2;

import apps.jbsmekorot.JbsTanachIndex;
import apps.jbsmekorot.JbsTanachMaleIndex;
import org.apache.lucene.document.Document;
import org.jetbrains.annotations.NotNull;
import spanthera.Span;
import spanthera.SpanTagger;
import org.apache.commons.lang3.StringUtils;


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
        //tanachMale = new JbsTanachMaleIndex();
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


    private List<Document> filterOutExtremeEdits(List<Document> docs, Span s) {
        if(Config.MAX_EDITS > 1){
            return docs;
        }
        if(s.size()> Config.EXTREME_EDITS_FILTER_CANDIDATE){
            return docs;
        }


        List<Document> filtered_docs = new ArrayList<>();
        for(Document d : docs) {
            double diffGrade = 0.0;
            int numOfWords = s.size();
            String[] docWords = d.getFields().get(0).stringValue().split(" ");
            String[] spanWords = s.getTextFormatted().split(" ");
            //find starting index:
            int starting_idx = -1;

            for (int i = 0; i < docWords.length - numOfWords + 1; i++) {
                int numberOfCloseWords = 0;
                for (int j = 0; j < numOfWords; j++) {
                    if (StringUtils.getLevenshteinDistance(spanWords[j],docWords[i + j]) > 1) {
                        break;
                    }
                    numberOfCloseWords += 1;
                }
                if (numberOfCloseWords == numOfWords) {
                    starting_idx = i;
                    break;
                }
            }
            if (starting_idx == -1) {
                break;
            }
            String[] temp_docWords = new String[numOfWords];
            for(int i = 0 ; i < numOfWords ; i++){
                temp_docWords[i] = docWords[starting_idx + i];
            }
            docWords = temp_docWords;
            for (int i = 0; i < numOfWords; i++) {
                String spanWord = spanWords[i];
                if (spanWord.length() < Config.MIN_WORD_LENGTH_FOR_FUZZY) {
                    continue;
                }
                char[] docChars = docWords[i].toCharArray();
                char[] spanChars = spanWord.toCharArray();
                if (docChars.length == spanChars.length) {
                    // Edit = letter change or No change at all
                    for (int j = 0; j < spanChars.length; j++) {
                        if (spanChars[j] != docChars[j]) {
                            diffGrade += Config.calcGradeDiff(spanChars[j], docChars[j]) / numOfWords;
                            break;
                        }
                    }
                } else if (docChars.length > spanChars.length) {
                    // Edit = letter was added to the orig. first letter on docChars that doest match spanChars in the j'th
                    // index is the added letter.
                    Boolean handled_flag = false;
                    for (int j = 0; j < spanChars.length; j++) {
                        if (spanChars[j] != docChars[j]) {
                            diffGrade += Config.calcGradeDiff('a', docChars[j]) / numOfWords; // 'a' for added
                            handled_flag = true;
                            break;
                        }
                    }
                    if (handled_flag == false) {
                        diffGrade += Config.calcGradeDiff('a', docChars[docChars.length - 1]) / numOfWords; // 'a' for added
                    }
                } else {
                    // Edit = letter was deleted from the orig. first letter on docChars that doest match spanChars in the j'th
                    // index - indicates that spanChars[j] is the deleted letter
                    Boolean handled_flag = false;
                    for (int j = 0; j < docChars.length; j++) {
                        if (spanChars[j] != docChars[j]) {
                            diffGrade += Config.calcGradeDiff('d', docChars[j]) / numOfWords; // 'd' for deleted
                            handled_flag = true;
                            break;
                        }
                    }
                    if (handled_flag == false) {
                        diffGrade += Config.calcGradeDiff('d', docChars[docChars.length - 1]) / numOfWords; // 'd' for deleted
                    }
                }
            }
            if (diffGrade <= Config.MAXIMUM_DIFF_GRADE) {
                filtered_docs.add(d);
            }
        }
        return filtered_docs;
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
        List<Document>  docs= searchByAllMeans(s,text);
        //filter out tags

        HashMap<String, Double> matches = contextFinder.getTagsInContext(s,docs);
        List<String> result = new ArrayList<>();

        if(!matches.isEmpty()){
            result = getBestKtags(matches, Config.NUMBER_OF_TAGS_TO_KEEP_L3);
        }
        //intersecting spans will not be candidates .
        if(result.size() > 0 )
            markSpanSegment(s.getStart(),s.size());
        return result;
    }

    @NotNull
    private List<String> HandleSecondLayerSpans(Span s, String text) {
        //1. exact in Tanach
        List<Document> docs = searchByAllMeans(s, text);
        //filter out tags
        //TODO: consult with Oren: when do we want to consider keeping more than 1 tag?
        // only helps decide which is better. but if not found in context - keep them anyway
        List<String> result = new ArrayList<>();
        if(docs.size() < 2) {
            for(Document d : docs){
                result.add(d.get("uri"));
            }
        } else {
            HashMap<String, Double> matches = contextFinder.getTagsInContext(s,docs);

            if(!matches.isEmpty()){
                result = getBestKtags(matches,Config.NUMBER_OF_TAGS_TO_KEEP_L2);
            } else {
                for(Document d : docs){
                    result.add(d.get("uri"));
                }
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
        List<Document> docs = searchByAllMeans(s, text);
        List<String> result = new ArrayList<>();
        for (Document doc : docs)
            result.add(doc.get("uri"));
        //intersecting spans will not be candidates .
        if(result.size() > 0 )
            markSpanSegment(s.getStart(),s.size());

        return result;
    }

    private List<Document> searchByAllMeans(Span s, String text) {
        List<Document>  docs= tanach.searchExactInText(text);
        if(docs.size()==0){
            //2. exact in Tanach Male
//            docs= tanachMale.searchExactInText(text);
//            if(docs.size()==0) {
                //3. Fuzzy in Tanach
                docs= tanach.searchFuzzyInTextRestriction(s.getTextFormatted() , Config.MAX_EDITS , Config.MIN_WORD_LENGTH_FOR_FUZZY);
                if(docs.size()==0){
                    if (s.getStringExtra(AddTextWithShemAdnutTopDown.ADNUT_TEXT) != null)
                        docs = tanach.searchFuzzyInTextRestriction(
                                s.getStringExtra(AddTextWithShemAdnutTopDown.ADNUT_TEXT), Config.MAX_EDITS, Config.MIN_WORD_LENGTH_FOR_FUZZY);
                //}
                docs = filterOutExtremeEdits(docs,s);
            }

        }
        return docs;
    }



    private List<String>  getBestKtags(HashMap<String, Double> matches, int min) {
        List<String> toKeep = new ArrayList<>();
        for(int i = 0 ; i < min ; i++ ){
            String bestString = getBest(matches);
            if(matches.get(bestString) < Config.MINIMUM_GRADE){
                break;
            }
            toKeep.add(bestString);
            matches.remove(bestString);
        }
        return  toKeep;
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

