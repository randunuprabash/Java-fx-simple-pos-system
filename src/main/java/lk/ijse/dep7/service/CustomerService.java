package lk.ijse.dep7.service;

import lk.ijse.dep7.dto.CustomerDTO;
import lk.ijse.dep7.exception.DuplicateIdentifierException;
import lk.ijse.dep7.exception.FailedOperationException;
import lk.ijse.dep7.exception.NotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer Service API
 *
 * <br>
 * <img src="https://www.ijse.lk/images/dep/dep-logo.png" width="150"/>
 *
 * @author ranjith-suranga
 * @version 0.1.0
 */
public class CustomerService {

    private Connection connection;

    public CustomerService() {
    }

    public CustomerService(Connection connection) {
        this.connection = connection;
    }

    /**
     * Invoke to save a new customer
     *
     * @param customer
     * @throws DuplicateIdentifierException
     * @throws FailedOperationException
     */
    public void saveCustomer(CustomerDTO customer) throws DuplicateIdentifierException, FailedOperationException {
        try {
            if (existCustomer(customer.getId())) {
                throw new DuplicateIdentifierException(customer.getId() + " already exists");
            }

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO customer (id,name, address) VALUES (?,?,?)");
            pstm.setString(1, customer.getId());
            pstm.setString(2, customer.getName());
            pstm.setString(3, customer.getAddress());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to save the customer", e);
        }
    }

    boolean existCustomer(String id) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement("SELECT id FROM customer WHERE id=?");
        pstm.setString(1, id);
        return pstm.executeQuery().next();
    }

    public void updateCustomer(CustomerDTO customer) throws FailedOperationException, NotFoundException {
        try {

            if (!existCustomer(customer.getId())){
                throw new NotFoundException("There is no such customer associated with the id " + customer.getId());
            }

            PreparedStatement pstm = connection.prepareStatement("UPDATE customer SET name=?, address=? WHERE id=?");
            pstm.setString(1,customer.getName());
            pstm.setString(2,customer.getAddress());
            pstm.setString(3,customer.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to update the customer " + customer.getId(), e);
        }
    }

    public void deleteCustomer(String id) throws NotFoundException, FailedOperationException {
        try {
            if (!existCustomer(id)){
                throw new NotFoundException("There is no such customer associated with the id " + id);
            }

            PreparedStatement pstm = connection.prepareStatement("DELETE FROM customer WHERE id=?");
            pstm.setString(1,id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to delete the customer " + id, e);
        }
    }

    public CustomerDTO findCustomer(String id) throws NotFoundException, FailedOperationException {
        try {
            if (!existCustomer(id)){
                throw new NotFoundException("There is no such customer associated with the id " + id);
            }

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM customer WHERE id=?");
            pstm.setString(1, id);
            ResultSet rst = pstm.executeQuery();
            rst.next();
            return new CustomerDTO(id, rst.getString("name"), rst.getString("address"));
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to find the customer " + id, e);
        }
    }

    public List<CustomerDTO> findAllCustomers() throws FailedOperationException {
        try {
            List<CustomerDTO> customersList = new ArrayList<>();

            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM customer");

            while (rst.next()){
                customersList.add(new CustomerDTO(rst.getString("id"), rst.getString("name"), rst.getString("address")));
            }

            return customersList;
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to find customers", e);
        }
    }

    public String generateNewCustomerId() throws FailedOperationException {
        try {
            ResultSet rst = connection.createStatement().executeQuery("SELECT id FROM customer ORDER BY id DESC LIMIT 1;");
            if (rst.next()){
                String id = rst.getString("id");
                int newCustomerId = Integer.parseInt(id.replace("C", "")) + 1;
                return String.format("C%03d", newCustomerId);
            }else{
                return "C001";
            }
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to generate a new id", e);
        }
    }

}
