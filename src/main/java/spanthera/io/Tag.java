package spanthera.io;

import java.io.Serializable;

/**
 * Created by valkh on 29-Nov-16.
 */
public class Tag{

    private String uri;
    private String span;

    public Tag(String span, String uri){
        this.uri = uri;
        this.span = span;
    }

    public Tag(int startSpan, int endSpan, String uri){
        this.uri = uri;
        this.span = startSpan + "-" + endSpan;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (uri != null ? !uri.equals(tag.uri) : tag.uri != null) return false;
        return span != null ? span.equals(tag.span) : tag.span == null;

    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (span != null ? span.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "uri='" + uri + '\'' + " span= " + span +
                '}';
    }
}
