package apps.jbsmekorot;

import java.io.Serializable;

/**
 * Created by omishali on 06/11/2017.
 */
public class JbsTanachIndex extends JbsIndex implements Serializable {
    /**
     * To clean the index, just remove its directory. If the directory
     * exists, the index is not recreated.
     *
     * @throws Exception
     */
    public JbsTanachIndex() {
        super();
    }
    public JbsTanachIndex(String pathToIndex){
        super(pathToIndex);
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
