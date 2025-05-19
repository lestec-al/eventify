package com.lestec.eventify.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class LocalRepo private constructor(context: Context): SQLiteOpenHelper(
    context, "app_db", null, 5
) {

    companion object {
        @Volatile
        private var instance: LocalRepo? = null

        fun getInstance(context: Context): LocalRepo {
            return instance ?: synchronized(this) {
                instance ?: LocalRepo(context).also { instance = it }
            }
        }
    }

    private var db: SQLiteDatabase = this.writableDatabase

    fun closeDB() {
        db.close()
    }

    private fun createTables(dbPassed: SQLiteDatabase) {
        dbPassed.execSQL(
            "CREATE TABLE eventsTypes (id INTEGER PRIMARY KEY, color INTEGER, text TEXT)"
        )
        dbPassed.execSQL(
            "CREATE TABLE eventsEntries (id INTEGER PRIMARY KEY, typeId INTEGER, date INTEGER)"
        )
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}


    //
    private fun insert(
        tableName: String,
        data: ContentValues
    ) {
        if (!db.isOpen) db = this.writableDatabase
        db.insert(tableName, null, data)
    }

    private fun <T> getList(
        tableName: String,
        b: Boundaries? = null,
        block: (Cursor) -> T
    ): List<T> {
        val list = mutableListOf<T>()
        if (!db.isOpen) db = this.writableDatabase
        val cur = db.rawQuery(
            "SELECT * FROM ${
                if (b == null) tableName else "$tableName WHERE date BETWEEN ${b.start} AND ${b.end}"
            }",
            null
        )
        if (cur.moveToFirst()) {
            do {
                list.add(block(cur))
            } while (cur.moveToNext())
        }
        cur.close()
        return list
    }

    private fun <T> getOne(
        tableName: String,
        id: Int,
        block: (Cursor) -> T
    ): T? {
        if (!db.isOpen) db = this.writableDatabase
        val cur = db.rawQuery("SELECT * FROM $tableName WHERE id = $id", null)
        return if (cur.moveToFirst()) {
            val obj = block(cur)
            cur.close()
            obj
        } else {
            cur.close()
            null
        }
    }

    private fun update(
        tableName: String,
        id: Int,
        data: ContentValues
    ) {
        if (!db.isOpen) db = this.writableDatabase
        db.update(tableName, data, "id = $id", null)
    }

    private fun delete(
        tableName: String,
        where: String
    ) {
        if (!db.isOpen) db = this.writableDatabase
        db.delete(tableName, where, null)
    }


    //
    fun addEvent(e: EventType) {
        insert("eventsTypes", ContentValues().apply {
            this.put("color", e.color)
            this.put("text", e.text)
        })
    }
    fun addEvent(e: EventEntry) {
        insert("eventsEntries", ContentValues().apply {
            this.put("typeId", e.typeId)
            this.put("date", e.date)
        })
    }

    fun getEventsTypes(boundaries: Boundaries? = null): List<EventType> {
        return getList("eventsTypes", boundaries) {
            EventType(
                id = it.getInt(0),
                color = it.getInt(1),
                text = it.getString(2)
            )
        }
    }
    fun getEventsEntries(b: Boundaries? = null): List<EventEntry> {
        val list = mutableListOf<EventEntry>()
        if (!db.isOpen) db = this.writableDatabase
        val cur = db.rawQuery(
            """
                SELECT eventsEntries.id, typeId, date, color, text
                FROM eventsEntries
                INNER JOIN eventsTypes on eventsTypes.id = eventsEntries.typeId
                ${if (b == null) "" else "WHERE date BETWEEN ${b.start} AND ${b.end}"}
            """.trimIndent(),
            null
        )
        if (cur.moveToFirst()) {
            do {
                list.add(
                    EventEntry(
                        id = cur.getInt(0),
                        typeId = cur.getInt(1),
                        date = cur.getLong(2),
                        color = cur.getInt(3),
                        text = cur.getString(4)
                    )
                )
            } while (cur.moveToNext())
        }
        cur.close()
        return list
    }

    fun updateEvent(e: EventType) {
        update("eventsTypes", e.id, ContentValues().apply {
            this.put("color", e.color)
            this.put("text", e.text)
        })
    }
    fun updateEvent(e: EventEntry) {
        update("eventsEntries", e.id, ContentValues().apply {
            this.put("typeId", e.typeId)
            this.put("date", e.date)
        })
    }

}