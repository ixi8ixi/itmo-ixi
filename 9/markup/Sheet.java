package markup;

import java.util.List;

public class Sheet {
    private final List<ParagraphHighlighting> SHEET;

    public Sheet(List<ParagraphHighlighting> sheet) {
        SHEET = sheet;
    }

    public void toHTML(StringBuilder in) {
        for (ParagraphHighlighting paragraph : SHEET) {
            paragraph.toHTML(in);
            in.append(System.lineSeparator());
        }
    }
}
