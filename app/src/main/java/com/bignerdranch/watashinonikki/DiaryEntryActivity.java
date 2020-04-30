package com.bignerdranch.watashinonikki;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class DiaryEntryActivity extends SingleFragmentActivity implements DiaryEntryFragment.replaceFrgmentCallbacks{

    public static final String EXTRA_ENTRYID = "Entry Id";
    public static final String EXTRA_EDITABLE = "Editable";
    public static final String EXTRA_MODIFIABLE = "Modifiable";

    private String mIdString;
    private UUID mId;
    private Boolean mEditable;
    private Boolean mModifiable;



    public static Intent DiaryEntryIntent(Context context, UUID id, Boolean editable, Boolean modifiable) {

        Intent intent = new Intent(context, DiaryEntryActivity.class);

        intent.putExtra(EXTRA_ENTRYID, id.toString());
        intent.putExtra(EXTRA_EDITABLE, editable);
        intent.putExtra(EXTRA_MODIFIABLE, modifiable);

        return intent;
    }

    public static Intent DiaryEntryIntent(Context context, Boolean editable, Boolean modifiable) {

        Intent intent = new Intent(context, DiaryEntryActivity.class);

        intent.putExtra(EXTRA_EDITABLE, editable);
        intent.putExtra(EXTRA_MODIFIABLE, modifiable);

        return intent;
    }

    @Override
    protected Fragment createFragment() {

        mEditable = getIntent().getBooleanExtra(EXTRA_EDITABLE, false);
        mModifiable = getIntent().getBooleanExtra(EXTRA_MODIFIABLE, false);

        mIdString =getIntent().getStringExtra(EXTRA_ENTRYID);

        if(mIdString != null){
            mId = UUID.fromString(mIdString);
            return DiaryEntryFragment.newInstance(mId, mEditable, mModifiable);
        }
        else{
            return DiaryEntryFragment.newInstance(mEditable, mModifiable);
        }



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
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        DiaryEntryFragment fragment =(DiaryEntryFragment) fm.findFragmentById(R.id.fragment_container);
        boolean result = fragment.onBackPressed();
        boolean modifiable = fragment.getModifiableState();

        if(result && !modifiable){
            super.onBackPressed();
        }
        else{
            DiaryEntryFragment newFragment = DiaryEntryFragment.newInstance(mId, false, false);
            fm.beginTransaction()
                    .replace(R.id.fragment_container, newFragment)
                    .commit();
        }

    }

    @Override
    public void replaceFragment(DiaryEntryFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
