package com.example.kimjipsa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.example.kimjipsa.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class NoteEdit extends Activity{
	//*************************db***************************8
	private MySQLiteOpenHelper helper;
	String dbName = "st_file.db";
	int dbVersion = 1; // 데이터베이스 버전
	private SQLiteDatabase db;
	String tag = "SQLite"; // Log 에 사용할 tag
	String []purchase_item=new String [200];
	String BeaconID="";

	//*************************beacon***************************


	private ArrayList<Item> ret;

	public static int numTitle = 1;
	public static String curDate = "";
	public static String curText = "";
	private EditText mTitleText;
	private EditText mBodyText;
	private TextView mDateText;
	private Long mRowId;
	private HashMap<String, Integer> Items=new HashMap<String, Integer>();

	private Cursor note;

	private NotesDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.note_edit);
		setTitle(R.string.app_name);


		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		mDateText = (TextView) findViewById(R.id.notelist_date);

		long msTime = System.currentTimeMillis();
		Date curDateTime = new Date(msTime);

		SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");
		curDate = formatter.format(curDateTime);

		mDateText.setText(""+curDate);


		mRowId = (savedInstanceState == null) ? null :
				(Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
					: null;
		}


		populateFields();
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


	}



	public static class LineEditText extends EditText{
		// we need this constructor for LayoutInflater
		public LineEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			mRect = new Rect();
			mPaint = new Paint();
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setColor(Color.BLUE);
		}

		private Rect mRect;
		private Paint mPaint;

		@Override
		protected void onDraw(Canvas canvas) {

			int height = getHeight();
			int line_height = getLineHeight();

			int count = height / line_height;

			if (getLineCount() > count)
				count = getLineCount();

			Rect r = mRect;
			Paint paint = mPaint;
			int baseline = getLineBounds(0, r);

			for (int i = 0; i < count; i++) {

				canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
				baseline += getLineHeight();

				super.onDraw(canvas);
			}

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.noteedit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_about:

		    	/* Here is the introduce about myself */
				AlertDialog.Builder dialog = new AlertDialog.Builder(NoteEdit.this);
				dialog.setTitle("About");
				dialog.setMessage(" Developer : 가톨릭대학교 컴퓨터정보공학부 이종원,정원모,하진호,설다솜,안수진" + " Version : 1.0.0"

				);
				dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});
				dialog.show();
				return true;
			case R.id.menu_delete:
				if(note != null){
					note.close();
					note = null;
				}
				if(mRowId != null){
					mDbHelper.deleteNote(mRowId);
				}
				finish();

				return true;
			case R.id.menu_save:
				saveState();
				finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void saveState() {
		String body = mBodyText.getText().toString();
		int titleLength=0;
		if(body.length()<10){
			titleLength=body.length();
		}else
			titleLength=10;
		if(titleLength==0)return;
		String title = body.substring(0,titleLength);

		Intent intent=getIntent();
		if(mRowId == null){
			long id = mDbHelper.createNote(title, body, curDate);
			Items =setPurchase_item();
			Parse parsing=new Parse();
			ret=parsing.Parsing(body,Items);
			update_Iteam();
			if(id > 0){
				mRowId = id;
			}else{
				Log.e("saveState","failed to create note");
			}
		}else{
			if(!mDbHelper.updateNote(mRowId, title, body, curDate)){
				Log.e("saveState","failed to update note");
			}
		}
	}


	private void populateFields() {
		if (mRowId != null) {
			note = mDbHelper.fetchNote(mRowId);
			startManagingCursor(note);
			mTitleText.setText(note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
			mBodyText.setText(note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
			curText = note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
		}
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
