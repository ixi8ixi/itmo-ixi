package markup;

import java.util.List;

public class Paragraph implements ParagraphHighlighting {
    private final List<InTextHighlighting> INTERNAL_STYLES;

    public Paragraph(List<InTextHighlighting> internalStyles) {
        this.INTERNAL_STYLES = internalStyles;
    }

    @Override
    public void toHTML(StringBuilder in) {
        in.append("<p>");
        for (InTextHighlighting styles : INTERNAL_STYLES) {
            styles.toHTML(in);
        }
        in.append("</p>");
    }
}
