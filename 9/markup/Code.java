package markup;

import java.util.List;

public class Code extends AbstractHighlighting {
    public Code(List<InTextHighlighting> internalStyles) {
        super(internalStyles);
    }

    @Override
    String getHTMLOpenTag() {
        return "<code>";
    }

    @Override
    String getHTMLCloseTag() {
        return "</code>";
    }

    @Override
    String getWR() {
        return "`";
    }
}
