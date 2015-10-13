package lab.sodino.provence.activity;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import lab.sodino.provence.R;
import lab.sodino.util.TotalExceptionHandler;
import lab.ui.TitlebarActivity;
import lab.sodino.provence.fragment.AudioFileFragment;
import lab.sodino.provence.fragment.BasicFragment;
import lab.sodino.provence.fragment.MainDrawerFragment;
import lab.util.FLog;
import lab.util.FileUtil;


public class MainActivity extends TitlebarActivity
        implements MainDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {

    private BasicFragment fragAudio;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private MainDrawerFragment mDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerFragment = (MainDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        imgTitlebarLeft.setImageResource(R.drawable.ic_drawer);
        imgTitlebarLeft.setOnClickListener(this);

        // Set up the drawer.
        mDrawerFragment.setUp();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        BasicFragment frag = null;
        switch(position) {
            case 0:
                if (fragAudio == null) {
                    fragAudio = new AudioFileFragment();
                }

                frag = fragAudio;
                break;
        }
        if (frag != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, frag).commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerFragment.syncToggleState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebarLeftImg:
                /////////////////////////////////////////////////////////////////////
                FLog.d("Test", "onClick() test");

                int i = 0;

                if (i == 0) {
                    return;
                }
                /////////////////////////////////////////////////////////////////////

                if (FLog.isDebug()) {
                    FLog.d("Listen", "onClick()..");
                }
                if (mDrawerFragment.isDrawerOpen()) {
                    mDrawerFragment.closeDrawer();
                } else {
                    mDrawerFragment.openDrawer();
                }
                break;
        }
    }

}
