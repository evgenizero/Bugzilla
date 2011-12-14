package org.elsys;

import org.elsys.data.Account;
import org.elsys.utilities.Utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Main extends Activity {

	private SharedPreferences settings;

	private Account currentAccount;

	private Intent intent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
	}

	private boolean currentAccountAvailable() {
		settings = getSharedPreferences(Utilities.CURRENT_ACCOUNT, 0);
		if (!settings.getAll().isEmpty()) {
			currentAccount = new Account(settings.getString(Utilities.CURRENT_ACCOUNT_EMAIL, null),
					settings.getString(Utilities.CURRENT_ACCOUNT_PASSWORD, null),
					settings.getString(Utilities.CURRENT_ACCOUNT_URL, null));
			currentAccount.setAccountDescription(settings.getString(
					Utilities.CURRENT_ACCOUNT_DESCRIPTION, null));
			return true;
		}
		return false;
	}

	public void onActionProfileClick(View v) {

		if (!currentAccountAvailable()) {
			Toast.makeText(this, "No account selected.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void onActionSearchClick(View v) {
		Toast.makeText(this, "Action 2", Toast.LENGTH_SHORT).show();
	}

	public void onActionPostClick(View v) {
		Toast.makeText(this, "Action 3", Toast.LENGTH_SHORT).show();
	}

	public void onActionAccountsClick(View v) {
		intent = new Intent(this, org.elsys.AccountManager.class);
		startActivity(intent);
	}

	public void onActionBarAccountClick(View v) {
		intent = new Intent(this, org.elsys.AccountManager.class);
		startActivity(intent);
	}
	
}