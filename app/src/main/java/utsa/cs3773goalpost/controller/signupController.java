package utsa.cs3773goalpost.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import utsa.cs3773goalpost.ConnectionClass;
import utsa.cs3773goalpost.MainActivity;
import utsa.cs3773goalpost.R;
import utsa.cs3773goalpost.databinding.ActivitySignupBinding;
import utsa.cs3773goalpost.homeActivity;

public class signupController implements View.OnClickListener{
    private final Activity activity;
    private final ActivitySignupBinding binding;
    private final ConnectionClass connectionClass;
    private String query;
    public signupController(Activity activity, ActivitySignupBinding binding){
        this.activity = activity;
        this.binding = binding;
        connectionClass = ConnectionClass.getInstance();
    }


    /**
     * move to next screen if button being clicked
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.register) {
            register();
        }

    }

    public void register(){
        //get inputs
        String username = binding.username.getText().toString();
        String password = binding.password.getText().toString();
        String repassword = binding.repassword.getText().toString();
        String email = binding.email.getText().toString();
        String hashedPassword;
        Log.d("test", "username: " + username + "email: "+ email);
        //check if theyre the same
        if(!password.equals(repassword)){
            Toast.makeText(activity, "Sign-up failed. Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        //hash password
        //Generate salt
        String salt = generateSalt();
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
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> futureAuthenticated = executorService.submit(() -> {
            boolean userNameExists = false;
            try {
                query = "SELECT * FROM users WHERE username = '" + username + "';";
                PreparedStatement stmt = connectionClass.getDbConn().prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    userNameExists = true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if(userNameExists) {
                return true;
            }
            try {
                query = "INSERT INTO users (username, email, password, goalAchieved, isAdmin, salt) VALUES ('" + username + "', '" + email + "', '" + hashedPassword + "', 0, false, '"+ salt +"');";
                Statement stmt = connectionClass.getDbConn().createStatement();
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return false;
        });
        executorService.shutdown();
        boolean userNameExists;
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            userNameExists = futureAuthenticated.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        if(userNameExists){
            Toast.makeText(activity, "Sign-up failed. Username already exists.", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("isAdmin", false);
            intent.putExtra("username", username);
            activity.startActivity(intent);
            activity.finish();
        }


    }

    private String generateSalt() {
            SecureRandom random = new SecureRandom();
            byte bytes[] = new byte[20];
            random.nextBytes(bytes);
            return bytes.toString();
    }
}
