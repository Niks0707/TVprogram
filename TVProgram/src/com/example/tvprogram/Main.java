package com.example.tvprogram;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.content.DialogInterface;
import android.content.Intent;

public class Main extends Activity {

	public ArrayList<String> NameList = new ArrayList<String>();
	public ArrayList<String> AdressList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private ListView lv;
	String url;
	String begin = "option value=";
	String end = "</option>";
//	DatabaseHelper dbHelper;
//	SQLiteDatabase sdb;
//	private final String DATABASE_NAME = "TvProgram.db";
//	private final String DATABASE_TABLE_Cnannel_Names = "'CnanelNames'";
//	private final String DATABASE_TABLE_Channel_Adress = "'CnanelAdress'";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		url = "http://tvgid.ua/cities/1/";
		UpdateData();
		lv = (ListView) findViewById(R.id.listView1);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(Main.this, Channel.class);
				intent.putExtra("name", NameList.get(position));
				intent.putExtra("adress", AdressList.get(position));
				startActivity(intent);
			}
		});
	}

	public void UpdateData() {
		new NewThread().execute();
		adapter = new ArrayAdapter<String>(this, R.layout.list_item,
				R.id.product_name, NameList);
	}

	public class NewThread extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg) {

			Document doc;
			try {
				doc = Jsoup.connect(url).get();
				NameList.clear();

				int start = 0;
				int finish = 0;
				String sub;
				String html = doc.body().html();
				for (int i = 0; i < html.length() - begin.length(); i++) {
					start = i;
					start = html.indexOf(begin, start);
					if (start != -1) {
						finish = html.indexOf(end, start);
						sub = html.substring(start, finish);
						if (sub.substring(sub.lastIndexOf(">") + 1)
								.indexOf("&") == 0) {
							NameList.add(sub.substring(sub.lastIndexOf(";") + 1));
						}
						AdressList.add(sub.substring(sub.indexOf("=") + 2,
								sub.indexOf(">") - 1));
						if (finish != -1) {
							i = finish;
						} else {
							i = html.length() - begin.length();
						}
					} else {
						i = html.length() - begin.length();
					}
				}
				AdressList.remove(0);
				AdressList.remove(0);
				AdressList.remove(0);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			try {
//				putData(NameList, DATABASE_TABLE_Cnannel_Names);
//				NameList.clear();

//				for (String str : getData(DATABASE_TABLE_Cnannel_Names)) {
//					NameList.add(str);
//				}
				lv.setAdapter(adapter);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

//	public void putData(ArrayList<String> arg, String database_table)
//			throws IOException {
//
//		dbHelper = new DatabaseHelper(this, DATABASE_NAME, null, 1);
//		sdb = dbHelper.getWritableDatabase();
//		try {
//			sdb.execSQL("drop table " + database_table);
//		} catch (Exception ex) {
//		}
//		sdb.execSQL("create table " + database_table + " (" + BaseColumns._ID
//				+ " integer primary key autoincrement, " + database_table
//				+ " text not null);");
//		// ContentValues newValues = new ContentValues();
//		for (int i = 0; i < arg.size(); i++) {
//			// newValues.put(Integer.toString(i), arg.get(i));
//			sdb.execSQL("INSERT INTO " + database_table + " (" + database_table
//					+ ") VALUES ('" + arg.get(i) + "')");
//		}
//		// sdb.insert(database_table, null, newValues);
//	}
//
//	public ArrayList<String> getData(String database_table) throws IOException {
//		ArrayList<String> arg = new ArrayList<String>();
//		dbHelper = new DatabaseHelper(this, DATABASE_NAME, null, 1);
//		sdb = dbHelper.getWritableDatabase();
//		try {
//			// Cursor cursor = sdb.query(database_table,
//			// new String[] { database_table }, null, null, null, null,
//			// null);
//			Cursor cursor = sdb.rawQuery("SELECT * FROM " + database_table, null);
//			cursor.moveToFirst();
//			// cursor.moveToNext();
//			// if (cursor.isLast()) {
//			// Dialog("err", "isLast = " + cursor.isLast());
//			// } else {
//			// Dialog("err", "isLast = !" + cursor.isLast());
//			// }
//			for (; !cursor.isLast(); cursor.moveToNext()) {
//				arg.add(cursor.getString(cursor.getColumnIndex(database_table)));
//			}
//		} catch (Exception ex) {
//			Dialog("error", ex.getMessage());
//		}
//		return arg;
//	}
//
//	public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
//
//		// private final String DATABASE_CREATE_SCRIPT = "create table "
//		// + DATABASE_TABLE + " (" + BaseColumns._ID
//		// + " integer primary key autoincrement, " + Program
//		// + " text not null);";
//
//		public DatabaseHelper(Context context, String name,
//				CursorFactory factory, int version,
//				DatabaseErrorHandler errorHandler) {
//			super(context, name, factory, version, errorHandler);
//		}
//
//		public DatabaseHelper(Context context, String name,
//				CursorFactory factory, int version) {
//			super(context, name, factory, version);
//		}
//
//		@Override
//		public void onCreate(SQLiteDatabase db) {
//
//		}
//
//		@Override
//		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//		}
//	}

	@SuppressWarnings("unused")
	private void Dialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() { // Кнопка ОК
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // Отпускает диалоговое окно
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			UpdateData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
