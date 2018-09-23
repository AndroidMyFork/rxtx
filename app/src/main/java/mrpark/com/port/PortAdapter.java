package mrpark.com.port;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PortAdapter extends RecyclerView.Adapter<PortAdapter.PortHolder> {
    private List<PortItem> mList;
    private EventPortClick event;

    public PortAdapter(List<PortItem> mList, EventPortClick event) {
        this.mList = mList;
        this.event = event;
    }

    public interface EventPortClick {
        void OnClickPort(PortItem portItem);
    }

    @NonNull
    @Override
    public PortAdapter.PortHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_port, parent, false);
        return new PortHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PortAdapter.PortHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class PortHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPort;

        PortHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPort = itemView.findViewById(R.id.tv_port);
        }
        void bind(final PortItem portItem){
            tvPort.setText(portItem.getPort());
            tvName.setText(portItem.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    event.OnClickPort(portItem);
                }
            });
        }
    }
}
