package login;

import javafx.scene.control.Alert;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Collectors;

public class DatabaseConnect {
    public static Connection con;
    public static String url = "jdbc:mysql://restaurant.c1pmrcfs8z8r.ap-southeast-1.rds.amazonaws.com/restaurant";
//    public static String url = "jdbc:mysql://localhost:3306/restaurant";

    public Connection getConnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, "admin", "dat222003");
//            con = DriverManager.getConnection(url, "root", "password");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Cannot connect to database, please check your db configuration");
            alert.showAndWait();
        }

        return con;
    }

    public static boolean createSession(String key) {
        try {

            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO restaurant.user_session (`key`) VALUES (?)");
            preparedStatement.setString(1, key);
            preparedStatement.executeUpdate();
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean checkUserSession() {
        try {
            String key = UserSession.getSession();
            if (key == null) {
                return false;
            }
            getConnect();
            PreparedStatement preparedStatement = con.prepareStatement("Select * from user_session where `key` = ?");
            preparedStatement.setString(1, hash(key));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void deleteUserSession() {
        try {
            if (checkUserSession()) {
                getConnect();
                PreparedStatement preparedStatement = con.prepareStatement("delete from user_session where `key` = ?");
                preparedStatement.setString(1, hash(UserSession.getSession()));
                UserSession.deleteUserSession();
                preparedStatement.executeUpdate();
            }
            UserSession.deleteLocalSession();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String hash(String inp) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messaged = md.digest(inp.getBytes());
            BigInteger bigInteger = new BigInteger(1, messaged);
            return bigInteger.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
