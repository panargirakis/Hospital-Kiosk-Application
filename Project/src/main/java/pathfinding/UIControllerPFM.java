package pathfinding;

import application.CurrentUser;
import application.UIController;
import application.UIControllerPUD;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXToggleButton;
import database.DBController;
import database.DBControllerNE;
import entities.Graph;
import entities.Node;
import helper.RoomCategoryFilterHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the path_find_main.fxml file
 *
 * @author panagiotisargyrakis, dimitriberardi, ryano647
 */

public class UIControllerPFM extends UIController {

    @FXML
    private AnchorPane topAnchorPane;
    @FXML private Path pathLL2, pathLL1, pathG, path1, path2, path3;
    @FXML private JFXTabPane mapTabPane;
    @FXML private Menu homeMenu;

    @FXML public JFXComboBox<String> initialLocationCombo;
    @FXML public JFXComboBox<String> destinationCombo;
    private RoomCategoryFilterHelper initialFilterHelper;
    private RoomCategoryFilterHelper destinationFilterHelper;

    @FXML
    private ImageView backgroundImage;


    @FXML private GesturePane lowerLevel2GesturePane;
    @FXML private GesturePane lowerLevel1GesturePane;
    @FXML private GesturePane groundFloorGesturePane;
    @FXML private GesturePane firstFloorGesturePane;
    @FXML private GesturePane secondFloorGesturePane;
    @FXML private GesturePane thirdFloorGesturePane;

    @FXML private AnchorPane lowerLevel2AnchorPane;
    @FXML private AnchorPane lowerLevel1AnchorPane;
    @FXML private AnchorPane groundFloorAnchorPane;
    @FXML private AnchorPane firstFloorAnchorPane;
    @FXML private AnchorPane secondFloorAnchorPane;
    @FXML private AnchorPane thirdFloorAnchorPane;

    @FXML private JFXButton reservationButton;
    @FXML private  JFXButton resolveRequestButton;

    @FXML
    TitledPane titledPane;

    // The multiplication factor at which the map changes size
    @FXML
    private JFXButton directionsRequest;
    @FXML JFXToggleButton noStairsButton;

    private PathHandler pathHandler;

    private AnchorPaneHandler anchorPaneHandler;
    private CurrentObjects currentObjects;
    private GesturePaneHandler gesturePaneHandler;

    /**
     * Initialize various componets, especially panes, tabs and pathHandler
     */
    @FXML
    public void initialize() {
       // titledPane.prefHeightProperty().bind(primaryStage.heightProperty());
        backgroundImage.fitWidthProperty().bind(primaryStage.widthProperty());

        // ensures new tab has same x,y on the map and path animation changes between floors
        mapTabPane.getSelectionModel().selectedItemProperty().addListener(
                (ov, t, t1) -> {
                    currentObjects.clearAnimation();
                    GesturePane oldPane = currentObjects.getCurrentGesturePane();
                    currentObjects.setFloorIndex(Floors.getByName(t1.getText()).getIndex());
                    GesturePane pane = currentObjects.getCurrentGesturePane();
                    pane.centreOn(oldPane.targetPointAtViewportCentre());
                    gesturePaneHandler.changeTabs(pane, oldPane);
                    if (pathHandler.isActive()) {
                        gesturePaneHandler.newAnimation(currentObjects);
                    }
                    currentObjects.clearContextMenu();
                }
        );

        pathHandler = new PathHandler(pathLL2, pathLL1, pathG, path1, path2, path3, primaryStage);

        gesturePaneHandler = new GesturePaneHandler(lowerLevel2GesturePane, lowerLevel1GesturePane,
                groundFloorGesturePane, firstFloorGesturePane, secondFloorGesturePane, thirdFloorGesturePane);

        anchorPaneHandler = new AnchorPaneHandler(lowerLevel2AnchorPane, lowerLevel1AnchorPane,
                groundFloorAnchorPane, firstFloorAnchorPane, secondFloorAnchorPane, thirdFloorAnchorPane,
                topAnchorPane, this);

        currentObjects = new CurrentObjects(0, null, null, null, null,
                pathHandler, anchorPaneHandler, gesturePaneHandler);

        anchorPaneHandler.setCurrentObjects(currentObjects);
        gesturePaneHandler.setCurrentObjects(currentObjects);

        directionsRequest.setDisable(true);

    }

    /**
     * Initialize choice boxes and setup circles as node indicators
     */
    @Override
    public void onShow() {

        // ~~~~~ init choice boxes
        Connection conn = DBController.dbConnect();

        LinkedList<LinkedList<Node>> roomsAtEachFloor = new LinkedList<>();

        assert conn != null;
        roomsAtEachFloor.add(DBControllerNE.generateListOfNodes(conn, DBControllerNE.ALL_ROOMS_FLOOR_L2));
        roomsAtEachFloor.add(DBControllerNE.generateListOfNodes(conn, DBControllerNE.ALL_ROOMS_FLOOR_L1));
        roomsAtEachFloor.add(DBControllerNE.generateListOfNodes(conn, DBControllerNE.ALL_ROOMS_FLOOR_G));
        roomsAtEachFloor.add(DBControllerNE.generateListOfNodes(conn, DBControllerNE.ALL_ROOMS_FLOOR_1));
        roomsAtEachFloor.add(DBControllerNE.generateListOfNodes(conn, DBControllerNE.ALL_ROOMS_FLOOR_2));
        roomsAtEachFloor.add(DBControllerNE.generateListOfNodes(conn, DBControllerNE.ALL_ROOMS_FLOOR_3));

        DBController.closeConnection(conn);

        initialFilterHelper = new RoomCategoryFilterHelper(initialLocationCombo, param -> {
            if (initialFilterHelper.getLongName() == null)
                return;

            currentObjects.setInitialID(initialFilterHelper.getNodeID());

            currentObjects.setInitCircle(initialFilterHelper.getLongName());

            getPath();
        }, true);
        destinationFilterHelper = new RoomCategoryFilterHelper(destinationCombo, param -> {
            if (destinationFilterHelper.getLongName() == null)
                return;

            currentObjects.setDestID(destinationFilterHelper.getNodeID());

            currentObjects.setDestCircle(destinationFilterHelper.getLongName());

            getPath();
        }, true);

        anchorPaneHandler.initCircles(roomsAtEachFloor, initialLocationCombo, destinationCombo);


    }

    void setInitialLocation(String longName) {
        initialLocationCombo.getSelectionModel().select(longName);
        currentObjects.clearContextMenu();
    }

    void setDestinationLocation(String longName) {
        destinationCombo.getSelectionModel().select(longName);
        currentObjects.clearContextMenu();
    }

    /**
     *
     */
    @FXML
    private void setTitledPane(){
        if (titledPane.isExpanded() == false){
            final Color color = Color.TRANSPARENT;
            titledPane.setBackground(new Background(new BackgroundFill(color, null, null)));
        }else{
            final Color color2 = Color.web("#ffc41e");
            titledPane.setBackground(new Background(new BackgroundFill(color2, null, null)));
        }

    }
    /**
     * Allows for a default starting location
     * @param longName Name of starting node
     */
    private void setUpDefaultStartingLocation(String longName){
//        initialLocationSelect.setValue(longName);
    }


    /**
     * Callback for cancel. Clears path, animation, node selection and drop down menus
     */
    @FXML
    private void cancel(ActionEvent actionEvent) {
        pathHandler.cancel();
        clearTabColors();

        currentObjects.clearContextMenu();
        currentObjects.cancel();
        currentObjects.clearLabels();

        directionsRequest.setDisable(true);

        initialLocationCombo.getSelectionModel().clearSelection();
        destinationCombo.getSelectionModel().clearSelection();
        setInitialLocation(CurrentUser.startingLocation);
    }

    /**
     * Handles the generation, display and animation of a new path
     */
    private void getPath() {

        if(currentObjects.anyNullEndNodes())
            return;

        List<String> pathIDs;
        pathIDs = Graph.getGraph().shortestPath(currentObjects.getInitialID(), currentObjects.getDestID());

        Connection connection = DBController.dbConnect();
        assert connection != null;
        Node initialNode = DBControllerNE.fetchNode(currentObjects.getInitialID(), connection);
        Node destNode = DBControllerNE.fetchNode(currentObjects.getDestID(), connection);
        DBController.closeConnection(connection);

        currentObjects.clearAnimation(); // reset stuff
        pathHandler.cancel(); // reset stuff

        if(pathIDs == null) {
            clearTabColors();
            popupMessage("There is no path between these two nodes.", true);
        }
        else {
            // change tab based on initial node -- order here is important! Do not move below.
            mapTabPane.getSelectionModel().select(Floors.getByID(initialNode.getFloor()).getIndex());

            // update paths -- order here is important! Do not move above change tab.
            pathHandler.displayNewPath(Graph.getGraph().separatePathByFloor(pathIDs), initialNode);

            gesturePaneHandler.centerOnInitialNode(pathHandler, currentObjects.getCurrentGesturePane());

            List<Integer> floorsUsed = pathHandler.getFloorsUsed();
            clearTabColors();
            for (Integer floor : floorsUsed) {
                mapTabPane.getTabs().get(floor).setStyle("-fx-background-color: #efffff");
            }
        }

        currentObjects.clearLabels();

        currentObjects.newInitLabel(initialNode);
        currentObjects.newDestLabel(destNode);

        gesturePaneHandler.newAnimation(currentObjects);

        directionsRequest.setDisable(false);

    }

    /**
     * Clear marking of tab headers
     */
    private void clearTabColors() {
        for (Tab tab : mapTabPane.getTabs()) {
            tab.setStyle("-fx-background-color: #015080");
        }
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        goToScene(UIController.LOGIN_MAIN);
    }

    /**
     * Allows the map to increase in size, up to scroll_AnchorPane.getMaxWidth
     * @param actionEvent Triggered when zoom_button is pressed
     */
    @FXML
    public void zoom(ActionEvent actionEvent) {
        gesturePaneHandler.zoom(currentObjects.getCurrentGesturePane());
    }

    /**
     * Allows the map to decrease in size, down to scroll_AnchorPane.getMinWidth
     *
     * @param actionEvent Triggered when zoom_button is pressed
     */
    @FXML
    public void unZoom(ActionEvent actionEvent) {
        gesturePaneHandler.un_zoom(currentObjects.getCurrentGesturePane());
    }

    @FXML
    private void directionSelection() {
        String direction = Graph.getGraph().textDirections(Graph.getGraph().shortestPath(currentObjects.getInitialID(),
                currentObjects.getDestID()));
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/directions_popup.fxml"));

            Scene popupScene = new Scene(fxmlLoader.load(), 600, 400);
            Stage popupStage = new Stage();

            popupStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/directions.png")));

            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(primaryStage);

            UIControllerPUD controller = fxmlLoader.getController();
            controller.setDirections(direction);

            popupStage.setTitle("Directions");
            popupStage.setScene(popupScene);
            popupStage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger((getClass().getName()));
            logger.log(Level.SEVERE, "Failed to create new window.", e);

        }
    }

    @FXML
    private void setLoginButton() {
        goToScene(UIController.LOGIN_MAIN);
    }

    @FXML
    private void setServiceRequestButton() {
        goToScene(UIController.SERVICE_REQUEST_MAIN);
    }

    @FXML
    private void setAboutButton() {
        goToScene(UIController.ABOUT_PAGE);
    }

    @FXML
    private void noStairsToggled() {
        Graph.noStairsIsOn = noStairsButton.isSelected();
        getPath();
    }

    @FXML
    private void setReservationButton(){

    }
    @FXML
    private void setResolveRequestButton(){

    }


}



