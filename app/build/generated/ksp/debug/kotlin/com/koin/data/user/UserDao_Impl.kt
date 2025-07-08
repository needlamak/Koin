package com.koin.`data`.user

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class UserDao_Impl(
  __db: RoomDatabase,
) : UserDao {
  private val __db: RoomDatabase

  private val __deleteAdapterOfUserEntity: EntityDeleteOrUpdateAdapter<UserEntity>

  private val __upsertAdapterOfUserEntity: EntityUpsertAdapter<UserEntity>
  init {
    this.__db = __db
    this.__deleteAdapterOfUserEntity = object : EntityDeleteOrUpdateAdapter<UserEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `users` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__upsertAdapterOfUserEntity = EntityUpsertAdapter<UserEntity>(object :
        EntityInsertAdapter<UserEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `users` (`id`,`name`,`email`,`avatarUri`,`bio`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.email)
        val _tmpAvatarUri: String? = entity.avatarUri
        if (_tmpAvatarUri == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpAvatarUri)
        }
        val _tmpBio: String? = entity.bio
        if (_tmpBio == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpBio)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<UserEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `users` SET `id` = ?,`name` = ?,`email` = ?,`avatarUri` = ?,`bio` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.email)
        val _tmpAvatarUri: String? = entity.avatarUri
        if (_tmpAvatarUri == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpAvatarUri)
        }
        val _tmpBio: String? = entity.bio
        if (_tmpBio == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpBio)
        }
        statement.bindLong(6, entity.id)
      }
    })
  }

  public override suspend fun delete(user: UserEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __deleteAdapterOfUserEntity.handle(_connection, user)
  }

  public override suspend fun upsert(user: UserEntity): Long = performSuspending(__db, false, true)
      { _connection ->
    val _result: Long = __upsertAdapterOfUserEntity.upsertAndReturnId(_connection, user)
    _result
  }

  public override fun getAll(): Flow<List<UserEntity>> {
    val _sql: String = "SELECT * FROM users ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfAvatarUri: Int = getColumnIndexOrThrow(_stmt, "avatarUri")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpAvatarUri: String?
          if (_stmt.isNull(_columnIndexOfAvatarUri)) {
            _tmpAvatarUri = null
          } else {
            _tmpAvatarUri = _stmt.getText(_columnIndexOfAvatarUri)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          _item = UserEntity(_tmpId,_tmpName,_tmpEmail,_tmpAvatarUri,_tmpBio)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getById(id: Long): Flow<UserEntity?> {
    val _sql: String = "SELECT * FROM users WHERE id = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfAvatarUri: Int = getColumnIndexOrThrow(_stmt, "avatarUri")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _result: UserEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpEmail: String
          _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          val _tmpAvatarUri: String?
          if (_stmt.isNull(_columnIndexOfAvatarUri)) {
            _tmpAvatarUri = null
          } else {
            _tmpAvatarUri = _stmt.getText(_columnIndexOfAvatarUri)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          _result = UserEntity(_tmpId,_tmpName,_tmpEmail,_tmpAvatarUri,_tmpBio)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM users WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
