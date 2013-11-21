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

    public final static int FRAG_SINGLEWEIBOREPOSTS_POS = 0;
    public final static int FRAG_SINGLEWEIBO_POS = 1;
    public final static int FRAG_SINGLEWEIBOCOMMENTS_POS = 2;

    public final static int FRAGMENT_COUNT = 3;


	private List<Fragment> mFragments = new ArrayList<Fragment>();

	public SingleWeiboPagerAdapter(FragmentManager fm) {
		super(fm);

		// add fragments
		// 顺序不要错
        mFragments.add(new SingleWeiboRepostsFragment());
		mFragments.add(new SingleWeiboFragment());
		mFragments.add(new SingleWeiboCommentsFragment());
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return FRAGMENT_COUNT;
	}
	
	public SingleWeiboFragment getSingleWeiboFrag(){
		return (SingleWeiboFragment) getItem(FRAG_SINGLEWEIBO_POS);
	}

	public SingleWeiboCommentsFragment getSingleWeiboCommentsFrag(){
		return (SingleWeiboCommentsFragment) getItem(FRAG_SINGLEWEIBOCOMMENTS_POS);
	}
	
	public SingleWeiboRepostsFragment getSingleWeiboRepostsFrag(){
		return (SingleWeiboRepostsFragment) getItem(FRAG_SINGLEWEIBOREPOSTS_POS);
	}
	
}
