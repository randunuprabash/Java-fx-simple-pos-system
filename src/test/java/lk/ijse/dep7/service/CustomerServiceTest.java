package lk.ijse.dep7.service;

import lk.ijse.dep7.dbutils.SingleConnectionDataSource;
import lk.ijse.dep7.dto.CustomerDTO;
import lk.ijse.dep7.exception.DuplicateIdentifierException;
import lk.ijse.dep7.exception.FailedOperationException;
import lk.ijse.dep7.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    private CustomerService customerService;

    @BeforeEach
    private void initBeforeEachTest() throws SQLException {
        SingleConnectionDataSource.init("jdbc:mysql://localhost:3306/dep7_backup_pos", "root", "mysql");
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        this.customerService = new CustomerService(connection);
        connection.setAutoCommit(false);
        connection.prepareStatement("INSERT INTO customer VALUES ('C001', 'Dinusha', 'Galle')").executeUpdate();
    }

    @AfterEach
    private void finalizeAfterEachTest() throws SQLException {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        connection.rollback();
        connection.setAutoCommit(true);
    }

    @Test
    void saveCustomer() throws FailedOperationException, DuplicateIdentifierException, SQLException {
        customerService.saveCustomer(new CustomerDTO("C002", "Sovis", "Moratuwa"));
        //assertTrue(customerService.existCustomer("C002"));
        assertThrows(DuplicateIdentifierException.class, () ->
                customerService.saveCustomer(new CustomerDTO("C001", "Viduranga", "Galle")));
    }

    @Test
    void updateCustomer() throws FailedOperationException, NotFoundException, SQLException {
        customerService.updateCustomer(new CustomerDTO("C001", "Sachintha", "Matara"));
        ResultSet rst = SingleConnectionDataSource.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM customer WHERE id='C001'");
        rst.next();
        assertEquals(rst.getString("name"), "Sachintha");
        assertEquals(rst.getString("address"), "Matara");
        assertThrows(NotFoundException.class, () ->
                customerService.updateCustomer(new CustomerDTO("C100", "Gayal", "Nuwara")));
    }

    @Test
    void deleteCustomer() throws FailedOperationException, NotFoundException, SQLException {
        customerService.deleteCustomer("C001");
        assertFalse(SingleConnectionDataSource.getInstance().getConnection().prepareStatement("SELECT * FROM customer WHERE id='C001'").executeQuery().next());
        assertThrows(NotFoundException.class, () ->
                customerService.deleteCustomer("C100"));
    }

//    @Test
//    void existCustomer() throws SQLException {
//        assertTrue(customerService.existCustomer("C001"));
//        assertFalse(customerService.existCustomer("C002"));
//    }
}