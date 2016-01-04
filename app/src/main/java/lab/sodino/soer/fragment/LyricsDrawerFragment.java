package lab.sodino.soer.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import lab.sodino.constant.AppConstant;
import lab.sodino.soer.R;
import lab.sodino.soer.activity.LyricsActivity;
import lab.sodino.soer.listener.PlayModeListener;
import lab.util.FLog;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class LyricsDrawerFragment extends BasicFragment<LyricsActivity> implements View.OnClickListener {
    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;

    /**整曲循环*/
    private CheckBox cbLoopAudio;
    /**单句循环*/
    private CheckBox cbLoopSentence;
    /**单句5次*/
    private CheckBox cbLoop5;

    private TextView txtMergeSentence;

    private int mode = AppConstant.Player.MODE_LOOP_AUDIO;
    private long count = 0;

    private PlayModeListener playModeListener;
    private boolean isModeChanged = false;

    public LyricsDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lyrics_drawer_layout, container, false);

        cbLoopAudio = (CheckBox) v.findViewById(R.id.cbLoopAudio);
        cbLoopSentence = (CheckBox) v.findViewById(R.id.cbLoopSentence);
        cbLoop5 = (CheckBox) v.findViewById(R.id.cbLoop5);

        cbLoopAudio.setOnClickListener(this);
        cbLoopSentence.setOnClickListener(this);
        cbLoop5.setOnClickListener(this);

        txtMergeSentence = (TextView) v.findViewById(R.id.txtMergeSentence);
        txtMergeSentence.setOnClickListener(this);

        return v;
    }

    public void onResume() {
        super.onResume();
        changeCheckBox(mode, count);
    }

    private void changeCheckBox(int mode, long count) {
        if (FLog.isDebug()) {
            FLog.d("LyricsDrawer", "changeCheckBox() mode=" + mode + " count=" + count);
        }
        cbLoopAudio.setChecked((mode == AppConstant.Player.MODE_LOOP_AUDIO));
        cbLoopSentence.setChecked((mode == AppConstant.Player.MODE_LOOP_SENTENCE) && (count == Long.MAX_VALUE));
        cbLoop5.setChecked((mode == AppConstant.Player.MODE_LOOP_SENTENCE) && (count == AppConstant.Player.LOOP_5));
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     */
    public void setUp() {
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mFragmentContainerView = mDrawerLayout.findViewById(R.id.navigation_drawer);

        // set a custom shadow that overlays the menu_main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        // set up the drawer's list view with items and click listener

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (FLog.isDebug()) {
                    FLog.d("LyricsDrawer", "onDrawerClosed() isModeChanged=" + isModeChanged + " new=" + mode + " count=" + count);
                }
                if (isModeChanged && playModeListener != null) {
                    playModeListener.onPlayModeSelected(mode, count);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void syncToggleState() {
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    public void openDrawer(int mode, long count) {
        if (FLog.isDebug()) {
            FLog.d("Listen", "openDrawer() mode=" + mode + " count=" + count);
        }
        this.mode = mode;
        this.count = count;
        mDrawerLayout.openDrawer(mFragmentContainerView);
        changeCheckBox(this.mode, this.count);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cbLoopAudio:{
                isModeChanged = true;
                mode = AppConstant.Player.MODE_LOOP_AUDIO;
                count = 0;
                changeCheckBox(mode, count);
            }
            break;
            case R.id.cbLoopSentence:{
                isModeChanged = true;
                mode = AppConstant.Player.MODE_LOOP_SENTENCE;
                count = Long.MAX_VALUE;
                changeCheckBox(mode, count);
            }
            break;
            case R.id.cbLoop5:{
                isModeChanged = true;
                mode = AppConstant.Player.MODE_LOOP_SENTENCE;
                count = AppConstant.Player.LOOP_5;
                changeCheckBox(mode, count);
            }
            break;
            case R.id.txtMergeSentence:{

            }
            break;
        }
    }

    public void setPlayModeListener(PlayModeListener listener) {
        playModeListener = listener;
    }
}
