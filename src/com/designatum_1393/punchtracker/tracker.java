package com.designatum_1393.punchtracker;

import android.app.ListActivity;
import android.os.Bundle;
import java.io.File;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.app.Dialog;
import android.widget.SimpleCursorAdapter;
import android.widget.EditText;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnClickListener;

//menu
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

//context menu
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

import android.util.Log;
import android.view.MotionEvent;
import android.view.Display;
import android.content.ContentResolver;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.text.util.Linkify;
import android.content.pm.PackageInfo;

public class tracker extends ListActivity
{

	private File dbFile = new File("/data/data/com.designatum_1393.punchtracker/databases/", "data");
	private subsDbAdapter mDbHelper;
	private Cursor mSubsCursor;
	private int width = 0;
	
	private static final String TAG = "PunchTracker: MAIN";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		
		mDbHelper = new subsDbAdapter(this);
		mDbHelper.open();
		mSubsCursor = mDbHelper.fetchAllSubs();
		
		fillData();
		registerForContextMenu(getListView());
    }
	
	@Override
	protected void onStop()
	{
		super.onStop();
		mDbHelper.close();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	
		mDbHelper.open();
		fillData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_menu, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.add_item:
				addItem();
				return true;
			case R.id.menu_settings:
				aboutDialog();
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.entry_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId())
		{
			case R.id.delete_item:
				deleteDialog(info.position);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	public void deleteDialog(int item){
		AlertDialog.Builder dd = new AlertDialog.Builder(this);
		dd.setIcon(R.drawable.icon);
		dd.setTitle("Are you sure?");
		dd.setView(LayoutInflater.from(this).inflate(R.layout.delete_selected_dialog,null));
		final int itemSelected = item;
		dd.setPositiveButton("Yes, I can forgive them", 
		new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
			deleteItem(itemSelected);
			}
		});
		
		dd.setNegativeButton("NO, BWAHAHAHA", 
		new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
			}
		});
		
		dd.show();
	}
	
	public void aboutDialog(){
		final TextView message = new TextView(this);
		
		try{
			PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
			String versionInfo = pInfo.versionName;

			String versionString = String.format("Version: %s", versionInfo);
			String authors = "Authors: Sean Barag and Vincent Tran";
			String website = "Visit our website: http://1393Designatum.com";
			String cp = "\u00A92011 1393 Designatum, All Rights Reserved.";

			message.setPadding(10, 10, 10, 10);
			message.setText(versionString + "\n\n" + authors + "\n\n" + website + "\n\n" +	cp);
			Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES);
			Linkify.addLinks(message, Linkify.WEB_URLS);
			
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setIcon(R.drawable.icon);
			ad.setTitle("About");
			ad.setView(message);

			ad.setPositiveButton("That's nice", 
			new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
				}
			});
			ad.show();
			
		}catch(Exception e){}

	}
	
/*-------------------------------------------------------------
-------------------- Database Manipulation --------------------
-------------------------------------------------------------*/
	
	private void fillData()
	{
		mSubsCursor = mDbHelper.fetchAllSubs();
		mSubsCursor.setNotificationUri(getContentResolver(), Uri.parse("content://com.designatum_1393.punchtracker/dbUpdate"));
		startManagingCursor(mSubsCursor);
				
		String[] from = new String[]{subsDbAdapter.KEY_NAME, subsDbAdapter.KEY_NUM_PUNCH};
		int[] to = new int[]{R.id.ShortTextMain, R.id.LongTextMain};
			
		// Now create an array adapter and set it to display using the stock android row
		SimpleCursorAdapter subsAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.subs_row, mSubsCursor, from, to);

		setListAdapter(subsAdapter);
	}
	
	public void addItem()
	{
		final Dialog dialog = new Dialog(tracker.this);
		dialog.setContentView(R.menu.maindialog);
		dialog.setTitle("Adding a victim");
		dialog.setCancelable(true);
		
		TextView name_text = (TextView) dialog.findViewById(R.id.name_label);
		final EditText name_input = (EditText) dialog.findViewById(R.id.name_entry);

		Button cancel_button = (Button) dialog.findViewById(R.id.cancelButton);
		cancel_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		Button okay_button = (Button) dialog.findViewById(R.id.okayButton);
		okay_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String name = name_input.getText().toString();
				mDbHelper.createSub(name);
				fillData();
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void deleteItem(int item)
	{
		Cursor c = mSubsCursor;
		c.moveToPosition(item);
		final String old_name = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_NAME));
		final String old_num  = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_NUM_PUNCH));
		mDbHelper.deleteSub(old_name, old_num);
		fillData();
	}
}
