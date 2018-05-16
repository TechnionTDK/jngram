package jngram.io;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class TaggerOutput implements Comparable<TaggerOutput>{
    private List<TaggedSubject> subjects;

    public TaggerOutput(List<TaggedSubject> taggedSubjectArray) {
        this.subjects = taggedSubjectArray;
    }
    public TaggerOutput() {
        this.subjects = new ArrayList<TaggedSubject>();
    }

    public List<TaggedSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<TaggedSubject> subjects) {
        this.subjects = subjects;
    }

    public void addTaggedSubject(TaggedSubject s) {
        subjects.add(s);
    }

    public int compareTo(TaggerOutput o) {
        return 0;
    }

    private boolean containsSubject(TaggedSubject ts){
        for (TaggedSubject myts: this.getSubjects()){
            if (ts.getUri().equals(myts.getUri()) && ts.equals(myts)){
                    return true;
            }
        }
        return false;
    }

    public double countTags(){
        double tagsCounter = 0;
        for (TaggedSubject ts: this.getSubjects()){
            tagsCounter += ts.countTagsInSubject();
        }

        return tagsCounter;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
