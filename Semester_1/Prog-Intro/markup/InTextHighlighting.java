package markup;

public abstract class InTextHighlighting implements Markup {
    public abstract MdSymbolType getSymbolType();

    public boolean isMDSymbol() {
        switch (getSymbolType()) {
            case EMPHASIS_SYMBOL_STAR:
            case EMPHASIS_SYMBOL_UNDERLINE:
            case STRONG_SYMBOL_STAR:
            case STRONG_SYMBOL_UNDERLINE:
            case STRIKEOUT_SYMBOL:
            case CODE_SYMBOL:
            case CODE_SYMBOL_WR:
                return true;
            default:
                return false;
        }
    }

    abstract void toWR(StringBuilder in);
}
