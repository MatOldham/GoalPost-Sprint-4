// This class is a fragment that displays the preferences.

package utsa.cs3773goalpost.ui.more;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.Context;

import utsa.cs3773goalpost.ConnectionClass;
import utsa.cs3773goalpost.MainActivity;
import utsa.cs3773goalpost.PreferenceUtils;
import utsa.cs3773goalpost.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Future;

import utsa.cs3773goalpost.databinding.FragmentMoreBinding;
import utsa.cs3773goalpost.loginActivity;

public class MoreFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final String PREFS_NAME = "UserData";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private final ConnectionClass connectionClass;
    private static final int NOTIFICATION_ID = 1001;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private FragmentMoreBinding binding;

    public static MoreFragment newInstance() {

        return new MoreFragment();
    }

    public MoreFragment() {
        // initializion
        connectionClass = ConnectionClass.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(MoreViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference changeUsernamePref = findPreference("change_username");
        Preference changePasswordPref = findPreference("change_password");
        Preference changeEmailPref = findPreference("change_email");
        Preference logoutPref = findPreference("logout");
        Preference deleteAccountPref = findPreference("delete_account");
        CheckBoxPreference notificationsPref = findPreference("notifications");
        SwitchPreferenceCompat themePref = findPreference("theme");

        if (changeUsernamePref != null) {
            // get current username
            String currentUsername = getLastLoggedInUser();
            if (currentUsername != null) {
                // display current username
                changeUsernamePref.setSummary(currentUsername);
            }
        }

        if (changeEmailPref != null) {
            // get current email
            String currentEmail = getLastLoggedInEmail();
            if (currentEmail != null) {
                // display current email
                changeEmailPref.setSummary(currentEmail);
            }
        }

        changeUsernamePref.setOnPreferenceClickListener(this);
        changePasswordPref.setOnPreferenceClickListener(this);
        //changeEmailPref.setOnPreferenceClickListener(this);
        logoutPref.setOnPreferenceClickListener(this);
        deleteAccountPref.setOnPreferenceClickListener(this);
        notificationsPref.setOnPreferenceChangeListener(this);
        themePref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // handles click events for preference items
        String key = preference.getKey();
        switch (key) {
            case "change_username":
                openChangeUsernameDialog();
                return true;
            case "change_password":
                openChangePasswordDialog();
                return true;
            case "change_email":
                openChangeEmailDialog();
                return true;
            case "logout":
                performLogout();
                return true;
            case "delete_account":
                openDeleteAccountDialog();
                return true;
        }
        return true;
    }

    private void openChangeUsernameDialog() {
        String currentUsername = getLastLoggedInUser();
        if (currentUsername != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Change Username");

            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newUsername = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(newUsername)) {
                        performUsernameChange(currentUsername, newUsername);
                    } else {
                        Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else {
            Toast.makeText(requireContext(), "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLastLoggedInUser() {
        Context context = requireContext();
        return PreferenceUtils.getLastLoggedInUser(context);
    }

    private void performUsernameChange(String currentUsername, String newUsername) {
        // change username using database
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> futureUsernameChanged = executorService.submit(() -> {
            boolean usernameChanged = false;
            try {
                // update username using sql query
                String query = "UPDATE users SET username = ? WHERE username = ?";
                PreparedStatement stmt = connectionClass.getDbConn().prepareStatement(query);
                stmt.setString(1, newUsername);
                stmt.setString(2, currentUsername);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    usernameChanged = true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return usernameChanged;
        });

        executorService.shutdown();

        try {
            boolean usernameChanged = futureUsernameChanged.get();
            if (usernameChanged) {
                // show message if username change is successful
                Toast.makeText(requireContext(), "Username changed successfully", Toast.LENGTH_SHORT).show();

                // get preference item for the username
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                Preference changeUsernamePref = preferenceScreen.findPreference("change_username");
                if (changeUsernamePref != null) {
                    // update summary for new username
                    changeUsernamePref.setSummary(newUsername);
                }
            } else {
                // show message if username change failed
                Toast.makeText(requireContext(), "Failed to change username", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e("MoreFragment", "Failed to change username: " + e.getMessage());
        }
    }

    private void openChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Change Password");

        // dialog box
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        EditText currentPasswordEditText = view.findViewById(R.id.current_password_edit_text);
        EditText newPasswordEditText = view.findViewById(R.id.new_password_edit_text);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text);
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentPassword = currentPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                // Validate passwords
                if (validatePasswords(currentPassword, newPassword, confirmPassword)) {
                    // Update password
                    updatePassword(currentPassword, newPassword);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private boolean validatePasswords(String currentPassword, String newPassword, String confirmPassword) {
        // password validation
        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // check if new password matches confirm password
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updatePassword(String currentPassword, String newPassword) {
        // hash current and new password
        String hashedCurrentPassword = hashPassword(currentPassword);
        String hashedNewPassword = hashPassword(newPassword);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> futurePasswordChanged = executorService.submit(() -> {
            boolean passwordChanged = false;
            try {
                // get username of currently logged in user
                String username = getLastLoggedInUser();
                if (username != null) {
                    // database operation
                    ConnectionClass connectionClass = ConnectionClass.getInstance();
                    Connection conn = connectionClass.getDbConn();
                    if (conn != null) {
                        // update password in database
                        String query = "UPDATE users SET password = ? WHERE username = ? AND password = ?";
                        PreparedStatement preparedStatement = conn.prepareStatement(query);
                        preparedStatement.setString(1, hashedNewPassword); // Use the new hashed password
                        preparedStatement.setString(2, username);
                        preparedStatement.setString(3, hashedCurrentPassword); // Assuming currentPassword is the current hashed password
                        int rowsAffected = preparedStatement.executeUpdate();

                        // check if it successfully updated
                        if (rowsAffected > 0) {
                            passwordChanged = true;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return passwordChanged;
        });

        executorService.shutdown();

        try {
            boolean passwordChanged = futurePasswordChanged.get();
            if (passwordChanged) {
                // show message if password change is successful
                Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
            } else {
                // show message if password change failed
                Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
        }
    }

    private String hashPassword(String password) {
        String salt = null;
        try {
            salt = getSalt(getLastLoggedInUser());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            // create instance for SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // reset it
            md.reset();

            // add salt bytes
            md.update(salt.getBytes());

            // get hashed bytes of password with salt
            byte[] mdArray = md.digest(password.getBytes());

            // convert bytes to hexadecimal format
            StringBuilder sb = new StringBuilder(mdArray.length * 2);
            for (byte b : mdArray) {
                int v = b & 0xff;
                if (v < 16)
                    sb.append('0');
                sb.append(Integer.toHexString(v));
            }

            // return hashed password as hexadecimal string
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getSalt(String lastLoggedInUser) throws ExecutionException, InterruptedException {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> futureAuthenticated = executorService.submit(() -> {
                String salt = "";
                try {
                    String query = "SELECT salt FROM users WHERE username = '" + lastLoggedInUser + "'";
                    PreparedStatement stmt = connectionClass.getDbConn().prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();
                    while(rs.next()) {
                        salt = rs.getString("salt");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return salt;
            });
            executorService.shutdown();
            Log.d("salt", "" + futureAuthenticated.get());
            return futureAuthenticated.get();
    }

    private void openChangeEmailDialog() {
        String currentEmail = getLastLoggedInEmail();
        if (currentEmail!= null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Change Email");

            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newEmail = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(newEmail)) {
                        performEmailChange(currentEmail, newEmail);
                    } else {
                        Toast.makeText(requireContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else {
            Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLastLoggedInEmail() {
        Context context = requireContext();
        return PreferenceUtils.getLastLoggedInEmail(context);
    }

    private void performEmailChange(String currentEmail, String newEmail) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> futureEmailChanged = executorService.submit(() -> {
            boolean emailChanged = false;
            try {
                Connection conn = ConnectionClass.getInstance().getDbConn();
                if (conn != null) {
                    // use sql query to update email
                    String query = "UPDATE users SET email = ? WHERE email = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, newEmail);
                    stmt.setString(2, currentEmail);

                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        emailChanged = true;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return emailChanged;
        });

        executorService.shutdown();

        try {
            boolean emailChanged = futureEmailChanged.get();
            if (emailChanged) {
                // show message if email change is successful
                Toast.makeText(requireContext(), "Email changed successfully", Toast.LENGTH_SHORT).show();

                // update email in preferences
                PreferenceUtils.saveLoggedInEmail(requireContext(), newEmail);

                // update summary
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                Preference changeEmailPref = preferenceScreen.findPreference("change_email");
                if (changeEmailPref != null) {
                    changeEmailPref.setSummary(newEmail);
                }
            } else {
                // show message if email change failed
                Toast.makeText(requireContext(), "Failed to change email", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e("MoreFragment", "Failed to change email: " + e.getMessage());
        }
    }

    private void performLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutUser();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void openDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call a method to perform the account deletion
                deleteAccount();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deleteAccount() {
        // get username of the currently logged-in user
        String username = getLastLoggedInUser();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> futureAccountDeleted = executorService.submit(() -> {
            boolean accountDeleted = false;
            try {
                Connection conn = ConnectionClass.getInstance().getDbConn();

                if (conn != null) {
                    // sql delete statement
                    String sql = "DELETE FROM users WHERE username = ?";

                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, username);

                        // execute sql statement
                        int rowsAffected = pstmt.executeUpdate();

                        if (rowsAffected > 0) {
                            accountDeleted = true;
                        }
                    }
                } else {
                    System.out.println("Error: Database connection is null.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return accountDeleted;
        });

        executorService.shutdown();

        try {
            boolean accountDeleted = futureAccountDeleted.get();
            if (accountDeleted) {
                // logout the user
                logoutUser();
                // show success message
                System.out.println("Account deleted successfully.");
            } else {
                // show failed message
                System.out.println("Failed to delete account. Please try again.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void logoutUser() {
        // go back to the sign in page once logged out
        Intent intent = new Intent(requireActivity(), loginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if ("notifications".equals(key)) {
            boolean isChecked = (boolean) newValue;
            if (isChecked) {
                // enable notifs
                enableNotifications();
            } else {
                // disable notifs
                disableNotifications();
            }
            return true;
        } else if ("theme".equals(key)) {
            boolean isChecked = (boolean) newValue;
            // change light mode and dark mode
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
            requireActivity().recreate();
            return true;
        }
        return false;
    }

    private void enableNotifications() {
        String channelId = "CHANNEL_ID_NOTIFICATION";
        String channelName = "Notification Channel";

        // creates a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // builds the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Notifications Enabled")
                .setContentText("Notifications are now enabled!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // request permission if it hasn't been granted
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        } else {
            // show the notification if permission is already granted
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void disableNotifications() {
        // remove channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "CHANNEL_ID_NOTIFICATION";
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.deleteNotificationChannel(channelId);
        }

        // cancel existing notifs
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}