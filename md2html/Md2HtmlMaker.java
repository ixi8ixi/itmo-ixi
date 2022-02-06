package md2html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import markup.Code;
import markup.CodeWR;
import markup.Emphasis;
import markup.Heading;
import markup.InTextHighlighting;
import markup.MdSymbol;
import markup.MdSymbolType;
import markup.Paragraph;
import markup.ParagraphHighlighting;
import markup.Sheet;
import markup.Strikeout;
import markup.Strong;
import markup.Text;

public class Md2HtmlMaker {
    private int position = 0;
    private List<InTextHighlighting> currentParagraph = new ArrayList<>();
    private List<InTextHighlighting> resultParagraph = new ArrayList<>();
    private final ArrayList<ParagraphHighlighting> sheet = new ArrayList<>();
    private final MarkdownScanner in;
    private boolean wr = false;

    public Md2HtmlMaker(MarkdownScanner in) {
        this.in = in;
    }

    List<InTextHighlighting> createHighLighting() {
        if (currentParagraph.get(position).isMDSymbol()) {
            MdSymbolType type = currentParagraph.get(position).getSymbolType();
            ArrayList<InTextHighlighting> internalStyles = new ArrayList<>();
            internalStyles.add(currentParagraph.get(position));
            position++;
            while (position < currentParagraph.size() &&
                    !(currentParagraph.get(position).isMDSymbol() &&
                            currentParagraph.get(position).getSymbolType() == type)) {
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
        if (resultParagraph.get(0).getSymbolType() == MdSymbolType.TEXT) {
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
            while (!(prev.getSymbolType() == MdSymbolType.LINE_SEPARATOR &&
                    current.getSymbolType() == MdSymbolType.LINE_SEPARATOR) || wr) {
                if (prev.isMDSymbol() &&
                        current.isMDSymbol() &&
                        prev.getSymbolType() == current.getSymbolType()
                    ) {
                    switch (prev.getSymbolType()) {
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
                            if (current.isMDSymbol() &&
                                    current.getSymbolType() == MdSymbolType.CODE_SYMBOL) {
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
            if (prev.getSymbolType() == MdSymbolType.LINE_SEPARATOR) {
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
