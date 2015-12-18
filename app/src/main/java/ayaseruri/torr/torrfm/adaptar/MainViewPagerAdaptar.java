package ayaseruri.torr.torrfm.adaptar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ayaseruri on 15/12/17.
 */
public class MainViewPagerAdaptar extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainViewPagerAdaptar(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
