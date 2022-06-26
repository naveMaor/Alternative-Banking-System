package client.sub.Information;

import com.google.gson.Gson;
import client.sub.Information.transactionsTableView.transactionsController;
import client.sub.main.CustomerMainBodyController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import loan.enums.eLoanStatus;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servletDTO.ClientDTOforServlet;
import servletDTO.LoanInformationObj;
import util.AddJavaFXCell;
import util.Constants;
import util.HttpClientUtil;

import java.io.IOException;

public class CustomerInformationBodyCont {

    private CustomerMainBodyController customerMainBodyController;

    @FXML
    private BorderPane transactions;
    @FXML
    private transactionsController transactionsController;

    @FXML
    private TableColumn<LoanInformationObj, String> borrowerLoanID;

    @FXML
    private TableColumn<LoanInformationObj, String> lenderLoanID;

    @FXML
    private TableColumn<LoanInformationObj, String> borrowerLoanCategory;

    @FXML
    private TableColumn<LoanInformationObj, String> lenderLoanCat;

    @FXML
    private TableColumn<LoanInformationObj, eLoanStatus> borrowerLoanStatus;

    @FXML
    private TableColumn<LoanInformationObj, eLoanStatus> lenderLoanStatus;

    @FXML
    private TableColumn<LoanInformationObj, String> lenderBorrowerName;

    @FXML
    private TableColumn<LoanInformationObj, Double> remainFund;

    @FXML
    private TableView<LoanInformationObj> lenderTable;

    @FXML
    private TableView<LoanInformationObj> borrowerTable;

    private ObservableList<LoanInformationObj> clientAsLenderLoanList = FXCollections.observableArrayList();
    private ObservableList<LoanInformationObj> clientAsBorrowLoanList = FXCollections.observableArrayList();

    private final Button btnSell = new Button("Action");

    public void initializeClientTable(){
        createLoansAsLenderRequest();
        createLoansAsBorrowerRequest();
    }

    public void setMainController(CustomerMainBodyController customerMainBodyController) {
        this.customerMainBodyController = customerMainBodyController;
    }

    @FXML public void initialize() {
        clientAsLenderLoanList = FXCollections.observableArrayList();
        clientAsBorrowLoanList = FXCollections.observableArrayList();
        if (transactionsController != null) {
            transactionsController.setMainController(this);
        }
        borrowerLoanID.setCellValueFactory(new PropertyValueFactory<>("loanID"));
        lenderLoanID.setCellValueFactory(new PropertyValueFactory<>("loanID"));
        borrowerLoanCategory.setCellValueFactory(new PropertyValueFactory<>("loanCategory"));
        lenderLoanCat.setCellValueFactory(new PropertyValueFactory<>("loanCategory"));
        lenderBorrowerName.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        borrowerLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        lenderLoanStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        remainFund.setCellValueFactory(new PropertyValueFactory<>("totalRemainingFund"));
        AddJavaFXCell.addSellButtonToTable(lenderTable,this::putLoanOnSaleRequest);
    }


    public SimpleStringProperty customerNameProperty() {
        return customerMainBodyController.customerNameProperty();
    }

    public void loadTransactionsTable(){
        transactionsController.loadTableData();
    }

    private void customiseFactory(TableColumn<LoanInformationObj, eLoanStatus> calltypel) {
        calltypel.setCellFactory(column -> {
            return new TableCell<LoanInformationObj, eLoanStatus>() {
                @Override
                protected void updateItem(eLoanStatus item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(empty ? "" : getItem().toString());
                    setGraphic(null);

                    TableRow<LoanInformationObj> currentRow = getTableRow();

                    if (!isEmpty()) {

                        if(item==eLoanStatus.RISK)
                            currentRow.setStyle("-fx-background-color:red");

                    }
                }
            };
        });
    }

    private void createLoansAsLenderRequest(){
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                //todo parameter name here
                .parse(Constants.LOANS_AS_LENDER)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        //updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("failed to call url information body controller")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        if(response.code()==200){
                            clientAsLenderLoanList.clear();
                            String jsonOfClientString = response.body().string();
                            // response.body().close();
                            Gson gson = new Gson();
                            LoanInformationObj[] loanAsLenderList = new Gson().fromJson(jsonOfClientString, LoanInformationObj[].class);
                            clientAsLenderLoanList.addAll(loanAsLenderList);
                            lenderTable.setItems(clientAsLenderLoanList);
                            customiseFactory(lenderLoanStatus);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        });
    }

    private void createLoansAsBorrowerRequest(){
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOANS_AS_BORROW)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();


        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("failed to call url information body")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        if(response.code()==200){
                            clientAsBorrowLoanList.clear();
                            String jsonOfClientString = response.body().string();
                            response.body().close();
                            LoanInformationObj[] loanAsBorrowList = new Gson().fromJson(jsonOfClientString, LoanInformationObj[].class);
                            clientAsBorrowLoanList.addAll(loanAsBorrowList);
                            borrowerTable.setItems(clientAsBorrowLoanList);
                            customerMainBodyController.resetFields();
                            //customiseFactory(borrowerLoanStatus);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        });
    }

    public ClientDTOforServlet getCurrClient(){
        return customerMainBodyController.getCurrClient();
    }

    private void putLoanOnSaleRequest(LoanInformationObj loan){


        String jsonExistChosenCategories = HttpClientUtil.GSON_INST.toJson(loan.getLoanID(),String.class);

        RequestBody body = RequestBody.create(jsonExistChosenCategories, HttpClientUtil.JSON);

        String finalUrl = HttpUrl
                .parse(Constants.PUT_LOAN_ON_SELL)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(body)
                .build();


        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("failed to call url put loan on sale")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    String jsonOfClientString = null;
                    try {
                        jsonOfClientString = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    response.body().close();

                    if(response.code()==200){
                        customerMainBodyController.loadData();
                        loan.setOnSale(true);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"loan "+loan.getLoanID()+"is now on sale!");
                        alert.showAndWait();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.ERROR,jsonOfClientString);
                        alert.showAndWait();
                    }
                });
            }

        });

    }



}
