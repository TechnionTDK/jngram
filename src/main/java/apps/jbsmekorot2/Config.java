package apps.jbsmekorot2;



public class Config {
    public static int MAXIMAL_PASUK_LENGTH= 14;
    public static int SPAN_SIZE_LAYER_1 = 5;
    public static int SPAN_SIZE_LAYER_2 = 4;
    public static int SPAN_SIZE_LAYER_3 = 2;
    public static int MAX_EDITS = 1;
    public static int MIN_WORD_LENGTH_FOR_FUZZY = 3;
    public static int CONTEXT_DISTANCE = 100 ;
    public static double MINIMUM_GRADE = 0.0;
    public static int NUMBER_OF_TAGS_TO_KEEP_L1 = 3;
    public static int NUMBER_OF_TAGS_TO_KEEP_L2 = 2;
    public static int NUMBER_OF_TAGS_TO_KEEP_L3 = 1;
    public static double contextGrade(int foundDistance){ //if we want to return grade in [0,1]
//        if(foundDistance > CONTEXT_DISTANCE) return 0;
//        else                  return 1;
        return 1 - foundDistance / CONTEXT_DISTANCE ;
    }
}
