package com.lestec.eventify.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject

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


    // EXPORT & IMPORT
    fun export(): JSONArray {
        if (!db.isOpen) db = this.writableDatabase
        val array = JSONArray()
        // Loop
        for (table in listOf("eventsTypes", "eventsEntries")) {
            val cur = db.rawQuery("SELECT * FROM $table", null)
            if (cur.moveToFirst()) {
                do {
                    val obj = JSONObject()
                    // Add to json object
                    obj.put("id", cur.getInt(0))
                    obj.put("type", table)
                    when (table) {
                        "eventsTypes" -> {
                            obj.put("color", cur.getInt(1))
                            obj.put("text", cur.getString(2))
                        }
                        "eventsEntries" -> {
                            obj.put("typeId", cur.getInt(1))
                            obj.put("date", cur.getLong(2))
                        }
                    }
                    // Add json object to json array
                    array.put(obj)
                } while (cur.moveToNext())
            }
            cur.close()
        }
        return array
    }

    fun import(data: String): Boolean {
        if (!db.isOpen) db = this.writableDatabase
        val oldData = export()
        clean()
        return try {
            val jsonData = JSONArray(data)
            for (i in 0..<jsonData.length()) {
                val obj = jsonData.getJSONObject(i)
                val cv = ContentValues()
                cv.put("id", obj.getInt("id"))
                val type = obj.getString("type")
                when (type) {
                    "eventsTypes" -> {
                        cv.put("color", obj.getInt("color"))
                        cv.put("text", obj.getString("text"))
                    }
                    "eventsEntries" -> {
                        cv.put("typeId", obj.getInt("typeId"))
                        cv.put("date", obj.getLong("date"))
                    }
                }
                db.insert(type, null, cv)
            }
            true
        } catch (_: Exception) {
            import(oldData.toString())
            false
        }
    }

    private fun clean() {
        if (!db.isOpen) db = this.writableDatabase
        db.execSQL("DELETE FROM eventsTypes")
        db.execSQL("DELETE FROM eventsEntries")
    }


    //
    private fun insert(
        tableName: String,
        data: ContentValues
    ) {
        if (!db.isOpen) db = this.writableDatabase
        db.insert(tableName, null, data)
    }

    private fun delete(
        tableName: String,
        id: Int
    ) {
        if (!db.isOpen) db = this.writableDatabase
        db.delete(tableName, "id = $id", null)
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

    fun getEventsTypes(b: Boundaries? = null): List<EventType> {
        val list = mutableListOf<EventType>()
        if (!db.isOpen) db = this.writableDatabase
        val cur = db.rawQuery(
            "SELECT * FROM ${
                if (b == null) "eventsTypes" else "eventsTypes WHERE date BETWEEN ${b.start} AND ${b.end}"
            }",
            null
        )
        if (cur.moveToFirst()) {
            do {
                list.add(
                    EventType(
                        id = cur.getInt(0),
                        color = cur.getInt(1),
                        text = cur.getString(2)
                    )
                )
            } while (cur.moveToNext())
        }
        cur.close()
        return list
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
        val data = ContentValues().apply {
            this.put("color", e.color)
            this.put("text", e.text)
        }
        if (!db.isOpen) db = this.writableDatabase
        db.update("eventsTypes", data, "id = ${e.id}", null)
    }

    fun deleteEvent(e: EventType) {
        delete("eventsTypes", e.id)
        // Delete entries for this type
        if (!db.isOpen) db = this.writableDatabase
        db.delete("eventsEntries", "typeId = ${e.id}", null)
    }
    fun deleteEvent(e: EventEntry) {
        delete("eventsEntries", e.id)
    }
}