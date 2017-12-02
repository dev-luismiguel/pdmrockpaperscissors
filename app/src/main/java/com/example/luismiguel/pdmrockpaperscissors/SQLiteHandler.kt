package com.example.luismiguel.pdmrockpaperscissors

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Luis Miguel on 02/12/2017.
 */
class SQLiteHandler (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, FACTORY, VERSION){
    companion object {
        internal val DATABASE_NAME = "pdmdatabase"
        internal val FACTORY = null
        internal val VERSION = 1
    }

    fun updateMatch(player1Name: String, player2Name: String, player1Wins: Int, player2Wins: Int) {
        val db = writableDatabase
        db.execSQL("UPDATE Match SET Player1Wins = $player1Wins, Player2Wins = $player2Wins WHERE Player1Name = '$player1Name' AND Player2Name = '$player2Name' ")
    }

    fun getMatch(player1Name: String, player2Name: String): Match? {
        val db = readableDatabase
        var c = db.rawQuery("SELECT * FROM Match WHERE Player1Name = '$player1Name' AND Player2Name = '$player2Name'", null)

        if (c.count > 0) {
            c.moveToFirst()

            var match = Match(c.getLong(c.getColumnIndex("Id")),
                    c.getString(c.getColumnIndex("Player1Name")),
                    c.getInt(c.getColumnIndex("Player1Wins")),
                    c.getString(c.getColumnIndex("Player2Name")),
                    c.getInt(c.getColumnIndex("Player2Wins")))

            return match
        } else {
            return null
        }
    }

    fun deleteMatch(player1Name: String, player2Name: String){
        val db = writableDatabase
        db.execSQL("DELETE FROM Match WHERE Player1Name = '$player1Name' AND Player2Name = '$player2Name'")
    }

    fun insertMatch(player1Name: String, player1Wins: Int, player2Name: String, player2Wins: Int){
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues()
        values.put("Player1Name", player1Name)
        values.put("Player1Wins", player1Wins)
        values.put("Player2Name", player2Name)
        values.put("Player2Wins", player2Wins)

        db.insert("Match", null, values)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE Match (Id integer primary key autoincrement, Player1Name VARCHAR(50), Player1Wins INTEGER, Player2Name VARCHAR(50), Player2Wins INTEGER)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}