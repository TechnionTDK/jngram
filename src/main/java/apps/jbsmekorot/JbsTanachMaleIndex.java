package apps.jbsmekorot;

/**
 * Created by omishali on 06/11/2017.
 */
public class JbsTanachMaleIndex extends JbsIndex {
    @Override
    protected String getInputJsonDirectory() {
        return "./src/main/resources/tanach_ktivmale";
    }

    @Override
    protected String getOutputIndexDirectory() {
        return "tanach_ktivmale";
    }
}
