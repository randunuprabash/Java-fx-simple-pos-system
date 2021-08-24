package lk.ijse.dep7.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.dep7.dbutils.SingleConnectionDataSource;
import lk.ijse.dep7.dto.OrderDetailDTO;
import lk.ijse.dep7.exception.FailedOperationException;
import lk.ijse.dep7.exception.NotFoundException;
import lk.ijse.dep7.service.ItemService;
import lk.ijse.dep7.service.OrderService;
import lk.ijse.dep7.util.OrderDetailTM;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ViewOrderFormController {
    public AnchorPane root;
    public JFXTextField txtCustomerName;
    public TableView<OrderDetailTM> tblOrderDetails;
    public Label lblDate;
    public Label lblId;
    public Label lblTotal;
    public JFXTextField txtCustomerID;

    private final OrderService orderService = new OrderService(SingleConnectionDataSource.getInstance().getConnection());
    private final ItemService itemService = new ItemService(SingleConnectionDataSource.getInstance().getConnection());

    public void initialize(){
        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    public void initWithData(String orderId, LocalDate orderDate, String customerId, String customerName, BigDecimal orderTotal){
        lblId.setText("Order Id: " + orderId);
        lblDate.setText(orderDate.toString());
        txtCustomerID.setText(customerId);
        txtCustomerName.setText(customerName);
        lblTotal.setText("Total: " + orderTotal.setScale(2).toString());

        try {
            List<OrderDetailDTO> orderDetails = orderService.findOrderDetails(orderId);

            orderDetails.forEach(detail->{
                try {
                    tblOrderDetails.getItems().add(new OrderDetailTM(detail.getItemCode(),
                            itemService.findItem(detail.getItemCode()).getDescription(),
                            detail.getQty(),
                            detail.getUnitPrice(),
                            detail.getUnitPrice().multiply(new BigDecimal(detail.getQty())).setScale(2)));
                } catch (NotFoundException | FailedOperationException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                    throw new RuntimeException(e);
                }
            });

        } catch (NotFoundException e) {
            e.printStackTrace();                    // With our UI deign this will never happen
        } catch (FailedOperationException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            throw new RuntimeException(e);
        }
    }
}
