package com.joewoo.ontime.singleWeibo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
        mFragments.add(new Frag_SingleWeibo_Reposts());
		mFragments.add(new Frag_SingleWeibo());
		mFragments.add(new Frag_SingleWeibo_Comments());
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return FRAGMENT_COUNT;
	}
	
	public Frag_SingleWeibo getSingleWeiboFrag(){
		return (Frag_SingleWeibo) getItem(FRAG_SINGLEWEIBO_POS);
	}

	public Frag_SingleWeibo_Comments getSingleWeiboCommentsFrag(){
		return (Frag_SingleWeibo_Comments) getItem(FRAG_SINGLEWEIBOCOMMENTS_POS);
	}
	
	public Frag_SingleWeibo_Reposts getSingleWeiboRepostsFrag(){
		return (Frag_SingleWeibo_Reposts) getItem(FRAG_SINGLEWEIBOREPOSTS_POS);
	}
	
}
