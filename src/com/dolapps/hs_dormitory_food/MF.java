package com.dolapps.hs_dormitory_food;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;

public class MF {
	public String DownloadHtml(String addr) {
		StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(pol);
		StringBuilder html = new StringBuilder();
		try {
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					for (;;) {
						String line = br.readLine();
						if (line == null)
							break;
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

	public String[][] mf_html(String html) {
		String str = "";
		html.replace("\n", "");
		int tnf = 0;
		for (int i = 1; i < html.length(); i++) {
			if (html.charAt(i - 1) == '\'' && html.charAt(i) == '>')
				tnf = 1;
			else if (tnf == 1 && html.charAt(i - 1) != '<'
					&& html.charAt(i) != '/')
				str += html.charAt(i);
			else if (tnf == 1 && html.charAt(i - 1) == '<'
					&& html.charAt(i) == '/') {
				str += "\n";
				tnf = 0;
			}
		}
		html = str;
		html = html.replace("아침<r >", "|");
		html = html.replace("점심<r >", "|");
		html = html.replace("저녁<r >", "|");
		html = html.replace("<\n\n", "<r >|");
		html = html.replace("<\n<r >", "");
		String[] temp = html.split("\\|");
		str = temp[2] + "!" + temp[3] + "!" + temp[4];
		str = str.replace("\n", "");
		str = str.replace("<r >", "#");
		str = str.replace("<", "@");
		// !: 아침점심저녁, @: 요일, #: 메뉴구분
		String[] time_part = str.split("!");
		String[][] mf;// mf[i][j], i: 아점저, j:요일
		mf = new String[3][];
		for (int i = 0; i < 3; i++)
			mf[i] = time_part[i].split("@");
		return mf;

	}

	public Boolean error_catch(String[][] str, int posit, int yi) {

		try {
			String str1 = str[posit][yi];
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

	}

}
