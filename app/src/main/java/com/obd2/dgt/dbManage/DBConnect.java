package com.obd2.dgt.dbManage;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.obd2.dgt.R;
import com.obd2.dgt.utils.MyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DBConnect extends SQLiteOpenHelper {

    static String m_db_name = "dgt_db.sqlite";
    static String m_db_path = "";
    private SQLiteDatabase m_db = null;
    private String TAG = "DBConnect";


    public DBConnect(Context context) {
        super(MyUtils.mContext, m_db_name, null, 1);
        m_db_path = context.getFilesDir().getPath() + "/";
    }

    @Override
    public synchronized void close() {
        if (m_db != null)
            m_db.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDatabase() {
        String path = m_db_path + m_db_name;
        try {
            File f = new File(path);
            if (!f.exists()) {
                copyDatabase();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyDatabase() throws IOException {
        try {
            Resources r = MyUtils.mContext.getResources();
            InputStream in = r.openRawResource(R.raw.dgt_db);

            String outPath = m_db_path + m_db_name;

            File dir = new File(m_db_path);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            File f = new File(outPath);
            if (!f.exists()) {
                FileOutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[1024];
                int length = 0;
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
                out.close();
                in.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDatabase() {
        try {
            String path = m_db_path + m_db_name;
            m_db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            throw new Error("Unabel open databse.");
        }
    }

    public SQLiteDatabase getDatabase() throws Exception {
        openDatabase();
        if (m_db != null && m_db.isOpen()) {
            return m_db;
        } else {
            getDatabase();
        }
        return m_db;
    }

    public Cursor sqlSelect(String table) {
        Cursor cursor = null;

        String query = "SELECT * FROM  " + table;
        cursor = m_db.rawQuery(query, null);
        return cursor;
    }

    public Cursor sqlSelect(String table, String fieldname, String where) {
        Cursor cursor = null;

        String str_where = "";
        if (!where.isEmpty())
            str_where = "  where " + where;
        String query = "SELECT " + fieldname + " FROM  " + table + str_where;
        cursor = m_db.rawQuery(query, null);
        return cursor;
    }

    public Cursor sqlSelect(String table, String field, boolean order) {
        Cursor cursor = null;

        String query = "SELECT * FROM " + table + " ORDER BY " + field;
        if (order) {
            query += " ASC";
        } else {
            query += " DESC";
        }
        cursor = m_db.rawQuery(query, null);

        return cursor;
    }

    public Cursor sqlSelect(String table, String[] fields) {
        Cursor cursor = null;

        String orderByField = "";
        for (int i = 0; i < fields.length; i++) {
            if(i == 0)
                orderByField += fields[i];
            else
                orderByField += "," + fields[i];
        }
        String query = "SELECT * FROM " + table + " ORDER BY " + orderByField +" ASC";
        cursor = m_db.rawQuery(query, null);

        return cursor;
    }

    public void sqlInsert(String table, String[][] rows) {
        if (rows.length > 0) {
            ContentValues row = new ContentValues();
            for (int i = 0; i < rows.length; i++) {
                row.put(rows[i][0], rows[i][1]);
            }
            m_db.insert(table, null, row);
        }
    }

    public void sqlInsert(String table, List<String[]> rows) {
        if (rows.size() > 0) {
            ContentValues row = new ContentValues();
            for (int i = 0; i < rows.size(); i++) {
                row.put(rows.get(i)[0], rows.get(i)[1]);
            }
            m_db.insert(table, null, row);
        }
    }

    public long sqlinsertOrThrow(String table, String[][] rows) {
        long index_id = 0;
        if (rows.length > 0) {
            ContentValues row = new ContentValues();
            for (int i = 0; i < rows.length; i++) {
                row.put(rows[i][0], rows[i][1]);
            }
            index_id = m_db.insertOrThrow(table, null, row);
        }
        return index_id;
    }

    public void sqlUpdate(String table, String[][] rows, String where) {
        if (rows.length > 0) {
            ContentValues row = new ContentValues();
            for (int i = 0; i < rows.length; i++) {
                row.put(rows[i][0], rows[i][1]);
            }
            m_db.update(table, row, where, null);
        }
    }

    public void sqlDelete(String table, String where) {
        m_db.delete(table, where, null);
    }

    public void sqlDeleteAll(String table) {
        m_db.delete(table, "", null);
    }
}
