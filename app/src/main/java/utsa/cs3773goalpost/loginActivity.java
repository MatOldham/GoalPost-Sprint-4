package utsa.cs3773goalpost;


import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import utsa.cs3773goalpost.controller.loginController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import utsa.cs3773goalpost.databinding.ActivityLoginBinding;

public class loginActivity extends AppCompatActivity{
    private ActivityLoginBinding binding;
    private loginController controller;
    private ConnectionClass connectionClass;
    private String query;
    private static final String PREFS_NAME = "UserData";
    private static final String KEY_USERNAME = "username";
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
      
        //Connect to mysql database
        connect();
      
        controller = new loginController(this);

        //set up the buttons
        setupButtons(R.id.loginbtn, this);
        setupButtons(R.id.forgotPw, this);
        setupButtons(R.id.signup, this);
    }

    private void setupButtons(int buttonID, loginActivity activity) {

        Button button = (Button) findViewById(buttonID);

        //set the controller as the click listener for the login button
        button.setOnClickListener(v -> {
            // Check which button was clicked
            switch (buttonID) {
                case R.id.loginbtn:
                    login();
                    break;
                case R.id.forgotPw:
                    break;
                case R.id.signup:
                    Intent intent = new Intent(activity, signupActivity.class);
                    activity.startActivity(intent);
                    break;
            }
        });

 }
    public void connect(){
        connectionClass = ConnectionClass.getInstance();
    }

    private boolean authenticateUser(String username, String password) {
        String hashedPassword;
        //Generate salt
        String salt = null;
        try {
            salt = getSalt(username);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //hash using salt
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.reset();
            md.update(salt.getBytes());
            byte[] mdArray = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder(mdArray.length * 2);
            for(byte b : mdArray) {
                int v = b & 0xff;
                if(v < 16)
                    sb.append('0');
                sb.append(Integer.toHexString(v));
            }
            hashedPassword = sb.toString();
            System.out.println("hashed password: " + hashedPassword + " ---");
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }// catch (InvalidKeySpecException e) {
          //  throw new RuntimeException(e);
       // }
        //make database query
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> futureAuthenticated = executorService.submit(() -> {
            boolean authenticated = false;
            try {
                query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + hashedPassword + "'";
                PreparedStatement stmt = connectionClass.dbConn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    authenticated = true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return authenticated;
        });
        executorService.shutdown();
        // logs in if username and password are not empty
        boolean authenticated;
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            authenticated = futureAuthenticated.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return authenticated;
    }

    public String getSalt(String username) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureAuthenticated = executorService.submit(() -> {
            String salt = "";
            try {
                query = "SELECT salt FROM users WHERE username = '" + username + "'";
                PreparedStatement stmt = connectionClass.dbConn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    salt = rs.getString("salt");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return salt;
        });
        executorService.shutdown();
        Log.d("salt", "" + futureAuthenticated.get());
        return futureAuthenticated.get();
    }
    public void login() {
        String username = binding.username.getText().toString();
        String password = binding.password.getText().toString();

        //Changed to send user id. This is so we can query the account table to determine if a user is an admin and log them in accordingly
        boolean isAuthenticated = authenticateUser(username, password);


        if (isAuthenticated) {
            PreferenceUtils.saveLoggedInUser(this, username);
            //Check if user logged in is an admin
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Boolean> futureAuthenticated = executorService.submit(() -> {
                boolean isAdmin = false;
                try {
                    query = "SELECT isAdmin FROM users WHERE userName = '" + username + "'";
                    PreparedStatement stmt = connectionClass.dbConn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    if(rs.next()){
                        if(rs.getBoolean("isAdmin")){
                            isAdmin = true;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return isAdmin;
            });
            executorService.shutdown();
            //retrieve result from executor service
            boolean isAdmin;
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                isAdmin = futureAuthenticated.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            // this moves to the main app with the fragments after logging in
            Intent intent = new Intent(loginActivity.this, MainActivity.class);
            intent.putExtra("isAdmin", isAdmin);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        } else {
            // fails if username and password are empty
            Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

}

