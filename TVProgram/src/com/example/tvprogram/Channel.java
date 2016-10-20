package com.example.tvprogram;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Channel extends Activity {

	class Data {
		public String ChannelUrl;
		public ArrayList<String> TimeList = new ArrayList<String>();
		public ArrayList<String> UrlList = new ArrayList<String>();
		public ArrayList<String> DateList = new ArrayList<String>();
		public ArrayList<String>[] ProgramList = (ArrayList<String>[]) new ArrayList[7];
		public int DayOfWeek;
	}

	ArrayList<String> currentProgram = new ArrayList<String>();
	String BasicUrl = "http://tvgid.ua";
	Data data;
	ArrayAdapter<String> adapter;
	ListView lv;
	String url;
	String begin = "class=\"time\"";
	String end = "</a>";
	String name;
	String h1 = "</h1>";
	String id = "day-week-num-active";
	String beginUrl = "<a href=";

	// DatabaseHelper dbHelper;
	// SQLiteDatabase sdb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel);

		data = new Data();
		name = getIntent().getStringExtra("name");
		data.ChannelUrl = getIntent().getStringExtra("adress");
		data.UrlList.add(getIntent().getStringExtra("adress"));
		url = BasicUrl + data.UrlList.get(0);
		lv = (ListView) findViewById(R.id.listView1);
		data.ProgramList[0] = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, R.layout.list_item,
				R.id.product_name, currentProgram);
		UpdateData();
	}

	// public void ShowData() {
	// // try {
	// // ProgramList = getData();
	// // } catch (IOException e) {
	// // e.printStackTrace();
	// // }
	// // if (ProgramList.isEmpty()) {
	// // UpdateData();
	// // }
	// // android.widget.a
	// }

	public void UpdateData() {
		NewThread thread = new NewThread();
		if (thread.getStatus() == Status.RUNNING) {
			thread.cancel(false);
		}
		thread.execute();
		lv.setAdapter(adapter);
	}

	public class NewThread extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg) {

			Document doc;
			try {
				doc = Jsoup.connect(url).get();

				String html = doc.body().html();
				data.UrlList = getUrlList(html);
				data.ProgramList[0].clear();
				data.ProgramList[0] = getProgramList(html);

				for (int i = 1; i < 7; i++) {
					String url = data.UrlList.get(i);
					url = BasicUrl + url.substring(0, url.indexOf("\">") - 1);
					doc = Jsoup.connect(url).get();
					html = doc.body().html();
					data.ProgramList[i] = getProgramList(html);
				}

				Calendar local = new GregorianCalendar();
				data.DayOfWeek = local.get(Calendar.DAY_OF_WEEK);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return null;
		}

		private ArrayList<String> getProgramList(String html) {

			ArrayList<String> programList = new ArrayList<String>();
			String sub;
			int start = 0;
			int finish = 0;
			html = html.substring(html.indexOf(h1));
			for (int i = 0; i < html.length() - begin.length(); i++) {
				start = i;
				start = html.indexOf(begin, start);
				if (start != -1) {
					finish = html.indexOf(end, start);
					sub = html.substring(start, finish);
					programList.add(sub.substring(sub.indexOf(":") + -2,
							sub.indexOf(":") + 3)
							+ "  " + sub.substring(sub.lastIndexOf(">") + 1));
					if (finish != -1) {
						i = finish;
					} else {
						i = html.length() - begin.length();
					}
				} else {
					i = html.length() - begin.length();
				}
			}
			return programList;
		}

		private ArrayList<String> getUrlList(String html) {

			ArrayList<String> urlList = new ArrayList<String>();
			String sub;
			int start = 0;
			int finish = 0;
			html = html
					.substring(
							html.indexOf(id),
							html.indexOf(end,
									html.lastIndexOf(id.substring(0, 10))) + 5);
			for (int i = 0; i < html.length() - beginUrl.length(); i++) {
				start = i;
				start = html.indexOf(beginUrl, start);
				if (start != -1) {
					finish = html.indexOf(end, start);
					sub = html.substring(start + 9, finish);
					urlList.add(sub);
					if (finish != -1) {
						i = finish;
					} else {
						i = html.length() - beginUrl.length();
					}
				} else {
					i = html.length() - beginUrl.length();
				}
			}
			return urlList;
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				currentProgram.clear();
				for (String s : data.ProgramList[0]) {
					currentProgram.add(s);
				}
				SetSegmentedButton();
				lv.setAdapter(adapter);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public void SetSegmentedButton() {
		String[] array_day = getResources().getStringArray(R.array.array_day);
		((SegmentedControlButton) findViewById(R.id.option1)).setChecked(true);
		for (int i = 0; i < 7; i++) {
			SegmentedControlButton segmentBtn = (SegmentedControlButton) findViewById(R.id.option1
					+ i);
			segmentBtn.setText(array_day[(data.DayOfWeek + i + 5) % 7]
					+ ","
					+ data.UrlList.get(i).substring(
							data.UrlList.get(i).indexOf("\">") + 2));
			segmentBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					currentProgram.clear();
					for (String s : data.ProgramList[v.getId() - R.id.option1]) {
						currentProgram.add(s);
					}
					lv.setAdapter(adapter);
				}
			});
		}
	}

	// public void putData(ArrayList<String> arg) throws IOException {
	// // FileOutputStream fos = new FileOutputStream("temp.out");
	// // ObjectOutputStream oos = new ObjectOutputStream(fos);
	// // oos.writeObject(arg);
	// // oos.flush();
	// // oos.close();
	// dbHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
	//
	// sdb = dbHelper.getWritableDatabase();
	// try {
	// sdb.execSQL("drop table " + dbHelper.DATABASE_TABLE);
	// } catch (Exception ex) {
	// }
	// sdb.execSQL(dbHelper.DATABASE_CREATE_SCRIPT);
	// ContentValues newValues = new ContentValues();
	// for (String str : data.ProgramList[0]) {
	// newValues.put(dbHelper.Program, str);
	// sdb.execSQL("INSERT INTO " + dbHelper.DATABASE_TABLE + " ("
	// + dbHelper.Program + ")VALUES (" + str + ")");
	// }
	// // sdb.insert(dbHelper.DATABASE_TABLE, null, newValues);
	// }
	//
	// public ArrayList<String> getData() throws IOException {
	// ArrayList<String> arg = new ArrayList<String>();
	// // FileInputStream fis = new FileInputStream("temp.out");
	// // ObjectInputStream oin = new ObjectInputStream(fis);
	// // try {
	// // for(Object object: ((ArrayList<String>)oin.readObject()).toArray()){
	// // arg.add((String)object);
	// // }
	// // } catch (ClassNotFoundException e) {
	// // e.printStackTrace();
	// // } finally {
	// // oin.close();
	// // }
	// try {
	// Cursor cursor = sdb.query(dbHelper.DATABASE_TABLE,
	// new String[] { dbHelper.Program }, null, null, null, null,
	// null);
	// cursor.moveToFirst();
	// // cursor.moveToNext();
	// if (cursor.isLast()) {
	// Dialog("err", "isLast = " + cursor.isLast());
	// } else {
	// Dialog("err", "isLast = !" + cursor.isLast());
	// }
	// for (; !cursor.isLast(); cursor.moveToNext()) {
	// arg.add(cursor.getString(cursor
	// .getColumnIndex(dbHelper.Program)));
	// }
	// } catch (Exception ex) {
	// Dialog("error", ex.getMessage());
	// }
	// return arg;
	// }
	//
	// public class DatabaseHelper extends SQLiteOpenHelper implements
	// BaseColumns {
	//
	// private final String DATABASE_NAME = "Program.db";
	// private final String DATABASE_TABLE = "'" + name + "'";
	// public final String Program = "Program";
	//
	// private final String DATABASE_CREATE_SCRIPT = "create table "
	// + DATABASE_TABLE + " (" + BaseColumns._ID
	// + " integer primary key autoincrement, " + Program
	// + " text not null);";
	//
	// public DatabaseHelper(Context context, String name,
	// CursorFactory factory, int version,
	// DatabaseErrorHandler errorHandler) {
	// super(context, name, factory, version, errorHandler);
	// }
	//
	// public DatabaseHelper(Context context, String name,
	// CursorFactory factory, int version) {
	// super(context, name, factory, version);
	// }
	//
	// @Override
	// public void onCreate(SQLiteDatabase db) {
	//
	// }
	//
	// @Override
	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	// {
	//
	// }
	// }

	// public void UpdateData(){
	// // определение данных
	// lv = (ListView) findViewById(R.id.listView1);
	// // запрос к нашему отдельному поток на выборку данных
	// new NewThread().execute();
	// // Добавляем данные для ListView
	// adapter = new ArrayAdapter<String>(this,R.layout.list_item,
	// R.id.product_name, titleList);
	//
	// }
	//
	// public class NewThread extends AsyncTask<String, Void, String> {
	//
	// @Override
	// protected String doInBackground(String... arg) {
	//
	// Document doc;
	// try {
	// doc = Jsoup.connect(url).get();
	// title = doc.select(".b-channel__title__link");
	// title = doc.getAllElements();
	// titleList.clear();
	// // }
	// titleList.add(doc.body().toString());
	// title = null;
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	//
	// try{
	// // после запроса обновляем листвью
	// lv.setAdapter(adapter);
	//
	// }
	// catch(Exception ex){
	// ex.printStackTrace();
	// }
	// }
	// }

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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.channel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			UpdateData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}