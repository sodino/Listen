package lab.sodino.soer.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import lab.sodino.soer.orm.entity.AudioContentInfo;
import lab.sodino.soer.orm.entity.AudioFileInfo;
import lab.sodino.soer.orm.entity.Lyrics;

/**
 * Created by sodino on 15-11-30.
 */
public class OrmSqliteHelper extends OrmLiteSqliteOpenHelper {

    public static final String DB_NAME = "soer";
    public static final int DB_VERSION = 1;

    public OrmSqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Lyrics.class);
            TableUtils.createTableIfNotExists(connectionSource, AudioFileInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, AudioContentInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (oldVersion == 0) {
            update$0to1(sqLiteDatabase, connectionSource);
            oldVersion = 1;
        }
    }

    private void update$0to1(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

    }
}
