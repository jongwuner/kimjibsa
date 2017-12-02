package com.example.kimjipsa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class PopupActivity extends Activity {
	Button list;
	Button canclebtn;
	 @Override
	  protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  requestWindowFeature(Window.FEATURE_NO_TITLE);

	  WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
	  layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
	  layoutParams.dimAmount = 0.7f;
	  getWindow().setAttributes(layoutParams);
	  setContentView(R.layout.popup);


	  list = (Button)findViewById(R.id.btn_list);
	  list.setOnClickListener(new View.OnClickListener() {
		public void onClick(View arg0) {
			Intent i = new Intent(PopupActivity.this, ItemList.class);
	        startActivity(i);
		}
	  });
		 canclebtn = (Button)findViewById(R.id.btn_cancel);
		 canclebtn.setOnClickListener(new View.OnClickListener() {
			 public void onClick(View arg0) {

					 // 프로그램을 종료한다
					 PopupActivity.this.finish();

			 }
		 });
	  /*ȣ��� ���� �ڵ�
	   Intent intent_ = new Intent(context, SOSPopupActivity.class);
	   intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	   context.startActivity(intent_);
	   */
	 }
}
