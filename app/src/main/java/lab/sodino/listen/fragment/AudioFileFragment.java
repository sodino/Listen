package lab.sodino.listen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

import lab.sodino.constant.AppConstant;
import lab.sodino.listen.activity.LyricsActivity;
import lab.sodino.listen.adapter.AudioFileAdapter;
import lab.sodino.listen.activity.MainActivity;
import lab.sodino.listen.R;
import lab.sodino.listen.info.AudioFileInfo;
import lab.sodino.listen.thread.ThreadPool;
import lab.util.FLog;

/**
 * A placeholder fragment containing a simple view.
 */
public class AudioFileFragment extends BasicFragment implements Handler.Callback, FilenameFilter, RecyclerView.OnItemClickListener {
    public static final int MSG_SCAN_LISTEN_FOLDER = 1;
    private static final int MSG_SCAN_FOLDER_OK = 2;

    private RecyclerView mRecyclerView;
    private AudioFileAdapter mAdapter;
    private TextView txtHint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_audio_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_audio);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(getActivity());
        linearLayoutMgr.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutMgr);
        mRecyclerView.setOnItemClickListener(this);

        mAdapter = new AudioFileAdapter();
        mRecyclerView.setAdapter(mAdapter);

        ((MainActivity)getActivity()).startTitleLoading();
        ThreadPool.getFileHandler().sendEmptyMessage(MSG_SCAN_LISTEN_FOLDER, this);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        showHintView();
    }

    public void showHintView() {
        if (txtHint == null) {
            txtHint = new TextView(getActivity());
            txtHint.setText("Scanning folder...\n \"/sdcard/listen/\" ...");
            txtHint.setGravity(Gravity.CENTER);

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            txtHint.setLayoutParams(lp);
            txtHint.setVisibility(View.INVISIBLE);
        }

        FrameLayout parentLayout = (FrameLayout) mRecyclerView.getParent();
        if (txtHint.getVisibility() != View.VISIBLE) {
            parentLayout.addView(txtHint);
            txtHint.setVisibility(View.VISIBLE);
        }
    }

    public void removeHintView() {
        FrameLayout parentLayout = (FrameLayout) mRecyclerView.getParent();
        parentLayout.removeView(txtHint);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MSG_SCAN_LISTEN_FOLDER:
                scanListenFolder();
                break;
            case MSG_SCAN_FOLDER_OK:{
                LinkedList<AudioFileInfo> list = (LinkedList<AudioFileInfo>) msg.obj;
                mAdapter.setAudioInfo(list);
                mAdapter.notifyDataSetChanged(); // 全部更换了..
                removeHintView();
                ((MainActivity)getActivity()).stopTitleLoading();
            }
                break;
        }
        return true;
    }

    private void scanListenFolder() {
        File folder = new File(AppConstant.LISTEN_FOLDER);
        if (folder.isDirectory() == false) {
            folder.delete();
            folder.mkdirs();
        } else if (folder.exists() == false) {
            folder.mkdirs();
        }

        String[] arrFile = folder.list(this);
        if (FLog.isDebug()) {
            FLog.d("Listen", "scanListenFolder() arrFile.len=" + arrFile.length);
        }

        LinkedList<AudioFileInfo> list = AudioFileInfo.parseListenFile(arrFile);


        Message msg = Message.obtain();
        msg.what = MSG_SCAN_FOLDER_OK;
        msg.obj = list;
        getUIHandler().sendMessage(msg, this);
    }

    public boolean accept(File dir, String filename) {
        if (FLog.isDebug()) {
            FLog.d("Listen", "accept() file=" + filename);
        }
        filename = filename.toLowerCase();
        if (filename != null
                && (filename.endsWith("." + AppConstant.LRC)
                || filename.endsWith("." + AppConstant.BILINGUAL)
                || filename.endsWith("." + AppConstant.MP3))
                ) {
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(int curPostion, RecyclerView.ViewHolder holder, View view) {
        if (FLog.isDebug()) {
            FLog.d("Listen", "onItemClick() curPostion=" + curPostion);
        }

        AudioFileInfo info = (AudioFileInfo) view.getTag(R.id.wrap_content);
        Intent intent = new Intent(getActivity(), LyricsActivity.class);
        intent.putExtra(AppConstant.KEY_INFO, info);
        startActivity(intent);
    }
}