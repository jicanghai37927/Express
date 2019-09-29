package com.haiyunshan.express.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * 工具类
 *
 */
public class AssetUtils {


	/**
	 *
	 * @param context
	 * @param name
	 * @param encoding
	 * @return
	 */
	public static final List<String> readLines(Context context, String name, String encoding) {
		ArrayList<String> list = new ArrayList<>();

		InputStream is = null;
		try {
			is = context.getAssets().open(name);
			InputStreamReader isr = new InputStreamReader(is, encoding);
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}

			br.close();
			isr.close();

		} catch (IOException e) {

		} finally {

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

				}
			}
		}

		return list;
	}

	public static final JSONObject readFileToJSONObject(Context context, String fileName) {
		return readAssetToJSONObject(context, fileName);
	}

	/**
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static final JSONObject readAssetToJSONObject(Context context, String fileName) {
		String str = readAssetToString(context, fileName, "UTF-8");
		if (str == null) {
			return null; 
		}
		
		JSONObject json = null; 
		try {
			json = new JSONObject(str);
		} catch (JSONException e) {
		}
		
		return json; 
	}
	
	/**
	 * @param context
	 * @param fileName
	 * @param encoding
	 * @return
	 */
	public static final String readAssetToString(Context context, String fileName, String encoding) {
		byte[] data = readAssetToByteArray(context, fileName); 
		if (data == null) {
			return null; 
		}

		String str = null; 
		try {
			str = new String(data, encoding);
		} catch (UnsupportedEncodingException e) {
		} 

		return str; 
	}
	
	/**
	 * @param context
	 * @param name
	 * @return
	 */
	public static final byte[] readAssetToByteArray(Context context, String name) {
		byte[] data = null; 
		
		InputStream is = null; 
		try {
			is = context.getAssets().open(name);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(307200); 
			
			byte[] buffer = new byte[204800]; 
			int len = 0; 
			while ((len = is.read(buffer)) >= 0) {
				baos.write(buffer, 0, len);
			}
			
			data = baos.toByteArray(); 
			baos.close(); 
		} catch (IOException e) {
			
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			} 
		}
		
		return data; 
	}
	
}
