/*
 * The MIT License Copyright 2015 Leroy. Permission is hereby granted, free of
 * charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package ch.gaps.slasher.database.driver.mysql;

import ch.gaps.slasher.corrector.Corrector;
import ch.gaps.slasher.database.driver.Driver;
import ch.gaps.slasher.database.driver.database.*;
import ch.gaps.slasher.highliter.Highlighter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author j.leroy
 */
public class MySql implements Driver {

  private Connection connection;
  private Boolean connected = false;

  @Override
  public String id() {
    return MySql.class.getName().toLowerCase();
  }

  @Override
  public String toString() {
    return "MySql";
  }

  @Override
  public DataHandlingType type() {
    return DataHandlingType.SERVER;
  }

  @Override
  public void connect(Server server, String username, String password,
                      String database) throws SQLException, ClassNotFoundException {
    Class.forName("com.mysql.jdbc.Driver");

    Properties info = new Properties();
    info.setProperty("user", username);
    info.setProperty("password", password);
    info.setProperty("useSSL", "true");

    if (database == null) {
      database = "";
    }

    String url = "jdbc:mysql://" + server.getHost() + ":" + server.getPort()
        + "/" + database;
    connection = DriverManager.getConnection(url, info);

    connected = true;
  }

  @Override
  public List<Table> getTables(Schema schema)
      throws SQLException {
    LinkedList<Table> tables = new LinkedList<>();

    Statement statement = connection.createStatement();
    ResultSet rs = statement.executeQuery(
        "SELECT DISTINCT TABLE_NAME " + "FROM INFORMATION_SCHEMA.COLUMNS "
            + "WHERE TABLE_SCHEMA='" + schema.getDatabase().getName() + "'");

    while (rs.next()) {
      System.out.println(rs.getString(1));
      tables.add(new Table(rs.getString(1), schema.getDatabase()));
    }

    return tables;
  }

  @Override
  public List<Schema> getSchemas(Database database) throws SQLException {
    LinkedList<Schema> schemas = new LinkedList<>();

    ResultSet rs = connection.getMetaData().getCatalogs();
    while (rs.next()) {
      schemas.add(new Schema(rs.getString("TABLE_CAT"), database));
    }

    return schemas;
  }

  @Override
  public void close() throws SQLException {
    if (!connected) {
      connection.close();
      connected = false;
    }
  }

  @Override
  public LinkedList<Database> getDatabases(Server server, String username,
      String password) throws ClassNotFoundException, SQLException {
    LinkedList<Database> databases = new LinkedList<>();

    Class.forName("com.mysql.jdbc.Driver");

    Properties info = new Properties();
    info.setProperty("user", username);
    info.setProperty("password", password);
    info.setProperty("useSSL", "true");

    String url = "jdbc:mysql://" + server.getHost() + ":" + server.getPort();
    connection = DriverManager.getConnection(url, info);
    ResultSet rs = connection.getMetaData().getCatalogs();
    while (rs.next()) {
      databases.add(new Database(new MySql(), rs.getString("TABLE_CAT"), null,
          server, username));

    }

    return databases;
  }

  @Override
  public ResultSet executeQuery(String query) throws SQLException {
    if (connected) {
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      return rs;
    } else {
      return null;
    }
  }

  @Override
  public Boolean isConnected() {
    return connected;
  }

  @Override
  public List<View> getViews(Schema schema) {
    return null;
  }

  @Override
  public List<Trigger> getTriggers(Schema schema) {
    return null;
  }

  @Override
  public ResultSet getAllData(Database database, Schema schema, Table table)
      throws SQLException {
    return connection.createStatement().executeQuery("SELECT * FROM " + table);
  }

  // TODO: implement the MySql highlighter and this method
  @Override
  public Highlighter getHighlighter() {
    throw new UnsupportedOperationException();
  }

  // TODO: implement
  @Override
  public List<String> getColumnNames(Table table) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Table getTable(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Corrector getCorrector() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getDefaultPort() {
    return 3306;
  }

  @Override
  public boolean hasSchema() {
    return false;
  }

}
