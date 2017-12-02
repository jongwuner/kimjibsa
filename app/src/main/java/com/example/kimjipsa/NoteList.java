package com.example.kimjipsa;

import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;

import com.example.kimjipsa.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.util.Log;

import android.os.Handler;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.HashMap;

public class NoteList extends ListActivity implements BeaconConsumer {
    Item it = new Item();
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int DELETE_ID = Menu.FIRST;

    private NotesDbAdapter mDbHelper;

    //*************************db***************************

    private MySQLiteOpenHelper helper;
    String dbName = "st_file.db";
    int dbVersion = 1; // 데이터베이스 버전
    private SQLiteDatabase db;
    String tag = "SQLite"; // Log 에 사용할 tag
    String[] purchase_item = new String[200];
    String BeaconID = "";

    private HashMap<String, Integer> Items = new HashMap<String, Integer>();
    //*************************beacon***************************
    private BeaconManager beaconManager;
    // 감지된 비콘들을 임시로 담을 리스트
    private List<Beacon> beaconList = new ArrayList<Beacon>();
    int t;

    //***********************************************************
    Button btnv;
    Button btnItemlist, set;
    Gloval gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notelist);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
        Button addnote = (Button) findViewById(R.id.addnotebutton);
        addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNote();
            }
        });
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
        insert_Item();
        insert_Beacon();
        select();
        setPurchase_item();
        gv = new Gloval();
        gv.setCycleTime("1");

        //*************************beacon***************************// 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);


        handler.sendEmptyMessage(0);

        btnv = (Button) findViewById(R.id.addvoicebutton);

        btnv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(NoteList.this, VoiceActivity.class);
                startActivity(intent);
            }
        });

        btnItemlist = (Button) findViewById(R.id.ItemList);
        btnItemlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(NoteList.this, ItemList.class);
                startActivity(intent);
            }
        });

        set = (Button) findViewById(R.id.Setting);

        set.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(NoteList.this, Setting.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notelist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:

                AlertDialog.Builder dialog = new AlertDialog.Builder(NoteList.this);
                dialog.setTitle("About");
                dialog.setMessage(" Developer : ���縯���б� ��ǻ���������к� \n" + "������, ������, ����ȣ, ���ټ�, �ȼ���\n" + " Version : 1.0.0");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNote() {
        setPurchase_item();
        //Intent i = new Intent(this, NoteEdit.class);
        Intent i = new Intent(this, NoteEdit.class);

        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);


        String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_DATE};
        int[] to = new int[]{R.id.text1, R.id.date_row};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        setListAdapter(notes);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    HashMap<String, Integer> setPurchase_item() {
        Cursor c = db.rawQuery("select Item_Name,weight from ItemListDB;", null);
        int i = 1;
        while (c.moveToNext()) {
            Items.put(c.getString(0), c.getInt(1));
            Log.d(tag, "물품: " + Items.toString());
            i++;
        }
        return Items;
    }

    void select() {
        Cursor c = db.rawQuery("select * from ItemListDB;", null);
        while (c.moveToNext()) {
            String id = c.getString(0);
            int weight = c.getInt(1);
            String bol = c.getString(2);

            Log.d(tag, "물품: " + id + ",weight: " + weight + ",Bool_Item: " + bol);

        }
        Log.d(tag, "select 성공~!");
    }

    void insert_Item() {
        boolean result = insertResult();
        if (result == false) {
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('우유',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('과자',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('맥주',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('소주',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('담배',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('봉투',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('라면',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('비빔면',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('콜라',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('쌀',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('두부',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('사과',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('사이다',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('휴지',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('돼지고기',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('소고기',0,'false');");
            db.execSQL("insert into ItemListDB (Item_Name, weight, Bool_Item) values('상추',0,'false');");
            Log.d(tag, "insert 성공~!");
        } else {
            Log.d(tag, "insert 이미됨");
        }
    }

    void insert_Beacon() {
        boolean result = insertResult();
        if (result == false) {
            db.execSQL("insert into BeaconDB (id, weight) values('MiniBeacon_55290',0);");
            Log.d(tag, "insert 성공~!");
        } else {
            Log.d(tag, "beacon insert 이미됨");
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 비콘의 아이디 textView에 넣는다.
            BeaconID = "";
            for (Beacon beacon : beaconList) {
                BeaconID = beacon.getBluetoothName();
                if (BeaconID != "") {
                    gv.setTime(t);
                    Intent intent = new Intent(NoteList.this, PopupActivity.class);
                    intent.putExtra("data", "Test Popup");

                    if (gv.getTime() < Integer.parseInt(gv.getCycleTime()) && gv.getPopup() == false) {
                        gv.setTime(t);
                        gv.setPopup(true);
                        startActivityForResult(intent, 1);
                        t = 0;
                    } else if (gv.getTime() < Integer.parseInt(gv.getCycleTime()) && gv.getPopup() == true) {
                        gv.setTime(t);
                        t++;
                        gv.setPopup(true);
                    } else if (gv.getTime() >= Integer.parseInt(gv.getCycleTime()) && gv.getPopup() == true) {
                        gv.setTime(0);
                        gv.setPopup(false);
                    } else if (gv.getTime() >= Integer.parseInt(gv.getCycleTime()) && gv.getPopup() == false) {
                        gv.setTime(0);
                        gv.setPopup(true);
                        startActivityForResult(intent, 1);
                        t = 0;
                    }

                }
            }
            beaconList.clear();
            // 자기 자신을 1초마다 호출
            t++;
            gv.setTime(t);
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    public boolean insertResult() {
        Cursor c = db.rawQuery("select * from ItemListDB;", null);
        String id = "";
        boolean result = false;
        while (c.moveToNext()) {
            id = c.getString(0);
            int weight = c.getInt(1);
            String bol = c.getString(2);
            if (id != "") {
                result = true;
            }
            Log.d(tag, "물품: " + id + ",weight: " + weight + ",Bool_Item: " + bol);

        }
        return result;
    }
}

