package utsa.cs3773goalpost.ui.goals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import utsa.cs3773goalpost.R;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<Goal> goals;
    private OnGoalItemClickListener listener;

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    public void setOnGoalItemClickListener(OnGoalItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Goal goal = goals.get(position);
        holder.textViewGoalTitle.setText(goal.getTitle());
        holder.textViewGoalDescription.setText("Description: " + goal.getDescription());
        holder.textViewGoalDeadline.setText("Deadline: " + goal.getDeadline());
        holder.textViewGoalMilestone.setText("Milestone: " + goal.getMilestone());
        holder.textViewGoalStatus.setText("Status: " + goal.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(goal);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals != null ? goals.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewGoalTitle;
        TextView textViewGoalDescription;
        TextView textViewGoalDeadline;
        TextView textViewGoalMilestone;
        TextView textViewGoalStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGoalTitle = itemView.findViewById(R.id.textViewGoalTitle);
            textViewGoalDescription = itemView.findViewById(R.id.textViewGoalDescription);
            textViewGoalDeadline = itemView.findViewById(R.id.textViewGoalDeadline);
            textViewGoalMilestone = itemView.findViewById(R.id.textViewGoalMilestone);
            textViewGoalStatus = itemView.findViewById(R.id.textViewGoalStatus);
        }
    }

    public interface OnGoalItemClickListener {
        void onItemClick(Goal goal);
    }
}
