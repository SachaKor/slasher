/*
 * The MIT License
 *
 * Copyright 2016 leroy.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ch.gaps.slasher.views.connectServer;

import ch.gaps.slasher.database.driver.Driver;
import ch.gaps.slasher.database.driver.database.Database;
import ch.gaps.slasher.database.driver.database.Server;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;


public class ServerServerController implements ServerController {
    @FXML private TextField host;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private AnchorPane mainPane;
    @FXML private ListView<Item> listView;
    @FXML private Button connect;

    private GridPane serverInfos;

    private BooleanProperty hostOk = new SimpleBooleanProperty(false);
    private BooleanProperty usernameOk = new SimpleBooleanProperty(false);
    private BooleanProperty passwordOk = new SimpleBooleanProperty(false);

    private String [] connectionData = new String[3];
    private BooleanProperty filedOk = new SimpleBooleanProperty(false);
    private BooleanProperty allOk = new SimpleBooleanProperty(false);

    private Driver driver;
    private Server server;

    IntegerProperty dbCount = new SimpleIntegerProperty(0);


    @FXML
    public void initialize(){
        filedOk.bind(hostOk.and(usernameOk).and(passwordOk));
        allOk.bind(dbCount.isNotEqualTo(0));

        host.textProperty().addListener((observable, oldValue, newValue) -> {
            connectionData[0] = newValue;
            if (newValue == null || newValue.isEmpty())
                hostOk.set(false);
            else
                hostOk.set(true);
        });

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            connectionData[1] = newValue;
            if (newValue == null || newValue.isEmpty())
                usernameOk.set(false);
            else
                usernameOk.set(true);
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            connectionData[2] = newValue;
            if (newValue == null || newValue.isEmpty())
                passwordOk.set(false);
            else
                passwordOk.set(true);
        });


        //serverInfos = (GridPane)mainPane.getChildren().get(0);

        listView.setCellFactory(CheckBoxListCell.forListView((Callback<Item, ObservableValue<Boolean>>) param -> {
            return param.onProperty();
        }));

        connect.disableProperty().bind(filedOk.not());

    }

    /**
     * Fonction appele par le GUI pour afficher la liste des bases de donnees disponibles
     * sur le server avec le nom d'utilisateur saisi
     */
    @FXML
    public void loadDatabases(){
        server = new Server(driver, host.getText());
        Database[] databases = server.getDatabases(username.getText(), password.getText());

        for (Database database : databases) {
            Item item = new Item(database);
            item.onProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue){
                    server.addDatabase(item.database);
                    dbCount.set(dbCount.get() + 1);
                }
                else {
                    server.removeDatabase(item.database);
                    dbCount.set(dbCount.get() - 1);
                }

            });
            listView.getItems().add(item);
        }
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public BooleanProperty getFieldValidation() {
        return allOk;
    }

    @Override
    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    class Item{
        private Database database;
        private BooleanProperty selected;

        Item(Database database){
            this.selected = new SimpleBooleanProperty(false);
            this.database = database;
        }

        public BooleanProperty onProperty(){
            return selected;
        }

        public String toString (){
            return database.toString();
        }
    }


}
