package markup;

import java.util.List;

public class Strong extends AbstractHighlighting {
    private final MdSymbolType TYPE;

    public Strong(List<InTextHighlighting> internalStyles, MdSymbolType type) {
        super(internalStyles);
        TYPE = type;
    }

    @Override
    String getHTMLOpenTag() {
        return "<strong>";
    }

    @Override
    String getHTMLCloseTag() {
        return "</strong>";
    }

    @Override
    String getWR() {
        if (TYPE == MdSymbolType.STRONG_SYMBOL_STAR) {
            return "**";
        } else {
            return "__";
        }
    }

    @Override
    public MdSymbolType getSymbolType() {
        return TYPE;
    }
}
