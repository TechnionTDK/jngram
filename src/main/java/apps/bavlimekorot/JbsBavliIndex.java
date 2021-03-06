package apps.bavlimekorot;


import java.io.Serializable;
import apps.jbsmekorot.JbsIndex;


/**
 * Created by AsafYehsurun on 18/05/2018.
 */

public class JbsBavliIndex extends JbsIndex implements Serializable {
    /**
     * @throws Exception
     */
    public JbsBavliIndex() {
        super();
        NUM_OF_RESULTS = 2500;
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