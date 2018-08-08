package apps.jbsmekorot;

import java.io.Serializable;

/**
 * Created by omishali on 06/11/2017.
 */
public class JbsTanachIndex extends JbsIndex implements Serializable {
    private static JbsTanachIndex instance = null;
    /**
     * Singleton, to prevent creation of multiple instances.
     * To clean the index, just remove its directory. If the directory
     * exists, the index is not recreated.
     *
     * @throws Exception
     */
    private JbsTanachIndex() {
        super();
    }

    public static JbsTanachIndex instance() {
        if (instance == null)
            instance = new JbsTanachIndex();

        return instance;
    }

    public JbsTanachIndex(String pathToIndex){
        super(pathToIndex);
    }

    @Override
    protected String getInputJsonDirectory() {
        return "./src/main/resources/tanach-json/";
    }

    @Override
    protected String getOutputIndexDirectory() {
        return "tanach";
    }
}
