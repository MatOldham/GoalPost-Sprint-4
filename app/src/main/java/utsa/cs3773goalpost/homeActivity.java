package utsa.cs3773goalpost;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import utsa.cs3773goalpost.controller.homeController;
import utsa.cs3773goalpost.controller.signupController;
import utsa.cs3773goalpost.ui.dashboard.DashboardFragment;

public class homeActivity extends AppCompatActivity {
    private homeController controller;

    /**
     * set up layout of signup screen
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        controller = new homeController(this);

        //set up the signup button
        setupButton();
    }
    private void setupButton() {

        //Find the button

    }
}
