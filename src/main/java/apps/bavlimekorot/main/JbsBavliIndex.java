package apps.bavlimekorot.main;


import java.io.Serializable;
import apps.jbsmekorot.JbsIndex;


/**
 * Created by AsafYehsurun on 18/05/2018.
 */

public class JbsBavliIndex extends JbsIndex implements Serializable {
    /**
     * To clean the index, just remove its directory. If the directory
     * exists, the index is not recreated.
     *
     * @throws Exception
     */
    public JbsBavliIndex() {
        super();
    }

    public JbsBavliIndex(String pathToIndex){
        super(pathToIndex);
    }

    @Override
    protected String getInputJsonDirectory() {
        return "./src/main/resources/bavli-json/";
    }

    @Override
    protected String getOutputIndexDirectory() {
        return "bavli";
    }
}