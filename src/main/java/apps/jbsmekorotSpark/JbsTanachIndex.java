package apps.jbsmekorotSpark;

/**
 * Created by omishali on 06/11/2017.
 */
public class JbsTanachIndex extends JbsIndex {
    @Override
    protected String getInputJsonDirectory() {
        return "./jbs-text/tanach/";
    }

    @Override
    protected String getOutputIndexDirectory() {
        return "tanach";
    }
}
