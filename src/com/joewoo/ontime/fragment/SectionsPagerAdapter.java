package com.joewoo.ontime.fragment;

import java.util.ArrayList;
import java.util.List;
import static com.joewoo.ontime.info.Defines.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> mFragments = new ArrayList<Fragment>();

	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);

		// add fragments
		// 顺序不要错
		mFragments.add(new Frag_FriendsTimeLine());
		mFragments.add(new Frag_Comments());
		mFragments.add(new Frag_Mentions());

		
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return FRAGMENT_COUNT;
	}
	
	public Frag_FriendsTimeLine getFriendsTimeLineFrag(){
		return (Frag_FriendsTimeLine) getItem(FRAG_FRIENDSTIMELINE_POS);
	}

	public Frag_Comments getCommentsFrag(){
		return (Frag_Comments) getItem(FRAG_COMMENTS_POS);
	}
	
	public Frag_Mentions getMentionsFrag(){
		return (Frag_Mentions) getItem(FRAG_MENTIONS_POS);
	}
	
}
