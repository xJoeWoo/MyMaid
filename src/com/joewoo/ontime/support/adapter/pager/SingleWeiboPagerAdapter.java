package com.joewoo.ontime.support.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.joewoo.ontime.ui.singleweibo.SingleWeiboCommentsFragment;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboFragment;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboRepostsFragment;

import java.util.ArrayList;
import java.util.List;


public class SingleWeiboPagerAdapter extends FragmentPagerAdapter {

    public final static int FRAG_SINGLE_WEIBO_REPOSTS_POS = 0;
    public final static int FRAG_SINGLE_WEIBO_POS = 1;
    public final static int FRAG_SINGLE_WEIBO_COMMENTS_POS = 2;

    public final static int FRAGMENT_COUNT = 3;

    private SingleWeiboRepostsFragment singleWeiboRepostsFragment;
    private SingleWeiboFragment singleWeiboFragment;
    private SingleWeiboCommentsFragment singleWeiboCommentsFragment;


    private List<Fragment> mFragments = new ArrayList<Fragment>();

    public SingleWeiboPagerAdapter(FragmentManager fm) {
        super(fm);

        singleWeiboRepostsFragment = new SingleWeiboRepostsFragment();
        singleWeiboFragment = new SingleWeiboFragment();
        singleWeiboCommentsFragment = new SingleWeiboCommentsFragment();

        // add fragments
        // 顺序不要错
        mFragments.add(singleWeiboRepostsFragment);
        mFragments.add(singleWeiboFragment);
        mFragments.add(singleWeiboCommentsFragment);

    }

    @Override
    public Fragment getItem(int arg0) {
        return mFragments.get(arg0);
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    public SingleWeiboFragment getSingleWeiboFrag() {
        return singleWeiboFragment;
    }

    public SingleWeiboCommentsFragment getSingleWeiboCommentsFrag() {
        return singleWeiboCommentsFragment;
    }

    public SingleWeiboRepostsFragment getSingleWeiboRepostsFrag() {
        return singleWeiboRepostsFragment;
    }

}
