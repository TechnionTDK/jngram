package apps.jbsmekorot;

/**
 * Created by omishali on 06/11/2017.
 */
public class JbsTanachMaleIndex extends JbsIndex {
    /**
     * To clean the index, just remove its directory. If the directory
     * exists, the index is not recreated.
     *
     * @throws Exception
     */
    public JbsTanachMaleIndex() {
        super();
    }
    public JbsTanachMaleIndex(String pathToIndex)
    {
        super(pathToIndex);
    }

    @Override
    protected String getInputJsonDirectory() {
        return "./src/main/resources/tanach_ktivmale/";
    }

    @Override
    protected String getOutputIndexDirectory() {
        return "tanach_ktivmale";
    }
}
