package com.joewoo.ontime.support.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.joewoo.ontime.ui.maintimeline.AboutFragment;
import com.joewoo.ontime.ui.maintimeline.CommentsToMeFragment;
import com.joewoo.ontime.ui.maintimeline.FriendsTimeLineFragment;
import com.joewoo.ontime.ui.maintimeline.MentionsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public final static int FRAG_MENTIONS_POS = 0;
    public final static int FRAG_FRIENDSTIMELINE_POS = 1;
    public final static int FRAG_COMMENTS_POS = 2;
    public final static int FRAG_SETTINGS_POS = 3;

    public final static int FRAGMENT_COUNT = 4;

    private List<Fragment> mFragments = new ArrayList<>();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        // add fragments
        // 顺序不要错
        mFragments.add(new MentionsFragment());
        mFragments.add(new FriendsTimeLineFragment());
        mFragments.add(new CommentsToMeFragment());
        mFragments.add(new AboutFragment());
    }

    @Override
    public Fragment getItem(int arg0) {
        return mFragments.get(arg0);
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    public FriendsTimeLineFragment getFriendsTimeLineFrag() {
        return (FriendsTimeLineFragment) getItem(FRAG_FRIENDSTIMELINE_POS);
    }

    public CommentsToMeFragment getCommentsFrag() {
        return (CommentsToMeFragment) getItem(FRAG_COMMENTS_POS);
    }

    public MentionsFragment getMentionsFrag() {
        return (MentionsFragment) getItem(FRAG_MENTIONS_POS);
    }

}
