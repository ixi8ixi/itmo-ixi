package markup;

import java.util.List;

public class Strikeout extends AbstractHighlighting {
    public Strikeout(List<InTextHighlighting> internalStyles) {
        super(internalStyles);
    }

    @Override
    String getHTMLOpenTag() {
        return "<s>";
    }

    @Override
    String getHTMLCloseTag() {
        return "</s>";
    }

    @Override
    String getWR() {
        return "--";
    }
}