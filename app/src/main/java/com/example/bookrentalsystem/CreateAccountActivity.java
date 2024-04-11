package com.example.bookrentalsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookrentalsystem.LogUtility;

import android.os.AsyncTask;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity{

    private static final Map<String, String> userPasswordMap = new HashMap<>();
    private int invalidAttemptCount = 0;
    private int duplicateUsernameCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        UserDatabase.initializeUsers(UserDatabase.getInstance(this));
    }

    public void createAccount(View view) {
        // Step 3: The Customer enters a username and a password.
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Step 4: The System verifies that the username and password are valid.
        User existingUser = UserDatabase.getInstance(this).userDAO().getUserByUsername(username);

        if (existingUser == null && isValid(username, password)) {
            // Username is not in the database, and it is valid

            User newUser = new User(username, password);
            new InsertUserTask(CreateAccountActivity.this).execute(newUser);

            // Step 5: The System informs the Customer that his/her account has been created successfully.
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_LONG).show();

            // Step 6: The System records the operation information.
            LogUtility.logOperation(CreateAccountActivity.this,"new account", username);
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            LogEntry log = new LogEntry("New Account",timeStamp,"Customer's Username: " +  username);
            RentDatabase.getInstance(this).rentDAO().insertLog(log);
            // Step 7: The System displays the main menu.
            finish();
        } else {
            invalidAttemptCount++;

            if (existingUser != null && existingUser.getUsername().equals(username)) {
                // Display an error message for duplicate username
                Toast.makeText(this, "Username already exists, choose a different one", Toast.LENGTH_LONG).show();
            } else if("Admin2".equalsIgnoreCase(username) && "Admin2".equals(password)) {
                Toast.makeText(this, "For Librarian. Can't use username & password.", Toast.LENGTH_LONG).show();
                finish();
            } else if (!isValid(username, password)) {
                Toast.makeText(this, "Invalid username or password format", Toast.LENGTH_LONG).show();
            } else {
                // Display an error message for invalid username or password
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show();
            }

            // Check for exceeding maximum invalid attempts
            if (invalidAttemptCount >= 2) {
                Toast.makeText(this, "Exceeded maximum invalid attempts. Returning to the main menu.", Toast.LENGTH_LONG).show();
                // Step 7: The System displays the main menu.
                finish();
            }
        }
    }




    private boolean isValid(String username, String password) {

        // Implement your validation logic here
        // This is a simplified example
        return username.length() >= 4 && username.length() <= 10
                && password.length() >= 4 && password.length() <= 10
                && isAlphanumeric(username) && isAlphanumeric(password)
                && containsonedigitandthreecharactersinusername(username)
                && containsonedigitandthreecharactersinpassword(password)
                && !username.equalsIgnoreCase("Admin2");
    }

    private static class InsertUserTask extends AsyncTask<User, Void, Void> {
        private Context context;

        InsertUserTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(User... users) {
            UserDatabase.getInstance(context).userDAO().insert(users[0]);
            return null;
        }
    }

    private boolean isAlphanumeric(String input){
        for(char c : input.toCharArray()){
            if(!Character.isLetterOrDigit(c)){
                return false;
            }
        }
        return true;
    }

    private boolean containsonedigitandthreecharactersinusername(String username){
        // Check if the password contains at least one digit and three alphabetical characters
        int digitCount = 0;
        int alphaCount = 0;

        for (char c : username.toCharArray()) {
            if (Character.isDigit(c)) {
                digitCount++;
            } else if (Character.isLetter(c)) {
                alphaCount++;
            }
            else{
                return false;
            }
        }

        return digitCount > 0 && alphaCount >= 3;
    }

    private boolean containsonedigitandthreecharactersinpassword(String password){
        // Check if the password contains at least one digit and three alphabetical characters
        int digitCount = 0;
        int alphaCount = 0;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digitCount++;
            } else if (Character.isLetter(c)) {
                alphaCount++;
            }
            else{
                return false;
            }
        }

        return digitCount > 0 && alphaCount >= 3;
    }

}
