package com.bignerdranch.watashinonikki;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import net.dankito.richtexteditor.android.RichTextEditor;
import net.dankito.richtexteditor.android.toolbar.AllCommandsEditorToolbar;
import net.dankito.richtexteditor.android.toolbar.GroupedCommandsEditorToolbar;
import net.dankito.richtexteditor.callback.GetCurrentHtmlCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;



public class DiaryEntryFragment extends Fragment {

    public static final String TAG = "com.bignerdranch.watashinonikki.DiaryEntryFragment";


    public static final String ARGS_ID = "ID";
    public static final String ARGS_EDITABLE = "Editable";
    public static final String ARGS_MODIFIABLE = "Modifiable";
    public static final String ARGS_DATE = "Date";
    public static final String ARGS_CONTENT = "Content";

    public static final String DIALOG_DATE = "DialogDate";
    public static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 1;
    private static final int REQUEST_TIME = 0;
    private static final int REQUEST_PHOTO = 2;

    private UUID mId;
    private Boolean mEditable;
    private Boolean mModifiable;
    private File mPhotoFile;
    private EventBus mUpdateEvent = EventBus.getDefault();

    private EditText mEditableEntryTitle;
    private EditText mEditableEntryDate;
    private ImageView mEditableEntryImageView;
    private Spinner mWeatherChooser;
    private RichTextEditor mEditor;
    private AllCommandsEditorToolbar mBottomGroupedCommandsToolbar;

    private TextView mEntryTitle;
    private TextView mEntryDate;
    private ImageView mEntryImageView;
    private ImageView mWeatcherIconView;
    private TextView mEntryTextView;

    private DiaryEntry mCurrent;
    private replaceFrgmentCallbacks mCallbacks;

    public interface replaceFrgmentCallbacks{
        public void replaceFragment(DiaryEntryFragment fragment);
    }


    public static DiaryEntryFragment newInstance(UUID id, Boolean editable, Boolean modifiable) {

        Bundle args = new Bundle();

        args.putString(ARGS_ID, id.toString());
        args.putBoolean(ARGS_EDITABLE, editable);
        args.putBoolean(ARGS_MODIFIABLE, modifiable);

        DiaryEntryFragment fragment = new DiaryEntryFragment();

        fragment.setArguments(args);

        return fragment;
    }

    public static DiaryEntryFragment newInstance(Boolean editable, Boolean modifiable) {

        Bundle args = new Bundle();

        args.putBoolean(ARGS_EDITABLE, editable);
        args.putBoolean(ARGS_MODIFIABLE, modifiable);

        DiaryEntryFragment fragment = new DiaryEntryFragment();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        mCallbacks = (replaceFrgmentCallbacks) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String idString = getArguments().getString(ARGS_ID);



        if( idString != null ) mId = UUID.fromString(idString);


        mEditable = getArguments().getBoolean(ARGS_EDITABLE);
        mModifiable = getArguments().getBoolean(ARGS_MODIFIABLE);

        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if( !mEditable )
        inflater.inflate(R.menu.activity_entry_menu, menu);
        else inflater.inflate(R.menu.activity_entry_editable_menu,menu);



    }

    public void callUpdateEvent(){
        updateEvent newUpdateEvent = new updateEvent();
        newUpdateEvent.setFrom(TAG);
        mUpdateEvent.post(newUpdateEvent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DiaryEntryFragment newFragment;

        switch(item.getItemId()){
            case R.id.modify_entry:
                mEditable = true;

                newFragment = DiaryEntryFragment.newInstance(mCurrent.getId(),mEditable, true);

                mCallbacks.replaceFragment(newFragment);

                return true;
            case R.id.delete_entry:

                File photoFile = getPhotoFile( Diary.get(getActivity()).diaryEntryBasedonId(mId));
                if(photoFile != null || photoFile.exists()){
                    photoFile.delete();
                }

                Diary.get(getActivity()).deleteEntry(mId);

                callUpdateEvent();
                getActivity().finish();

                return true;
            case R.id.save_entry:
                mEditable = false;

                if( !mEditor.getCachedHtml().isEmpty() && !mEditor.getCachedHtml().equals("<p>\u200B</p>") ){

                    mCurrent.setContent(mEditor.getCachedHtml());
                }

                if( Diary.get(getActivity()).diaryEntryBasedonId(mCurrent.getId()) == null )    Diary.get(getActivity()).addEntry(mCurrent);
                else  Diary.get(getActivity()).modifyEntry(mCurrent);

                callUpdateEvent();

                newFragment = DiaryEntryFragment.newInstance(mCurrent.getId(),mEditable, false);

                mCallbacks.replaceFragment(newFragment);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }


    public File getPhotoFile(DiaryEntry entry){
        File dir = getActivity().getFilesDir();
        return new File(dir, entry.getPhotoFilename());
    }

    public void updatePhotoView(ImageView image) {

        File photoFile = getPhotoFile(mCurrent);

        Glide.with( getActivity().getApplicationContext() )
                .load(photoFile)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_launcher_background)
                .into( image );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null){
            mId = UUID.fromString(savedInstanceState.getString(ARGS_ID));
            mModifiable = savedInstanceState.getBoolean(ARGS_MODIFIABLE);
            mEditable = savedInstanceState.getBoolean(ARGS_EDITABLE);

            if(!mModifiable && mEditable){
                mCurrent = new DiaryEntry(mId);
            }
            else{
                mCurrent = Diary.get(getActivity()).diaryEntryBasedonId(mId);
            }

            mCurrent.setContent( savedInstanceState.getString(ARGS_CONTENT)) ;
            try{
                mCurrent.setDate( new SimpleDateFormat().parse( savedInstanceState.getString(ARGS_DATE) ));
            }
            catch (Exception e){
                Log.e("DiaryEntyrFRag","Error!!!!!!");
            }

        }
        else{
            if (mId != null)
                mCurrent = Diary.get(getActivity()).diaryEntryBasedonId(mId);
            else {
                mCurrent = new DiaryEntry();
                mId = mCurrent.getId();
            }
        }

        mPhotoFile = getPhotoFile(mCurrent);

        if(mEditable){
            View v = inflater.inflate(R.layout.fragment_diary_entry_editable, container, false);

            mEditableEntryDate = v.findViewById(R.id.entry_date_editable);
            mEditableEntryTitle = v.findViewById(R.id.entry_title_editable);
            mEditableEntryImageView = v.findViewById(R.id.entry_image_editable);

            updatePhotoView(mEditableEntryImageView);


            mWeatherChooser = v.findViewById(R.id.weather_chooser);

            mEditor = v.findViewById(R.id.editor);
            mEditor.setEditorBackgroundColor(Color.DKGRAY);


            mBottomGroupedCommandsToolbar = (AllCommandsEditorToolbar) v.findViewById(R.id.editorToolbar);
            mBottomGroupedCommandsToolbar.setEditor(mEditor);

            mEditor.setEditorFontSize(20);
            mEditor.setPadding((4 * (int) getResources().getDisplayMetrics().density));

            if( mCurrent.getContent() != null)
            mEditor.setHtml(mCurrent.getContent());


            updateDateTime();

            mEditableEntryTitle.setText( mCurrent.getTitle() );


            final PackageManager packageManager = getActivity().getPackageManager();

            final Intent diaryEntryPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


            if( mPhotoFile != null && packageManager.resolveActivity(diaryEntryPhoto,PackageManager.MATCH_DEFAULT_ONLY ) != null){

                mEditableEntryImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.watashinonikki.fileprovider",mPhotoFile);
                        mCurrent.setImagePath(uri.toString());
                        diaryEntryPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                        List<ResolveInfo> cameraActivities = packageManager
                                .queryIntentActivities(diaryEntryPhoto, PackageManager.MATCH_DEFAULT_ONLY);

                        for (ResolveInfo activity : cameraActivities) {
                            getActivity().grantUriPermission(activity.activityInfo.packageName,
                                    uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }
                        startActivityForResult(diaryEntryPhoto, REQUEST_PHOTO);
                    }
                });
            }

            mEditableEntryDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentManager fm = getFragmentManager();
                    TimePickerFragment timeDialog = TimePickerFragment.newInstance(mCurrent.getDate());
                    timeDialog.setTargetFragment(DiaryEntryFragment.this, REQUEST_TIME);
                    timeDialog.show(fm,DIALOG_TIME);

                    DatePickerFragment dateDialog = DatePickerFragment.newInstance(mCurrent.getDate());
                    dateDialog.setTargetFragment(DiaryEntryFragment.this, REQUEST_DATE);
                    dateDialog.show(fm,DIALOG_DATE);
                }
            });

            mEditableEntryTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    mCurrent.setTitle(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.weather_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mWeatherChooser.setAdapter(adapter);
            mWeatherChooser.setSelection( getIndex(mWeatherChooser, mCurrent.getWeather()));

            mWeatherChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mCurrent.setWeather(adapterView.getItemAtPosition(i).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    mWeatherChooser.setSelection( getIndex(mWeatherChooser, mCurrent.getWeather()) );
                }
            });


            return v;
        }
        else{

            View v = inflater.inflate(R.layout.fragment_diary_entry, container, false);

            mEntryTitle = v.findViewById(R.id.entry_title);
            mEntryDate = v.findViewById(R.id.entry_date);
            mEntryImageView = v.findViewById(R.id.entry_image);

            updatePhotoView(mEntryImageView);

            mEntryTextView = v.findViewById(R.id.entry_text);
            mWeatcherIconView = v.findViewById(R.id.entry_weather_icon);

            switch(mCurrent.getWeather()){
                case "Cloudy":
                    mWeatcherIconView.setImageResource(R.drawable.weather_icon_cloudy);
                    break;
                case "Rainy":
                    mWeatcherIconView.setImageResource(R.drawable.weather_icon_rainy);
                    break;
                case "Snowy":
                    mWeatcherIconView.setImageResource(R.drawable.weather_icon_snowy);
                    break;
                case "Stormy":
                    mWeatcherIconView.setImageResource(R.drawable.weather_icon_stormy);
                    break;
                case "Sunny":
                    mWeatcherIconView.setImageResource(R.drawable.weather_icon_sunny);
                    break;
                case "Windy":
                    mWeatcherIconView.setImageResource(R.drawable.weather_icon_windy);
                    break;
                default:
            }

            mEntryTitle.setText(mCurrent.getTitle());
            if(mCurrent.getContent() == null){
                mEntryTextView.setText(mCurrent.getContent());
            }
            else{

                SpannableStringBuilder s = new SpannableStringBuilder(HtmlCompat.fromHtml(mCurrent.getContent(),HtmlCompat.FROM_HTML_MODE_LEGACY));
                mEntryTextView.setText(s);
            }
            SimpleDateFormat sf = new SimpleDateFormat("EEEE,MMM dd yyyy 'at' HH:mm");
            mEntryDate.setText( sf.format(mCurrent.getDate()) );
            return v;

        }

    }

    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE,MMM dd yyyy HH:mm");
        mEditableEntryDate.setText(dateFormat.format(mCurrent.getDate()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if( resultCode != Activity.RESULT_OK) return;

        if(requestCode == REQUEST_TIME){
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCurrent.setDate(time);
            updateDateTime();
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCurrent.setDate(date);
            updateDateTime();
        }

        if(requestCode == REQUEST_PHOTO){

            Uri uri = FileProvider.getUriForFile(getActivity(),"com.bignerdranch.watashinonikki.fileprovider",mPhotoFile );

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            CacheRefreshAsyncTask refreshTask = new CacheRefreshAsyncTask();
            refreshTask.execute();

            Glide.get( getActivity().getApplicationContext() )
                    .clearMemory();

            updatePhotoView(mEditableEntryImageView);

        }
    }

    public class CacheRefreshAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Glide.get( getActivity().getApplicationContext() )
                    .clearDiskCache();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDetach() {
        mCallbacks = null;
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mEditable)
        mCurrent.setContent( !mEditor.getCachedHtml().equals("<p>\u200B</p>") ? mEditor.getCachedHtml() : mCurrent.getContent());

//        Log.v("DiaryEntryFrag", ""+mCurrent.getContent() );

        outState.putString(ARGS_ID, mCurrent.getId().toString());
        outState.putString(ARGS_DATE, mCurrent.getDate().toString());
        outState.putString(ARGS_CONTENT, mCurrent.getContent() );
        outState.putBoolean(ARGS_EDITABLE, mEditable);
        outState.putBoolean(ARGS_MODIFIABLE,mModifiable);
    }

    public Boolean getModifiableState(){
        return mModifiable;
    }

    public Boolean onBackPressed(){

        if( mBottomGroupedCommandsToolbar != null){
            return mBottomGroupedCommandsToolbar.handlesBackButtonPress() == false;
        }
        else{
            return true;
        }
    }
}
