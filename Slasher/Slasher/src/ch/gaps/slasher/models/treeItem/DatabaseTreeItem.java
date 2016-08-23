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
package ch.gaps.slasher.models.treeItem;

import ch.gaps.slasher.database.driver.Driver;
import ch.gaps.slasher.database.driver.database.*;
import ch.gaps.slasher.views.main.MainController;
import ch.gaps.slasher.views.structure.StructureTabController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Optional;

public class DatabaseTreeItem extends DbObjectTreeItem {

    private MenuItem editor = new MenuItem("New Editor");
    private MenuItem connect = new MenuItem("Connect");
    private MenuItem disconnect = new MenuItem("Disconnect");
    private MenuItem remove= new MenuItem("Remove");

    public DatabaseTreeItem(Database db) {
        super(db);
        connect.disableProperty().bind(db.disabledProperty());
        disconnect.disableProperty().bind(db.disabledProperty().not());

        refreshTree();
    }


    public Pane getStructureTab(){
        AnchorPane pane = null;

        FXMLLoader loader = new FXMLLoader(StructureTabController.class.getResource("StructureTabView.fxml"));
        try
        {
            pane = new AnchorPane();
            pane = loader.load();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return pane;
    }

    /**
     * Refresh the tree
     */
    private void refreshTree(){
        Database db = (Database)getValue();

        if (db.isConnected())
        {
            if (db.hasSchemas())
            {
                LinkedList<Schema> schemas = db.getSchemas();

                for (Schema schema : schemas)
                {
                    DbObjectTreeItem schemaItem = new SchemaTreeItem(schema, this);
                    LinkedList<Table> tables = schema.getTables();

                    for (Table table : tables)
                    {
                        schemaItem.getChildren().add(new TableTreeItem(table, this));
                    }

//                    View[] views = schema.getViews();
//
//                    for (View view: views){
//                        schemaItem.getChildren().add(new ViewTreeItem(view, this));
//                    }
//
//                    Trigger[] triggers = schema.getTriggers();
//
//                    for (Trigger trigger: triggers){
//                        schemaItem.getChildren().add(new TriggerTreeItem(trigger, this));
//                    }

                    getChildren().add(schemaItem);
                }
            } else
            {
                LinkedList<Table> tables = db.getTables();
                for (Table table : tables)
                {
                    this.getChildren().add(new TableTreeItem(table, this));
                }
            }
        }

        //menu
        if (((Database) getValue()).type() == Driver.ServerType.Server) {
            buildContextMenu();
        }
    }

    /**
     * Built the ContextMenu
     */
    @Override
    public void buildContextMenu(){

        editor.setOnAction(event -> {

            MainController.getInstance().newEditorTab();

        });

        contextMenu.getItems().add(editor);

        connect.setOnAction(event ->
        {
            // Create the custom dialog.
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Database Login");

            // Set the button types.
            ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            // Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            PasswordField password = new PasswordField();
            password.setPromptText("Password");

            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);

            // Enable/Disable login button depending on whether a username was entered.
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);


            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return password.getText();
                }
                return null;
            });

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(passwordText -> {
                try
                {
                    ((Database) getValue()).connect(passwordText);
                } catch (SQLException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                refreshTree();

            });

            MainController.getInstance().refreshTreeView();
        });
        contextMenu.getItems().add(connect);

        // Disconnect option
        disconnect.setOnAction(event ->
        {
            disconnect();
        });
        contextMenu.getItems().add(disconnect);

        // Remove option to remove the database forme the database list
        remove.setOnAction(event ->
        {
            ((Server) getParent().getValue()).removeDatabase((Database) getValue());
            getParent().getChildren().remove(this);
            disconnect();
        });
        contextMenu.getItems().add(remove);
    }

    /**
     * Disconnect the database
     */
    public void disconnect() {
        ((Database) getValue()).close();
        getChildren().clear();
    }


    @Override
    public ContextMenu getContextMenu(){
        return contextMenu;
    }

    @Override
    public TreeItemType getType()
    {
        return TreeItemType.DATABASE;
    }

}
