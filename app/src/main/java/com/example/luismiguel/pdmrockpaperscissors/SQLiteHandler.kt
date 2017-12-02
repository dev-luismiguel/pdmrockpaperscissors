package com.example.luismiguel.pdmrockpaperscissors

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Luis Miguel on 02/12/2017.
 */
class SQLiteHandler (context: Context) : SQLiteOpenHelper(context, dbname, factory, version){
    companion object {
        internal val dbname = "pdmdatabase"
        internal val factory = null
        internal val version = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE Player (Id integer primary key auto_increment, name varchar(50))")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}