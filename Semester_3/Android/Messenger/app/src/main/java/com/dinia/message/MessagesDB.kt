package com.dinia.message

import android.graphics.Bitmap
import androidx.room.*

@Entity(tableName = "user")
data class MsgData(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "position") val pos: Int,
    @ColumnInfo(name = "is_image") val isImage: Boolean,
    @ColumnInfo(name = "from") val from: String,
    @ColumnInfo(name = "to") val to: String,
    @ColumnInfo(name = "message_text") val messageText: String?,
    @ColumnInfo(name = "image_link") val imageLink: String?,
    @ColumnInfo(name = "chat_title") val chatTitle: String,
    @ColumnInfo(name = "time") val time: String
) {
    @Ignore var image: Bitmap? = null
}

@Entity(tableName = "chats")
data class ChatData(
    @PrimaryKey
    @ColumnInfo(name = "title") val title: String
)

@Dao
interface MessagesDao {
    @Query("SELECT * FROM user")
    fun getAll() : List<MsgData>

    @Query("SELECT * FROM user WHERE id > :topId")
    fun getAllAfter(topId: Int) : List<MsgData>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(message: MsgData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertList(messages: List<MsgData>)

    @Query("SELECT COUNT(*) FROM user")
    fun size(): Int

    @Query("SELECT MAX(id) FROM user")
    fun maxIdInTable(): Int

    @Query("SELECT * FROM user WHERE id > :topId AND chat_title = :chat")
    fun getChatMessages(topId: Int, chat: String): List<MsgData>

    @Query("DELETE FROM user")
    fun clearTable()

    @Update
    fun update(message: MsgData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addChats(chats: List<ChatData>)

    @Query("SELECT * FROM chats")
    fun getChats(): List<ChatData>
}

@Database(entities = [MsgData::class, ChatData::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessagesDao
}
