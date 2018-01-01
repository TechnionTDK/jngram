package apps.jbsmekorot2;



public class Config {
    public static int SPAN_SIZE_LAYER_1 = 5;
    public static int SPAN_SIZE_LAYER_2 = 4;
    public static int SPAN_SIZE_LAYER_3 = 2;
    public static int MAX_EDITS = 1;
    public static int MAX_EDITS_LIMIT_BY_WORD_SIZE = 3;
    public static int CONTEXT_DISTANCE = 100 ;
    public static double MINIMUM_GRADE = 0.5;
    public static int NUMBER_OF_TAGS_TO_KEEP = 2;
    public static double contextGrade(int foundDistance, int textSize){ //if we want to return grade in [0,1]
        if(foundDistance > CONTEXT_DISTANCE) return 0;
        else                  return 1;
    }
}
