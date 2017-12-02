package com.example.kimjipsa;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class VoiceActivity extends Activity {
    Button voiceAddBtn;
    private ArrayList<String> items;
    private ArrayAdapter adapter;
    private ListView listview;


    //*************************db***************************8
    private MySQLiteOpenHelper helper;
    String dbName = "st_file.db";
    int dbVersion = 1; // 데이터베이스 버전
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log 에 사용할 tag
    String []purchase_item=new String [200];
    String BeaconID="";
    private ArrayList<Item> ret;
    private HashMap<String, Integer> Items=new HashMap<String, Integer>();



    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";

    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, date text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;


    private static final String TAG = VoiceActivity.class.getSimpleName();
    private static final String CLIENT_ID = "B4bceQg6pK0Cpi00mMy0";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private ListView mListView;
    private TextView txtResult;
    private Button btnStart;
    private String mResult;
    List<String> results;
    private AudioWriterPCM writer;
    Memo memo;
    NoteEdit Ne;
    NotesDbAdapter notesdb;
    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                //txtResult.setText(mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.

                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                results = speechRecognitionResult.getResults();
                memo.setText(results.get(0));
                Log.d(tag, "Voice결과: " + results.get(0));

                StringBuilder strBuf = new StringBuilder();
               for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
                //txtResult.setText(mResult);
               items = new ArrayList<String>() ;
                // ArrayAdapter 생성. 아이템 View를 선택(multiple choice)가능하도록 만듦.
                adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items) ;
                // listview 생성 및 adapter 지정.
                listview = (ListView) findViewById(R.id.list2) ;
                listview.setAdapter(adapter) ;
                for(int x=0;x<results.size();x++){

                    items.add(results.get(x));
                    String id = results.get(x);

                    Log.d(tag, "물품: " + id );

                }
                // listview 갱신
                adapter.notifyDataSetChanged();
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;

/*
                Intent intent = new Intent(VoiceActivity.this,ItemList.class);
                setPurchase_item();
                Parse parsing=new Parse();
                ret=parsing.Parsing(results.get(0),Items);
                update_Iteam();
                startActivity(intent);
                btnStart.setEnabled(true);*/

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //*************************db***************************8
        helper = new MySQLiteOpenHelper(
                this,  // 현재 화면의 제어권자
                dbName,// db 이름
                null,  // 커서팩토리-null : 표준커서가 사용됨
                dbVersion);       // 버전
        try {
            db = helper.getWritableDatabase(); // 읽고 쓸수 있는 DB
            Log.e(tag, "데이터베이스를 연결성공");
            //db = helper.getReadableDatabase(); // 읽기 전용 DB select문
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(tag, "데이터베이스를 얻어올 수 없음");
            finish(); // 액티비티 종료
        }

        setContentView(R.layout.voiceactivity);
        Ne = new NoteEdit();
        memo=new Memo();

        txtResult = (TextView) findViewById(R.id.txt_result);
        btnStart = (Button) findViewById(R.id.btn_start);

        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    btnStart.setText(R.string.str_stop);
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    btnStart.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });

        voiceAddBtn = (Button)findViewById(R.id.VoiceAddBtn);
        voiceAddBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int count, checked ;
                count = adapter.getCount() ;

                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();

                    if (checked > -1 && checked < count) {
                        Intent intent = new Intent(VoiceActivity.this, NoteList.class);
                        setPurchase_item();
                        Parse parsing=new Parse();
                        ret=parsing.Parsing(results.get(checked),Items);
                        update_Iteam();
                        startActivity(intent);
                    }
                }


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<VoiceActivity> mActivity;

        RecognitionHandler(VoiceActivity activity) {
            mActivity = new WeakReference<VoiceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            VoiceActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    } public long createNote(String title, String body, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE, date);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    HashMap<String, Integer> setPurchase_item(){
        Cursor c = db.rawQuery("select Item_Name,weight,Bool_Item from ItemListDB;", null);
        int i=1;
        while (c.moveToNext()) {
            String cc=c.getString(2);
            Items.put(c.getString(0),c.getInt(1));
            Log.d(tag, "물품: " + Items.toString() );
            i++;
        }
        return Items;
    }
    void update_Iteam(){
        for(int i=0;i<ret.size();i++){
            db.execSQL("update ItemListDB set Bool_Item='true', weight="+ret.get(i).getWeight() +" where Item_Name='"+ret.get(i).getItem_name()+"';");
            Log.d(tag, "update ItemListDB set Bool_Item='true', weight="+ret.get(i).getWeight() +" where Item_Name='"+ret.get(i).getItem_name()+"';");
        }
        Log.d(tag, "update 성공~!");
    }
}
