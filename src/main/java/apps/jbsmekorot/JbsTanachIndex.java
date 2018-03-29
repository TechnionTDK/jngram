package apps.jbsmekorot;

/**
 * Created by omishali on 06/11/2017.
 */
public class JbsTanachIndex extends JbsIndex {
    /**
     * To clean the index, just remove its directory. If the directory
     * exists, the index is not recreated.
     *
     * @param rootDirectory
     * @param isSpark
     * @throws Exception
     */
    public JbsTanachIndex(String rootDirectory, boolean isSpark) {
        super();
    }

    @Override
    protected String getInputJsonDirectory() {
        return "./src/main/resources/jbs-text/tanach/";
    }

    @Override
    protected String getOutputIndexDirectory() {
        return "tanach";
    }
}
