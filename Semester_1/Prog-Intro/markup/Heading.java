package markup;

import java.util.List;

public class Heading implements ParagraphHighlighting {
    private final List<InTextHighlighting> INTERNAL_STYLES;
    private final int level;

    public Heading(List<InTextHighlighting> internal_styles, int level) {
        INTERNAL_STYLES = internal_styles;
        this.level = level;
    }

    @Override
    public void toHTML(StringBuilder in) {
        in.append(String.format("<h%s>", level));
        for (InTextHighlighting styles : INTERNAL_STYLES) {
            styles.toHTML(in);
        }
        in.append(String.format("</h%s>", level));
    }
}
