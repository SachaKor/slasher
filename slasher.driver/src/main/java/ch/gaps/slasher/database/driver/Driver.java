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
package ch.gaps.slasher.database.driver;

import ch.gaps.slasher.corrector.Corrector;
import ch.gaps.slasher.database.driver.database.*;
import ch.gaps.slasher.highliter.Highlighter;
import sun.tools.jconsole.Tab;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This is the base interface that defines the structure of any drivers.
 *
 * @author Julien Leroy, Jérôme Varani
 */
public interface Driver {

  /**
   * @return the human readable name of this driver.
   */
  String toString();

  // **************************************************************************
  // Object Overrides
  // **************************************************************************

  /**
   * @return the id of this driver. Can be the lower case name of the main class.
   */
  String id();

  // **************************************************************************
  // Driver Parameters
  // **************************************************************************

  /** @return the {@link DataHandlingType}. */
  DataHandlingType type();

  /** @return the default port used by the DBMS. */
  int getDefaultPort();

  /**
   * Use by the server to list the databases
   *
   * @param server the {@link Server}.
   * @param username the user name.
   * @param password the password.
   *
   * @return a list of {@Database} instances.
   *
   * @throws SQLException if the query to get databases fails.
   * @throws ClassNotFoundException the JDBC driver has not been founded in the classpath.
   */
  List<Database> getDatabases(Server server, String username, String password) throws SQLException,
                                                                                               ClassNotFoundException;

  // **************************************************************************
  // Connection Matters
  // **************************************************************************

  /**
   * Connects to the database that can be reached by the parameters given.
   *
   * @param server the {@link Server}.
   * @param username the user name.
   * @param password the password.
   * @param database the name of the database.
   *
   * @throws SQLException a problem occurs while opening the connection.
   * @throws ClassNotFoundException the JDBC driver has not been found in the classpath.
   */
  void connect(Server server, String username, String password, String database) throws SQLException,
                                                                                                 ClassNotFoundException;

  /**
   * Closes the database
   *
   * @throws SQLException if the connection was already closed.
   */
  void close() throws SQLException;

  /**
   * @return {@code true} if the connection to the database is opened, otherwise {@code false}.
   */
  Boolean isConnected();

  /**
   * @return {@code true} if the DBMS handle schemas, otherwise {@code false}.
   */
  boolean hasSchema();

  // **************************************************************************
  // Schema Matters
  // **************************************************************************

  /**
   * Retrieves the schemas handled by the {@link Database}.
   *
   * @param database the {@link Database} where to retrieve schemas.
   *
   * @return a list of {@link Schema} instances.
   *
   * @throws SQLException if an error occurs while retrieving the schemas.
   */
  List<Schema> getSchemas(Database database) throws SQLException;

  /**
   * Retrieves all tables in the {@link Schema}.
   *
   * @param schema the {@link Schema} where to retrieve tables.
   *
   * @return a list of {@link Table} instances.
   *
   * @throws SQLException if the retrieval of tables fails.
   */
  List<Table> getTables(Schema schema) throws SQLException;

  /**
   * Retrieves all views in the {@link Schema}.
   *
   * @param schema the {@link Schema} where to retrieve views.
   *
   * @return a list of {@link View} instances.
   *
   * @throws SQLException if the retrieval of views fails.
   */
  List<View> getViews(Schema schema) throws SQLException;

  /**
   * Retrieves all triggers in the {@link Schema}.
   *
   * @param schema the {@link Schema} where to retrieve triggers.
   *
   * @return a list of {@link Trigger} instances.
   *
   * @throws SQLException if the retrieval of triggers fails.
   */
  List<Trigger> getTriggers(Schema schema) throws SQLException;

  /**
   * Executes the query.
   *
   * @param query the SQL query.
   *
   * @return the {@link ResultSet} holding the result.
   *
   * @throws SQLException if the query fails.
   */
  ResultSet executeQuery(String query) throws SQLException;

  // **************************************************************************
  // Queries Matters
  // **************************************************************************

  /**
   * Retrieves all data from the table
   *
   * @param database the {@link Database}.
   * @param schema the {@link Schema}.
   * @param table the {@link Table}.
   *
   * @return a {@link ResultSet} containing all data of the table.
   *
   * @throws SQLException if something went wrong while retrieving data.
   */
  // TODO : why this method takes a database and a schema ??
  ResultSet getAllData(Database database, Schema schema, Table table) throws SQLException;

  /** The various way DMBSs handle data. */
  enum DataHandlingType {
    /** Data are handled remotely on a server. */
    SERVER, /** Data are handled in a local file. */
    FILE
  }

  /**
   * Getter
   * @return the {@link Highlighter} for the given driver
   */
  Highlighter getHighlighter();

  List<String> getColumnNames(Table table) throws SQLException;

  Table getTable(String name) throws SQLException;

  Corrector getCorrector();
}
