package apps.jbsmekorot2spark;

import org.apache.lucene.document.Document;
import spanthera.Span;

import java.util.HashMap;
import java.util.List;

import static apps.jbsmekorot2.Config.CONTEXT_DISTANCE;
import static apps.jbsmekorot2.Config.contextGrade;


public class ContextFinder {
    public class SpanKey{
        public SpanKey(int start , int end){
            Start = start;
            End = end;
        }
        public int Start;
        public int End;
    }

    public HashMap<String, Double>  getTagsInContext(Span span, List<Document> tags){
        int contextStartIndex =  Math.max(span.getStart() - CONTEXT_DISTANCE , 0);
        HashMap<String, Double>  matches = new HashMap<>();
        for(SpanKey sk : SpanKey2Tags.keySet()){
            if(sk.End >=  contextStartIndex && sk.End < span.getStart()){
                try{
                    for(Document tag : tags){
                        String tagStr = tag.get("uri");
                        if(SpanKey2Tags.get(sk).contains(tagStr)){
                            Double grade = contextGrade(span.getStart() - sk.End);
                            matches.put(tagStr,grade);
                        }
                    }
                } catch (NullPointerException e){
                    System.out.println("contextFinder" + sk.End);
                }
            }
        }
        return matches;
    }
    public void insertTags(Span span, List<String> tags){
        SpanKey spanKey= new SpanKey(span.getStart(), span.getEnd());
        SpanKey2Tags.put(spanKey, tags);
    }

    private HashMap<SpanKey , List<String> > SpanKey2Tags = new HashMap<>();

}
