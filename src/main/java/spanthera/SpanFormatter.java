package spanthera;

/**
 * In case we want to format the text of all spans in the document,
 * we should implement this interface and then pass the object to
 * SpannedDocument.format method. Formatted text will be avaliable via
 * Span.getTextFormatted().
 * Created by omishali on 07/01/2018.
 */
public interface SpanFormatter {
    public String format(Span s);
    public boolean isCandidate(Span s);
}
