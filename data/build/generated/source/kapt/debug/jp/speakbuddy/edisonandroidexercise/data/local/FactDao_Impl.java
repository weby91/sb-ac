package jp.speakbuddy.edisonandroidexercise.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.paging.LimitOffsetPagingSource;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class FactDao_Impl implements FactDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FactEntity> __insertionAdapterOfFactEntity;

  public FactDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFactEntity = new EntityInsertionAdapter<FactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `facts` (`id`,`fact`,`timestamp`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FactEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getFact() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getFact());
        }
        statement.bindLong(3, entity.getTimestamp());
      }
    };
  }

  @Override
  public Object insertFact(final FactEntity fact, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFactEntity.insert(fact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<FactEntity> getLatestFact() {
    final String _sql = "SELECT * FROM facts ORDER BY id DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"facts"}, new Callable<FactEntity>() {
      @Override
      @Nullable
      public FactEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFact = CursorUtil.getColumnIndexOrThrow(_cursor, "fact");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final FactEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpFact;
            if (_cursor.isNull(_cursorIndexOfFact)) {
              _tmpFact = null;
            } else {
              _tmpFact = _cursor.getString(_cursorIndexOfFact);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new FactEntity(_tmpId,_tmpFact,_tmpTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public PagingSource<Integer, FactEntity> getSavedFacts() {
    final String _sql = "SELECT * FROM facts ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new LimitOffsetPagingSource<FactEntity>(_statement, __db, "facts") {
      @Override
      @NonNull
      protected List<FactEntity> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfFact = CursorUtil.getColumnIndexOrThrow(cursor, "fact");
        final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(cursor, "timestamp");
        final List<FactEntity> _result = new ArrayList<FactEntity>(cursor.getCount());
        while (cursor.moveToNext()) {
          final FactEntity _item;
          final int _tmpId;
          _tmpId = cursor.getInt(_cursorIndexOfId);
          final String _tmpFact;
          if (cursor.isNull(_cursorIndexOfFact)) {
            _tmpFact = null;
          } else {
            _tmpFact = cursor.getString(_cursorIndexOfFact);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = cursor.getLong(_cursorIndexOfTimestamp);
          _item = new FactEntity(_tmpId,_tmpFact,_tmpTimestamp);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public PagingSource<Integer, FactEntity> searchSavedFacts(final String query) {
    final String _sql = "SELECT * FROM facts WHERE fact LIKE '%' || ? || '%' ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return new LimitOffsetPagingSource<FactEntity>(_statement, __db, "facts") {
      @Override
      @NonNull
      protected List<FactEntity> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfFact = CursorUtil.getColumnIndexOrThrow(cursor, "fact");
        final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(cursor, "timestamp");
        final List<FactEntity> _result = new ArrayList<FactEntity>(cursor.getCount());
        while (cursor.moveToNext()) {
          final FactEntity _item;
          final int _tmpId;
          _tmpId = cursor.getInt(_cursorIndexOfId);
          final String _tmpFact;
          if (cursor.isNull(_cursorIndexOfFact)) {
            _tmpFact = null;
          } else {
            _tmpFact = cursor.getString(_cursorIndexOfFact);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = cursor.getLong(_cursorIndexOfTimestamp);
          _item = new FactEntity(_tmpId,_tmpFact,_tmpTimestamp);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
