package com.lestec.eventify.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject

class LocalRepo private constructor(context: Context): SQLiteOpenHelper(context, "app_db", null, 6) {

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
            "CREATE TABLE $types ($id INTEGER PRIMARY KEY, $color INTEGER, $text TEXT)"
        )
        dbPassed.execSQL(
            "CREATE TABLE $entries ($id INTEGER PRIMARY KEY, $typeId INTEGER, $date INTEGER, $color INTEGER, $text TEXT)"
        )
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion && newVersion == 6 && db != null) {
            // Get type data
            val eventTypesList = mutableListOf<EventType>()
            val cur = db.rawQuery("SELECT * FROM $types", null)
            if (cur.moveToFirst()) {
                do {
                    eventTypesList.add(EventType(cur.getInt(0), cur.getInt(1), cur.getString(2)))
                } while (cur.moveToNext())
            }
            cur.close()
            // Alter & update table
            db.execSQL("ALTER TABLE $entries ADD COLUMN $color INTEGER")
            db.execSQL("ALTER TABLE $entries ADD COLUMN $text TEXT")
            eventTypesList.forEach {
                val data = ContentValues().apply {
                    this.put(color, it.color)
                    this.put(text, it.text)
                }
                db.update(entries, data, "$typeId = ${it.id}", null)
            }
        }
    }


    // HELPING
    fun export(): JSONArray {
        if (!db.isOpen) db = this.writableDatabase
        val array = JSONArray()
        for (table in listOf(types, entries)) {
            val cur = db.rawQuery("SELECT * FROM $table", null)
            if (cur.moveToFirst()) {
                do {
                    val obj = JSONObject()
                    obj.put(id, cur.getInt(0))
                    obj.put(type, table)
                    when (table) {
                        types -> {
                            obj.put(color, cur.getInt(1))
                            obj.put(text, cur.getString(2))
                        }
                        entries -> {
                            obj.put(typeId, cur.getInt(1))
                            obj.put(date, cur.getLong(2))
                            obj.put(color, cur.getInt(3))
                            obj.put(text, cur.getString(4))
                        }
                    }
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
        clean(db)
        return try {
            val typesList = mutableListOf<EventType>() // Support for old imports
            val jsonData = JSONArray(data)
            for (i in 0..<jsonData.length()) {
                val obj = jsonData.getJSONObject(i)
                val cv = ContentValues()
                val typeDbName = obj.getString(type)
                when (typeDbName) {
                    types -> {
                        cv.put(id, obj.getInt(id))
                        cv.put(color, obj.getInt(color))
                        cv.put(text, obj.getString(text))
                        // Support for old imports
                        typesList.add(EventType(obj.getInt(id), obj.getInt(color), obj.getString(text)))
                    }
                    entries -> {
                        cv.put(id, obj.getInt(id))
                        cv.put(typeId, obj.getInt(typeId))
                        cv.put(date, obj.getLong(date))
                        try {
                            cv.put(color, obj.getInt(color))
                            cv.put(text, obj.getString(text))
                        } catch (_: Exception) {
                            // Support for old imports
                            typesList.find { it.id == obj.getInt(typeId) }?.also {
                                cv.put(color, it.color)
                                cv.put(text, it.text)
                            }
                        }
                    }
                }
                db.insert(typeDbName, null, cv)
            }
            true
        } catch (_: Exception) {
            import(oldData.toString())
            false
        }
    }

    private fun clean(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM $types")
        db.execSQL("DELETE FROM $entries")
    }

    private fun insert(
        tableName: String,
        data: ContentValues
    ) {
        if (!db.isOpen) db = this.writableDatabase
        db.insert(tableName, null, data)
    }

    private fun delete(
        tableName: String,
        objId: Int
    ) {
        if (!db.isOpen) db = this.writableDatabase
        db.delete(tableName, "$id = $objId", null)
    }


    // DAO
    fun addEvent(e: EventType) {
        insert(types, ContentValues().apply {
            this.put(color, e.color)
            this.put(text, e.text)
        })
    }

    fun addEvent(e: EventEntry) {
        insert(entries, ContentValues().apply {
            this.put(typeId, e.typeId)
            this.put(date, e.date)
            this.put(color, e.color)
            this.put(text, e.text)
        })
    }

    fun getEventsTypes(b: Boundaries? = null): List<EventType> {
        val list = mutableListOf<EventType>()
        if (!db.isOpen) db = this.writableDatabase
        val cur = db.rawQuery(
            "SELECT * FROM $types${
                if (b == null) "" else " WHERE $date BETWEEN ${b.start} AND ${b.end}"
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
            "SELECT * FROM $entries${
                if (b == null) "" else " WHERE $date BETWEEN ${b.start} AND ${b.end}"
            }",
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
            this.put(color, e.color)
            this.put(text, e.text)
        }
        if (!db.isOpen) db = this.writableDatabase
        db.update(types, data, "$id = ${e.id}", null)
    }

    fun deleteEvent(e: EventType) {
        delete(types, e.id)
    }

    fun deleteEvent(e: EventEntry) {
        delete(entries, e.id)
    }
}