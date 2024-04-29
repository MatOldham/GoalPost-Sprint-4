package utsa.cs3773goalpost.ui.goals;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utsa.cs3773goalpost.R;

public class GoalsFragment extends Fragment implements GoalsAdapter.OnGoalItemClickListener {

    private List<Goal> goalsList = new ArrayList<>();
    private GoalsAdapter adapter;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_goals, container, false);

        sharedPreferences = requireContext().getSharedPreferences("MyGoals", Context.MODE_PRIVATE);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewGoals);
        adapter = new GoalsAdapter();
        adapter.setOnGoalItemClickListener(this); // Set item click listener
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        goalsList = new ArrayList<>(); // Initialize goalsList here
        loadGoalsFromSharedPreferences();

        Button buttonAddNewGoal = root.findViewById(R.id.buttonAddNewGoal);
        buttonAddNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGoalDialog();
            }
        });

        return root;
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Goal");

        View viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_goal, null);
        final EditText inputTitle = viewInflated.findViewById(R.id.editTextTitle);
        final EditText inputDescription = viewInflated.findViewById(R.id.editTextDescription);
        final EditText inputDeadline = viewInflated.findViewById(R.id.editTextDeadline);
        final EditText inputMilestone = viewInflated.findViewById(R.id.editTextMilestone);
        final EditText inputStatus = viewInflated.findViewById(R.id.editTextStatus);
        final EditText inputImportance = viewInflated.findViewById(R.id.editTextImportance);

        builder.setView(viewInflated);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = inputTitle.getText().toString();
                String description = inputDescription.getText().toString();
                String deadline = inputDeadline.getText().toString();
                String milestone = inputMilestone.getText().toString();
                String status = inputStatus.getText().toString();
                int importance = Integer.parseInt(inputImportance.getText().toString()); // Parse importance as int

                // Call the appropriate constructor with five arguments
                Goal goal = new Goal(title, description, deadline, milestone, status, importance);
                addGoal(goal);
                saveGoalsToSharedPreferences();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addGoal(Goal goal) {
        goalsList.add(goal);
        adapter.setGoals(goalsList);
    }

    private void loadGoalsFromSharedPreferences() {
        Set<String> savedGoals = sharedPreferences.getStringSet("goals", new HashSet<String>());
        for (String goalString : savedGoals) {
            String[] parts = goalString.split(",");
            if (parts.length == 6) {
                Goal goal = new Goal(parts[0], parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]));

                goalsList.add(goal);
            }
        }
        adapter.setGoals(goalsList);
    }

    private void saveGoalsToSharedPreferences() {
        Set<String> goalsSet = new HashSet<>();
        for (Goal goal : goalsList) {
            String goalString = goal.getTitle() + "," + goal.getDescription() + "," +
                    goal.getDeadline() + "," + goal.getMilestone() + "," + goal.getStatus();
            goalsSet.add(goalString);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("goals", goalsSet);
        editor.apply();
    }

    @Override
    public void onItemClick(Goal goal) {
        // Handle item click implementation
    }
}
