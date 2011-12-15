package eu.AndroidTraining.Dashboard;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class Database extends Activity {
	
	private SQLiteDatabase mDataBase;
	private DataBaseManager mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mHelper = new DataBaseManager(this);
	}

	@Override
	protected void onPause() {
			super.onPause();
			mDataBase.close();
			
	}

	@Override
	protected void onResume() {
			super.onResume();
			mDataBase = mHelper.getReadableDatabase();
	}

}

