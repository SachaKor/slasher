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
package ch.gaps.slasher.database.driver.database;

import ch.gaps.slasher.corrector.Corrector;
import ch.gaps.slasher.database.driver.Driver;
import ch.gaps.slasher.highliter.Highlighter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Julien Leroy
 */
public class Database implements DBObject, DBParent {

  // **************************************************************************
  // Attributes
  // **************************************************************************
  /** The {@link Driver}. */
  private Driver driver;
  /** The {@link Server}. */
  private Server server;
  /** The name of the database. */
  private String name;
  /** The user name to connect to the database. */
  private String username;
  /** The description of the database. */
  private String description;
  /** The property representation of the state of the connection. */
  private BooleanProperty connectedProperty;

  // **************************************************************************
  // Constructors
  // **************************************************************************

  /**
   * Creates a new {@link Database} instance.
   *
   * @param driver the {@link Driver}.
   * @param name the name of the database.
   * @param server the {@link Server}.
   * @param username the user name.
   */
  public Database(Driver driver, String name, Server server, String username) {
    this(driver, name, null, server, username);
  }

  /**
   * Creates a new {@link Database} instance.
   *
   * @param driver the {@link Driver}.
   * @param name the name of the database.
   * @param description the description of the database.
   * @param server the {@link Server}.
   * @param username the user name.
   */
  public Database(Driver driver, String name, String description, Server server, String username) {
    this.driver = driver;
    this.name = name;
    this.server = server;
    this.username = username;
    if (description == null) {
      description = username + "@" + name;
    }
    this.description = description;
    connectedProperty = new SimpleBooleanProperty(false);
  }

  // **************************************************************************
  // DBObject Overrides
  // **************************************************************************
  public String toString() {
    return description;
  }

  // **************************************************************************
  // Connection Matters
  // **************************************************************************

  /**
   * Connects to the database.
   *
   * @param password the password.
   *
   * @throws SQLException if something went wrong while opening the connection.
   * @throws ClassNotFoundException if the JDBC driver has not been founded in the classpath.
   */
  public void connect(String password) throws SQLException, ClassNotFoundException {
    if (!connectedProperty.getValue()) {
      driver.connect(server, username, password, name);
      connectedProperty.setValue(true);
    }
  }

  /**
   * Closes the connection.
   *
   * @throws SQLException if the connection was already closed.
   */
  public void close() throws SQLException {
    if (connectedProperty.getValue()) {
      driver.close();
      connectedProperty.setValue(false);
    }
  }

  /**
   * @return {@code true} if the connection is openned, otherwise {@code false}.
   */
  public boolean isConnected() {
    return connectedProperty.getValue();
  }

  // **************************************************************************
  // Connection Matters
  // **************************************************************************

  /**
   * @return {@code true} if this database handle schemas, otherwise {@code false}.
   */
  public boolean hasSchemas() {
    return driver.hasSchema();
  }

  /**
   * @return a list of {@link Schema} instances.
   *
   * @throws SQLException if something went wrong while retrieving the schemas.
   */
  public List<Schema> getSchemas() throws SQLException {
    return driver.getSchemas(this);
  }

  /**
   * @return talbe(s) list of the database
   *
   * @throws SQLException
   */
  public List<Table> getTables() throws SQLException {
    return driver.getTables(null);
  }

  /**
   * If the database has a schema
   *
   * @param schema
   *
   * @return talbe(s) list of the database
   *
   * @throws SQLException
   */
  public List<Table> getTables(Schema schema) throws SQLException {
    return driver.getTables(schema);
  }

  /**
   * Returns the views contained in the schema given.
   *
   * @param schema The {@link Schema}.
   *
   * @return view(s) list
   */
  public List<View> getViews(Schema schema) throws SQLException {
    return driver.getViews(schema);
  }

  /**
   * @return database name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the username linked to this database
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param newDescritpion new description to set
   */
  public void setDescription(String newDescritpion) {
    description = newDescritpion;
  }

  /**
   * @return database description
   */
  public String getDescritpion() {
    return description;
  }

  /**
   * @return driver server type
   */
  public Driver.DataHandlingType type() {
    return driver.type();
  }

  /**
   * @return !connected
   */
  @Override
  public boolean disabled() {
    return !connectedProperty.getValue();
  }

  /**
   * @return enabled property
   */
  @Override
  public BooleanProperty enabledProperty() {
    return connectedProperty;
  }

  /**
   * To execute a query on the driver
   *
   * @param query
   *
   * @return query result set
   *
   * @throws SQLException
   */
  public ResultSet executeQuery(String query) throws SQLException {
    return driver.executeQuery(query);
  }

  /**
   * To get all the data of a table with a schema
   *
   * @param schema
   * @param table
   *
   * @return the result set with all the data
   *
   * @throws SQLException
   */
  public ResultSet getAllData(Schema schema, Table table) throws SQLException {
    return driver.getAllData(this, schema, table);
  }

  /**
   * Implementation of the DbParent method
   *
   * @param table
   *
   * @return result set with all the talbe data
   *
   * @throws SQLException
   */
  @Override public ResultSet getAllData(Table table) throws SQLException {
    return driver.getAllData(this, null, table);
  }

  public Highlighter getHighliter() {
    return driver.getHighlighter();
  }

  public List<String> getColumnNames(Table table) throws SQLException {
    return driver.getColumnNames(table);
  }

  public Table getTable(String name) throws SQLException {
    return driver.getTable(name);
  }

  public Corrector getCorrector() {
    Corrector corrector = driver.getCorrector();
    corrector.setDatabase(this);
    return corrector;
  }
}
