package com.dolapps.hs_dormitory_food;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.*;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.*;

public class FoodWidget extends AppWidgetProvider {
	GregorianCalendar cal = new GregorianCalendar();
	int HH = cal.get(Calendar.HOUR_OF_DAY), mm = cal.get(Calendar.MINUTE),
			yoil = cal.get(Calendar.DAY_OF_WEEK),
			year = cal.get(Calendar.YEAR), mon = cal.get(Calendar.MONTH) + 1,
			day = cal.get(Calendar.DAY_OF_MONTH);

	public void onEnabled(Context context) {
		super.onEnabled(context);

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, FoodWidget.class);
		intent.setAction("FoodUpdate");
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent,
				0);
		am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10800000,
				pending);
	}

	public void onDisabled(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, FoodWidget.class);
		intent.setAction("FoodUpdate");
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent,
				0);
		am.cancel(pending);

		super.onDisabled(context);

	}

	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals("FoodUpdate")) {
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			ComponentName thiswidget = new ComponentName(context,
					FoodWidget.class);
			int[] ids = appWidgetManager.getAppWidgetIds(thiswidget);
			onUpdate(context, appWidgetManager, ids);
		}
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		RemoteViews remote = new RemoteViews(context.getPackageName(),
				R.layout.foodwidget);
		Intent intent = new Intent(context, FoodWidget.class);
		intent.setAction("FoodUpdate");
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent,
				0);
		remote.setOnClickPendingIntent(R.id.widgetlayout, pending);
		
		int int_thistime;
		String thistime = "";
		if (HH * 60 + mm >= 0 && HH * 60 + mm <= 8 * 60 + 30) {
			thistime = "아침";
			int_thistime = 0;
		} else if (HH * 60 + mm > 8 * 60 + 30 && HH * 60 + mm <= 13 * 60) {
			thistime = "점심";
			int_thistime = 1;
		} else {
			thistime = "저녁";
			int_thistime = 2;
		}

		String[] yoil_name = { "토", "일", "월", "화", "수", "목", "금" };
		remote.setTextViewText(R.id.title, String.format(
				"%d년 %2d월 %2d일(%s)\n%s", year, mon, day, yoil_name[yoil % 7],
				thistime));
		MF mf1 = new MF();
		String html = mf1
				.DownloadHtml(String
						.format("http://www.hstree.or.kr/main/z1_food1.php?&years=%d&months=%d&days=%d",
								year, mon, day));
		if (html.contains("Error :"))
			remote.setTextViewText(R.id.foodmenu,
					"인터넷 연결이\n안되어 있습니다\n연결 후\n위젯클릭");
		else {
			if (yoil == 0)
				yoil = 7;
			if (mf1.error_catch(mf1.mf_html(html), int_thistime, yoil)) {
				String food_list = mf1.mf_html(html)[int_thistime][yoil];
				if (food_list.contains("#") == false || food_list.equals("#"))
					;
				else
					remote.setTextViewText(R.id.foodmenu,
							food_list.replace("#", "\n"));
			} else
				;
		}

		appWidgetManager.updateAppWidget(appWidgetIds, remote);
	}

}