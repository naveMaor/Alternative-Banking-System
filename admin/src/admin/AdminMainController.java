package admin;


import admin.adminClientTable.adminClientTableController;
import admin.adminLoanTables.TablesRefresher;
import admin.adminLoanTables.adminLoanTablesMain.adminLoanTablesController;
import admin.users.UsersListController;
import common.LoginController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import servletDTO.admin.AdminLoanObj;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AdminMainController {

    SimpleBooleanProperty increaseYaz = new SimpleBooleanProperty(false);
    private Timer timer;
    private TimerTask listRefresher;
    public final static int REFRESH_RATE = 2000;
    private final BooleanProperty autoUpdate = new SimpleBooleanProperty(true);

    @FXML
    private VBox usersList;
    @FXML
    private UsersListController usersListController;
    @FXML
    private AnchorPane LoginPage;
    @FXML
    private LoginController LoginPageController;
    @FXML
    private BorderPane rootBP;

    @FXML
    private Button CustomersInformationButtonId;

    @FXML
    private Button IncreaseYazButtonId;

    @FXML
    private Button LoadFileButtonId;


    @FXML
    private AnchorPane adminLoanTables;
    @FXML
    private adminLoanTablesController adminLoanTablesController;

    @FXML
    private AnchorPane adminClientTable;
    @FXML
    private adminClientTableController adminClientTableController;
    private StringProperty currentAdminName = new SimpleStringProperty();
    private StringProperty currentYaz = new SimpleStringProperty();
    private Node login;
    private Node body;


    @FXML
    void IncreaseYazButtonListener(ActionEvent event) {
//        engine.increaseYaz();
        initializeAdminTables();
        increaseYaz.setValue(true);
    }


    public void bindProperties(SimpleBooleanProperty isFileSelected, SimpleStringProperty selectedFileProperty, SimpleBooleanProperty isYazChanged) {
        //CustomersInformationButtonId.disableProperty().bind(isFileSelected.not());
        IncreaseYazButtonId.disableProperty().bind(isFileSelected.not());
        //LoadFileButtonId.disableProperty().bind(isFileSelected.not());
        isYazChanged.bindBidirectional(increaseYaz);
    }


    @FXML
    public void initialize() {
        login = rootBP.getTop();
        body = rootBP.getCenter();
        rootBP.setCenter(null);
        LoginPageController.setMainController(this);
        usersListController.setMainController(this);
        usersListController.startListRefresher();
    }


    public void initializeAdminTables() {
        //adminLoanTablesController.initializeLoansTable();
        adminClientTableController.initializeClientTable();
    }

    private void loadAdminTablesData(List<AdminLoanObj> adminLoanObj){
        adminLoanTablesController.initializeLoansTable(adminLoanObj);
    }

    public void rewindButtonListener(ActionEvent actionEvent) {
    }

    public void updateAdminName(String userName) {
        currentAdminName.set(userName);
    }

    public void switchToAdminDesktop() {
        synchronized (this) {
            rootBP.setTop(null);
            rootBP.setCenter(body);
            startLoanListRefresher();
            //initializeAdminTables();
            //        this.usersListController.startListRefresher();
        }
    }

    public void startLoanListRefresher() {
        listRefresher = new TablesRefresher(
                this::loadAdminTablesData,
                autoUpdate
                );
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }
}