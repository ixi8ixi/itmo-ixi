package markup;

public class LineSeparator implements InTextHighlighting{
    @Override
    public void toHTML(StringBuilder in) {
        in.append(System.lineSeparator());
    }

    @Override
    public void toWR(StringBuilder in) {
        in.append(System.lineSeparator());
    }
}
