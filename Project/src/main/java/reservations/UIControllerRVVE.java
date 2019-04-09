package reservations;

import application.DBController;
import application.UIController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import entities.Reservation;
import entities.ServiceRequest;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * The UIController for the viewing and editing, a user's reservations
 * Allows the user to see and manage reservations
 * @author Ryan O'Brien
 * @version iteration2
 */
public class UIControllerRVVE extends UIController {
    private static final int[] lengthRequirements = {-1, 10, 10, 10, 8, 8};
    private static final String[] reservationSetters  = {"setNodeID", "setUserID", "setDate", "setStartTime", "setEndTime", ""};
    private static final String[] reservationGetters  = {"getNodeID", "getUserID", "getDate", "getStartTime", "getEndTime", "getRsvID"};
                                                    /**< The Various Reservation Columns used for cell factories */
    @FXML
    private MenuItem backButton; /**< The Back Button */

    @FXML
    private Menu homeButton; /**< The Home Button */

    @FXML
    private TableView<Reservation> reservationTable; /**< The table that holds all of the reservations */

    /**
     * Called when the scene is first created
     * Sets up all the cell factories
     */
    @FXML
    public void initialize() {
        List<TableColumn<Reservation, ?>> tableColumns = reservationTable.getColumns();

        // Initialize the cell factories of the reservation field columns
        for(int i = 0; i < tableColumns.size() - 1; i++) {
            int indexOut = i;
            TableColumn<Reservation, Reservation> column = (TableColumn<Reservation, Reservation>) tableColumns.get(i);
            column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
            column.setCellFactory(param -> new EditableTextCell<Reservation, Reservation
                    >(column, indexOut) {

                // When the Reservation is updated on the textfield
                @Override
                protected void updateItem(Reservation reservation, boolean empty) {
                    super.updateItem(reservation, empty);

                    // Get the starting text of the label and textField
                    runStringGetterEditable(reservation, reservationGetters[index], label, textField);


                    // When an edit is committed with enter
                    textField.setOnAction(et -> {
                        // Catch if int is not able to be parsed
                        if(index == 1 || index == 2) {
                            try {
                                if(Integer.parseInt(textField.getText()) < 0) {
                                    popupMessage("Coordinate fields must be positive", true);
                                }
                            } catch(Exception e) {
                                setGraphic(label);
                                textField.setText(label.getText());
                                return;
                            }
                        } else {
                            // Resets if the input is too long
                            if(textField.getText().length() > lengthRequirements[index]) {
                                setGraphic(label);
                                textField.setText(label.getText());
                                popupMessage("Field must have equal to or less than " +  lengthRequirements[index] + " characters.", true);
                                return;
                            }
                        }
                        // Update the object with the new value
                        if(index == 1 || index == 2) {
                            runSetter(reservation, reservationSetters[index], int.class, Integer.parseInt(textField.getText()));
                        } else {
                            runSetter(reservation, reservationSetters[index], String.class, textField.getText());
                        }
                        System.out.println(reservation);

                        try{
                            Connection conn = DBController.dbConnect();
                            if(index == 0) {
                                DBController.deleteNode(label.getText(),conn); //TODO
                                DBController.addReservation(reservation,conn);
                            } else {
                                DBController.updateReservation(reservation, conn);
                            }
                            conn.close();
                        }catch(SQLException e){
                            e.printStackTrace();
                        }
                        setGraphic(label);
                        label.setText(textField.getText());
                    });
                }
            });
        }
        // Initialize cell factories of the remove node column
        TableColumn<Reservation, Reservation> removeColumn = (TableColumn<Reservation, Reservation>) tableColumns.get(tableColumns.size() - 1);
        removeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        removeColumn.setCellFactory(param -> new TableCell<Reservation, Reservation>() {
            private JFXButton removeButton = new JFXButton("Remove");

            @Override
            protected void updateItem(Reservation reservation, boolean empty) {
                super.updateItem(reservation, empty);
                if(reservation == null) {
                    return;
                }
                setGraphic(removeButton);
                removeButton.setOnAction( e -> {
                            try {
                                Connection conn = DBController.dbConnect();
                                DBController.deleteReservation(reservation.getRsvID(), conn);
                                conn.close();
                            }catch(SQLException e1){
                                e1.printStackTrace();
                            }
                            getTableView().getItems().remove(reservation);
                        }
                );
            }

        });

    }

    /**
     * Run when the scene is shown
     * Gets the nodes from the database and puts them into the table
     */
    @Override
    public void onShow() {
        //DB get Nodes
        reservationTable.getItems().clear();
        Connection conn = DBController.dbConnect();
        ObservableList<Reservation> rsvData = FXCollections.observableArrayList();
        try{
            ResultSet rs = conn.createStatement().executeQuery("Select * from RESERVATIONS");
            while (rs.next()){
                rsvData.add(new Reservation(rs.getString(1),rs.getString(2),rs.getString(3),
                        rs.getString(4),rs.getString(5),rs.getInt(6)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        reservationTable.setItems(rsvData);
    }

    /**
     * Adds an empty to the table
     */
    @FXML
    private void setAddButton() {
        Reservation reservation = new Reservation("", "", "", "", "");
        reservationTable.getItems().add(reservation);
    }

    /**
     * Goes back to the admin tools application menu
     */
    @FXML
    private void setBackButton() {
        this.goToScene(UIController.ADMIN_TOOLS_MAIN);
    }
}
