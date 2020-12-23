package com.bookiescrape.app.fx.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.CellStyle;

import com.bookiescrape.app.fx.FXMLReference;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


/**
 * The controller class for {@code RootLayout.fxml}.
 *
 * @author Jonathan Henly
 */
public class RootController {
    
    /* Package Private Constants */
    
    /** Constant representing the inactive state of a top button. */
    static int TOP_BTN_INACTIVE_STATE = 0;
    /** Constant representing the selected state of a top button. */
    static int TOP_BTN_SELECTED_STATE = 1;
    
    /* Private Constants */
    private static final String SETTINGS_BUTTON_ID = "settingsButton";
    private static final String LOG_BUTTON_ID = "historyButton";
    private static final String DASH_TITLE = "Dashboard";
    private static final String SETTINGS_TITLE = "Settings";
    private static final String LOG_TITLE = "Log";
    
    /* Private Members and FXML Members */
    
    @FXML
    private ResourceBundle resources;
    
    @FXML
    private HBox mainTopHBox;
    @FXML
    private BorderPane rootViewPane;
    @FXML
    private HBox bottomRightHBox;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private Button settingsButton;
    private boolean settingsActive = false;
    @FXML
    private Button logButton;
    private boolean logActive = false;
    
    @FXML
    private Button viewClose;
    @FXML
    private Label viewTitleLabel;
    
    // holds the currently selected top hbox button, if one is selected
    private Button activeTopButton;
    
    @FXML
    private Label scraperStatusLabel;
    @FXML
    private Circle scraperStatusCircle;
    
    private List<String> bookiesList = new ArrayList<>();
    private CellStyle style = null;
    
    private double stageXOffset;
    private double stageYOffset;
    
    // primary stage reference
    private Stage primaryStage;
    
    // root view reference
    private Parent rootView;
    
    /* root view's sub views and their controllers */
    private Parent dashView;
    private DashController dashController;
    private Parent settingsView;
    private SettingsController settingsController;
    private Parent logView;
    private LogController logController;
    
    // reference to the actively showing view
    private Parent activeView;
    
    @FXML
    private void initialize() {}
    
    /**
     * Sets root's reference to the primary stage.
     *
     * @param stage - the primary stage
     */
    public void setPrimaryStage(Stage stage) { primaryStage = stage; }
    
    /**
     * Sets a reference to this controller's view.
     * <p>
     * The reference allows root to maintain the stage's minimum width and
     * height.
     *
     * @param rootView - the view associated with this controller
     * @see FXMLLoader#load()
     */
    public void setRootView(Parent rootView) { this.rootView = rootView; }
    
    /**
     * Sets root's references to the dashboard view and dashboard controller.
     * <p>
     * The references allow root to show the dashboard view and handle events in
     * the dashboard related to root.
     *
     * @param dashReference - the loaded dashboard fxml reference
     * @see FXMLLoader#load()
     * @see FXMLLoader#getController()
     */
    public void setDashReference(FXMLReference dashReference) {
        dashView = dashReference.getView();
        dashController = dashReference.getController();
    }
    
    /**
     * Sets root's references to the settings view and settings controller.
     * <p>
     * The references allow root to show the settings view and handle events in
     * the settings view related to root.
     *
     * @param settingsReference - the loaded settings fxml reference
     * @see FXMLLoader#load()
     * @see FXMLLoader#getController()
     */
    public void setSettingsReference(FXMLReference settingsReference) {
        settingsView = settingsReference.getView();
        settingsController = settingsReference.getController();
    }
    
    /**
     * Sets root's references to the log view and log controller.
     * <p>
     * The references allow root to show the log view and handle events in the
     * log view related to root.
     *
     * @param logReference - the loaded log fxml reference
     * @see FXMLLoader#load()
     * @see FXMLLoader#getController()
     */
    public void setLogReference(FXMLReference logReference) {
        logView = logReference.getView();
        logController = logReference.getController();
    }
    
    /**
     * Used to record the start of dragging the main window across the screen.
     *
     * @param event - the mouse pressed event caused by mouse pressing main's
     *        top HBox
     */
    @FXML
    void onMainTopHBoxMousePressed(MouseEvent event) {
        stageXOffset = event.getSceneX();
        stageYOffset = event.getSceneY();
    }
    
    /**
     * Handles dragging the window across the screen after mouse pressing main's
     * top HBox.
     *
     * @param event - the drag event caused by dragging the main window across
     *        the screen
     */
    @FXML
    void onMainTopHBoxMouseDragged(MouseEvent event) {
        Stage stage = (Stage) ((HBox) event.getSource()).getScene().getWindow();
        
        stage.setX(event.getScreenX() - stageXOffset);
        stage.setY(event.getScreenY() - stageYOffset);
    }
    
    public void setCloseToTrayIcon(EventHandler<ActionEvent> handler) {
        
    }
    
    /**
     * Handles actions coming from the top most close button.
     *
     * @param event - the action event to handle
     */
    @FXML
    void onCloseButtonAction(ActionEvent event) {
        // TODO minimize application (preferably to tray) rather than exiting
        Platform.exit();
    }
    
    /**
     * Handles actions on the top right most minimize button.
     *
     * @param event - the action event to handle
     */
    @FXML
    void onMinimizeButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        stage.setIconified(!stage.isIconified());
    }
    
    /**
     * Handles actions on the top right most maximize button.
     *
     * @param event - the action event to handle
     */
    @FXML
    void onMaximizeButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        stage.setMaximized(!stage.isMaximized());
    }
    
    @FXML
    void onTopButtonAction(ActionEvent event) {
        Button topButton = (Button) event.getSource();
        
        // don't do anything if the button is already active
        if (activeTopButton == topButton) { return; }
        
        // set any active top button to inactive and set top button to selected
        setActiveTopButton(topButton);
        
        // call on action method for the active button
        switch (topButton.getId()) {
            case SETTINGS_BUTTON_ID:
                settingsButtonActivated();
                settingsActive = true;
                logActive = false;
                break;
            
            case LOG_BUTTON_ID:
                logButtonActivated();
                settingsActive = false;
                logActive = true;
                break;
            
            default:
                settingsActive = false;
                logActive = false;
        }
    }
    
    /* top buttons hover effect methods helper */
    private boolean topButtonIsActive(Button button) {
        return activeTopButton == button;
    }
    
    /* helper to set top buttons active */
    private void setActiveTopButton(Button button) {
        Button btn = Objects.requireNonNull(button);
        
        // if another button is active then set it to inactive
        if (activeTopButton != null) {
            changeTopButtonState(activeTopButton, TOP_BTN_INACTIVE_STATE);
        }
        
        // set the new active top hbox button and change its state to selected
        changeTopButtonState(btn, TOP_BTN_SELECTED_STATE);
        activeTopButton = btn;
    }
    
    /**
     * Changes the visible state of a specified top button.
     * <p>
     * The supported values for the parameter {@code state} follow:
     * <ul>
     * <li>{@linkplain #TOP_BTN_INACTIVE_STATE}</li>
     * <li>{@linkplain #TOP_BTN_SELECTED_STATE}</li>
     * </ul>
     * Passing a value other than those listed will produce undefined behavior.
     * <p>
     * This method assumes the passed in button has the following node
     * hierarchy:
     *
     * <pre>
     * &lt;Button&gt;
     *   &lt;Parent&gt;
     *     &lt;Node&gt; &lt;!-- Inactive State Node --&gt; &lt;/Node&gt;
     *     &lt;Node&gt; &lt;!-- Selected State Node --&gt; &lt;/Node&gt;
     *   &lt;/Parent&gt;
     * &lt;/Button&gt;
     * </pre>
     *
     * @param button - the button to change the state of, this argument cannot
     *        be {@code null}
     * @param state - the state to change to
     */
    void changeTopButtonState(Button button, int state) {
        Button btn = Objects.requireNonNull(button);
        
        // get the state nodes' parent container
        ObservableList<Node> states = ((Parent) btn.getChildrenUnmodifiable().get(0)).getChildrenUnmodifiable();
        
        Node inactive = states.get(TOP_BTN_INACTIVE_STATE);
        Node selected = states.get(TOP_BTN_SELECTED_STATE);
        
        inactive.setVisible(state == TOP_BTN_INACTIVE_STATE);
        selected.setVisible(state == TOP_BTN_SELECTED_STATE);
    }
    
    /* called when top settings button is activated, shows settings view */
    private void settingsButtonActivated() {
        viewClose.setVisible(true);
        
        viewTitleLabel.setText(SETTINGS_TITLE);
        
        // change view to settings view
        rootViewPane.setCenter(settingsView);
        activeView = settingsView;
        
        setPrimaryStageMinBounds();
    }
    
    /* called when top log button is activated, shows log view */
    private void logButtonActivated() {
        viewClose.setVisible(true);
        
        viewTitleLabel.setText(LOG_TITLE);
        
        // change view to log view
        rootViewPane.setCenter(logView);
        activeView = logView;
        
        setPrimaryStageMinBounds();
    }
    
    @FXML
    void onViewCloseAction(ActionEvent action) {
        // change view to dashboard
        showDashboard();
    }
    
    /**
     * Changes the view in root to the dashboard view.
     */
    public void showDashboard() {
        if (activeTopButton != null) {
            // make any active top button inactive
            changeTopButtonState(activeTopButton, TOP_BTN_INACTIVE_STATE);
            activeTopButton = null;
        }
        
        /* hide the close view button, change title and view to dashboard */
        viewClose.setVisible(false);
        viewTitleLabel.setText(DASH_TITLE);
        rootViewPane.setCenter(dashView);
        activeView = dashView;
        
        setPrimaryStageMinBounds();
    }
    
    
    private static final String MONEY_LINE = "https://classic.sportsbookreview.com/betting-odds/money-line/";
    
    /* enforces window to not become smaller than root's min bounds */
    private void setPrimaryStageMinBounds() {
        // get root and active view's bounds to calculate min width and height
        Bounds rootPrefBounds = getPrefBounds(rootView);
        Bounds activePrefBounds = getPrefBounds(activeView);
        
        double minWidth = (rootPrefBounds.getWidth() - activePrefBounds.getWidth() > 0.0) ? rootPrefBounds.getWidth()
            : activePrefBounds.getWidth();
        double minHeight = rootPrefBounds.getHeight() + activePrefBounds.getHeight();
        
        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);
    }
    
    private void printBounds(String name, Bounds toPrint) {
        System.out.printf("%s bounds -  width: %.1f  height: %.1f%n", name, toPrint.getWidth(), toPrint.getHeight());
        double deltaW = primaryStage.getWidth() - toPrint.getWidth();
        double deltaH = primaryStage.getHeight() - toPrint.getHeight();
        System.out.printf("%s deltas -  deltaW: %.1f  deltaH: %.1f%n", name, deltaW, deltaH);
    }
    
    /**
     * Method that calculates a specified node's preferred bounds.
     *
     * @param node - the node to calculate the preferred bounds of
     * @return a specified node's preferred bounds
     */
    public static Bounds getPrefBounds(Node node) {
        double prefWidth;
        double prefHeight;
        
        Orientation bias = node.getContentBias();
        if (bias == Orientation.HORIZONTAL) {
            prefWidth = node.prefWidth(-1);
            prefHeight = node.prefHeight(prefWidth);
        } else if (bias == Orientation.VERTICAL) {
            prefHeight = node.prefHeight(-1);
            prefWidth = node.prefWidth(prefHeight);
        } else {
            prefWidth = node.prefWidth(-1);
            prefHeight = node.prefHeight(-1);
        }
        
        return new BoundingBox(0, 0, prefWidth, prefHeight);
    }
    
}
