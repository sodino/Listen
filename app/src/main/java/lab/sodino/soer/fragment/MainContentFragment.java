package lab.sodino.soer.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.LinkedList;

import lab.sodino.constant.AppConstant;
import lab.sodino.soer.activity.LyricsActivity;
import lab.sodino.soer.adapter.AudioFileAdapter;
import lab.sodino.soer.activity.MainActivity;
import lab.sodino.soer.R;
import lab.sodino.soer.app.SoerRuntime;
import lab.sodino.soer.handler.LyricsHandler;
import lab.sodino.soer.observer.LyricsObserver;
import lab.sodino.soer.orm.entity.AudioFileInfo;
import lab.util.FLog;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainContentFragment extends BasicFragment<MainActivity> implements RecyclerView.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private AudioFileAdapter mAdapter;
    private TextView txtHint;

    private LyricsHandler lyricsHandler;

    private LyricsObserver lyricsObserver = new LyricsObserver() {
        protected void onScanAudioFileCompleted(boolean isSuccess) {
            LinkedList<AudioFileInfo> list = lyricsHandler.getAudioFileInfos();
            mAdapter.setAudioInfo(list);
            mAdapter.notifyDataSetChanged(); // 全部更换了..
            removeHintView();
            mActivity.stopTitleLoading();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_audio);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(mActivity);
        linearLayoutMgr.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutMgr);
        mRecyclerView.setOnItemClickListener(this);

        mAdapter = new AudioFileAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mActivity.startTitleLoading();

        runtime.addObserver(lyricsObserver);

        lyricsHandler = (LyricsHandler) runtime.getHandler(SoerRuntime.LYRICS_HANDLER);
        lyricsHandler.scanListenFile(AppConstant.PATH.FOLDER_SOER);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        runtime.removeObserver(lyricsObserver);
    }

    public void showHintView() {
        if (txtHint == null) {
            txtHint = new TextView(mActivity);
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
    public void onItemClick(int curPostion, RecyclerView.ViewHolder holder, View view) {
        if (FLog.isDebug()) {
            FLog.d("Listen", "onItemClick() curPostion=" + curPostion);
        }

        AudioFileInfo info = (AudioFileInfo) view.getTag(R.id.wrap_content);
        Intent intent = new Intent(mActivity, LyricsActivity.class);
        intent.putExtra(AppConstant.NAME, info.name);
        startActivity(intent);
    }
}