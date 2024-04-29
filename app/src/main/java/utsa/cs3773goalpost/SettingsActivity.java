// This class shows the settings screen to the user.

package utsa.cs3773goalpost;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    // This method is called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        // Might delete this later.
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                NavController navController = Navigation.findNavController(SettingsActivity.this, R.id.nav_host_fragment_activity_main);
//                switch (item.getItemId()) {
//                    case R.id.navigation_dashboard:
//                    case R.id.navigation_goals:
//                    case R.id.navigation_leaderboard:
//                        navController.navigate(item.getItemId());
//                        return true;
//                    case R.id.navigation_more:
//                        startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
//                        return true;
//                }
//                return false;
//            }
//        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    // This is the settings fragment within the activity.
    // Going to merge this with MoreFragment later.
    public static class SettingsFragment extends PreferenceFragmentCompat {
        // This method loads the preferences from XML and displays them on screen.
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}