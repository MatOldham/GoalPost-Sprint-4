// This class sets the layout/navigation and initializes the activities.

package utsa.cs3773goalpost;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import utsa.cs3773goalpost.controller.MainController;
import utsa.cs3773goalpost.databinding.ActivityMainBinding;
/**
 * this class include a controller object that implements the View.onClickListener
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private MainController controller;
    public static String username;
    private String query;


    /**
     * this method take in saveInstanceState as a parameter, it inflates the view to display on screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //retrieve extra variables from loginInfo, right now just isAdmin and dbConn
        Bundle extras = getIntent().getExtras();
        boolean isAdmin = false;
        if(extras != null){
            isAdmin = extras.getBoolean("isAdmin", false);
            username = extras.getString("username");
        }
        Log.d("isAdminCheck", "isAdmin: " + isAdmin + ".");
        Log.d("usernameCheck", "username: " + username + ".");

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_goals, R.id.navigation_leaderboard, R.id.navigation_more)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // This sets up the bottom navigation view so the user can select between them.
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        navController.navigate(R.id.navigation_dashboard);
                        return true;
                    case R.id.navigation_goals:
                        navController.navigate(R.id.navigation_goals);
                        return true;
                    case R.id.navigation_leaderboard:
                        navController.navigate(R.id.navigation_leaderboard);
                        return true;
                    case R.id.navigation_more:
                        navController.navigate(R.id.navigation_more);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }




    /**
     * getter for controller
     * @return
     */
    public MainController getController(){
        return this.controller;
       }

    /**
     * setter for controller
     * @param controller
     */
    public void setController(MainController controller){
        this.controller = controller;
       }

    }
