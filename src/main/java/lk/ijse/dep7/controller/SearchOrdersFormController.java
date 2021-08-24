package lk.ijse.dep7.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.dep7.dbutils.SingleConnectionDataSource;
import lk.ijse.dep7.dto.OrderDTO;
import lk.ijse.dep7.exception.FailedOperationException;
import lk.ijse.dep7.service.OrderService;
import lk.ijse.dep7.util.OrderTM;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SearchOrdersFormController {

    private final OrderService orderService = new OrderService(SingleConnectionDataSource.getInstance().getConnection());
    public AnchorPane root;
    public TextField txtSearch;
    public TableView<OrderTM> tblOrders;

    public void initialize() throws FailedOperationException {
        tblOrders.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderId"));
        tblOrders.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblOrders.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tblOrders.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblOrders.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("orderTotal"));

        loadOrders();

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                loadOrders();
            } catch (FailedOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadOrders() throws FailedOperationException {
        try {
            List<OrderDTO> orderList = orderService.searchOrders(txtSearch.getText());
            tblOrders.getItems().clear();

            orderList.forEach(order -> tblOrders.getItems().add(new OrderTM(order.getOrderId(),
                    order.getOrderDate(),
                    order.getCustomerId(),
                    order.getCustomerName(),
                    order.getOrderTotal().setScale(2))));

        } catch (FailedOperationException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to fetch orders").show();
            throw e;
        }
    }

    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/view/main-form.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }

    public void tblOrders_OnMouseClicked(MouseEvent mouseEvent) throws IOException {

        OrderTM selectedOrder = tblOrders.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) return;

        if (mouseEvent.getClickCount() == 2) {

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/view-order-form.fxml"));
            Parent root = fxmlLoader.load();
            ViewOrderFormController controller = fxmlLoader.getController();
            stage.setScene(new Scene(root));

            controller.initWithData(selectedOrder.getOrderId(),
                    selectedOrder.getOrderDate(),
                    selectedOrder.getCustomerId(),
                    selectedOrder.getCustomerName(),
                    selectedOrder.getOrderTotal());

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.show();
            stage.sizeToScene();

        }
    }
}
