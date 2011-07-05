package com.designatum_1393.punchtracker;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.content.Context;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.AttributeSet;
import android.text.method.PasswordTransformationMethod;
import android.widget.TextView;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import java.lang.Integer;
import android.database.Cursor;
import android.content.ContentResolver;
import android.net.Uri;


class tracker_item extends RelativeLayout
{
	private String _name;
	private String _num;

	private Button plusBtn;
	private Button minusBtn;
	private String[] arr;
	
	private subsDbAdapter mDbHelper;
	private Cursor mSubsCursor;
	
	private Context cxt;

	public tracker_item(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		cxt = context;
	}


	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
				
		plusBtn = (Button) findViewById(R.id.plusButton);
		minusBtn = (Button) findViewById(R.id.minusButton);

		plusBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				_name = ((TextView) findViewById(R.id.ShortTextMain)).getText().toString();
				_num  = ((TextView) findViewById(R.id.LongTextMain)).getText().toString();
				
				performClick(1, _name, _num);
			}
		});
		
		minusBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				_name = ((TextView) findViewById(R.id.ShortTextMain)).getText().toString();
				_num  = ((TextView) findViewById(R.id.LongTextMain)).getText().toString();
				
				performClick(0, _name, _num);
			}
		});
	}
	
	private void performClick(int choice, String name, String num)
	{
		cxt.getContentResolver().notifyChange(Uri.parse("content://com.designatum_1393.punchtracker/dbUpdate"), null);
		mDbHelper = new subsDbAdapter(cxt);
		mDbHelper.open();
		mDbHelper.updateSub(_name, _num, choice);
		mDbHelper.close();
	}

}
