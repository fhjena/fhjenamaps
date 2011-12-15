package eu.AndroidTraining.Dashboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseManager extends SQLiteOpenHelper {

	private static final String DB_NAME = "DB_NODES.db";
	private static final int DB_VERSION = 0;
	private static final String CREATE_CLASSES = 
			"CREATE TABLE  classes (" +
					"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"name" +
					";)";
	private static final String CLASSES_DROP = 
			"DROP TABLE IF EXISTS classes";
					


	public DataBaseManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CLASSES);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(CLASSES_DROP);
		onCreate(db);

	}

}
