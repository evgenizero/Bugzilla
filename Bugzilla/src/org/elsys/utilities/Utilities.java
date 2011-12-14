package org.elsys.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import org.elsys.R;
import org.elsys.data.Account;

import android.content.Context;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Utilities {

	private final static String mySettingsListKey = "accounts_list_key";
	private final static String mySettingsListValue = "accounts_list_value";
	
	public final static String CURRENT_ACCOUNT = "currentAccount";
	public final static String ACCOUNT_EMAIL = "accountEmail";
	public final static String ACCOUNT_PASSWORD = "accountPassword";
	public final static String ACCOUNT_DESCRIPTION = "accountDescription";
	public final static String ACCOUNT_URL = "accountUrl";
	
	public final static String CURRENT_ACCOUNT_EMAIL = "userEmail";
	public final static String CURRENT_ACCOUNT_PASSWORD = "userPassword";
	public final static String CURRENT_ACCOUNT_URL = "accountUrl";
	public final static String CURRENT_ACCOUNT_DESCRIPTION = "accountDescription";
	public final static String CURRENT_ACCOUNT_ID = "accountId";
	
	public static void updateListView(ListView list, Context context,
			ArrayList<Account> data) {

		ArrayList<HashMap<String, String>> listHash = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;

		for(int i=0;i<data.size();i++) {
			map = new HashMap<String, String>();
			map.put(mySettingsListKey, data.get(i).getAccountDescription());
			map.put(mySettingsListValue, data.get(i).getUserEmail());
			listHash.add(map);
		}

		SimpleAdapter mSchedule = new SimpleAdapter(context, listHash,
				R.layout.accounts_list_layout, new String[] {
						mySettingsListKey, mySettingsListValue },
				new int[] { R.id.accounts_list_key,
						R.id.accounts_list_value });
		
		list.setAdapter(mSchedule);
	}
	
}
