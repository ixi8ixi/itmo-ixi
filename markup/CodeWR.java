package markup;

import java.util.List;

public class CodeWR extends InTextHighlighting {
    private final List<InTextHighlighting> INTERNAL_STYLES;

    public CodeWR(List<InTextHighlighting> internalStyles) {
        this.INTERNAL_STYLES = internalStyles;
    }

    @Override
    public void toHTML(StringBuilder in) {
        in.append("<pre>");
        for (InTextHighlighting style : INTERNAL_STYLES) {
            style.toWR(in);
        }
        in.append("</pre>");
    }

    @Override
    public MdSymbolType getSymbolType() {
        return MdSymbolType.CODE_SYMBOL_WR;
    }

    @Override
    public void toWR(StringBuilder in) {
    }
}
