package com.example.chapter17mp3test.MemberActivityCollection

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter17mp3test.DBHelper
import com.example.chapter17mp3test.Mp3ActivityCollection.Mp3MainActivity
import com.example.chapter17mp3test.R
import com.example.chapter17mp3test.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var dbHelper: DBHelper = DBHelper(this, "MemberDB.db", null, 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }//end of onCreate

    fun btnClick(view: View) {
        when (view?.id) {
            //=================관리자 모드 버튼==============================
            R.id.btnLoginPreferences -> {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
            }
            //============================================================
            //=================로그인 버튼=============
            R.id.btnLogin -> {
                var id = binding.edtLoginId.text.toString()
                var pass = binding.edtLoginPass.text.toString()
                if(id.length == 0 || pass.length == 0) {
                    Toast.makeText(this, "다시입력하세요", Toast.LENGTH_SHORT).show()
                }else {
                    if(dbHelper.authLogin(id, pass)) {
                        val intent = Intent(this, Mp3MainActivity::class.java)
                        startActivity(intent)
                    }else {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }//end of btnLogin
            //=======================================

            //=================회원가입 다일러그 창==================================================
            R.id.btnLoginSignUp -> {
                var dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Mp3Play 회원가입")
                    dialog.setIcon(R.drawable.person_add_24)
                    dialog.setMessage("회원가입 하시겠습니까?")

                fun enter() {
                    Toast.makeText(this, "화면이동합니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, InsertActivity::class.java)
                    startActivity(intent)
                }
                var dialog_listener = object : DialogInterface.OnClickListener {
                    override fun onClick(dialog : DialogInterface?, which : Int) {
                        when(which) {
                            DialogInterface.BUTTON_POSITIVE ->
                                enter()
                        }
                    }
                }//end of dialog_listener
                dialog.setNegativeButton("취소", null)
                dialog.setPositiveButton("확인", dialog_listener)
                dialog.show()
                //==============================================================================
            }
        }
    }//end of btnClick

}//end of class