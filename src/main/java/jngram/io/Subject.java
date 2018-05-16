package jngram.io;

import com.google.gson.annotations.SerializedName;

public class Subject {
    private String uri;

    @SerializedName("jbo:text")
    private String text;

    public Subject(String uri, String text){
        this.uri  = uri;
        this.text = text;
    }

    public Subject() {
        this("", "");
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
