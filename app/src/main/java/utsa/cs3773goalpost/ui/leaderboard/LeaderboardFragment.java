package utsa.cs3773goalpost.ui.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import utsa.cs3773goalpost.ConnectionClass;
import utsa.cs3773goalpost.MainActivity;
import utsa.cs3773goalpost.R;
import utsa.cs3773goalpost.databinding.FragmentLeaderboardBinding;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private String username, query;
    private ConnectionClass connectionClass;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LeaderboardViewModel leaderboardViewModel =
                new ViewModelProvider(this).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        username = MainActivity.username;
        connectionClass = ConnectionClass.getInstance();
        String[] users = new String[5];
        int[] goals = new int[5];
        //tbh idk a better way to do this bc of the ids
        TextView leader1 = (TextView) view.findViewById(R.id.firstLeader);
        TextView leader2 = (TextView) view.findViewById(R.id.secondLeader);
        TextView leader3 = (TextView) view.findViewById(R.id.thirdLeader);
        TextView leader4 = (TextView) view.findViewById(R.id.fourthLeader);
        TextView leader5 = (TextView) view.findViewById(R.id.fifthLeader);
        TextView goal1 = (TextView) view.findViewById(R.id.firstGoalNum);
        TextView goal2 = (TextView) view.findViewById(R.id.secondGoalNum);
        TextView goal3 = (TextView) view.findViewById(R.id.thirdGoalNum);
        TextView goal4 = (TextView) view.findViewById(R.id.fourthGoalNum);
        TextView goal5 = (TextView) view.findViewById(R.id.fifthGoalNum);
        TextView moreGoals = (TextView) view.findViewById(R.id.moreGoals);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> userAchieved = executorService.submit(() -> {
            int usersAchieved = 0;
            try {
                int i = 0;
                query = "SELECT username, goalAchieved FROM users ORDER BY goalAchieved DESC LIMIT 5;";
                PreparedStatement stmt = connectionClass.getDbConn().prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                Log.d("query result", "got here");
                while (rs.next()) {
                    users[i] = rs.getString("username");
                    goals[i] = rs.getInt("goalAchieved");
                    i++;
                }

                query = "SELECT goalAchieved FROM users WHERE username = '" + username + "';";
                stmt = connectionClass.getDbConn().prepareStatement(query);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    usersAchieved = rs.getInt("goalAchieved");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return usersAchieved;
        });
        executorService.shutdown();

        // test
        int goalAchieved_user;
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            leader1.setText(users[0]);
            leader2.setText(users[1]);
            leader3.setText(users[2]);
            leader4.setText(users[3]);
            leader5.setText(users[4]);
            goal1.setText(""+goals[0]);
            goal2.setText(""+goals[1]);
            goal3.setText(""+goals[2]);
            goal4.setText(""+goals[3]);
            goal5.setText(""+goals[4]);
            goalAchieved_user = userAchieved.get();
            int goalsNeeded = goals[4] - goalAchieved_user + 1;
            if(goalsNeeded > 0) {
                if(goalsNeeded == 1) moreGoals.setText("Complete 1 more goal to reach the leaderboard!");
                else moreGoals.setText("Complete " + goalsNeeded + " more goals to reach the leaderboard!");
            } else moreGoals.setVisibility(View.INVISIBLE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
}