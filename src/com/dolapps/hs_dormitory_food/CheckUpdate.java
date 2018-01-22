package com.dolapps.hs_dormitory_food;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;

public class CheckUpdate {
	
	CheckUpdate(){
		
	}
	
	public Boolean Check_update(Context ctxt, String app_ver){
		app_ver= app_ver.replace(".", "#");
		int int_app_ver = Integer.parseInt(app_ver.split("#")[0])*100+
				Integer.parseInt(app_ver.split("#")[1])*10+
				Integer.parseInt(app_ver.split("#")[2]);
		
		String play_ver=New_App_ver(ctxt, "https://play.google.com/store/apps/details?id=com.dolapps.hs_dormitory_food");
		if (play_ver.contains("Error") || play_ver.contains("인터넷미연결")) {
			return false;
		}
		play_ver= play_ver.replace(".", "#");
		int int_play_ver = Integer.parseInt(play_ver.split("#")[0])*100+
				Integer.parseInt(play_ver.split("#")[1])*10+
				Integer.parseInt(play_ver.split("#")[2]);
		if(int_app_ver<int_play_ver) return true;
		else return false;
	}
	
	public String New_App_ver(Context ctxt, String uri) {
		StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(pol);
		if (network_check(ctxt) == false)
			return "인터넷미연결";
		else {
			
			return mf_ver(DownloadHtml(uri));
		}

	}
	
	public String New_App_feature(Context ctxt, String uri) {
		StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(pol);
		if (network_check(ctxt) == false)
			return "인터넷미연결";
		else {
			
			return mf_new_feature(DownloadHtml(uri));
		}

	}

	Boolean network_check(Context ctxt) {
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

	String DownloadHtml(String addr) {
		StringBuilder html = new StringBuilder(); 
		try {
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					for (;;) {
						String line = br.readLine();
						if (line == null) break;
						html.append(line + '\n'); 
					}
					br.close();
				}
				conn.disconnect();
			}
		} catch (NetworkOnMainThreadException e) {
			return "Error : 메인 스레드 네트워크 작업 에러 - " + e.getMessage();
		} catch (Exception e) {
			return "Error : " + e.getMessage();
		}
		return html.toString();
	}

	String mf_ver(String html) {
		if (html.contains("Error : "))
			return "Error";
		else if(html.contains("softwareVersion\">")){
			String str = html.replace("softwareVersion\">","###");
			str=str.split("###")[1];
			str=str.split("<")[0];
			str=str.replace(" ", "");
			return str;
		}
		else return "Error";
	}
	
	String mf_new_feature(String html) {
		if (html.contains("Error : "))
			return "Error";
		else if(html.contains("새로운 기능")){
			String str = html.split("새로운 기능")[1];
			str=str.split(" <div class=\"show-more-end\">")[0];
			str=str.replace("<div class=\"recent-change\">", "");
			str=str.replace("<div class=\"recent-change\">", "");
			str=str.replace("</h1> ", "");
			str=str.replace("</div>", "\n");
			return str;
		}
		else return "Error";
	}

}
