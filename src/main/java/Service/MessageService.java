package Service;

import java.util.ArrayList;
import java.util.List;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    
    public MessageDAO messageDAO;
    private AccountDAO accountDAO;

    public MessageService(){
        this.messageDAO = new MessageDAO();
        this.accountDAO = new AccountDAO();
    }

    //This constructor here is for mockito testing message DAO on its own without using messageSerivce in between
    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    public List<Message> getAllMessages(){
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int messageId){
        return messageDAO.getMessageById(messageId);
    }
    public Message createMessage(Message message) {
        System.err.println("Service: createMessage called");

        // validate message_text
        if (message.getMessage_text() == null || message.getMessage_text().isBlank() ||
            message.getMessage_text().length() > 255) {
            System.err.println("Validation failed: invalid message_text");
            return null;
        }

        // validate posted_by
        if (accountDAO.getAccountById(message.getPosted_by()) == null) {
            System.err.println("Validation failed: invalid posted_by user");
            return null;
        }

        
        if (message.getTime_posted_epoch() == 0) {
            message.setTime_posted_epoch(System.currentTimeMillis() / 1000);
        }
        //message.setTime_posted_epoch(System.currentTimeMillis() / 1000);

        // Call DAO to create the message
        Message createdMessage = messageDAO.createMessage(message);
        if (createdMessage != null) {
            System.err.println("Message successfully created: " + createdMessage);
        } else {
            System.err.println("Message creation failed in DAO");
        }
        return createdMessage;
    }


    public Message deleteMessage(int messageId) {
        System.err.print("Entering DAO delete message");
        return messageDAO.deleteMessageById(messageId);
    }

    public Message updateMessage(int messageId, String newMessageText) {
        if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() > 255) {
            return null; // Invalid input
        }
    
        // check if the message exists
        Message existingMessage = messageDAO.getMessageById(messageId);
        if (existingMessage == null) {
            return null; // Message does not exist
        }
    
        // Update
        existingMessage.setMessage_text(newMessageText);
        return messageDAO.updateMessage(existingMessage);
    }

    public List<Message> getMessagesByUser(int accountId) {
        try {
            // check if the user exists
            if (accountDAO.getAccountById(accountId) == null) {
                System.out.println("Account with ID " + accountId + " does not exist.");
                return new ArrayList<>(); //return empty list for nonexistant users
            }
    
            // get messages for the existing user
            List<Message> messages = messageDAO.getMessagesByUser(accountId);
            System.out.println("Retrieved " + messages.size() + " messages for account ID: " + accountId);
            return messages;
    
        } catch (Exception e) {
            System.err.println("Error in getMessagesByUser: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // return empty list on failure
        }
    }

}
