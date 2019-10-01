package com.example.coderstext;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter
{

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
       switch (i) {
           case 0:
               Chats chats = new Chats();
               return chats;
           case 1:
               Groups groups = new Groups();
               return groups;
           case 2:
               Contacts contacts = new Contacts();
               return contacts;

           case 3:
               RequestFragment requestFragment = new RequestFragment();
               return requestFragment;

               default:
                   return null;
       }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            case 3:
                return "Requests";

            default:
                return null;
        }
    }
}
