package utsa.cs3773goalpost.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import utsa.cs3773goalpost.R;
import utsa.cs3773goalpost.forgotpwActivity;
import utsa.cs3773goalpost.homeActivity;
import utsa.cs3773goalpost.signupActivity;

public class MainController implements View.OnClickListener{
    private final Activity activity;


    /**
     * Constructor
     * @param activity
     */
    public MainController(Activity activity) {
        this.activity = activity;

    }

    /**
     * move to next screen depends on which button being clicked
     * @param view
     */
    @Override
    public void onClick(View view) {


    }
}
