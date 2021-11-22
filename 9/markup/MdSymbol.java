package markup;

public class MdSymbol implements InTextHighlighting {
    private final MdSymbolType TYPE;

    public MdSymbol(MdSymbolType type) {
        TYPE = type;
    }

    public MdSymbolType getType() {
        return TYPE;
    }

    @Override
    public void toHTML(StringBuilder in) {
        switch (TYPE) {
            case EMPHASIS_SYMBOL_STAR:
                in.append("*");
                break;
            case EMPHASIS_SYMBOL_UNDERLINE:
                in.append("_");
                break;
            default:
                throw new IllegalStateException("Illegal type of markdown symbol: " + TYPE);
        }
    }

    @Override
    public void toWR(StringBuilder in) {
        String symbol;
        switch (TYPE) {
            case EMPHASIS_SYMBOL_STAR:
                symbol = "*";
                break;
            case EMPHASIS_SYMBOL_UNDERLINE:
                symbol = "_";
                break;
            case STRONG_SYMBOL_STAR:
                symbol = "**";
                break;
            case STRONG_SYMBOL_UNDERLINE:
                symbol = "__";
                break;
            case STRIKEOUT_SYMBOL:
                symbol = "--";
                break;
            case CODE_SYMBOL:
                symbol = "`";
                break;
            default:
                throw new IllegalStateException("Invalid state order: " + TYPE + "hasn't pair");
        }
        in.append(symbol);
    }
}