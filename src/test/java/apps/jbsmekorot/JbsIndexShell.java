package apps.jbsmekorot;

import org.apache.lucene.document.Document;
import org.junit.*;
import spanthera.SpannedDocument;
import spanthera.manipulations.MergeSiblingSpans;
import spanthera.manipulations.RemoveMatchesInContainedSpans;

import java.util.List;

/**
 * Created by omishali on 03/10/2017.
 */
public class JbsIndexShell {
    private JbsTanachIndex index;
    SpannedDocument doc;
    private String textOrchotTzadikim = "האמת. הנשמה נבראת ממקום רוח הקודש, שנאמר (בראשית ב ד): \"ויפח באפיו נשמת חיים\"; ונחצבה ממקום טהרה, ונבראת מזוהר העליון מכסא הכבוד. ואין למעלה במקום קודשי הקודשים שקר, אלא הכל אמת, שנאמר (ירמיהו י י): \"ויי אלהים אמת\". ומצאתי כי כתיב: \"אהיה אשר אהיה\" (שמות ג יד), וכתיב: \"ויי אלהים אמת, הוא אלהים חיים ומלך עולם\" (ירמיהו שם). ועתה יש להודיעך שהקדוש ברוך הוא אלהים אמת: כי תמצא עשרים ואחת פעמים \"אהיה\" שהוא בגימטריא \"אמת\", וגם כן \"אהיה\" בגימטריא עשרים ואחת.";
    private String textMidrashRabaEster_7_9 = "וירא המן כי אין מרדכי כרע ומשתחוה לו (אסתר ג, ה), אמר רבי איבו (תהלים סט, כד): תחשכנה עיניהם של רשעים מראות. לפי שמראית עיניהם של רשעים מורידות אותם לגיהנם, הדא הוא דכתיב (בראשית ו, ב): ויראו בני האלהים את בנות האדם. (בראשית ט, כב): וירא חם אבי כנען. (בראשית כח, ח): וירא עשו כי רעות בנות כנען. (במדבר כב, ב): וירא בלק בן צפור. (במדבר כד, א): וירא בלעם כי טוב בעיני ה' לברך את ישראל. וירא המן כי אין מרדכי כרע ומשתחוה לו. אבל מראית עיניהם של צדיקים תואר, לפי שמראית עיניהם של צדיקים מעלה אותם למעלה העליונה, הדא הוא דכתיב (בראשית יח, ב): וישא עיניו וירא והנה שלשה אנשים. (בראשית כב, יג): וירא והנה איל. (בראשית כט, ב): וירא והנה באר בשדה. (שמות ג, ב): וירא והנה הסנה. (במדבר כה, ז): וירא פינחס, לפיכך הם שמחים במראית עיניהם, שנאמר (תהלים קז, מב): יראו ישרים וישמחו.";

    @Before
    public void before() throws Exception {
        index = new JbsTanachIndex();
    }

    @Test
    public void getDocumentByURI() {
        List<Document> result = index.searchExactInUri("jbr:text-tanach-4-24-1");
        index.printDocs(result);
    }

    @Test
    public void getDocumentByText() {
        List<Document> result = index.searchExactInText("בעיני יהוה");
        System.out.println(result.size() + " results:");
        index.printDocs(result);
    }

    @Test
    public void printSpannedDocument() {
        doc = new SpannedDocument(textMidrashRabaEster_7_9, PsukimTagger.MINIMAL_PASUK_LENGTH, PsukimTagger.MAXIMAL_PASUK_LENGTH);
        doc.add(new PsukimTagger()).tag();
        doc.add(new MergeSiblingSpans()).manipulate();
        doc.add(new RemoveMatchesInContainedSpans()).manipulate();
        doc.add(new FilterTagsFromSmallSpans()).manipulate();

        System.out.println(doc.toString());
    }


}
