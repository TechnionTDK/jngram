package apps.jbsmekorot2spark;


import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public  final  class Config {
    public static final int MAXIMAL_PASUK_LENGTH= 14;
    public static final int SPAN_SIZE_LAYER_1 = 5;
    public static final int SPAN_SIZE_LAYER_2 = 4;
    public static final int SPAN_SIZE_LAYER_3 = 2;
    public static final int MAX_EDITS = 1;
    public static final int MIN_WORD_LENGTH_FOR_FUZZY = 3;
    public static final int CONTEXT_DISTANCE = 25 ;
    public static final double MINIMUM_GRADE = 0.0;
    public static final int NUMBER_OF_TAGS_TO_KEEP_L1 = 3;
    public static final int NUMBER_OF_TAGS_TO_KEEP_L2 = 2;
    public static final int NUMBER_OF_TAGS_TO_KEEP_L3 = 1;
    public static final int EXTREME_EDITS_FILTER_CANDIDATE = SPAN_SIZE_LAYER_2;
    public static final double MAXIMUM_DIFF_GRADE = 0.3;
    public static final double contextGrade(int foundDistance){ //if we want to return grade in [0,1]
//        if(foundDistance > CONTEXT_DISTANCE) return 0;
//        else                  return 1;
        return 1 - foundDistance / CONTEXT_DISTANCE ;
    }
    private static  Map<String, Double> diffGradeMap = new HashMap<String, Double>();
    private static  Map<String, Double> delGradeMap = new HashMap<String, Double>();
    private static  Map<String, Double> addGradeMap = new HashMap<String, Double>();
    private static final char[] hebChars = {'א','ב','ג','ד','ה','ו','ז','ח','ט','י','כ','ל','מ','נ','ס','ע','פ','צ','ק','ר','ש','ת','ן','ץ','ם','ך','ף'};


    static {
        // init del
        for(int i = 0 ; i < hebChars.length ; i ++){
            StringBuilder sb = new StringBuilder();
            sb.append(hebChars[i]);
            delGradeMap.put(sb.toString(),1.0);
        }
        delGradeMap.put(new Character('א').toString(),0.0);
        delGradeMap.put(new Character('ה').toString(),0.0);
        delGradeMap.put(new Character('ו').toString(),0.0);
        delGradeMap.put(new Character('י').toString(),0.0);
        delGradeMap.put(new Character('ב').toString(),0.0);
        delGradeMap.put(new Character('ל').toString(),0.0);
        delGradeMap.put(new Character('מ').toString(),0.0);

        // init add
        for(int i = 0 ; i < hebChars.length ; i ++){
            StringBuilder sb = new StringBuilder();
            sb.append(hebChars[i]);
            addGradeMap.put(sb.toString(),1.0);
        }
        addGradeMap.put(new Character('א').toString(),0.0);
        addGradeMap.put(new Character('ה').toString(),0.0);
        addGradeMap.put(new Character('ו').toString(),0.0);
        addGradeMap.put(new Character('י').toString(),0.0);
        addGradeMap.put(new Character('ב').toString(),0.0);
        addGradeMap.put(new Character('ל').toString(),0.0);
        addGradeMap.put(new Character('מ').toString(),0.0);

        // init diff
        for(int i = 0 ; i < hebChars.length ; i ++){
            for(int j = 0 ; j < hebChars.length ; j++ ){
                StringBuilder sb = new StringBuilder();
                sb.append(hebChars[i]).append(hebChars[j]);
                if(i==j){
                    diffGradeMap.put(sb.toString(),0.0);
                } else {
                    diffGradeMap.put(sb.toString(),1.0);
                }
            }
        }
    }
    public final static double calcGradeDiff(char spanChar, char docChar) {
        if (spanChar == 'a'){
            return addGradeMap.get(new Character(docChar).toString());
        }
        if (spanChar == 'd'){
            return delGradeMap.get(new Character(docChar).toString());
        }
        StringBuilder sb = new StringBuilder().append(spanChar).append(docChar);
        try{
            return diffGradeMap.get(sb.toString());
        } catch (NullPointerException e){
            System.out.println(sb.toString());
        }
        return 0.0;
    }


    @Test
    public     void testMap(){


        System.out.println(diffGradeMap.size());
        for(Map.Entry<String, Double> ent : diffGradeMap.entrySet()){
            System.out.println(ent.getKey() + " " + ent.getValue());
        }
    }

    private void t1(ArrayList<String> d){
        ArrayList<String> D2 = new ArrayList<>();

    }
    @Test
    public void main_t(){
        int res = StringUtils.getLevenshteinDistance("המדבר", "המחדבר");
        System.out.println(res);
        ArrayList<String> d = new ArrayList<>();
        d.add(new String("hi"));
        t1(d);
        System.out.println(d.size());
    }

}

