package jngram;

import jngram.Ngram;
import jngram.NgramDocument;
import jngram.NgramDocumentManipulation;

public abstract class NgramManipulation extends NgramDocumentManipulation {

    @Override
    public void manipulate(NgramDocument doc) {
        System.out.println("Apply maniuplation " + getName() + "...");
        for (Ngram ng : doc.getAllNgrams()) {
            if (!isCandidate(ng))
                continue;
            else
                manipulate(doc, ng);
        }
    }
    protected abstract boolean isCandidate(Ngram ng);
    protected abstract void manipulate(NgramDocument doc, Ngram ng);
}
