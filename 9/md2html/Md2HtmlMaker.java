package md2html;

import markup.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Md2HtmlMaker {
    private int position = 0;
    private ArrayList<InTextHighlighting> currentParagraph = new ArrayList<>();
    private ArrayList<InTextHighlighting> resultParagraph = new ArrayList<>();
    private final ArrayList<ParagraphHighlighting> sheet = new ArrayList<>();
    private final MarkdownScanner in;
    private boolean wr = false;

    public Md2HtmlMaker(MarkdownScanner in) {
        this.in = in;
    }

    List<InTextHighlighting> createHighLighting() {
        if (currentParagraph.get(position) instanceof MdSymbol) {
            MdSymbolType type = ((MdSymbol) currentParagraph.get(position)).getType();
            ArrayList<InTextHighlighting> internalStyles = new ArrayList<>();
            internalStyles.add(currentParagraph.get(position));
            position++;
            while (position < currentParagraph.size() &&
                    !(currentParagraph.get(position) instanceof MdSymbol &&
                            ((MdSymbol) currentParagraph.get(position)).getType() == type)) {
                internalStyles.addAll(createHighLighting());
            }
            if (position < currentParagraph.size()) {
                position++;
                InTextHighlighting element;
                List<InTextHighlighting> noSymbol = internalStyles.subList(1, internalStyles.size());
                switch (type) {
                    case EMPHASIS_SYMBOL_STAR:
                    case EMPHASIS_SYMBOL_UNDERLINE:
                        element = new Emphasis(noSymbol, type);
                        break;
                    case STRONG_SYMBOL_STAR:
                    case STRONG_SYMBOL_UNDERLINE:
                        element = new Strong(noSymbol, type);
                        break;
                    case STRIKEOUT_SYMBOL:
                        element = new Strikeout(noSymbol);
                        break;
                    case CODE_SYMBOL:
                        element = new Code(noSymbol);
                        break;
                    case CODE_SYMBOL_WR:
                        element = new CodeWR(noSymbol);
                        break;
                    default:
                        throw new IllegalStateException("Not a markdown symbol: " + type);
                }
                return List.of(element);
            } else {
                return internalStyles;
            }
        } else {
            position++;
            return List.of(currentParagraph.get(position - 1));
        }
    }

    ParagraphHighlighting createParagraph() {
        if (resultParagraph.get(0) instanceof Text) {
            String text = resultParagraph.get(0).toString().substring(0,
                    Math.min(7, resultParagraph.get(0).toString().length())
            );
            int counter = 0;
            while (text.charAt(counter) == '#' && counter < 6) {
                counter++;
            }
            if (Character.isWhitespace(text.charAt(counter)) && text.charAt(0) == '#') {
                resultParagraph.set(0, new Text(resultParagraph.get(0).toString().substring(counter + 1)));
                return new Heading(resultParagraph, counter);
            }
        }
        return new Paragraph(resultParagraph);
    }

    public StringBuilder createSheet() throws IOException {
        StringBuilder out = new StringBuilder();
        in.toNextParagraph();
        while (in.hasNext()) {
            currentParagraph = new ArrayList<>();
            resultParagraph = new ArrayList<>();
            position = 0;
            InTextHighlighting prev = new Text("");
            InTextHighlighting current = in.next();
            while (!(prev instanceof LineSeparator && current instanceof LineSeparator) || wr) {
                if (prev instanceof MdSymbol &&
                        current instanceof MdSymbol &&
                        ((MdSymbol) prev).getType() == ((MdSymbol) current).getType()
                    ) {
                    switch (((MdSymbol) prev).getType()) {
                        case EMPHASIS_SYMBOL_STAR:
                            currentParagraph.remove(currentParagraph.size() - 1);
                            current = new MdSymbol(MdSymbolType.STRONG_SYMBOL_STAR);
                            break;
                        case EMPHASIS_SYMBOL_UNDERLINE:
                            currentParagraph.remove(currentParagraph.size() - 1);
                            current = new MdSymbol(MdSymbolType.STRONG_SYMBOL_UNDERLINE);
                            break;
                        case CODE_SYMBOL:
                            current = in.next();
                            if (current instanceof MdSymbol &&
                                    ((MdSymbol) current).getType() == MdSymbolType.CODE_SYMBOL) {
                                currentParagraph.remove(currentParagraph.size() - 1);
                                current = new MdSymbol(MdSymbolType.CODE_SYMBOL_WR);
                                wr = !wr;
                            } else {
                                currentParagraph.add(new MdSymbol(MdSymbolType.CODE_SYMBOL));
                            }
                            break;
                    }
                }
                currentParagraph.add(current);
                prev = current;
                if (!in.hasNext()) {
                    break;
                }
                current = in.next();
            }
            if (prev instanceof LineSeparator) {
                currentParagraph.remove(currentParagraph.size() - 1);
            }
            while (position < currentParagraph.size()) {
                resultParagraph.addAll(createHighLighting());
            }
            sheet.add(createParagraph());
            in.toNextParagraph();
        }
        Sheet result = new Sheet(sheet);
        result.toHTML(out);
        return out;
    }
}
