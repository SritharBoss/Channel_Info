package com.example.channelinfo;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String URL="https://channel.trai.gov.in/";
    private SQLiteDatabase mSqLiteDatabase;
    CheckBox mCheckBox_hd;
    Spinner mSpinner_category,mSpinner_language;
    RecyclerView mRecyclerView;
    ArrayList<String> categoryList,languageList;
    RecyclerViewAdapter mAdapter;
    ArrayAdapter<String> categoryAdapter,languageAdapter;
    SharedPreferences sharedPreferences=null;
    ProgressBar progressBar;

    String c="";
    String l="";
    String hd="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TESTINGG", "onCreate: Started"+Thread.currentThread().getName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChannelDBHelper channelDBHelper=new ChannelDBHelper(this);
        mSqLiteDatabase=channelDBHelper.getWritableDatabase();

        Toolbar toolbar=findViewById(R.id.toolbar_main);
        toolbar.setTitle("Channel Info");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white,getTheme()));
        setSupportActionBar(toolbar);

        sharedPreferences=getSharedPreferences("ChannelInfo",MODE_PRIVATE);


        mCheckBox_hd=findViewById(R.id.checkBox_main);
        mSpinner_category=findViewById(R.id.spinner_main);
        mSpinner_category.setPrompt("Select Category");
        mSpinner_language=findViewById(R.id.spinner1_main);
        mSpinner_language.setPrompt("Select Language");
        mRecyclerView=findViewById(R.id.recyclerView_main);
        progressBar=findViewById(R.id.progressBar);

        initializeRecyclerView();
        loadSpinner();

        mSpinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                c=categoryList.get(position);
                mAdapter.swapCursor(getItemsByCategory(c,l,hd));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                l=languageList.get(position);
                mAdapter.swapCursor(getItemsByCategory(c,l,hd));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCheckBox_hd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked())
                    hd="hd";
                else
                    hd="";
                mAdapter.swapCursor(getItemsByCategory(c,l,hd));
            }
        });
        Log.d("TESTINGG", "onCreate: Ended"+Thread.currentThread().getName());
    }

    private void initializeRecyclerView() {
        Log.d("TESTINGG", "initializeRecyclerView: Started"+Thread.currentThread().getName());
        mRecyclerView.hasFixedSize();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new RecyclerViewAdapter(MainActivity.this,getAllItems());
        mRecyclerView.setAdapter(mAdapter);
        Log.d("TESTINGG", "initializeRecyclerView: Ended"+Thread.currentThread().getName());
    }


    private Cursor getAllItems() {
        Log.d("TESTINGG", "getAllItems: Called"+Thread.currentThread().getName());
        return mSqLiteDatabase.query(
                ItemContract.ChannelEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getItemsByCategory(String category,String language,String hd){
        Log.d("TESTINGG", "getItemsByCategory: Started"+Thread.currentThread().getName());
        String c=category;
        String l=language;
        if(category.equals("All"))
            c="";
        if (language.equals("All"))
            l="";
        Log.d("TESTINGG", "getItemsByCategory: Ended"+Thread.currentThread().getName());
        return mSqLiteDatabase.rawQuery("SELECT * FROM "+ItemContract.ChannelEntry.TABLE_NAME+" WHERE "+
                ItemContract.ChannelEntry.COLUMN_CATEGORY +" LIKE '%"+c+"%'" +" AND "+
                ItemContract.ChannelEntry.COLUMN_LANGUAGE +" LIKE '%"+l+"%'" +" AND "+
                ItemContract.ChannelEntry.COLUMN_HD+" LIKE '%"+hd+"%'",null);
    }

    private Cursor getSearch(String search){
        return mSqLiteDatabase.rawQuery("SELECT * FROM "+ItemContract.ChannelEntry.TABLE_NAME+" WHERE "+
                ItemContract.ChannelEntry.COLUMN_CHANNEL_NAME+" LIKE '%"+search+"%'",null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);

        MenuItem searchItem=menu.findItem(R.id.app_bar_search);
        SearchView searchView= (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Example","On Query Submit Invoked");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Example","ON Query Text Change Invoked");
//                mAdapter.getFilter().filter(newText);
                mAdapter.swapCursor(getSearch(newText));
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_bar_search:

                break;
            case R.id.share:
                Intent sendIntent=new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"Hi This is just a share button");
                sendIntent.setType("text/plain");

                startActivity(Intent.createChooser(sendIntent,null));
                break;
        }
        return true;
    }

    private void loadSpinner(){
        Log.d("TESTINGG", "loadSpinner: Started"+Thread.currentThread().getName());
        categoryList=new ArrayList<>();
        languageList=new ArrayList<>();


        categoryList.add(0,"All");
        languageList.add(0,"All");

        Cursor cursor=mSqLiteDatabase.rawQuery("select DISTINCT "+ ItemContract.ChannelEntry.COLUMN_CATEGORY +" FROM "+ItemContract.ChannelEntry.TABLE_NAME,
                null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            categoryList.add(cursor.getString(cursor.getColumnIndex(ItemContract.ChannelEntry.COLUMN_CATEGORY)));
        }
        cursor.close();

        categoryAdapter=new ArrayAdapter<>(this,R.layout.spinner_item,categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_category.setAdapter(categoryAdapter);

        Cursor c=mSqLiteDatabase.rawQuery("select DISTINCT "+ ItemContract.ChannelEntry.COLUMN_LANGUAGE +" FROM "+ItemContract.ChannelEntry.TABLE_NAME,
                null);
        c.moveToFirst();
        while (c.moveToNext()){
            languageList.add(c.getString(c.getColumnIndex(ItemContract.ChannelEntry.COLUMN_LANGUAGE)));
        }
        c.close();

        languageAdapter=new ArrayAdapter<>(this,R.layout.spinner_item,languageList);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_language.setAdapter(languageAdapter);
        Log.d("TESTINGG", "loadSpinner: Ended"+Thread.currentThread().getName());
    }

    void loadDataFromWeb(){
        Log.d("TESTINGG", "loadDataFromWeb: Started"+Thread.currentThread().getName());
            RequestQueue requestQueue= Volley.newRequestQueue(this);

            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,URL+"data.php", null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        sharedPreferences.edit().putBoolean("firstRun",false).apply();
                        for(int i=0;i<response.length();i++){
                            JSONObject jsonObject=response.getJSONObject(i);
                            String channel=jsonObject.getString("Channel").trim();
                            String category=jsonObject.getString("category").trim();
                            String imgUrl=jsonObject.getString("imageurl");
                            double price=jsonObject.getDouble("price");
                            String language=jsonObject.getString("language").trim();
                            String hd=jsonObject.getString("HD").trim();
//                            ItemModel itemModelObj=new ItemModel(channel,category,language,hd,price,imgUrl);
//                            addElement(categoryList,itemModelObj.getCategory().trim()); //spinner1
//                            addElement(languageList,itemModelObj.getLanguage().trim()); //spinner2

                            ContentValues cv=new ContentValues();
                            cv.put(ItemContract.ChannelEntry.COLUMN_CHANNEL_NAME,channel);
                            cv.put(ItemContract.ChannelEntry.COLUMN_CATEGORY,category);
                            cv.put(ItemContract.ChannelEntry.COLUMN_IMG_URL,imgUrl);
                            cv.put(ItemContract.ChannelEntry.COLUMN_PRICE,price);
                            cv.put(ItemContract.ChannelEntry.COLUMN_LANGUAGE,language);
                            cv.put(ItemContract.ChannelEntry.COLUMN_HD,hd);

                            mSqLiteDatabase.insert(ItemContract.ChannelEntry.TABLE_NAME,null,cv);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),"Data Loaded",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    initializeRecyclerView();
                    loadSpinner();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            requestQueue.add(jsonArrayRequest);
        Log.d("TESTINGG", "loadDataFromWeb: Ended"+Thread.currentThread().getName());
    }

    @Override
    protected void onResume() {
        Log.d("TESTINGG", "onResume: Started"+Thread.currentThread().getName());
        super.onResume();
        if(sharedPreferences.getBoolean("firstRun",true)) {
            progressBar.setVisibility(View.VISIBLE);
            loadDataFromWeb();
        }
        Log.d("TESTINGG", "onResume: Ended"+Thread.currentThread().getName());
    }

}