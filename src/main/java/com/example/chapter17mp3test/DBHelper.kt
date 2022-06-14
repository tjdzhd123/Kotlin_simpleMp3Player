package com.example.chapter17mp3test

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

//상속을 하나 받고 상속한 애 4가지를 넣어줍니다.(context, name, factory, version)
//오버라이딩 ctrl+i, 어떤 프로젝트든간에 이 구조로 설정합니다.

class DBHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int):
SQLiteOpenHelper(context, name, factory, version) {
    //end of class
    //메인컨텍스트 멤버변수 설정하기
    val mainContext = context

    override fun onCreate(db: SQLiteDatabase?) {
        //테이블을 설계하는 장소(회원멤버로 가입할때 어떤정보를 만들것인지)
        //id, password, name 등등 (db가 null이 아닐때)
        if (db != null) {
            var createQuery =
                "create table member(num integer primary Key autoincrement, id TEXT, pass TEXT, name TEXT)"

            db.execSQL(createQuery)
        }
    }//end of onCreate

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        //테이블설계를 다시 해야할 때
        var dropQuery = "drop table if exists member"
        //여기서는 null체크를 안했으니 ?를 넣어줘야합니다.
        db?.execSQL(dropQuery)
        onCreate(db)
    }//end of onUpgrade

    //================================회원가입========================
    fun insertMember(member: Member): Boolean {
        var insertFlag = false

        try {
            if(checkID(member.id)){
                Toast.makeText(mainContext, "존재하는 ID입니다.", Toast.LENGTH_SHORT).show()
            }else {
                val db:SQLiteDatabase = this.writableDatabase    //execSQL문을 진행하기 위해

                val insertQuery = "insert into member(num, id, pass, name) values(null, '${member.id}', '${member.pass}', '${member.name}')"
                db.execSQL(insertQuery)
                insertFlag = true
                db.close()
            }

        }catch (e: Exception) {
            Log.d("shin", "InsertMember error 예외발생 ${e.printStackTrace()}")
        }
        return insertFlag
    //================================================================

    }//end of inseertMember

    //================================회원 ID 유무 점검========================
    fun checkID(id: String): Boolean {
        var idFlag = false
        //readableDatabase는 rawQuery의 전용이다.
        val db: SQLiteDatabase = this.readableDatabase  //rawQuery()

        try {
            val checkIDQuery = "select id from member where id = '${id}'"
            //얘는 반드시 커서를 줍니다.
            var cursor = db.rawQuery(checkIDQuery, null)

            //만약 위 rawQuery가 실행을 안하면 정보가 아무것도 안간다. 가면 true이고 못가면 false
            if(cursor.moveToFirst()) {
                //아이디가 있다는 뜻
                var valueId = cursor.getString(0)
                if(valueId.equals(id)) idFlag = true
            }
        }catch (e: Exception) {
            Log.d("shin", "checkId 예외발생 ${e.printStackTrace()}")
        }finally {
            db.close()
        }
        return idFlag
    }//end of checkID
    //========================================================================

    //================================회원 ID 찾아 정보 리턴하기========================
    fun selectMember(id: String): Member? {
        var member: Member? = null

        val db: SQLiteDatabase = this.readableDatabase

        try {
            val selectQuery = "select * from member where id = '${id}'"
            //얘는 반드시 커서를 줍니다.
            var cursor = db.rawQuery(selectQuery, null)

            //만약 위 rawQuery가 실행을 안하면 정보가 아무것도 안간다. 가면 true이고 못가면 false
            if(cursor.moveToFirst()) {
                var num = cursor.getInt(0)
                var id = cursor.getString(1)
                var pass = cursor.getString(2)
                var name = cursor.getString(3)

                member = Member(num, id, pass, name)
            }
        }catch (e: Exception) {
            Log.d("shin", "checkId 예외발생 ${e.printStackTrace()}")
        }finally {
            db.close()
        }
        return member
    }//end of selectMember

    //========================================================================

    //========================회원 ID와 패스워드를 점검하여 확인=================
    fun authLogin(id: String, pass: String): Boolean {
        var LoginFlag = false
        //readableDatabase는 rawQuery의 전용이다.
        val db: SQLiteDatabase = this.readableDatabase  //rawQuery()

        try {
            val LoginDQuery = "select * from member where id = '${id}' and pass = '${pass}'"
            var cursor = db.rawQuery(LoginDQuery, null)

            //만약 위 rawQuery가 실행을 안하면 정보가 아무것도 안간다. 가면 true이고 못가면 false
            if(cursor.moveToFirst()) {
                LoginFlag = true
            }
        }catch (e: Exception) {
            Log.d("shin", "authLogin 예외발생 ${e.printStackTrace()}")
        }finally {
            db.close()
        }

        return LoginFlag
    }

    //========================================================================

    //================================모든 회원정보 넘기기========================
    fun selectAll(): MutableList<Member>? {
        val memberList = mutableListOf<Member>()

        val db:SQLiteDatabase = this.readableDatabase
        try {
            val selectQuery = "select * from member"
            var cursor = db.rawQuery(selectQuery, null)

            while(cursor.moveToNext()) {
                var num = cursor.getInt(0)
                var id = cursor.getString(1)
                var pass = cursor.getString(2)
                var name = cursor.getString(3)

                val member = Member(num, id, pass, name)

                memberList.add(member)
                Log.d("shin","${member.toString()}")
            }
        }catch (e: Exception) {
            Log.d("shin", "selectAll 예외발생 ${e.printStackTrace()}")
        }finally {
            db.close()
        }

        return memberList
    }//end of selectAll
    //========================================================================

    //============================삭제기능 구현 장소 ============================
    //잘 삭제됐는지 안됐는지 boolean을 선언합니다.
    fun memberDelete(id: String): Boolean {
        var deleteFlag = false

        if(!checkID(id)) {
            Toast.makeText(mainContext, "존재하는 ID입니다.", Toast.LENGTH_SHORT).show()
            Log.d("shin", "memberDelte 존재하지 않는 ID")
            return deleteFlag
        }

        val db: SQLiteDatabase = this.writableDatabase  //execSQL()

        try {
            val deleteQuery = "delete from member where id = '${id}'"
            db.execSQL(deleteQuery)
            deleteFlag = true
        }catch (e: Exception) {
            Log.d("shin", "memberDelete 예외발생 ${e.printStackTrace()}")
        }finally {
            db.close()
        }
        return deleteFlag
    }//end of memberDelete
    //==============================================================================

    //========================수정기능 구현 장소 =====================================
    fun updataMember(member:Member): Boolean{
        var updateFlag = false

        if(!checkID(member.id)) {
            Toast.makeText(mainContext, "존재하는 ID입니다.", Toast.LENGTH_SHORT).show()
            Log.d("shin", "updataMember 존재하지 않는 ID")
            return updateFlag
        }

        val db: SQLiteDatabase = this.writableDatabase  //execSQL()

        try {
            val updateQuery = "update member set pass = '${member.pass}', name = '${member.name}' where id = '${member.id}'"
            db.execSQL(updateQuery)
            updateFlag = true
        }catch (e: Exception) {
            Log.d("shin", "memberDelete 예외발생 ${e.printStackTrace()}")
        }finally {
            db.close()
        }

        return updateFlag
    }//end of updataMember
    //==============================================================================

}//end of class