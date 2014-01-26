package com.joewoo.ontime.ui.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.dialog.AcquireCountDialog;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.setting.MyMaidSettingHelper;

/**
 * Created by Joe on 14-1-24.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private Preference networkAcquireCountFriendsTimeline;
    private Preference networkAcquireCountMentions;
    private Preference networkAcquireCountCommentsToMe;
    private Preference networkAcquireCountCommentsMentions;
    private SwitchPreference uiDarkMode;
    private Preference otherCheckUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        findViews();
        setListeners();
        setInfo();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        String key = preference.getKey();

        if (key.equals(MyMaidSettingHelper.KEY_UI_DARK_MODE)) {

        } else if (key.equals(MyMaidSettingHelper.KEY_OTHER_CHECK_UPDATE)) {

        } else {
            AcquireCountDialog.show(getActivity(), key);
        }

        return true;
    }

    private void findViews() {
        networkAcquireCountFriendsTimeline = findPreference(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_FRIENDS_TIMELINE);
        networkAcquireCountMentions = findPreference(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_MENTIONS);
        networkAcquireCountCommentsMentions = findPreference(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_MENTIONS);
        networkAcquireCountCommentsToMe = findPreference(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_TO_ME);
        uiDarkMode = (SwitchPreference) findPreference(MyMaidSettingHelper.KEY_UI_DARK_MODE);
        otherCheckUpdate = findPreference(MyMaidSettingHelper.KEY_OTHER_CHECK_UPDATE);
    }

    private void setListeners() {
        networkAcquireCountCommentsToMe.setOnPreferenceClickListener(this);
        networkAcquireCountCommentsMentions.setOnPreferenceClickListener(this);
        networkAcquireCountFriendsTimeline.setOnPreferenceClickListener(this);
        networkAcquireCountMentions.setOnPreferenceClickListener(this);
        uiDarkMode.setOnPreferenceClickListener(this);
        otherCheckUpdate.setOnPreferenceClickListener(this);
    }
    
    private void setInfo() {
        networkAcquireCountMentions.setSummary(AcquireCount.MENTIONS_COUNT);
        networkAcquireCountFriendsTimeline.setSummary(AcquireCount.FRIENDS_TIMELINE_COUNT);
        networkAcquireCountCommentsMentions.setSummary(AcquireCount.COMMENTS_MENTIONS_COUNT);
        networkAcquireCountCommentsToMe.setSummary(AcquireCount.COMMENTS_TO_ME_COUNT);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setInfo();
        return true;
    }
}
