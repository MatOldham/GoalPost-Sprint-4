package utsa.cs3773goalpost.controller;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import utsa.cs3773goalpost.R;
import utsa.cs3773goalpost.forgotpwActivity;
import utsa.cs3773goalpost.homeActivity;
import utsa.cs3773goalpost.signupActivity;

public class loginController implements View.OnClickListener{
    private final Activity activity;
    public loginController(Activity activity) {
        this.activity = activity;

    }

    /**
     * move to next screen depends on which button being clicked
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup){
            Intent intent = new Intent(activity, signupActivity.class);
            activity.startActivity(intent);
        }
        if(view.getId() == R.id.forgotPw){
            Intent intent = new Intent(activity, forgotpwActivity.class);
            activity.startActivity(intent);
        }

        if(view.getId() == R.id.loginbtn){
            Intent intent = new Intent(activity, homeActivity.class);
            activity.startActivity(intent);
        }

    }
}
