package model;


import login.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class employeeDB {
    private static final DatabaseConnect databaseConnect = new DatabaseConnect();
    public boolean addEmployee(Employee employee) {
        try(
                Connection con = databaseConnect.getConnect();
        ) {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO user_account " +
                    "(user, password, name, phone, email, id_card, gender) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
            DatabaseConnect databaseConnect = new DatabaseConnect();
            preparedStatement.setString(1, employee.getUser());
            preparedStatement.setString(2, databaseConnect.hash(employee.getPassword()));
            preparedStatement.setString(3, employee.getName());
            preparedStatement.setString(4, employee.getPhone());
            preparedStatement.setString(5, employee.getEmail());
            preparedStatement.setString(6, employee.getIdcard());
            preparedStatement.setInt(7, employee.getGender());
            preparedStatement.executeUpdate();
            preparedStatement = con.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatement = con.prepareStatement("INSERT INTO employee (user_id, salary) VALUES (?, ?)");
                preparedStatement.setInt(1, resultSet.getInt(1));
                preparedStatement.setDouble(2, employee.getSalary());
                preparedStatement.executeUpdate();
                return true;
            }
            databaseConnect.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<Employee> getAllEmployee() {
        ArrayList<Employee> empList = new ArrayList<>();
        try(
                Connection con = databaseConnect.getConnect();
        ) {
            PreparedStatement preparedStatement = con.prepareStatement("select ua.user_id, user, name, phone, gender, salary, email, id_card, password, available  " +
                    "from employee  join user_account ua on ua.user_id = employee.user_id;");
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                if (resultset.getInt("available") == 1) {
                    empList.add(setEmployeeProp(resultset));
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return empList;
    }

    public Employee getOneEmployee(String user_id) {
        Employee employee = null;
        try(
                Connection con = databaseConnect.getConnect();
        ) {
            PreparedStatement preparedStatement = con.prepareStatement("select * from user_account " +
                    "join employee  on user_account.user_id = employee.user_id " +
                    "where user_account.user_id = ?");
            preparedStatement.setString(1, user_id);
            ResultSet resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                employee = setEmployeeProp(resultset);
            }
        } catch (SQLException e) {
            return null;
        }
        return employee;
    }

    public boolean deleteEmployee(String user_id) {
        try(
                Connection con = databaseConnect.getConnect();
        ) {
            PreparedStatement preparedStatement;
            preparedStatement = con.prepareStatement("update user_account set available = 0 where user_id = ?");
            preparedStatement.setString(1, user_id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static Employee setEmployeeProp(ResultSet resultset) throws SQLException {
        Employee employee = new Employee();
        employee.setUserid(Integer.parseInt(resultset.getString("user_id")));
        employee.setSalary(resultset.getDouble("salary"));
        employee.setEmail(resultset.getString("email"));
        employee.setUser(resultset.getString("user"));
        employee.setGender(resultset.getInt("gender"));
        employee.setIdcard(resultset.getString("id_card"));
        employee.setPassword(resultset.getString("password"));
        employee.setName(resultset.getString("name"));
        employee.setPhone(resultset.getString("phone"));
        return employee;
    }

    //update employee
       public boolean updateEmployee(Employee employee) {
            try(
                    Connection con = databaseConnect.getConnect();
            ) {
                PreparedStatement preparedStatement = con.prepareStatement("UPDATE user_account, employee" +
                            " set user=?,  name=?, phone=?, email=?,id_card=?, gender=?, employee.salary=?" +
                            " where user_account.user_id = ? and employee.user_id = ?");
                preparedStatement.setString(1, employee.getUser());
                preparedStatement.setString(2, employee.getName());
                preparedStatement.setString(3, employee.getPhone());
                preparedStatement.setString(4, employee.getEmail());
                preparedStatement.setString(5, employee.getIdcard());
                preparedStatement.setInt(6, employee.getGender());
                preparedStatement.setDouble(7, employee.getSalary());
                preparedStatement.setInt(8, employee.getUserid());
                preparedStatement.setInt(9, employee.getUserid());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
       }

    public boolean changePassword(Employee employee) {
        try(
                Connection con = databaseConnect.getConnect();
        ) {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user_account " +
                    "set password=? " +
                    "where user_id = ?");
            DatabaseConnect databaseConnect = new DatabaseConnect();
            preparedStatement.setString(1, databaseConnect.hash(employee.getPassword()));
            preparedStatement.setInt(2, employee.getUserid());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}


