package utsa.cs3773goalpost.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.view.View;

import utsa.cs3773goalpost.R;
import utsa.cs3773goalpost.signupActivity;

public class ForgotpwController implements View.OnClickListener{
    private final Activity activity;
    private AssetManager assets;


    /**
     * Constructor
     * @param activity
     */
    public ForgotpwController(Activity activity) {
        this.activity = activity;

    }

    /**
     * move to next screen if button being clicked
     * @param view
     */
    @Override
    public void onClick(View view) {

    }
}
