package apps.jbsmekorot2;

import org.apache.lucene.document.Document;
import spanthera.Span;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static apps.jbsmekorot2.Config.CONTEXT_DISTANCE;
import static apps.jbsmekorot2.Config.contextGrade;


public class ContextFinder {
    public class SpanKey{
        public SpanKey(int start , int end){
            start = start;
            end = end;
        }
        public int start;
        public int end;
    }
    public HashMap<String, Double>  getTagsInContext(Span span, List<Document> tags){
        int contextStartIndex =  Math.max(span.getStart() - CONTEXT_DISTANCE , 0);
        HashMap<String, Double>  matches = new HashMap<>();
        for(SpanKey sk : SpanKey2Tags.keySet()){
            if(sk.end >=  contextStartIndex){
                for(Document tag : tags){
                    String tagStr = tag.get("uri");
                    if(SpanKey2Tags.get(sk).contains(tagStr)){
                        //TODO: grade the tags by distance
                        Double grade = contextGrade(span.getStart() - sk.end);
                        matches.put(tagStr,grade);
                    }
                }
            }
        }
        //TODO: remove the tags from span if matches.size = 0
        return matches;
    }
    public void insertTags(Span span){

    }

    private HashMap<SpanKey , List<String> > SpanKey2Tags = new HashMap<>();

}
