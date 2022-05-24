package markup;

import java.util.List;

public abstract class AbstractHighlighting extends InTextHighlighting {
    protected final List<InTextHighlighting> INTERNAL_STYLES;

    abstract String getHTMLOpenTag();
    abstract String getHTMLCloseTag();
    abstract String getWR();

    public AbstractHighlighting(List<InTextHighlighting> internalStyles) {
        this.INTERNAL_STYLES = internalStyles;
    }

    @Override
    public void toHTML(StringBuilder in) {
        in.append(getHTMLOpenTag());
        for (InTextHighlighting styles : INTERNAL_STYLES) {
            styles.toHTML(in);
        }
        in.append(getHTMLCloseTag());
    }

    @Override
    public void toWR(StringBuilder in) {
        in.append(getWR());
        for (InTextHighlighting style : INTERNAL_STYLES) {
            style.toWR(in);
        }
        in.append(getWR());
    }
}
