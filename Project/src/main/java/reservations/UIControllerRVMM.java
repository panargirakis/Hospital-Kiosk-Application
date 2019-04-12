package reservations;

import application.UIController;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * The UIController for the Reservations Main Menu
 * Allows the user to select the various admin tools as well as go back to the Admin
 * Main Menu
 * @author Ryan O'Brien
 * @version iteration2
 */
public class UIControllerRVMM extends UIController {

    @FXML
    private Menu homeButton; /**< The Home Button*/

    @FXML
    private MenuItem backMenuItem; /**< The Home Button*/

    @FXML
    private JFXButton makeReservation; /**< The Make Reservation Button*/

    @FXML
    private JFXButton viewReservations; /**< The View/Edit Reservations Button*/



    public UIControllerRVMM() {

    }

    /**
     * Called when the scene is first created
     */
    @FXML
    public void initialize() {

    }


    /**
     * Goes to the View Node Scene
     */
    @FXML
    private void setMakeReservation() {
        this.goToScene(UIController.RESERVATIONS_MAIN);
    }

    /**
     * Goes to the View Edges Scene
     */
    @FXML
    private void setViewReservations() {
        this.goToScene(UIController.RESERVATIONS_EDIT);
    }


    /**
     * Goes back t0 the admin application Menu
     */
    @FXML
    private void setBackMenuItem() {
        this.goToScene(UIController.USER_MAIN_MENU_MAIN);
    }
}