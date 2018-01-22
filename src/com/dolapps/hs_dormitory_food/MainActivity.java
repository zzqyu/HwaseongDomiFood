package com.dolapps.hs_dormitory_food;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends SherlockActivity {
	Calendar cal = new GregorianCalendar();
	int HH = cal.get(Calendar.HOUR_OF_DAY), mm = cal.get(Calendar.MINUTE),
			yoil = cal.get(Calendar.DAY_OF_WEEK),
			year = cal.get(Calendar.YEAR), mon = cal.get(Calendar.MONTH) + 1,
			day = cal.get(Calendar.DAY_OF_MONTH);
	String[] yoil_name = { "토", "일", "월", "화", "수", "목", "금" };
	int[] limit_day = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	Context ctxt = this;

	ViewPager pager;
	TextView date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		CheckUpdate CU= new CheckUpdate();
		if(CU.Check_update(this, getText(R.string.app_ver).toString())){
			final String url = "https://play.google.com/store/apps/details?id=com.dolapps.hs_dormitory_food";
			new AlertDialog.Builder(this)
			.setTitle("업데이트 알림")
			.setMessage(CU.New_App_feature(this, url))
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			            // Action for '확인' Button
			        	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			    		startActivity(intent);
			        }
		        })
			.setNegativeButton("취소", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		            // Action for '취소' Button
		        	dialog.cancel();
		        }
	        })
			.show();
		}
		setTitle("홈");
		YunYear(year);
		pager = (ViewPager) findViewById(R.id.pager);
		if(network_check()) pager.setAdapter(new myPagerAdapter(ctxt, year, mon, day, yoil % 7));
		else;
		ThisTimeFood();
		date = (TextView) findViewById(R.id.date);
		View.OnClickListener ChangeDateClick = new View.OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.pre:
					day--;
					yoil--;
					if (day < 1) {
						mon = (mon) % 12 - 1;
						if (mon < 1)
							mon = 12;
						day = limit_day[mon];
						if (mon == 12 && day == 31)
							year--;
					}
					if (yoil < 0)
						yoil = 6;
					else
						;
					date.setText(String.format("%d년 %2d월 %2d일 (%s)", year, mon,
							day, yoil_name[yoil % 7]));
					pager.setAdapter(new myPagerAdapter(ctxt, year, mon, day, yoil % 7));
					break;
				case R.id.next:
					day++;
					yoil++;
					if (day > limit_day[mon]) {
						day = 1;
						mon = (mon) % 12 + 1;
						if (mon == 1 && day == 1)
							year++;
					}
					date.setText(String.format("%d년 %2d월 %2d일 (%s)", year, mon,
							day, yoil_name[yoil % 7]));
					pager.setAdapter(new myPagerAdapter(ctxt, year, mon, day, yoil % 7));
					break;

				case R.id.refresh:
					pager_refresh();
					break;
				}
			}
		};
		date.setText(String.format("%d년 %2d월 %2d일 (%s)", year, mon, day,
				yoil_name[yoil % 7]));
		Button pre = (Button) findViewById(R.id.pre);
		Button next = (Button) findViewById(R.id.next);
		Button refresh = (Button) findViewById(R.id.refresh);
		pre.setOnClickListener(ChangeDateClick);
		next.setOnClickListener(ChangeDateClick);
		refresh.setOnClickListener(ChangeDateClick);
	}

	public Boolean network_check() {
		ConnectivityManager manager = (ConnectivityManager) ctxt
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mobile.isConnected() || wifi.isConnected()) {
			// 어디 한군데라도 연결되어 있는 경우
			return true;
		} else {
			// 아무 네트워크에도 연결안되어 있는 경우
			return false;
		}
	}

	void YunYear(int year) {
		if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
			limit_day[2] = 29;
		else
			limit_day[2] = 28;
	}

	void ThisTimeFood() {
		if (year == cal.get(Calendar.YEAR)
				&& mon == cal.get(Calendar.MONTH) + 1
				&& day == cal.get(Calendar.DAY_OF_MONTH)) {
			if (HH * 60 + mm >= 0 && HH * 60 + mm <= 8 * 60 + 30)
				pager.setCurrentItem(0);
			else if (HH * 60 + mm > 8 * 60 + 30 && HH * 60 + mm <= 13 * 60)
				pager.setCurrentItem(1);
			else
				pager.setCurrentItem(2);
		} else
			pager.setCurrentItem(2);
	}

	void refresh() {
		yoil = cal.get(Calendar.DAY_OF_WEEK);
		year = cal.get(Calendar.YEAR);
		mon = cal.get(Calendar.MONTH) + 1;
		day = cal.get(Calendar.DAY_OF_MONTH);
		date.setText(String.format("%d년 %2d월 %2d일 (%s)", year, mon, day,
				yoil_name[yoil % 7]));
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new myPagerAdapter(ctxt, year, mon, day, yoil % 7));

		ThisTimeFood();
	}

	void pager_refresh() {
		if (network_check() == false) {
			Toast.makeText(ctxt, "네트워크에 연결해주세요.", Toast.LENGTH_SHORT).show();
		} else {
			refresh();
		}
	}

	private class myPagerAdapter extends PagerAdapter {
		int y, m, d, yi;

		private LayoutInflater mInflater;

		public myPagerAdapter(Context context, int year, int month, int day,
				int yoil) {
			super();
			mInflater = LayoutInflater.from(context);
			y = year;
			m = month;
			d = day;
			if (yoil == 0)
				yi = 7;
			else
				yi = yoil;
		}

		public int getCount() {
			return 3;
		}

		public Object instantiateItem(View pager, int position) {
			View v = mInflater.inflate(R.layout.bg_pager, null);
			MF mf1 = new MF();
			String html = mf1.DownloadHtml(String
					.format("http://www.hstree.or.kr/main/z1_food1.php?&years=%d&months=%d&days=%d",
							y, m, d));

			TextView indi1 = (TextView) v.findViewById(R.id.indi1);
			TextView indi2 = (TextView) v.findViewById(R.id.indi2);
			TextView indi3 = (TextView) v.findViewById(R.id.indi3);
			TextView food = (TextView) v.findViewById(R.id.food);
			if (mf1.error_catch(mf1.mf_html(html), position, yi)) {
				if (mf1.mf_html(html)[position][yi].contains("#") == false
						|| mf1.mf_html(html)[position][yi].equals("#"))
					food.setText("식단 정보가\n없습니다.");
				else
					food.setText(mf1.mf_html(html)[position][yi].replace("#", "\n"));
			} else
				food.setText("식단 정보가\n없습니다.");

			if (position == 0) {
				indi2.setText("아침");
				indi3.setText("점심 >");

			} else if (position == 1) {
				indi1.setText("< 아침");
				indi2.setText("점심");
				indi3.setText("저녁 >");
			} else {
				indi1.setText("< 점심");
				indi2.setText("저녁");
			}

			((ViewPager) pager).addView(v, null);

			return v;
		}

		// View를 삭제합니다.
		public void destroyItem(View pager, int position, Object view) {
			((ViewPager) pager).removeView((View) view);
		}

		// instantiateItem에서 생성한 객체를 이용할 것인지 여부를 반환합니다.
		public boolean isViewFromObject(View v, Object obj) {
			return v == obj;
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		menu.add(0, 1, 0, "어플정보")
				.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	boolean mbRunLast;

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent Intent1 = new Intent(ctxt, app_info.class);
			startActivity(Intent1);
			return true;
		}

		return false;

	}
}
