package utsa.cs3773goalpost;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import utsa.cs3773goalpost.controller.signupController;
import utsa.cs3773goalpost.databinding.ActivitySignupBinding;

/**
 * sets up the activity_signup.xml and the signupController class
 */
public class signupActivity extends AppCompatActivity {
    private signupController controller;
    private ActivitySignupBinding binding;
    /**
     * set up layout of signup screen
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new signupController(this, binding);
        //set up the signup button
        setupButton();
    }
    private void setupButton() {

        //Find the button
        Button signupButton = findViewById(R.id.register);
        //set the controller as the click listener for the register button
        signupButton.setOnClickListener(controller);
    }



}




