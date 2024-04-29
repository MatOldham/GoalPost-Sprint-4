package utsa.cs3773goalpost;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import utsa.cs3773goalpost.controller.ForgotpwController;
import utsa.cs3773goalpost.controller.signupController;

/**
 * sets up the activity_forgotpw.xml and ForgotpwController class
 */
public class forgotpwActivity extends AppCompatActivity {

    private ForgotpwController controller;

    /**
     * set up layout of forgot password screen
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpw);
        controller = new ForgotpwController(this);

        //set up the signup button
        setupButton();
    }
    private void setupButton() {

        //Find the button
        Button resetButton = findViewById(R.id.resetlink);
        //set the controller as the click listener for the register button
        resetButton.setOnClickListener(controller);
    }


}
