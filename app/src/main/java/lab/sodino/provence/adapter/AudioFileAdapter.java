package lab.sodino.provence.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

import lab.sodino.provence.R;
import lab.sodino.provence.union.AudioFileInfo;

/**
 * Created by sodino on 15-7-19.
 */
public class AudioFileAdapter extends RecyclerView.Adapter<AudioFileAdapter.ViewHolder>{
    private LinkedList<AudioFileInfo> listAudios = new LinkedList<AudioFileInfo>();

    public void setAudioInfo(LinkedList<AudioFileInfo> list) {
        if (list == null && list.size() > 0) {
            return;
        }
        listAudios.clear();

        listAudios.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_name, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AudioFileInfo info = listAudios.get(position);
        holder.txtName.setText(info.name);
        holder.itemView.setTag(R.id.wrap_content, info);

        Resources res = holder.itemView.getResources();
        if (position % 2 == 1) {
            holder.itemView.setBackgroundResource(R.drawable.list_item_bg_odd);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.list_item_bg_even);
        }
    }

    @Override
    public int getItemCount() {
        return listAudios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
        }
    }
}
