package spanthera;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omishali on 30/04/2017.
 */
public class Span {
    private SpannedDocument doc;
    private int start, end;
    private List<String> tags = new ArrayList<String>();

    public void setDoc(SpannedDocument doc) {
        this.doc = doc;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end){
        this.end = end;
    }

    public Span(SpannedDocument document, int start, int end) {
        setDoc(document);
        setStart(start);
        setEnd(end);
    }

    /**
     * Span's size in words
     * @return
     */
    public int size() {
        return end - start +1;
    }

    public Word getWord(int index) {
        if (index < 0 || index >= size())
            throw new DocumentException("Illegal word index");

        return doc.getWord(start + index);
    }
    public String text() {
        StringBuffer result = new StringBuffer();

        for (int i = start; i <= end; i++)
            result.append(doc.getWord(i).text() + " ");

        return result.toString().trim();
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public List<String> getTags() {
        return tags;
    }

    public void clearTags() {
        tags.clear();
    }

    public void addTags(List<String> tags) {
        this.tags.addAll(tags);
    }

    public void removeTags(List<String> tags) {
        this.tags.removeAll(tags);
    }

    @Override
    public String toString() {
        return "[" + getStart() + ", " + getEnd() + "]";
    }
}
