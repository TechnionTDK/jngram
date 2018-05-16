package jngram.io;

//import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TaggerInput {
    private List<Subject> subjects;

    public TaggerInput(List<Subject> subjects) {
        this.subjects = subjects;
    }
    public TaggerInput() {
        subjects = new ArrayList<Subject>();
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public void addSubject(Subject s) {
        subjects.add(s);
    }
}
