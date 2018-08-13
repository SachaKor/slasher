/*
 * The MIT License Copyright 2017 HEIG-VD // SIPA. Permission is hereby granted,
 * free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of
 * the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package ch.gaps.slasher.database.driver;

import ch.gaps.slasher.corrector.Corrector;
import ch.gaps.slasher.corrector.SqliteCorrector;
import ch.gaps.slasher.database.driver.database.*;
import ch.gaps.slasher.highliter.Highlighter;
import ch.gaps.slasher.highliter.sqlite.SqliteHighlighter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the driver to connect to a SQLite database.
 *
 * @author Julien Leroy
 */
public class Sqlite implements Driver {

  // **************************************************************************
  // Attributes
  // **************************************************************************
  /** the {@link Connection} on the database. */
  private Connection connection;

  private Highlighter highlighter = null;

  // **************************************************************************
  // Object Overrides
  // **************************************************************************
  @Override public String toString() {
    return "Sqlite";
  }

  // **************************************************************************
  // Driver Overrides - Driver Parameters
  // **************************************************************************
  @Override public String id() {
    return Sqlite.class.getName().toLowerCase();
  }

  @Override public DataHandlingType type() {
    return DataHandlingType.FILE;
  }

  @Override public int getDefaultPort() {
    return 0;
  }

  @Override public LinkedList<Database> getDatabases(Server server, String username, String password) {
    return null;
  }

  // **************************************************************************
  // Driver Overrides - Connection Matters
  // **************************************************************************
  @Override public void connect(Server server, String username, String password, String database) throws
    ClassNotFoundException, SQLException {

    Class.forName("org.sqlite.JDBC");
    connection = DriverManager.getConnection("jdbc:sqlite:" + server.getHost());
    //connection.getMetaData().getSQLKeywords()
  }

  @Override public void close() {
  }

  // **************************************************************************
  // Driver Overrides - Schema Matters
  // **************************************************************************

  @Override public Boolean isConnected() {
    return true;
  }

  @Override public boolean hasSchema() {
    return false;
  }

  @Override public List<Schema> getSchemas(Database database) {
    return null;
  }

  @Override public List<Table> getTables(Schema schema) {
    LinkedList<Table> tables = new LinkedList<>();
    try {
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table'");

      while (rs.next()) {
        tables.add(new Table(rs.getString("name"), /*schema.getDatabase()*/ null));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tables;
  }

  @Override public List<View> getViews(Schema schema) {
    return null;
  }

  @Override public List<Trigger> getTriggers(Schema schema) {
    return null;
  }

  // **************************************************************************
  // Driver Overrides - Queries Matters
  // **************************************************************************

  @Override public ResultSet executeQuery(String query) throws SQLException {
    return connection.createStatement().executeQuery(query);
  }

  @Override public ResultSet getAllData(Database database, Schema schema, Table table) {
    try {
      return connection.createStatement().executeQuery("SELECT * FROM " + table);
    } catch (SQLException e) {
      Logger.getLogger(Sqlite.class.getName()).log(Level.SEVERE, e.getMessage());
    }
    return null;
  }

  @Override
  public Highlighter getHighlighter() {
    return highlighter;
  }

  @Override
  public List<String> getColumnNames(Table table) throws SQLException {
    List<String> columnNames = new LinkedList<>();
    Statement statement = connection.createStatement();
    String tableName = table.name();
    ResultSet rs = statement.executeQuery("SELECT * FROM " + table.name());
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();

    for (int i = 1; i <= columnCount; i++ ) {
      String name = rsmd.getColumnName(i);
      columnNames.add(name);
    }
    return columnNames;
  }

  @Override
  public Table getTable(String name) throws SQLException {
    // TODO: is there a better way to get one single table by its name?
    Statement statement = connection.createStatement();
    List<Table> tables = getTables(null);
    for (Table t : tables) {
      if (t.name().equalsIgnoreCase(name)) {
        return t;
      }
    }
    return null;
  }

  @Override
  public Corrector getCorrector() {
    return new SqliteCorrector();
  }

  public Sqlite() {
    try {
      highlighter = new SqliteHighlighter();
    } catch (IOException e) {
      Logger.getLogger(Sqlite.class.getName()).log(Level.SEVERE, e.getMessage());
    } catch (URISyntaxException e) {
      Logger.getLogger(Sqlite.class.getName()).log(Level.SEVERE, e.getMessage());
    }
  }

}
