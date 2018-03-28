package apps.jbsmekorot;

/**
 * Created by omishali on 06/11/2017.
 */
public class JbsTanachIndex extends JbsIndex {
    @Override
    protected String getInputJsonDirectory() {
        return "./src/main/resources/jbs-text/tanach/";
    }

    @Override
    protected String getOutputIndexDirectory() {
        return "tanach";
    }
}
