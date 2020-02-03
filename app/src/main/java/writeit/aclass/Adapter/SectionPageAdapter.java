package writeit.aclass.Adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import writeit.aclass.Fragment.ChatFragment;
import writeit.aclass.Fragment.FriendFragment;
import writeit.aclass.Fragment.RequestFragment;

/**
 * Created by Gung Rama on 11/23/2017.
 */


public class SectionPageAdapter extends FragmentPagerAdapter{

    Drawable icon;

    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;

            case 2:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 1:
                FriendFragment friendFragment = new FriendFragment();
                return friendFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int potition){
        switch (potition){
            case 0:
                return "Friend";
            case 2:
                return "Setting";
            case 1:
                return "Chat";
                default:
                    return null;
        }
    }
}
