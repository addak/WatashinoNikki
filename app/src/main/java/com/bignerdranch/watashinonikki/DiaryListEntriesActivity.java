package com.bignerdranch.watashinonikki;

import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DiaryListEntriesActivity extends SingleFragmentActivity implements DiaryListEntriesFragment.SwipeCallbacks{



    @Override
    protected DiaryListEntriesFragment createFragment() {
        return DiaryListEntriesFragment.newInstance();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_diary;
    }

    @Override
    protected int getContainer() {
        return R.id.fragment_container;
    }

    @Override
    public void OnEntrySwipeRight(DiaryEntry entry) {

        DiaryListEntriesFragment listFragment = (DiaryListEntriesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.deleteEntry( entry );


    }

    @Override
    public void OnEntrySwipeLeft(DiaryEntry entry) {

        DiaryListEntriesFragment listFragment = (DiaryListEntriesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.modifyEntry(entry, true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void updateUI(){
        DiaryListEntriesFragment listFragment = (DiaryListEntriesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

}
