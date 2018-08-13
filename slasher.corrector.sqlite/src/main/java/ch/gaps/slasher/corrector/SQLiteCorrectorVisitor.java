package ch.gaps.slasher.corrector;

import ch.gaps.slasher.database.driver.database.Database;
import ch.gaps.slasher.database.driver.database.Table;
import ch.gaps.slasher.utils.CorrectorResult;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SQLiteCorrectorVisitor extends SQLiteBaseVisitor<CorrectorResult> {
    String text;
    CorrectorResult result;
    Database database;
    SubstringFinder finder;

    public SQLiteCorrectorVisitor() {
        result = new CorrectorResult();
    }
    public void setText(String text) {
        this.text = text;
        finder = new SubstringFinder(text);
    }
    public void setDatabase(Database database) {
        this.database = database;
    }

    public CorrectorResult visitSelect_core(SQLiteParser.Select_coreContext ctx) throws SQLException {
        List<String> columns = new LinkedList<>();
        List<String> from = new LinkedList<>();
        if (ctx.K_SELECT() != null) {
            if (ctx.K_FROM() != null) {
                for (SQLiteParser.Table_or_subqueryContext node : ctx.table_or_subquery()) {
                    if (node.table_name() != null) {
                        from.add(node.table_name().getText());
                    }
                }
            } else {
                List<Integer[]> errorIndexes = finder.find(ctx.getText());
                int i = 0;
                for (Integer[] match : errorIndexes) {
                    result.addError("FROM key is missing", match[0], match[1]);
                }
            }
            for (SQLiteParser.Result_columnContext rcc : ctx.result_column()) {
                if (rcc.expr() != null) {
                    columns.add(rcc.expr().getText());
                }
            }
        }

        for (String c : columns) {
            boolean tableFound = false;
            for (String f : from) {
                Table table = database.getTable(f);
                if (table != null) {
                    tableFound = true;
                    break;
                }
            }
            if (!tableFound) {
                List<Integer[]> errorIndexes = finder.find(ctx.getText());
                for (Integer[] indexes : errorIndexes) {
                    result.addError("Column " + c + " is not found in tables",
                            indexes[0], indexes[1]);
                }
            }
        }
        return result;
    }

//    public CorrectorResult visitFactored_select_stmt(SQLiteParser.Factored_select_stmtContext ctx) {
//
//        return null;
//    }

}
