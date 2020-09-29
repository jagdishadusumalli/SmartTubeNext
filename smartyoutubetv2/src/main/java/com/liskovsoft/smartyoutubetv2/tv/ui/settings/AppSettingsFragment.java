package com.liskovsoft.smartyoutubetv2.tv.ui.settings;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.preference.LeanbackPreferenceDialogFragment;
import androidx.leanback.preference.LeanbackPreferenceFragment;
import androidx.leanback.preference.LeanbackSettingsFragment;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.AppSettingsPresenter;
import com.liskovsoft.smartyoutubetv2.common.app.presenters.AppSettingsPresenter.SettingsCategory;
import com.liskovsoft.smartyoutubetv2.common.app.views.AppSettingsView;
import com.liskovsoft.smartyoutubetv2.tv.ui.settings.strings.StringListPreference;
import com.liskovsoft.smartyoutubetv2.tv.ui.settings.strings.StringListPreferenceDialogFragment;

import java.util.List;

public class AppSettingsFragment extends LeanbackSettingsFragment
        implements DialogPreference.TargetFragment, AppSettingsView {
    private AppPreferenceFragment mPreferenceFragment;
    private AppSettingsPresenter mSettingsPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsPresenter = AppSettingsPresenter.instance(getActivity());
        mSettingsPresenter.register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSettingsPresenter.onInitDone(); // should run before onActivityCreated
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSettingsPresenter.unregister(this);
    }

    @Override
    public void onPreferenceStartInitialScreen() {
        mPreferenceFragment = buildPreferenceFragment();
        startPreferenceFragment(mPreferenceFragment);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment,
        Preference preference) {
        return false;
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragment preferenceFragment,
        PreferenceScreen preferenceScreen) {
        PreferenceFragment frag = buildPreferenceFragment();
        startPreferenceFragment(frag);
        return true;
    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return mPreferenceFragment.findPreference(charSequence);
    }

    private AppPreferenceFragment buildPreferenceFragment() {
        return new AppPreferenceFragment();
    }

    @Override
    public void addCategories(List<SettingsCategory> categories) {
        mPreferenceFragment.addCategories(categories);
    }

    @Override
    public void setTitle(String title) {
        mPreferenceFragment.setTitle(title);
    }

    @Override
    public boolean onPreferenceDisplayDialog(@NonNull PreferenceFragment caller, Preference pref) {
        if (pref instanceof StringListPreference) {
            StringListPreference listPreference = (StringListPreference) pref;
            LeanbackPreferenceDialogFragment f = StringListPreferenceDialogFragment.newInstanceStringList(listPreference.getKey());
            f.setTargetFragment(caller, 0);
            startPreferenceFragment(f);

            return true;
        }

        return super.onPreferenceDisplayDialog(caller, pref);
    }

    public static class AppPreferenceFragment extends LeanbackPreferenceFragment {
        private List<SettingsCategory> mCategories;
        private Context mStyledContext;
        private AppSettingsFragmentHelper mManager;
        private String mTitle;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            mStyledContext = (Context) Helpers.getField(this, "mStyledContext");
            mManager = new AppSettingsFragmentHelper(mStyledContext);

            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(mStyledContext);
            screen.setTitle(mTitle);

            for (SettingsCategory category : mCategories) {
                if (category.items != null) {
                    screen.addPreference(mManager.createPreference(category));
                }
            }

            setPreferenceScreen(screen);

            // auto expand first preference
            if (mCategories.size() == 1) {
                getPreferenceManager().showDialog(screen.getPreference(0));
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            return super.onPreferenceTreeClick(preference);
        }

        public void addCategories(List<SettingsCategory> categories) {
            mCategories = categories;
        }

        public void setTitle(String title) {
            mTitle = title;
        }
    }
}