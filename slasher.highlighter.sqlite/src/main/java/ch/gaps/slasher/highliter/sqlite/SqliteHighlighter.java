package ch.gaps.slasher.highliter.sqlite;

import ch.gaps.slasher.highliter.Highlighter;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a highliter for the Sqlite syntax
 */
public class SqliteHighlighter implements Highlighter {

    // regex used for highlighting
    // specific to SQLite syntax
    private static final String QUOTE_ID_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACKET_ID_PATTERN = "\\[([^\"\\\\]|\\\\.)*\\]";

    // pattern group names
    private static final String QUOTE_ID_GROUP_NAME = "QUOTEID";
    private static final String PAREN_GROUP_NAME = "PAREN";
    private static final String BRACKET_ID_GROUP_NAME = "BRACKETID";


    private List<String> keywords;
    private String keywordPattern;
    private Pattern pattern;

    private static final String[] STYLE_CLASSES = {
            "keyword",
            "semicolon",
            "paren",
            "bracket",
            "brace",
            "string",
            "comment",
            "quoteid",
            "bracketid"
    };

    public SqliteHighlighter() throws IOException, URISyntaxException {
        keywords = getKeywords();
        keywordPattern = keywordPattern = "\\b(" + String.join("|", keywords) + ")\\b";
        String regex = "(?<" + KEYWORD_GROUP_NAME + ">"+ keywordPattern + ")"
                + "|(?<" + STRING_GROUP_NAME + ">" + STRING_PATTERN + ")"
                + "|(?<" + PAREN_GROUP_NAME + ">" + PAREN_PATTERN + ")"
                + "|(?<" + SEMICOLON_GROUP_NAME+ ">" + SEMICOLON_PATTERN + ")"
                + "|(?<" + COMMENT_GROUP_NAME+ ">" + COMMENT_PATTERN + ")"
                + "|(?<" + QUOTE_ID_GROUP_NAME + ">" + QUOTE_ID_PATTERN + ")"
                + "|(?<" + BRACKET_ID_GROUP_NAME + ">" + BRACKET_ID_PATTERN + ")";

        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    static List<String> getKeywords() throws URISyntaxException, IOException {

        return Files.readAllLines(Paths.get(SqliteHighlighter.class.getResource("/keywords_sqlite.txt").toURI()));
    }

    @Override
    public List<String> getAllKeywords() {
        try {
            return getKeywords();
        } catch (URISyntaxException e) {
            Logger.getLogger(SqliteHighlighter.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ArrayList<>();
        } catch (IOException e) {
            Logger.getLogger(SqliteHighlighter.class.getName()).log(Level.SEVERE, e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> getMatcherGroupNames() {
        String[] groupNames = {
                KEYWORD_GROUP_NAME,
                PAREN_GROUP_NAME,
                STRING_GROUP_NAME,
                SEMICOLON_GROUP_NAME,
                COMMENT_GROUP_NAME,
                QUOTE_ID_GROUP_NAME,
                BRACKET_ID_GROUP_NAME
        };

        return new ArrayList<>(Arrays.asList(groupNames));
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }

//    @Override
//    public StyleSpans<Collection<String>> computeHighlighting(String text) {
//        Matcher matcher = pattern.matcher(text);
//        int lastKwEnd = 0;
//        StyleSpansBuilder<Collection<String>> spansBuilder
//                = new StyleSpansBuilder<>();
//        while(matcher.find()) {
//            String styleClass =
//                    matcher.group(KEYWORD_GROUP_NAME) != null ? "keyword" :
//                            matcher.group(PAREN_GROUP_NAME) != null ? "paren" :
//                                    matcher.group(STRING_GROUP_NAME) != null ? "string" :
//                                            matcher.group(SEMICOLON_GROUP_NAME) != null ? "semicolon" :
//                                                    matcher.group(COMMENT_GROUP_NAME) != null ? "comment" :
//                                                            matcher.group(QUOTE_ID_GROUP_NAME) != null ? "quote_id" :
//                                                                    matcher.group(BRACKET_ID_GROUP_NAME) != null ? "bracket_id" :
//                                                                            null; /* never happens */ assert styleClass != null;
//            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
//            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
//            lastKwEnd = matcher.end();
//        }
//        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
//        return spansBuilder.create();
//    }
}
