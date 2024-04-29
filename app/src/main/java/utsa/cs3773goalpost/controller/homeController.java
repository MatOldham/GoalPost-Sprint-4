package utsa.cs3773goalpost.controller;

import android.app.Activity;
import android.view.View;

public class homeController implements View.OnClickListener{

    private final Activity activity;
    public homeController(Activity activity){
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
