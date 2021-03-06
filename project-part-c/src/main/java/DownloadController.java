import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DownloadController extends Controller{

    @FXML
    private Button downloadButton;

    @FXML
    private Button backButton;

    /**
     * Runs the download CSV function when the download button
     * is clicked
     */
    @FXML
    private void setDownloadButton() {
        dbController.exportData("newNodes.csv");
    }

    /**
     * Sets the current scene to the view nodes scene
     */
    @FXML
    private void setBackButton() {
        this.goToScene(this.VIEW_STRING);
    }
}
