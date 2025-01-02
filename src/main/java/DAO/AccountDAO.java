package DAO;

import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.Account;

public class AccountDAO {

    public Account createAccount(Account account){
        System.err.println("createAccount method called"); // Control print
        Connection connection = ConnectionUtil.getConnection();
        System.err.println("Connection established: " + (connection != null));
        try{
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            System.err.println("Attempting to create account: " + account.getUsername());

            int rowsAffected = preparedStatement.executeUpdate(); //if above 0 insert worked
            System.err.println("Rows affected: " + rowsAffected);

            if(rowsAffected > 0){
                ResultSet rs = preparedStatement.getGeneratedKeys(); //new keys in result set
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    account.setAccount_id(generatedId); //this rs should never be more than 1
                    System.err.println("Generated account_id: " + generatedId);
                }
                return account; //once acc created here it is
            }
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return null; //only if acc not created
        
    }

    public Account getAccountByUsername(String username) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getInt("account_id"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Account getAccountById(int accountId) {
        System.err.println("DAO: getAccountById called with accountId = " + accountId);
    
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM account WHERE account_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, accountId);
                System.err.println("Executing query: " + preparedStatement);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Account account = new Account(
                            resultSet.getInt("account_id"),
                            resultSet.getString("username"),
                            resultSet.getString("password")
                        );
                        System.err.println("Account found: " + account);
                        return account;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getAccountById: " + e.getMessage());
            e.printStackTrace();
        }
        System.err.println("No account found with accountId = " + accountId);
        return null;
    }
    
}
