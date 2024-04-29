package utsa.cs3773goalpost.ui.more;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import utsa.cs3773goalpost.R;
public class MoreOptionsAdapter extends RecyclerView.Adapter<MoreOptionsAdapter.ViewHolder> {
    private List<MoreOptions> options;

    public MoreOptionsAdapter(List<MoreOptions> options) {
        this.options = options;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_more_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoreOptions option = options.get(position);
        holder.textOptionTitle.setText(option.getTitle());
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textOptionTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textOptionTitle = itemView.findViewById(R.id.textOptionTitle);
        }
    }
}
