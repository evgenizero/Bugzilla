package org.elsys.requests;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

public abstract class BugzillaHttpRequest extends AsyncTask<String, Void, JSONObject> {

	private static final String BASE_URl = "http://nightly.theenergybridge.com/rms/rest/";
	private static ProgressDialog dialog;
	
	private UsernamePasswordCredentials credentials;
	private boolean showDialog;
	
	protected Context context;

	protected BugzillaHttpRequest(Context context, String username, String password, boolean showDialog) {
		this.context = context;
		this.showDialog = showDialog;
		credentials = new UsernamePasswordCredentials(username, password);
	}
	
	protected void cancelDialog() {
		dialog.cancel();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (showDialog) {
			if (dialog != null && !dialog.getContext().equals(context)) {
				dialog.cancel();
				dialog = null;
			}
			if (dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setTitle("Logging...");
			dialog.setCancelable(true);
			dialog.show();
			dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
		}
	}

	@Override
	protected JSONObject doInBackground(String ... params) {
		URI uri;
		try {
			uri = new URI(BASE_URl + params[0]);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}

		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(null, -1), credentials);

		HttpGet request = new HttpGet(uri);
		try {
			String response = EntityUtils.toString(httpclient.execute(request).getEntity());
			return (JSONObject) new JSONTokener(response).nextValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
}