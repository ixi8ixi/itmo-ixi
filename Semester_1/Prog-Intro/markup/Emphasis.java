package markup;

import java.util.List;

public class Emphasis extends AbstractHighlighting {
    private final MdSymbolType TYPE;
    public Emphasis(List<InTextHighlighting> internalStyles, MdSymbolType type) {
        super(internalStyles);
        TYPE = type;
    }

    @Override
    String getHTMLOpenTag() {
        return "<em>";
    }

    @Override
    String getHTMLCloseTag() {
        return "</em>";
    }

    @Override
    String getWR() {
        if (TYPE == MdSymbolType.EMPHASIS_SYMBOL_STAR) {
            return "*";
        } else {
            return "_";
        }
    }

    @Override
    public MdSymbolType getSymbolType() {
        return TYPE;
    }
}
