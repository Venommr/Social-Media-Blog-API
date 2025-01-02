package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {

    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    // Constructor for testing
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account registerAccount(Account account) { //VALIDATE HERE
        System.err.println("Service: registerAccount called with username: " + account.getUsername());
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            return null; //  blank username
        }
    
        // validate password is at least 4 characters long
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return null; //  short password
        }
    
        // check if the username already exists
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null; //  username already exists
        }
    
        System.err.println("All validations passed. Proceeding to DAO layer...");
        return accountDAO.createAccount(account);
    }

    public Account loginAccount(String username, String password) {
        //get acc by username
        Account account = accountDAO.getAccountByUsername(username);
    
        //check pass
        if (account != null && account.getPassword().equals(password)) {
            return account; 
        }
    
        return null; //failed
    }
}