package lk.ijse.dep7.service;

import lk.ijse.dep7.dbutils.SingleConnectionDataSource;
import lk.ijse.dep7.dto.CustomerDTO;
import lk.ijse.dep7.exception.DuplicateIdentifierException;
import lk.ijse.dep7.exception.FailedOperationException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
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
        assertTrue(customerService.existCustomer("C002"));
        assertThrows(DuplicateIdentifierException.class, () ->
                customerService.saveCustomer(new CustomerDTO("C001", "Viduranga", "Galle")));
    }

    @Test
    void existCustomer() throws SQLException {
        assertTrue(customerService.existCustomer("C001"));
        assertFalse(customerService.existCustomer("C002"));
    }
}