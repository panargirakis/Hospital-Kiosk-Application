package edu.wpi.cs3733.d19.teamD;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The UIController for the viewing, editing, adding, and removing service requests
 * Allows the admin to manage service Requests
 * @author Jonathan Chang, imoralessirgo
 * @version API
 */
public class UIControllerATVSR extends UIController {
    private static final String[] serviceRequestSetters  = {"", "", "", "setResolved", "setResolverID", ""};
    private static final String[] serviceRequestGetters  = {"getNodeID", "getServiceType", "getUserID", "isResolved", "getResolverID", "getMessage"};
    @FXML
    private ImageView backgroundImage;
    /**< The Various servicerequests Columns used for cell factories */
    @FXML
    private MenuItem backButton; /**< The Back Button */

    @FXML
    private Menu homeButton; /**< The Home Button */

    @FXML
    private TableView<ServiceRequest> serviceRequestTable; /**< The table that holds all of the nodes */

    /**
     * Called when the scene is first created
     * Sets up all the cell factories
     */
    @FXML
    public void initialize() {
        backgroundImage.fitWidthProperty().bind(primaryStage.widthProperty());

        ObservableList<TableColumn<ServiceRequest, ?>> tableColumns = serviceRequestTable.getColumns();

        // Set up the uneditable columns
        for(int i = 0; i < 3; i++) {
            int indexOut = i;
            TableColumn<ServiceRequest, ServiceRequest> column = (TableColumn<ServiceRequest, ServiceRequest>) tableColumns.get(i);
            column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
            column.setCellFactory(param -> new TableCell<ServiceRequest, ServiceRequest>() {
                private Label label = new Label();
                private int index = indexOut;


                @Override
                protected void updateItem(ServiceRequest serviceRequest, boolean empty) {
                    super.updateItem(serviceRequest, empty);

                    runStringGetter(serviceRequest, serviceRequestGetters[index], label);
                    setGraphic(label);
                }
            });
        }
        // Set up the Resolved Column
        TableColumn<ServiceRequest, ServiceRequest> resolvedColumn = (TableColumn<ServiceRequest, ServiceRequest>) tableColumns.get(3);
        resolvedColumn.setStyle( "-fx-alignment: CENTER;");
        resolvedColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        resolvedColumn.setCellFactory(param -> new TableCell<ServiceRequest, ServiceRequest>() {
            private JFXCheckBox checkBox = new JFXCheckBox();
            private int index = 3;

            @Override
            protected void updateItem(ServiceRequest serviceRequest, boolean empty) {
                super.updateItem(serviceRequest, empty);
                if(serviceRequest == null) {
                    setGraphic(null);
                    return;
                }
                // Get the initial value of the checkbox
                try {
                    Method method = serviceRequest.getClass().getMethod(serviceRequestGetters[index]);
                    checkBox.setSelected((boolean) method.invoke(serviceRequest));
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                setGraphic(checkBox);
                checkBox.setOnAction(et -> {
                    runSetter(serviceRequest, serviceRequestSetters[index], boolean.class, checkBox.isSelected());
                    Connection conn = DBControllerAPI.dbConnect();
                    DBControllerAPI.updateServiceRequest(serviceRequest,conn);
                    DBControllerAPI.closeConnection(conn);
                });
            }

        });
        // Set up the ResolverID Column
        TableColumn<ServiceRequest, ServiceRequest> resolverIDColumn = (TableColumn<ServiceRequest, ServiceRequest>) tableColumns.get(4);
        resolverIDColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        resolverIDColumn.setCellFactory(param -> new EditableTextCell<ServiceRequest, ServiceRequest>(resolverIDColumn, 4) {
            @Override
            protected void updateItem(ServiceRequest serviceRequest, boolean empty) {
                super.updateItem(serviceRequest, empty);
                if(serviceRequest == null) {
                    setGraphic(null);
                    return;
                }
                runStringGetterEditable(serviceRequest, serviceRequestGetters[index], label, textField);

                textField.setOnAction(et -> {
                    // Check Length
                    if(textField.getText().length() > 10) {
                        setGraphic(label);
                        textField.setText(label.getText());
                        popupMessage("Field must have equal to or less than " +  10 + " characters.", true);
                        return;
                    }
                    runSetter(serviceRequest, serviceRequestSetters[index],String.class, textField.getText());
                    Connection conn = DBControllerAPI.dbConnect();
                    DBControllerAPI.updateServiceRequest(serviceRequest,conn);
                    DBControllerAPI.closeConnection(conn);
                    setGraphic(label);
                    label.setText(textField.getText());
                });
            }
        });

        // Setup the message column
        TableColumn<ServiceRequest, ServiceRequest> column = (TableColumn<ServiceRequest, ServiceRequest>) tableColumns.get(5);
        column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        column.setCellFactory(param -> new TableCell<ServiceRequest, ServiceRequest>() {
            private Label label = new Label("TEST");
            private int index = 5;

            @Override
            protected void updateItem(ServiceRequest serviceRequest, boolean empty) {
                super.updateItem(serviceRequest, empty);

                runStringGetter(serviceRequest, serviceRequestGetters[index], label);

                setGraphic(label);
            }
        });

        // Initialize cell factories of the remove rsv column
        TableColumn<ServiceRequest, ServiceRequest> removeColumn = (TableColumn<ServiceRequest, ServiceRequest>) tableColumns.get(tableColumns.size() - 1);
        removeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        removeColumn.setCellFactory(param -> new TableCell<ServiceRequest, ServiceRequest>() {
            private JFXButton removeButton = new JFXButton("Remove");

            @Override
            protected void updateItem(ServiceRequest serviceRequest, boolean empty) {
                super.updateItem(serviceRequest, empty);
                if(serviceRequest == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(removeButton);
                removeButton.setOnAction( e -> {
                            try {
                                Connection conn = DBControllerAPI.dbConnect();
                                DBControllerAPI.deleteServiceRequest(serviceRequest.getServiceID(), conn);
                                conn.close();
                            }catch(SQLException e1){
                                e1.printStackTrace();
                            }
                            getTableView().getItems().remove(serviceRequest);
                        }
                );
            }

        });
    }

    /**
     * Run when the scene is shown
     */
    @Override
    public void onShow() {
        Connection conn = DBControllerAPI.dbConnect();
        ObservableList<ServiceRequest> serviceRequests = FXCollections.observableArrayList();
        try{
            ResultSet rs = conn.createStatement().executeQuery("Select * from SERVICEREQUEST");
            while (rs.next()){
                System.out.println("outtttt");
                serviceRequests.add(new ServiceRequest(rs.getString(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getBoolean(6),rs.getString(7),rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < serviceRequests.size(); i ++) {
            System.out.println("hello");
            System.out.println(serviceRequests.get(i));
        }
        serviceRequestTable.setItems(serviceRequests);
    }

    /**
     * Goes back to the admin tools application menu
     */
    @FXML
    private void setBackButton() {
        this.goToScene(UIController.SERVICE_REQUEST_MAIN,"");
    }
}
