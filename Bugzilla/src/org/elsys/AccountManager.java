package org.elsys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elsys.data.Account;
import org.elsys.quickaction.ActionItem;
import org.elsys.quickaction.QuickAction;
import org.elsys.utilities.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RemoteViews.ActionException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AccountManager extends Activity {

	private static final int ADD_ACC_DIALOG = 1;
	private static final int DELETE_ACC_DIALOG = 2;

	private Dialog dialog;

	private ListView list;

	private SharedPreferences settings;
	SharedPreferences.Editor editor;

	private Set<String> accountsIds;
	private Map<String, String> accountsUrlMap;
	private Map<String, String> accountsDescriptionMap;
	private Map<String, String> accountsEmailMap;
	private Map<String, String> accountPasswordMap;

	private ArrayList<Account> accountList = new ArrayList<Account>();

	private EditText urlEditText;
	private EditText emailEditText;
	private EditText passwordEditText;
	private EditText descriptionEditText;
	
	private Button addAccount; 

	private Intent intent;

	private TextView numberOfAccounts;

	private static final int ID_EDIT = 1;
	private static final int ID_DELETE = 2;

	private Integer accountToDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_manager);

		list = (ListView) findViewById(R.id.accounts_list);
		numberOfAccounts = (TextView) findViewById(R.id.numberOfAccounts);

		loadList();

		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				settings = getSharedPreferences(Utilities.CURRENT_ACCOUNT, 0);
				editor = settings.edit();

				Account currentAcc = accountList.get(arg2);

				editor.putString(Utilities.CURRENT_ACCOUNT_EMAIL,
						currentAcc.getUserEmail());
				editor.putString(Utilities.CURRENT_ACCOUNT_PASSWORD,
						currentAcc.getUserPassword());
				editor.putString(Utilities.CURRENT_ACCOUNT_URL,
						currentAcc.getAccountUrl());
				editor.putString(Utilities.CURRENT_ACCOUNT_DESCRIPTION,
						currentAcc.getAccountDescription());
				editor.putInt(Utilities.CURRENT_ACCOUNT_ID,
						currentAcc.getAccountId());

				editor.commit();

				intent = new Intent(AccountManager.this, org.elsys.Main.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		ActionItem editItem = new ActionItem(ID_EDIT, "Edit", getResources()
				.getDrawable(org.elsys.quickaction.R.drawable.ic_add));
		ActionItem deleteItem = new ActionItem(ID_DELETE, "Delete",
				getResources().getDrawable(
						org.elsys.quickaction.R.drawable.ic_up));

		final QuickAction mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(editItem);
		mQuickAction.addActionItem(deleteItem);

		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						//ActionItem actionItem = quickAction.getActionItem(pos);

						if (actionId == ID_DELETE) {
							showDialog(DELETE_ACC_DIALOG);
						} else {
							showDialog(ADD_ACC_DIALOG);
						}
					}
				});

		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				accountToDelete = new Integer(arg2);
				mQuickAction.show(arg1);
				return false;
			}
		});

	}

	
	public void onAddAccountButton(View v) {
		showDialog(ADD_ACC_DIALOG);
	}

	protected Dialog onCreateDialog(int id) {
		
		final int dialogId = id;
		
		switch (id) {
		case ADD_ACC_DIALOG: {

			setDialogSettings(this);
			setDialogViews();

			addAccount.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					addAccount();
				}

			});

			Button cancel = (Button) dialog
					.findViewById(R.id.cancel_acc_button);
			cancel.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					dialog.cancel();
				}
			});

			dialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
					removeDialog(dialogId);
				}
			});

			break;
		}
		case DELETE_ACC_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete this account?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									deleteAccount();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			dialog = alert;
			break;
		}
		}

		return dialog;
	}

	private void setDialogViews() {
		urlEditText = (EditText) dialog
				.findViewById(R.id.url_add_acc_dialog);
		emailEditText = (EditText) dialog
				.findViewById(R.id.email_add_acc_dialog);
		passwordEditText = (EditText) dialog
				.findViewById(R.id.password_add_acc_dialog);
		descriptionEditText = (EditText) dialog
				.findViewById(R.id.description_add_acc_dialog);
		addAccount = (Button) dialog.findViewById(R.id.add_acc_button);
		
		if(accountToDelete != null) {
			Account acc = accountList.get(accountToDelete);
			urlEditText.setText(acc.getAccountUrl());
			emailEditText.setText(acc.getUserEmail());
			passwordEditText.setText(acc.getUserPassword());
			descriptionEditText.setText(acc.getAccountDescription());
		}
	}

	private void setDialogSettings(AccountManager accountManager) {
		dialog = new Dialog(this);
		dialog.getWindow();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_acc_dialog);
	}

	private void addAccount() {
		
		int accId;
		
		String url = null;
		String email = null;
		String password = null;
		String description = null;
		
		url = urlEditText.getText().toString();
		email = emailEditText.getText().toString();
		password = passwordEditText.getText().toString();
		description = descriptionEditText.getText().toString();
		
		if ("".equals(url) || "".equals(email)
				|| "".equals(password) || "".equals(description)) {

			Toast.makeText(AccountManager.this,
					"You must fill all the fields.",
					Toast.LENGTH_SHORT).show();
		} else {
			accId = getGeneratedAccountId();
			
			persistAccount(accId, url, email, password, description);

			dialog.dismiss();
			removeDialog(ADD_ACC_DIALOG);
			
			loadList();
		}
	}


	private void persistAccount(int accId, String url, String email,
			String password, String description) {
		persistAccountData(Utilities.ACCOUNT_URL, accId, url);
		persistAccountData(Utilities.ACCOUNT_DESCRIPTION, accId, description);
		persistAccountData(Utilities.ACCOUNT_EMAIL, accId, email);
		persistAccountData(Utilities.ACCOUNT_PASSWORD, accId, password);
	}

	private void persistAccountData(String sharedPreferences, int accId, String data) {
		settings = getSharedPreferences(
				sharedPreferences, 0);
		editor = settings.edit();
		editor.putString(String.valueOf(accId), data);
		editor.commit();
	}
	
	private int getGeneratedAccountId() {
		int accId;
		settings = getSharedPreferences(Utilities.ACCOUNT_URL,
				0);

		while (settings
				.getAll()
				.keySet()
				.contains(
						String.valueOf((accId = Account
								.generateAccountId())))) {}
		return accId;
	}
	
	private void deleteAccount() {

		if (accountToDelete != null) {
			String id = String.valueOf(accountList.get(accountToDelete)
					.getAccountId());

			accountToDelete = null;
			
			removeAccount(id);

			settings = getSharedPreferences(Utilities.CURRENT_ACCOUNT, 0);
			if(!settings.getAll().isEmpty()) {
				if(String.valueOf(settings.getInt(Utilities.CURRENT_ACCOUNT_ID, 100)).equals(id)) {
					editor = settings.edit();
					editor.clear().commit();
				}
			}
			
			loadList();

			Toast.makeText(this, "Account deleted.", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Problem deleting account.",
					Toast.LENGTH_SHORT).show();
		}

	}


	private void removeAccount(String accountId) {
		deleteAccountData(Utilities.ACCOUNT_URL, accountId);
		deleteAccountData(Utilities.ACCOUNT_EMAIL, accountId);
		deleteAccountData(Utilities.ACCOUNT_PASSWORD, accountId);
		deleteAccountData(Utilities.ACCOUNT_DESCRIPTION, accountId);
	}

	private void deleteAccountData(String sharedPreferences, String id) {
		settings = getSharedPreferences(sharedPreferences, 0);
		editor = settings.edit();
		editor.remove(id).commit();
	}


	@SuppressWarnings("unchecked")
	public void loadList() {

		accountList.clear();

		Account account;
		String accUrl;
		String accEmail;
		String accDescription;
		String accPassword;
		String accId;

		settings = getSharedPreferences(Utilities.ACCOUNT_URL, 0);
		accountsIds = settings.getAll().keySet();
		if (!accountsIds.isEmpty()) {

			accountsUrlMap = (Map<String, String>) settings.getAll();
			settings = getSharedPreferences(Utilities.ACCOUNT_DESCRIPTION, 0);
			accountsDescriptionMap = (Map<String, String>) settings.getAll();
			settings = getSharedPreferences(Utilities.ACCOUNT_EMAIL, 0);
			accountsEmailMap = (Map<String, String>) settings.getAll();
			settings = getSharedPreferences(Utilities.ACCOUNT_PASSWORD, 0);
			accountPasswordMap = (Map<String, String>) settings.getAll();

			Iterator<String> iterator = accountsIds.iterator();

			while (iterator.hasNext()) {

				accId = iterator.next();
				accUrl = accountsUrlMap.get(accId);
				accEmail = accountsEmailMap.get(accId);
				accDescription = accountsDescriptionMap.get(accId);
				accPassword = accountPasswordMap.get(accId);

				account = new Account(accEmail, accPassword, accUrl);
				account.setAccountDescription(accDescription);
				account.setAccountId(Integer.valueOf(accId));

				accountList.add(account);
			}
		}
		Utilities.updateListView(list, this, accountList);

		numberOfAccounts.setText(accountsIds == null ? "0" : String
				.valueOf(accountsIds.size()));
	}

}
