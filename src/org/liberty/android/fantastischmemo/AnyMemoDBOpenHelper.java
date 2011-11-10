package org.liberty.android.fantastischmemo;

import org.liberty.android.fantastischmemo.domain.Card;
import org.liberty.android.fantastischmemo.domain.Category;
import org.liberty.android.fantastischmemo.domain.Deck;
import org.liberty.android.fantastischmemo.domain.Filter;
import org.liberty.android.fantastischmemo.domain.Setting;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import com.j256.ormlite.dao.Dao;

import com.j256.ormlite.support.ConnectionSource;

import com.j256.ormlite.table.TableUtils;

import android.content.Context;

import java.sql.SQLException;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

public class AnyMemoDBOpenHelper extends OrmLiteSqliteOpenHelper {

    private final String TAG = getClass().getSimpleName();

    private static String dbPath = null;

	private static AnyMemoDBOpenHelper helper = null;

    private static final int CURRENT_VERSION = 2;

    private Dao<Card, Integer> cardDao = null;
    
    private Dao<Deck, Integer> deckDao = null;

    private Dao<Setting, Integer> settingDao = null;

    private Dao<Filter, Integer> filterDao = null;

    private Dao<Category, Integer> categoryDao = null;

    public AnyMemoDBOpenHelper(Context context, String dbpath) {
        super(context, dbpath, null, CURRENT_VERSION);
    }

	public static synchronized AnyMemoDBOpenHelper getHelper(Context context, String dbpath)
        throws SQLException {
		if (helper == null) {
			helper = new AnyMemoDBOpenHelper(context, dbpath);
		} else if (!dbpath.equals(dbPath)) {
            Log.e("AnyMemoDBOpenHelper.getHelper",
                    "Open two database at the same. Please close the previous connection");
            throw new SQLException("Open two database at the same. Please close the previous connection");
        } else {
            Log.i("AnyMemoDBOpenHelper.getHelper", "Reuse database helper");
        }
		return helper;
	}

    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.v(TAG, "Now we are creating a new database!");

        try {
            TableUtils.createTable(connectionSource, Card.class);
            TableUtils.createTable(connectionSource, Deck.class);
            TableUtils.createTable(connectionSource, Setting.class);
            TableUtils.createTable(connectionSource, Filter.class);
            TableUtils.createTable(connectionSource, Category.class);
            if (database.getVersion() == 0) {
                //onUpgrade(database, connectionSource, 0, CURRENT_VERSION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database creation error: " + e.toString());
        }
    }

    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // Database before AnyMemo 9.0
        Log.v(TAG, "Old version" + oldVersion + " new version: " + newVersion);
        if (oldVersion < 2) {
            try {
                getCardDao();
                cardDao.queryRaw("insert into cards (ordinal, question, answer) select _id as ordinal, question, answer from dict_tbl");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Database creation error: " + e.toString());
            }
        }


    }

    public Dao<Card, Integer> getCardDao() throws SQLException {
        if (cardDao == null) {
            cardDao = getDao(Card.class);
        }
        return cardDao;
    }

    public Dao<Deck, Integer> getDeckDao() throws SQLException {
        if (deckDao == null) {
            deckDao = getDao(Deck.class);
        }
        return deckDao;
    }

    public Dao<Setting, Integer> getSettingDao() throws SQLException {
        if (settingDao == null) {
            settingDao = getDao(Setting.class);
        }
        return settingDao;
    }

    public Dao<Filter, Integer> getFilterDao() throws SQLException {
        if (filterDao == null) {
            filterDao = getDao(Filter.class);
        }
        return filterDao;
    }

    public Dao<Category, Integer> getCategoryDao() throws SQLException {
        if (categoryDao == null) {
            categoryDao = getDao(Category.class);
        }
        return categoryDao;
    }
}

