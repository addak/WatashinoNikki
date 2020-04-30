package com.bignerdranch.watashinonikki;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class DiaryListEntriesFragment extends Fragment {

    private View mRecyclerView;

    private EventBus mEventBus;

    private RecyclerView mDiaryEntriesRecyclerView;
    private DiaryEntryAdapter mAdapter;
    private SwipeCallbacks mDeleteCallbacks;

    public static DiaryListEntriesFragment newInstance() {

        DiaryListEntriesFragment fragment = new DiaryListEntriesFragment();

        return fragment;
    }

    public interface SwipeCallbacks {
        void OnEntrySwipeRight(DiaryEntry entry);
        void OnEntrySwipeLeft(DiaryEntry entry);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mDeleteCallbacks = (SwipeCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mDeleteCallbacks = null;
    }

    private void setRecyclerTouch(){

        ItemTouchHelper recyclerItemDelete = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                DiaryEntry entry = mAdapter.mDiaryEntries.get(pos);
                mDeleteCallbacks.OnEntrySwipeRight(entry);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemview = viewHolder.itemView;

                Drawable deleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_entry_delete);

                float IconHeight = deleteIcon.getIntrinsicHeight();
                float IconWidth = deleteIcon.getIntrinsicWidth();

                float itemHeight = itemview.getBottom() - itemview.getTop();

                if (actionState == ACTION_STATE_SWIPE) {

                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

                    RectF layout = new RectF(itemview.getLeft(), itemview.getTop(), itemview.getLeft() + dX, itemview.getBottom());

                    paint.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));


                    c.drawRect(layout, paint);

                    int deleteIconTop = (int) (itemview.getTop() + (itemHeight - IconHeight) / 2);
                    int deleteIconBottom = (int) (deleteIconTop + IconHeight);
                    int deleteIconMargin = (int) ((itemHeight - IconHeight) / 2);
                    int deleteIconLeft = (int)(itemview.getLeft() + deleteIconMargin);
                    int deleteIconRight = (int)(itemview.getLeft() + deleteIconMargin + IconWidth);

                    deleteIcon.setBounds(deleteIconLeft,deleteIconTop,deleteIconRight,deleteIconBottom);
                    deleteIcon.draw(c);

                    getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);

                }
            }
        });

        ItemTouchHelper recyclerItemModify = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                DiaryEntry entry = mAdapter.mDiaryEntries.get(pos);
                mDeleteCallbacks.OnEntrySwipeLeft(entry);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemview = viewHolder.itemView;

                Drawable modifyIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_entry_modify);

                float IconHeight = modifyIcon.getIntrinsicHeight();
                float IconWidth = modifyIcon.getIntrinsicWidth();

                float itemHeight = itemview.getBottom() - itemview.getTop();

                if (actionState == ACTION_STATE_SWIPE) {

                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

                    RectF layout = new RectF(itemview.getRight() + dX, itemview.getTop(), itemview.getRight(), itemview.getBottom());

                    paint.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                    c.drawRect(layout, paint);

                    int modifyIconTop = (int) (itemview.getTop() + (itemHeight - IconHeight) / 2);
                    int modifyIconBottom = (int) (modifyIconTop + IconHeight);
                    int modifyIconMargin = (int) ((itemHeight - IconHeight) / 2);
                    int modifyIconRight = (int)(itemview.getRight() - modifyIconMargin);
                    int modifyIconLeft = (int)(itemview.getRight() - modifyIconMargin - IconWidth);


                    modifyIcon.setBounds(modifyIconLeft,modifyIconTop,modifyIconRight,modifyIconBottom);
                    modifyIcon.draw(c);

                    getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);

                }
            }
        });

        recyclerItemDelete.attachToRecyclerView(mDiaryEntriesRecyclerView);
        recyclerItemModify.attachToRecyclerView(mDiaryEntriesRecyclerView);

    }

    public File getPhotoFile(DiaryEntry entry){
        File dir = getActivity().getFilesDir();
        return new File(dir, entry.getPhotoFilename());
    }

    private class DiaryEntryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private DiaryEntry mDiaryEntry;
        private ImageView mThumbnailImage;
        private TextView mTitle;
        private TextView mDate;
        private ImageView mWeather;

        public DiaryEntryHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.diary_list_entry_item, parent, false));

            mThumbnailImage = itemView.findViewById(R.id.list_entry_image);
            mTitle = itemView.findViewById(R.id.list_entry_title);
            mDate = itemView.findViewById(R.id.list_entry_date);
            mWeather = itemView.findViewById(R.id.list_weather_icon);

            itemView.setOnClickListener(this);
        }

        private void bind(DiaryEntry diaryentry){

            mDiaryEntry = diaryentry;

            mTitle.setText( diaryentry.getTitle() );

            File photoFile = getPhotoFile(mDiaryEntry);


            Glide.with(getActivity().getApplicationContext())
                    .load(photoFile)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into( mThumbnailImage);


            SimpleDateFormat sf = new SimpleDateFormat("EEEE,MMM dd yyyy 'at' HH:mm");
            mDate.setText( sf.format(diaryentry.getDate()) );

            switch(mDiaryEntry.getWeather()){
                case "Cloudy":
                    mWeather.setImageResource(R.drawable.weather_icon_cloudy);
                    break;
                case "Rainy":
                    mWeather.setImageResource(R.drawable.weather_icon_rainy);
                    break;
                case "Snowy":
                    mWeather.setImageResource(R.drawable.weather_icon_snowy);
                    break;
                case "Stormy":
                    mWeather.setImageResource(R.drawable.weather_icon_stormy);
                    break;
                case "Sunny":
                    mWeather.setImageResource(R.drawable.weather_icon_sunny);
                    break;
                case "Windy":
                    mWeather.setImageResource(R.drawable.weather_icon_windy);
                    break;
                default:
            }
        }

        @Override
        public void onClick(View view) {

            Intent intent = DiaryEntryActivity.DiaryEntryIntent(getActivity(), mDiaryEntry.getId(), false, false);
            startActivity(intent);

        }
    }

    private class DiaryEntryAdapter extends RecyclerView.Adapter<DiaryEntryHolder>{

        private List<DiaryEntry> mDiaryEntries;

        public DiaryEntryAdapter(List<DiaryEntry> diaryentries) {
            mDiaryEntries = diaryentries;
        }

        @NonNull
        @Override
        public DiaryEntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new DiaryEntryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DiaryEntryHolder holder, int position) {

            holder.bind( mDiaryEntries.get( holder.getAdapterPosition() ));
        }

        @Override
        public int getItemCount() {
            return mDiaryEntries.size();
        }

        public void setDiaryEntries(List<DiaryEntry> entries){ mDiaryEntries = entries; }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = EventBus.getDefault();

        mEventBus.register(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.activity_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch( item.getItemId() ){
            case R.id.additem :
                createEntry(true, false);
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRecyclerView = inflater.inflate(R.layout.fragment_diary_list, container, false);

        mDiaryEntriesRecyclerView = mRecyclerView.findViewById(R.id.diary_list_recyclerview);

        mDiaryEntriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setRecyclerTouch();

        return mRecyclerView;
    }

    public void updateUI(){

        Diary diary = Diary.get(getActivity());
        List<DiaryEntry> dairyEntries = diary.getEntries();

        if(mAdapter == null){
            mAdapter= new DiaryEntryAdapter(dairyEntries);
            mDiaryEntriesRecyclerView.setAdapter( mAdapter);
        } //If an adapter exists but changes were made to the contents referred by the adapter
        else {
            //The below line was added as unlike an object in memory which was linked to mCrime
            // Here we need to manual change the data state of the Adapter's data source so that notifyDataSetChanged works
            mAdapter.setDiaryEntries(dairyEntries);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        updateUI();
        super.onResume();
    }

    public void deleteEntry(DiaryEntry entry){
        File photoFile = getPhotoFile(entry);
        if(photoFile != null || photoFile.exists()){
            photoFile.delete();
        }

        Diary.get(getActivity()).deleteEntry(entry.getId());

        updateUI();
    }

    public void createEntry(Boolean editable, Boolean modifiable){
        Intent intent = DiaryEntryActivity.DiaryEntryIntent(getActivity(), editable, modifiable);
        startActivity(intent);
    }

    public void modifyEntry(DiaryEntry entry,Boolean editable, Boolean modifiable){
        Intent intent = DiaryEntryActivity.DiaryEntryIntent(getActivity(), entry.getId(), editable, modifiable);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
    }

    @Subscribe( threadMode = ThreadMode.MAIN)
    public void updateTheUI(updateEvent newUpdate){

        if(newUpdate.getFrom() == DiaryEntryFragment.TAG){
            updateUI();
        }
    }
}
