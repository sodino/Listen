package lab.sodino.soer.adapter;

import android.content.res.Resources;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

import lab.sodino.handler.Callback;
import lab.sodino.soer.R;
import lab.sodino.soer.orm.entity.Lyrics;
import lab.sodino.soer.thread.ThreadPool;
import lab.sodino.soer.union.Pair;

/**
 * Created by sodino on 15-7-24.
 */
public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.ViewHolder> {

    public static final int MSG_CHANGE_PLAYING_ITEM = 1;
    private LinkedList<Lyrics> listLyrics = new LinkedList<Lyrics>();

    private int playingPosition = -1;
    /**检查播放状态的Item的间隔时间。避免频繁检查刷新。*/
    private int checkPlayingInterval = -1;

    private Callback callback = new Callback () {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case MSG_CHANGE_PLAYING_ITEM:{
                    int oldPosition = msg.arg2;
                    notifyItemChanged(oldPosition);
                    notifyItemChanged(playingPosition);
                }
                break;
            }
            return true;
        }
    };


    public void setLyricsList(LinkedList<Lyrics> list) {
        if (list != null) {
            listLyrics.clear();
            listLyrics.addAll(list);
        }
    }

    public Lyrics getLyrics(int index) {
        if (index < 0 || index >= listLyrics.size() ) {
            return null;
        }
        Lyrics ly = listLyrics.get(index);
        return ly;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lyrics, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        if (FLog.isDebug()) {
//            FLog.d("LyricsAdapter", "onBindViewHolder() pos=" + position);
//        }
        Resources res = holder.txtLyrics.getResources();

        Lyrics lyrics = listLyrics.get(position);
        holder.txtLyrics.setText(lyrics.setence);
        holder.itemView.setTag(R.id.wrap_content, lyrics);
        if (position == playingPosition) {
            holder.txtLyrics.setTextColor(res.getColor(R.color.color_playing));
        } else {
            holder.txtLyrics.setTextColor(res.getColor(android.R.color.black));
        }
        if (position % 2 == 1) {
            // 奇数
            holder.itemView.setBackgroundResource(R.drawable.list_item_bg_odd);
        } else {
            // 偶数
            holder.itemView.setBackgroundResource(R.drawable.list_item_bg_even);
        }
    }

    @Override
    public int getItemCount() {
        return listLyrics.size();
    }

    public Lyrics checkPlayingItem(long curTime, Pair<Integer, Integer> pair) {
        Lyrics current = null;
        int oldPosition = playingPosition;

        int size = listLyrics.size();
        for (int i = 0; i < size; i ++) {
            Lyrics ly = listLyrics.get(i);
            if (curTime >= ly.timeStart && curTime < ly.timeStart + ly.duration) {
                playingPosition = i;
                current = ly;
                if (oldPosition != playingPosition){
                    Message msg = Message.obtain();
                    msg.what = MSG_CHANGE_PLAYING_ITEM;
                    msg.arg1 = playingPosition;
                    msg.arg2 = oldPosition;
                    ThreadPool.getUIHandler().sendMessage(msg, callback);
                }
                break;
            }
        }

        pair.first = oldPosition;
        pair.second = playingPosition;

        return current;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtLyrics;

        public ViewHolder(View itemView) {
            super(itemView);
            txtLyrics = (TextView) itemView.findViewById(R.id.txtLyrics);
        }
    }
}
