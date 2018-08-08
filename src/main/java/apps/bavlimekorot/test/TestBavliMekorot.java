package apps.bavlimekorot.test;

import apps.bavlimekorot.main.BavliMekorot;
import org.junit.*;
import java.util.LinkedList;
import java.util.List;
import jngram.io.Tag;



public class TestBavliMekorot {

    @Test
    public void findTextMekorot() {

        // If we expect 1 correct source, then if we get <=2 sources we're ok.
        // Id we expect 2 correct sources, then if we get <=4 sources we're ok and so on.
        final int stillNotTooManySourcesFactor = 2;

        String text1 = "כסף משנה  פעמים בכל וכו'. מפורש פרק קמא דברכות (דף י\"א.) דבין לבית שמאי בין לבית הלל פעמים ביום קוראים את שמע אלא שנחלקו היאך קוראין אותה וז''ל %ב''ש אומרים בערב כל אדם יטו ויקראו ובבקר יעמדו שנאמר בשכבך ובקומך וב''ה אומרים כל אדם קורין כדרכן שנאמר ובלכתך בדרך א''כ למה נאמר בשכבך ובקומך בשעה שדרך בני אדם שוכבים ובשעה שבני אדם עומדים.% ורבינו נקט לישנא דב''ה והקדים ק''ש של לילה כלישנא דקרא של לילה ברישא וטעמא דקרא משום דתחלת היום מן הלילה הוא כדכתיב ויהי ערב וכו':";
        String uri1 = "jbr:text-bavli-1-10-2";
        int expectedUris1 = 1;
        String text2 = "כסף משנה  ומה הוא קורא וכו'. כך מתבאר מתוך המשנה שאכתוב בסמוך: ומקדימין לקרות וכו'. פ''ב דברכות (דף י\"ג.) %אמר ריב''ק למה קדמה פרשת שמע לוהיה כדי שיקבל עליו עול מלכות שמים תחלה ואחר כך יקבל עליו עול מצות והיה אם שמוע לויאמר שוהיה אם שמוע נוהג בין ביום בין בלילה ויאמר אינו נוהג אלא ביום% כלומר דמשתעי בציצית שאינה נוהגת אלא ביום. ודע שגירסת רבינו הנכונה ואחריה והיה אם שמוע שיש בה צווי על שאר המצות וכך מצאתי בספר מדוייק";
        String uri2 = "jbr:text-bavli-1-13-1";
        int expectedUris2 = 1;
        String text3 = "והדין משנה סוף פרק קמא דברכות (דף י\"ב ע\"ב) %מזכירין יציאת מצרים בלילות שנאמר למען תזכור את יום צאתך מארץ מצרים כל ימי חייך ימי חייך הימים כל ימי חייך הלילות:%";
        String uri3 = "jbr:text-bavli-1-12-2";
        int expectedUris3 = 1;
        String text4 = "הקורא קריאת שמע כשהוא גומר וכו'. בפסחים (דף נ\"ו.) פרק מקום שנהגו %אמר רשב''ל ויקרא יעקב לבניו בקש יעקב לגלות לבניו קץ הימין ונסתלקה ממנו שכינה אמר שמא ח''ו יש במטתי פסול כאברהם שיצא ממנו ישמעאל ואבי יצחק שיצא ממנו עשו אמרו לו בניו שמע ישראל ה' אלהינו ה' אחד כשם שאין בלבך אלא אחד כך אין בלבבנו אלא אחד באותה שעה פתח יעקב אבינו ואמר ברוך שם כבוד מלכותו לעולם ועד. אמרי רבנן היכי נעביד נימריה לא אמריה משה רבינו לא נימריה הא אמריה יעקב התקינו שיהיו אומרים אותו בחשאי.% ורבינו לא נתן טעם למה אומרים אותו בלחש דברור הוא הטעם כדי להבדיל בינו למה שכתוב בתורה ולא בא אלא לתת טעם";
        String uri4 = "jbr:text-bavli-4-56-1";
        int expectedUris4 = 1;
        String text5 = "הקורא קריאת שמע מברך וכו'. משנה פ''ק דברכות (דף י\"א.) %בשחר מברך שתים לפניה ואחת לאחריה ובערב מברך שתים לפניה ושתים לאחריה ותניא אין אומרים אהבת עולם אלא אהבה רבה ורבנן אמרי אהבת עולם% ופסקו הרי''ף והרא''ש כרבנן וזה דעת רבינו:";
        String uri51 = "jbr:text-bavli-1-11-1";
        String uri52 = "jbr:text-bavli-1-11-2";
        int expectedUris5 = 2;
        String text6 = "שם (דף י\"א ע\"ב) %אמר רב יהודה אמר שמואל אהבה רבה וכן וכו' ורבנן אמרי אהבת עולם וכן הוא אומר וכו' תניא נמי הכי אין אומרים אהבה רבה אלא אהבת עולם% ע''כ כך היא גירסת הרי''ף ז''ל בהלכות";
        String uri6 = "jbr:text-bavli-1-11-2";
        int expectedUris6 = 1;
        String text7 = "הקדים ברכה שנייה וכו'. שם [י''א:] %תנן התם א''ל הממונה ברכו ברכה אחת מאי היא א''ר זריקא א''ר אמי אר''ל יוצר אור ואמרו שם דהא דרבי זריקא לאו בפי' איתמר אלא מכללא איתמר דאמר רבי זריקא א''ר אמי אר''ל זאת אומרת ברכות אין מעכבות זו את זו כלומר אא''ב יוצר אור הוו אמרי היינו דאין מעכבות זו את זו דלא הוו אמרי אהבה רבה אא''א אהבה רבה הוו אמרי דילמא הא דלא אמרי יוצר אור משום דלא מטא זמן יוצר אור וכי מטא זמניה הוו אמרי ליה ואי מכללא מאי כלומר מאי גריעותיה מאילו איתמר בפירוש ומהדר דאי מכללא לעולם אהבה רבה הוו אמרי וכי מטא זמן יוצר אור הוו אמרי ליה ומאי ברכות אין מעכבות זו את זו סדר ברכות.% משמע מהכא דכיון דקי''ל דברכות אין מעכבות זו את זו מכ''ש שאין סדר לברכות";
        String uri71 = "jbr:text-bavli-1-11-2";
        String uri72 = "jbr:text-bavli-1-12-1";
        int expectedUris7 = 2;

        List<Tag> currTags;
        boolean containsCorrectSources;
        boolean notTooManySources;

        currTags = BavliMekorot.findTextMekorot(text1, true);
//        containsCorrectSources = getUris(currTags).contains(uri1);
//        notTooManySources = currTags.size() <= expectedUris1 * stillNotTooManySourcesFactor;
//        Assert.assertTrue(containsCorrectSources && notTooManySources);

        currTags = BavliMekorot.findTextMekorot(text2, true);
//        containsCorrectSources = getUris(currTags).contains(uri2);
//        notTooManySources = currTags.size() <= expectedUris2 * stillNotTooManySourcesFactor;
//        Assert.assertTrue(containsCorrectSources && notTooManySources);

        currTags = BavliMekorot.findTextMekorot(text3, true);
//        containsCorrectSources = getUris(currTags).contains(uri3);
//        notTooManySources = currTags.size() <= expectedUris3 * stillNotTooManySourcesFactor;
//        Assert.assertTrue(containsCorrectSources && notTooManySources);

        currTags = BavliMekorot.findTextMekorot(text4, true);
//        containsCorrectSources = getUris(currTags).contains(uri4);
//        notTooManySources = currTags.size() <= expectedUris4 * stillNotTooManySourcesFactor;
//        Assert.assertTrue(containsCorrectSources && notTooManySources);

        currTags = BavliMekorot.findTextMekorot(text5, true);
//        containsCorrectSources = getUris(currTags).contains(uri51) && getUris(currTags).contains(uri52);
//        notTooManySources = currTags.size() <= expectedUris5 * stillNotTooManySourcesFactor;
//        Assert.assertTrue(containsCorrectSources && notTooManySources);

        currTags = BavliMekorot.findTextMekorot(text6, true);
//        containsCorrectSources = getUris(currTags).contains(uri6);
//        notTooManySources = currTags.size() <= expectedUris6 * stillNotTooManySourcesFactor;
//        Assert.assertTrue(containsCorrectSources && notTooManySources);

        currTags = BavliMekorot.findTextMekorot(text7, true);
//        containsCorrectSources = getUris(currTags).contains(uri71) && getUris(currTags).contains(uri72);
//        notTooManySources = currTags.size() <= expectedUris7 * stillNotTooManySourcesFactor;
//        Assert.assertTrue(containsCorrectSources && notTooManySources);

        return;
    }

    public static List<String> getUris(List<Tag> tags) {
        LinkedList<String> uris = new LinkedList<>();
        String uri;
        for(Tag tag : tags) {
            uri = tag.getUri();
            if(!uris.contains(uri)) {
                uris.add(uri);
            }
        }
        return uris;
    }

}