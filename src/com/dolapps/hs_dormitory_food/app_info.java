package com.dolapps.hs_dormitory_food;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class app_info extends Activity {
	String url= "https://play.google.com/store/apps/details?id=com.dolapps.hs_dormitory_food";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.app_info);
	    setTitle("어플정보");
	    
	    CheckUpdate CU= new CheckUpdate();
	    TextView app_ver =(TextView) findViewById(R.id.sub1_1);
		app_ver.setText(CU.New_App_ver(this, url));
	    // TODO Auto-generated method stub\
	}
	public void ll3(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(intent);
		
	}
	public void ll2_1(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/n/?HsDormitoryFood&ref=stale_email&medium=email&mid=92137fdG5af38ef8807eG0G147&bcode=1.1387739855.Abm_vOq7859GWFdY&n_m=wjdrb0626%40naver.com"));
		startActivity(intent);
		
	}

}
