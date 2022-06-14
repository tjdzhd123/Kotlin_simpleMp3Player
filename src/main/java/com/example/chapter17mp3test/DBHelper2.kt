package com.example.chapter17mp3test

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.SQLException

class DBHelper2(context: Context, dbName: String, version: Int): SQLiteOpenHelper(context, dbName, null, version) {
    companion object {
        val TABLE_NAME = "musicTBL"
    }
    //테이블 설계하기(확실하게 정한다음 작성)
    override fun onCreate(mp3db: SQLiteDatabase?) {
        //mp3db, execSQL() -> 테이블에 뭔가 변화를 주면 : Inseert, update, delete, drop, create
        //mp3db.rawQuery() -> 테이블에 변화없이 데이터를 가져오는 것: select -> Cursor
        val createQuery = "create table ${TABLE_NAME}(id TEXT primary key, title TEXT, artist TEXT, albumId TEXT, duration INTEGER, favorite INTEGER)"
        mp3db?.execSQL(createQuery)
    }//end of onCreate

    override fun onUpgrade(mp3db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //테이블 제거
        val dropQuery = "drop table $TABLE_NAME"
        mp3db?.execSQL(dropQuery)
        this.onCreate(mp3db)
    }//end of onUpgrade

    //삽입 : insert into 테이블명 ( ) values (   )
    fun insertMusic(music: Music): Boolean {
        var insertFlag = false
        val insertQuery = """
            insert into $TABLE_NAME(id, title, artist, albumId, duration, favorite)
                values('${music.id}', '${music.title}', '${music.artist}' ,'${music.albumId}' ,'${music.duration}', ${music.favorite})
                """.trimIndent()

        //mp3db: SQLiteDatabase 가져오는 방법은 : writableDatabase, readableDatabase
        var mp3db = this.writableDatabase
        try {
            mp3db.execSQL(insertQuery)
            insertFlag = true
        }catch (e: SQLException) {
            Log.d("shin", "${e.printStackTrace()}")
        }finally {
            mp3db.close()
        }
        return insertFlag
    }//end of insertMusic

    //전체 레코드를 다 가져옵니다.
    fun selectMusicAll(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        //다 가져오라는 뜻
        val selectQuery = """
                select * from $TABLE_NAME
                """.trimIndent()

        val mp3db = this.readableDatabase

        try {
            cursor = mp3db.rawQuery(selectQuery, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val favorite = cursor.getInt(5)
                    //뮤직으로 그대로 객체를 만들고 Music에 추가합니다.
                    val music = Music(id, title, artist, albumId, duration, favorite)
                    musicList?.add(music)
                }
            //만약 오류가 생기면 null을줍니다.
            } else {
                musicList = null
            }
        }catch (e: Exception) {
            Log.d("shin", e.toString())
        }finally {
            cursor?.close()
            mp3db.close()
        }
        return musicList
    }//end of selectMusicAll

    //내가 원하는 내용을 추가하기 + 선택: 조건에 맞게
    fun selectMusic(query: String): MutableList<Music> {
        var music: MutableList<Music> = mutableListOf()
        val mp3db = this.readableDatabase
        var cursor: Cursor? = null

        val selectQuery = """
            select * from $TABLE_NAME where artist like '$query%' or title like '$query%'
            """.trimIndent()

        try {
            cursor = mp3db.rawQuery(selectQuery, null)
            if(cursor.count > 0) {
                if(cursor.moveToFirst()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val favorite = cursor.getInt(5)

                    music?.add(Music(id, title, artist, albumId, duration, favorite))
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }finally {
            cursor?.close()
            mp3db.close()
        }
        return music
    }//end of selectMusic

    //노래 즐겨찾기, DB 구현(좋아요선택하면 저장하는방식입니다.)
    fun updateFavorite(music: Music): Boolean{
        var flag = false
        val db = this.writableDatabase

        var updateQuery: String = """
            update $TABLE_NAME set favorite = '${music.favorite}' where id = '${music.id}'
        """.trimIndent()

        try {
            db.execSQL(updateQuery)
            flag = true
        } catch (e: android.database.SQLException){
            Log.d("shin", "${e.printStackTrace()}")
        }

        return flag
    }

    fun  selectFavorite(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null

        val selectQuery = """
            select * from $TABLE_NAME where favorite = 1 
            """.trimIndent()

        val mp3db = this.readableDatabase
        try {
            cursor = mp3db.rawQuery(selectQuery, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val favorite = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, favorite)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        }catch (e: Exception) {
            Log.d("shin", e.toString())
        }finally {
            cursor?.close()
            mp3db.close()
        }
        return musicList
    }

}//end of class