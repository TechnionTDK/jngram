package apps.jbsmekorot;

import org.apache.commons.collections.ListUtils;

import java.util.*;

public class HebrewUtils {
    private final static Set<Character> EHEVI = new TreeSet<>(Arrays.asList('י', 'ו', 'ה', 'א'));

    /**
     * Based on Moshe & Eylon's project.
     * The method actually allows for more replacements than EHEVI.
     * @param word1
     * @param word2
     * @return
     */
    public static boolean isEheviDiff(String word1, String word2) {
        List<Character> chars1 = getChars(word1);
        chars1 = ListUtils.subtract(chars1, getChars(word2));

        List<Character> chars2 = getChars(word2);
        chars2 = ListUtils.subtract(chars2, getChars(word1));

//        System.out.println("ORIG:");
//        System.out.println(chars1);
//        System.out.println(chars2);

        chars1.removeAll(EHEVI);
        chars2.removeAll(EHEVI);

//        System.out.println("REMOVED EHEVI:");
//        System.out.println(chars1);
//        System.out.println(chars2);

        removeAllowedPairs(chars1, chars2);

//        System.out.println("REMOVED PAIRS:");
//        System.out.println(chars1);
//        System.out.println(chars2);

        return chars1.isEmpty() && chars2.isEmpty();

    }

    /**
     * Some letters may be replaced, e.g., shin may be replaced with samech.
     * This methods maintains a list of allowed pairs, and removes them from
     * the input sets iff shin appears in the first (second) set AND samech
     * appears in the second (first) set.
     * @param chars1
     * @param chars2
     */
    private static void removeAllowedPairs(List<Character> chars1, List<Character> chars2) {
        // new pair of chars? add the first to list1 and the second to list2
        List<Character> list1 = new ArrayList<>(Arrays.asList('ש', 'ם', 'נ'));
        List<Character> list2 = new ArrayList<>(Arrays.asList('ס', 'ן', 'ן'));

        for (int i=0; i<list1.size(); i++) {
            if (chars1.contains(list1.get(i)) && chars2.contains(list2.get(i))) {
                chars1.remove(list1.get(i));
                chars2.remove(list2.get(i));
            }
            if (chars1.contains(list2.get(i)) && chars2.contains(list1.get(i))) {
                chars1.remove(list2.get(i));
                chars2.remove(list1.get(i));
            }
        }
    }

    public static List<Character> getChars(String word) {
        List<Character> res = new ArrayList<>();
        for (char c : word.toCharArray()) {
            res.add(c);
        }
        return res;
    }
}
