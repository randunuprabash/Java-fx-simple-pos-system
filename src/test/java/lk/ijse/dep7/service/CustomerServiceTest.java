package lk.ijse.dep7.service;

import lk.ijse.dep7.dbutils.SingleConnectionDataSource;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

    private CustomerService customerService;

    @BeforeEach
    private void initBeforeEachTest() throws SQLException {
        SingleConnectionDataSource.init("jdbc:mysql://localhost:3306/dep7_backup_pos","root","mysql");
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
    void saveCustomer() {
        System.out.println("Save Customer Test");
    }

    @Test
    void existCustomer() throws SQLException {
        assertTrue(customerService.existCustomer("C001"));
        assertFalse(customerService.existCustomer("C002"));
    }
}