package Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    MessageService messageService;
    AccountService accountService;

    public SocialMediaController(){
        this.messageService = new MessageService();
        this.accountService = new AccountService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", ctx -> {          //passing in the parameter of message_id into the handler
            int messageId = Integer.parseInt(ctx.pathParam("message_id")); 
            getMessageByIdHandler(ctx, messageId);
        });

        //creating account endpoints
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);

        //creating a new message endpoint
        app.post("/messages", this::createMessageHandler);

        //delete
        app.delete("/messages/{message_id}", this::deleteMessageHandler);

        //patch
        app.patch("/messages/{message_id}", this::updateMessageHandler);

        //getMessagesByUser_id
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


    public void getAllMessagesHandler(Context ctx){
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    public void getMessageByIdHandler(Context ctx, int messageId){
        Message message = messageService.getMessageById(messageId);

        if(message != null){
            ctx.json(message);
        } 
        
       // ctx.json(messageService.getMessageById());
    }

    public void registerHandler(Context ctx) {
        System.err.println("Controller: registerHandler called");
        try {
            Account account = ctx.bodyAsClass(Account.class);

            System.err.println("Parsed account: " + account);

            Account createdAccount = accountService.registerAccount(account);
          
            if (createdAccount != null) {
                System.err.println("Registration successful: " + createdAccount);
                ctx.json(createdAccount);
                ctx.status(200);
            } else {
                System.err.println("Registration failed: invalid input or duplicate username");
                ctx.status(400); 
            }
        } catch (Exception e) {
            
            System.out.println("Error in registration: " + e.getMessage());
            ctx.status(500); 
        }
    }

    public void loginHandler(Context ctx) {
        Account loginRequest = ctx.bodyAsClass(Account.class); 
        Account loggedInAccount = accountService.loginAccount(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        );
    
        if (loggedInAccount != null) {
            ctx.json(loggedInAccount);    
            ctx.status(200);           
        } else {
            ctx.status(401);           //fail
        }
    }

    public void createMessageHandler(Context ctx) {
        System.err.println("Controller: createMessageHandler called");

        try {
            Message message = ctx.bodyAsClass(Message.class); // make JSON request body into a Message object
            System.err.println("Parsed message: " + message);

            Message createdMessage = messageService.createMessage(message);

            if (createdMessage != null) {
                System.err.println("Message creation successful: " + createdMessage);
                ctx.json(createdMessage);
                ctx.status(200); 
            } else {
                System.err.println("Message creation failed: invalid input");
                ctx.status(400); 
            }
        } catch (Exception e) {
            System.err.println("Error in createMessageHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500); 
        }
    }

    public void deleteMessageHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessage(messageId);
    
        if (deletedMessage != null) {
            ctx.json(deletedMessage);
            ctx.status(200);
        } else {
            ctx.status(200).json(""); 
        }
    }

    public void updateMessageHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
    
           
            String newMessageText = ctx.bodyAsClass(Map.class).get("message_text").toString();
    
            Message updatedMessage = messageService.updateMessage(messageId, newMessageText);
    
            if (updatedMessage != null) {
                ctx.json(updatedMessage); 
                ctx.status(200);
            } else {
                ctx.status(400); 
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message ID format.");
            System.err.println("Invalid message ID: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error.");
            System.err.println("Error updating message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getMessagesByUserHandler(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            System.out.println("Fetching messages for account ID: " + accountId);
    
            List<Message> userMessages = messageService.getMessagesByUser(accountId);
    
            if (userMessages != null) {
                ctx.json(userMessages); //Return messages
                System.out.println("Messages retrieved: " + userMessages.size());
                ctx.status(200);
            } else {
                ctx.json(new ArrayList<>()); //empty
                ctx.status(200);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account ID format.");
            System.err.println("Invalid account ID: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Internal server error.");
            System.err.println("Error retrieving messages by user: " + e.getMessage());
            e.printStackTrace();
        }
    }


}