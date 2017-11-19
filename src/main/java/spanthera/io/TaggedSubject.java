package spanthera.io;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valkh on 29-Nov-16.
 */
public class TaggedSubject {
    private String uri;
    private List<Tag> tags;

    public TaggedSubject(TaggedSubject other) {
        this.uri = new String(other.uri);
        this.tags = new ArrayList<Tag>();
        for (Tag t: other.tags){
            this.tags.add(new Tag(t.getSpan(), t.getUri()));
        }
    }

    public TaggedSubject(List<Tag> tags) {
        this.tags = tags;
    }

    public TaggedSubject() {
        tags = new ArrayList<Tag>();
    }

    public TaggedSubject(String uri) {
        this.tags = new ArrayList<Tag>();
        this.uri = uri;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() { return uri; }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public  TaggedSubject findRelatedSubject(TaggerOutput other){
        for (TaggedSubject myts: other.getSubjects()){
            if (this.getUri().equals(myts.getUri())){
                return myts;
            }
        }
        return null;
    }

    public double countTagsInSubject(){
        return this.getTags().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaggedSubject that = (TaggedSubject) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;


        boolean retVal = true;
        for (Tag t: this.tags){
            if (!that.containTag(t)){
                retVal = false;
            }
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }

    public boolean containTag(Tag t){
        for (Tag tag: this.tags){
            if (t.getUri().equals(tag.getUri()) && t.getSpan().equals(tag.getSpan())){
                return true;
            }
        }

        return false;
    }

    public Tag findTag(Tag t){
        for (Tag tag: this.tags){
            if (t.getUri().equals(tag.getUri())){
                return tag;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("uri= " + uri + "\n");
        for (Tag tag : getTags())
            buffer.append("tag= " + tag + " ");

        return buffer.toString();
    }
}
