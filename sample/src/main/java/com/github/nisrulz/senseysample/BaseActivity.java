/*
 * Copyright 2017 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.nisrulz.senseysample;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;

/**
 * Created by YanZhenjie on 2017/7/21.
 */
public abstract class BaseActivity<D> extends AppCompatActivity implements SwipeItemClickListener {

    protected Toolbar mToolbar;
    protected ActionBar mActionBar;
    protected SwipeMenuRecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.ItemDecoration mItemDecoration;

    protected BaseAdapter mAdapter;
    protected List<D> mDataList;//D抽象list

    protected BoxStore store;


    //for test
    private static int visitornumber = 0;
    byte[] rgb_data = new byte[] { (byte)0xba, (byte)0x8a };
    byte[] infrared_data = new byte[] { (byte)0xba, (byte)0x8a };
    byte[] feature = new byte[] { (byte)0xba, (byte)0x8a };
    //




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        store = ((MyApplication) getApplication()).getBoxStore();

        mToolbar = findViewById(R.id.toolbar);//此处加载toolbar,显示标题栏"sensey".

        mRecyclerView = findViewById(R.id.recycler_view);

        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (displayHomeAsUpEnabled()) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mLayoutManager = createLayoutManager();
        mItemDecoration = createItemDecoration();
        mDataList = createDataList();
        mAdapter = createAdapter();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.setSwipeItemClickListener(this);




    }

    protected int getContentView() {
        return R.layout.activity_scroll;
    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(this);
    }

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
    }

    protected abstract List<D> createDataList();

    protected abstract BaseAdapter createAdapter();

    @Override
    public void onItemClick(View itemView, int position) {
        Toast.makeText(this, "第" + position + "个", Toast.LENGTH_SHORT).show();
    }

    protected boolean displayHomeAsUpEnabled() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }



    /**
     *
     * @param NoteBox
     * @param name
     * @return List<Note> name, date, ID, visitor
     */
    protected List<Note> query_user_inNote(Box<Note> NoteBox, String name){

        QueryBuilder<Note> builder = NoteBox.query().contains(Note_.name, name);
        List<Note> getuser = builder.build().find();

        return getuser;
    }


    /**
     *
     * @param FaceBox
     * @param name
     * @return List<Face> name, RGB_data, infrared_data, feature, ID
     */
    protected List<Face> query_user_inFace(Box<Face> FaceBox, String name){

        QueryBuilder<Face> builder = FaceBox.query().contains(Face_.name, name);
        List<Face> getuser = builder.build().find();

        return getuser;
    }


    /**
     * Return RGB_data for the username
     * @param FaceBox
     * @param name
     * @return byte[] RGB_data
     */
    protected byte[] query_userFace(Box<Face> FaceBox, String name){
        QueryBuilder<Face> builder = FaceBox.query();
        builder.contains(Face_.name, name);
        Face result = builder.build().findFirst();

        return result.RGB_data;

    }



    /**
     * make the visitor to user, default visitor
     * @param NoteBox
     * @param name
     */
    protected void adduser(Box<Note> NoteBox, String name){

        List<Note> notes = query_user_inNote(NoteBox, name);
        for(int i = 0; i < notes.size(); i++){
            notes.get(i).visitor = false;
            NoteBox.put(notes.get(i));
        }
    }

    /**
     * make the user to visitor
     * @param NoteBox
     * @param name
     */
    protected void deluser(Box<Note> NoteBox, String name){

        List<Note> notes = query_user_inNote(NoteBox, name);
        for(int i = 0; i < notes.size(); i++){
            notes.get(i).visitor = true;
            NoteBox.put(notes.get(i));
        }
    }


    protected List<Note> query_all_note(Box<Note> NoteBox)
    {
        QueryBuilder<Note> builder = NoteBox.query();
        List<Note> result = builder.build().find();

        return result;
    }

    /**
     *
     * @param name
     * @param date : current date
     * @param type : visitor or not
     * @param rgb_data
     * @param infrared_data
     * @param feature
     */
    protected void addNote(Box<Note> NoteBox, String name, Date date, boolean type, byte[] rgb_data, byte[] infrared_data, byte[] feature){

        if(name == "")
        {
            name = "访客" + visitornumber;
            visitornumber++;
            type = true;
        }
        else{
            type = false;
        }

        Face face1 = new Face(name, rgb_data, infrared_data, feature);

        Note ma = new Note(name , type , date);
        ma.faces.add(face1);

        NoteBox.put(ma);
    }


    /**
     * delete all the information of the user from face and note tables
     * @param NoteBox
     * @param FaceBox
     * @param name
     */
    private void deluserdata(Box<Note> NoteBox, Box<Face> FaceBox,String name){
        List<Note> notes = query_user_inNote(NoteBox, name);
        List<Face> faces = query_user_inFace(FaceBox, name);
        for(int i = 0; i < notes.size(); i++){
            NoteBox.remove(notes.get(i));
        }

        for(int j = 0; j < faces.size(); j++){
            FaceBox.remove(faces.get(j));
        }

    }
}