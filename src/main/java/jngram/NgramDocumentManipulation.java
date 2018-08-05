package jngram;

/**
 * Created by omishali on 04/09/2017.
 */
public abstract class NgramDocumentManipulation {
    /**
     * Performs a manipulation on the given NgramDocument.
     * @param doc
     */
    public abstract void manipulate(NgramDocument doc);
    public String getName() {
        return getClass().getSimpleName();
    }
}
