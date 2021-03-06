package admintools;

import application.UIController;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * The UIController for the Admin Tools Main Menu
 * Allows the user to select the various admin tools as well as go back to the Admin
 * Main Menu
 * @author Jonathan Chang, Shiyi Liu
 * @version iteration1
 */
public class UIControllerATM extends UIController {

    @FXML
    private ImageView backgroundImage;

    @FXML
    private MenuItem backMenuItem; /**< The Home Button*/

    @FXML
    private JFXButton viewNodesButton; /**< The View Node Button*/

    @FXML
    private JFXButton viewEdgesButton; /**< The View Edges Button*/

    @FXML
    private JFXButton viewUsersButton; /**< The View Edges Button*/

    @FXML
    private JFXButton ReservationsButton; /**< The View Edges Button*/

    @FXML
    private JFXButton viewServiceRequestsButton; /**< The View Service Request Button */

    @FXML
    private JFXButton findPathButton;

    @FXML
    private JFXButton applicationSettingButton;

    public UIControllerATM() {

    }

    /**
     * Called when the scene is first created
     */
    @FXML
    public void initialize() {
        backgroundImage.fitWidthProperty().bind(primaryStage.widthProperty());
    }


    /**
     * Goes to the View Node Scene
     */
    @FXML
    private void setViewNodesButton() {
        this.goToScene(UIController.ADMIN_TOOLS_VIEW_NODES);
    }

    /**
     * Goes to the View Edges Scene
     */
    @FXML
    private void setViewEdgesButton() {
        this.goToScene(UIController.ADMIN_TOOLS_VIEW_EDGES);
    }

    /**
     * Goes to the
     */
    @FXML
    private void setViewServiceRequestsButton() {
        this.goToScene(UIController.ADMIN_TOOLS_VIEW_SERVICE_REQUESTS);
    }

    /**
     * Goes to the view users page
     */
    @FXML
    private void setUsersButton() {
        this.goToScene(UIController.ADMIN_TOOLS_VIEW_USERS);
    }

    @FXML
    private void setAlgorithmButton() {
        this.goToScene(UIController.ADMIN_TOOLS_CHANGE_ALGORITHM);
    }

    @FXML
    private void setReservationsButton() { this.goToScene(UIController.ADMIN_RESERVATION_MAIN);}

    @FXML
    private void setViewMapButton() {
        this.goToScene(UIController.ADMIN_TOOLS_MAP_VIEW);
    }

    @FXML
    private void setFindPathButton() {
        this.goToScene(UIController.PATHFINDING_MAIN);
    }

    @FXML
    private void setApplicationSettingButton() {
        this.goToScene(UIController.ADMIN_TOOLS_APPLICATION_SETTING);
    }


    /**
     * Goes back t0 the admin application Menu
     */
    @FXML
    private void setBackMenuItem() {
        this.goToScene(UIController.LOGIN_MAIN);
    }
}
