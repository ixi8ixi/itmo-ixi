package markup;

public class LineSeparator extends InTextHighlighting {
    @Override
    public void toHTML(StringBuilder in) {
        in.append(System.lineSeparator());
    }

    @Override
    public MdSymbolType getSymbolType() {
        return MdSymbolType.LINE_SEPARATOR;
    }

    @Override
    public void toWR(StringBuilder in) {
        in.append(System.lineSeparator());
    }
}
