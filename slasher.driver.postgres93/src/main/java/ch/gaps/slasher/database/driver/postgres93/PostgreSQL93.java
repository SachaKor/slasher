/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.gaps.slasher.database.driver.postgres93;

import ch.gaps.slasher.corrector.Corrector;
import ch.gaps.slasher.database.driver.Driver;
import ch.gaps.slasher.database.driver.database.*;
import ch.gaps.slasher.highliter.Highlighter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author jvarani
 */
public class PostgreSQL93 implements Driver {
  
  // <editor-fold desc="Driver Overrides" defaultstate="collapsed">
  @Override
  public String id() { return PostgreSQL93.class.getName().toLowerCase(); }
  
  @Override
  public String toString() { return "PostgresQL 9.3.*"; }
  // </editor-fold>


  @Override
  public DataHandlingType type() {
    return null;
  }

  @Override
  public int getDefaultPort() {
    return 0;
  }

  @Override
  public List<Database> getDatabases(Server server, String username,
                                     String password) throws SQLException, ClassNotFoundException {
    return null;
  }

  @Override
  public void connect(Server server, String username, String password,
                      String database) throws SQLException, ClassNotFoundException {

  }

  @Override
  public void close() throws SQLException {

  }

  @Override
  public Boolean isConnected() {
    return null;
  }

  @Override
  public boolean hasSchema() {
    return false;
  }

  @Override
  public List<Schema> getSchemas(Database database) throws SQLException {
    return null;
  }

  @Override
  public List<Table> getTables(Schema schema) throws SQLException {
    return null;
  }

  @Override
  public List<View> getViews(Schema schema) throws SQLException {
    return null;
  }

  @Override
  public List<Trigger> getTriggers(Schema schema) throws SQLException {
    return null;
  }

  @Override
  public ResultSet executeQuery(String query) throws SQLException {
    return null;
  }

  @Override
  public ResultSet getAllData(Database database, Schema schema, Table table) throws SQLException {
    return null;
  }

  // TODO: implement the PostgreSQL94 highlighter and implement the method
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

}
