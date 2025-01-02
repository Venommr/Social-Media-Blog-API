package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.util.List;

import org.h2.command.Prepared;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Util.ConnectionUtil;

public class MessageDAO {
    

    public List<Message> getAllMessages(){
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Message message = new Message(rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch"));
                messages.add(message);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return messages;
        
    }

    public Message getMessageById(int message_id){
        Connection connection = ConnectionUtil.getConnection();
        Message message = null;
        try {
            String sql = "SELECT * FROM message WHERE message_id = (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,message_id);
            ResultSet rs = preparedStatement.executeQuery();
            //Message message = new Message();
            if(rs.next()){
                message = new Message(rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch"));
                return message;
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Message createMessage(Message message) {
        System.err.println("DAO: createMessage called");
    
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, message.getPosted_by());
                preparedStatement.setString(2, message.getMessage_text());
                preparedStatement.setLong(3, message.getTime_posted_epoch());
    
                System.err.println("Executing query: " + preparedStatement);
    
                int rowsAffected = preparedStatement.executeUpdate();
                System.err.println("Rows affected: " + rowsAffected);
    
                if (rowsAffected > 0) {
                    try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                        if (rs.next()) {
                            int generatedId = rs.getInt(1);
                            message.setMessage_id(generatedId);
                            System.err.println("Generated message_id: " + generatedId);
                        }
                    }
                    return message;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during message creation: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public Message deleteMessageById(int message_id) {
        Connection connection = ConnectionUtil.getConnection();
        Message deletedMessage = null;
    
        try {
            deletedMessage = getMessageById(message_id);
    
            if (deletedMessage != null) {
                String sql = "DELETE FROM message WHERE message_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, message_id);
                preparedStatement.executeUpdate();
            }
    
        } catch (SQLException e) {
            System.out.println("Error during message deletion: " + e.getMessage());
        }
    
        // Return the deleted messageor null if it didnt exist
        return deletedMessage;
    }

    public Message updateMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
    
        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
    
            preparedStatement.setString(1, message.getMessage_text());
            preparedStatement.setInt(2, message.getMessage_id());
    
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected > 0) {//if successful
                return message;
            }
    
        } catch (SQLException e) {
            System.err.println("Error during message update: " + e.getMessage());
        }
    
        // Return null if the update failed
        return null;
    }

    public List<Message> getMessagesByUser(int accountId) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
    
        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId);
            ResultSet rs = preparedStatement.executeQuery();
    
            while (rs.next()) {
                Message message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving messages by user: " + e.getMessage());
        }
    
        return messages;
    }
}
