package apps.bavlimekorot.main;

import jngram.Ngram;
import jngram.NgramFormatter;

public class BavliNgramFormatter implements NgramFormatter{

    public BavliNgramFormatter() {}

    public String format(Ngram s) {
        return this.format(s.getText());
    }

    public boolean isCandidate(Ngram s) {
        return true;
    }

    private String format(String s) {
        String result = s.replace("\"", "").replace("%", "").replace(";", "").replace(".", "").replace(",", "").replace(":", "").replace("?", "").replace("-", "dash").replaceAll("\\bdash\\b", "@").replace("dash", "").replace("׳", "tag").replace("'", "tag").replaceAll("\\bיי\\b", "יהוה").replaceAll("\\bבהtag\\b", "ביהוה").replaceAll("\\bויי\\b", "ויהוה").replaceAll("\\bהtag\\b", "יהוה").replaceAll("\\bדtag\\b", "יהוה").replaceAll("\\bוהtag\\b", "ויהוה").replaceAll("\\bודtag\\b", "ויהוה").replaceAll("\\bכהtag\\b", "כיהוה").replaceAll("\\bלהtag\\b", "ליהוה").replaceAll("\\bכדtag\\b", "כיהוה").replaceAll("\\bאלוקים\\b", "אלהים").replaceAll("\\bאלוקים\\b", "אלהים").replaceAll("\\bאלקים\\b", "אלהים").replaceAll("\\bואלקים\\b", "ואלהים").replaceAll("\\bואלוקים\\b", "ואלהים").replaceAll("\\bאלקיך\\b", "אלהיך").replaceAll("\\bמאלקיך\\b", "מאלהיך").replaceAll("\\bכאלוקים\\b", "כאלהים").replaceAll("\\bכאלקים\\b", "כאלהים").replaceAll("\\bלאלקים\\b", "לאלהים").replaceAll("\\bאלקיכם\\b", "אלהיכם").replace("tag", "");
        return result;
    }
}
