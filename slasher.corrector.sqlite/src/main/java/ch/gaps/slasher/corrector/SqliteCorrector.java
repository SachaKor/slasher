package ch.gaps.slasher.corrector;

import ch.gaps.slasher.database.driver.database.Database;
import ch.gaps.slasher.utils.CorrectorResult;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class SqliteCorrector implements Corrector {
    private Database database;
    private CorrectorResult result;
    SQLiteLexer lexer;
    SQLiteParser parser;
    public SqliteCorrector() {
        result = new CorrectorResult();
    }

    @Override
    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public CorrectorResult checkStatement(String stmt) {
        lexer = new SQLiteLexer(CharStreams.fromString(stmt));
        parser = new SQLiteParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.parse();
        SQLiteCorrectorVisitor visitor = new SQLiteCorrectorVisitor();
        visitor.setText(stmt);
        CorrectorResult result = visitor.visit(tree);
        return result;
    }
}
