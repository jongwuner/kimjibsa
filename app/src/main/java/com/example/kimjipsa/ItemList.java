package com.example.kimjipsa;

import android.app.Activity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.kimjipsa.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.util.Log;

public class ItemList extends Activity {
	Button btnNotelist;
	Button deleteBtn;
	ListView mListView;
	//*************************db***************************8
	private MySQLiteOpenHelper helper;
	String dbName = "st_file.db";
	int dbVersion = 1; // 데이터베이스 버전
	private SQLiteDatabase db;
	String tag = "SQLite"; // Log 에 사용할
	private NotesDbAdapter mDbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




		setContentView(R.layout.itemlist);

		//*************************db***************************8
		mDbHelper = new NotesDbAdapter (this);
		mDbHelper.open();
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
		//*************************db***************************8

		// listview 생성 및 adapter 지정.
		Cursor c = db.rawQuery("select Item_Name from ItemListDB where Bool_Item='true';", null);
		final ArrayList<String> items = new ArrayList<String>() ;
		// ArrayAdapter 생성. 아이템 View를 선택(multiple choice)가능하도록 만듦.
		final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items) ;

		// listview 생성 및 adapter 지정.
		final ListView listview = (ListView) findViewById(R.id.list1) ;
		listview.setAdapter(adapter) ;
		while (c.moveToNext()) {
			items.add(c.getString(0));
			String id = c.getString(0);

			Log.d(tag, "물품: " + id );

		}
		Log.d(tag, "select 성공~!");
		// listview 갱신
		adapter.notifyDataSetChanged();




		deleteBtn= (Button)findViewById(R.id.deleteitembutton) ;
		deleteBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
				int count = adapter.getCount() ;
				for (int i = count-1; i >= 0; i--) {
					if (checkedItems.get(i)) {
						update_Iteam(items.get(i));
						items.remove(i) ;
					}

				} // 모든 선택 상태 초기화. listview.clearChoices() ; adapter.notifyDataSetChanged();

				checkedItems.clear();
				adapter.notifyDataSetChanged();
			}
		}) ;


		btnNotelist = (Button)findViewById(R.id.memoList);
		btnNotelist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ItemList.this, NoteList.class);
				startActivity(intent);

			}
		});
	}

	void update_Iteam(String itemName){
		db.execSQL("update ItemListDB set Bool_Item='false', weight=1 where Item_Name='"+itemName+"';");
	}

}
