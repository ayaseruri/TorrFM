package ayaseruri.torr.torrfm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ayaseruri.torr.torrfm.objectholder.ChannelInfo;
import ayaseruri.torr.torrfm.objectholder.SongInfo;

/**
 * Created by ayaseruri on 15/12/14.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "torrFM.db";
    private static final int DATABASE_VERSION = 1;
    private static DBHelper instance;

    private Map<String, Dao> daos = new HashMap<String, Dao>();

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (null == instance) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ChannelInfo.class);
            TableUtils.createTable(connectionSource, SongInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ChannelInfo.class, true);
            TableUtils.dropTable(connectionSource, SongInfo.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Dao getDBDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (!daos.containsKey(className)) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        } else {
            dao = daos.get(className);
        }
        return dao;
    }

    @Override
    public void close() {
        super.close();
        daos = null;
    }
}
