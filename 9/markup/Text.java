package markup;

public class Text implements InTextHighlighting {
    private final String TEXT_VALUE;

    public Text(String text) {
        this.TEXT_VALUE = text;
    }

    @Override
    public String toString() {
        return TEXT_VALUE;
    }

    @Override
    public void toHTML(StringBuilder in) {
        in.append(TEXT_VALUE);
    }

    @Override
    public void toWR(StringBuilder in) {
        in.append(TEXT_VALUE);
    }
}
