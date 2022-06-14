package com.example.chapter17mp3test.MemberActivityCollection

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter17mp3test.DBHelper
import com.example.chapter17mp3test.Member
import com.example.chapter17mp3test.R
import com.example.chapter17mp3test.databinding.ActivityUpdateBinding


class UpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateBinding
    lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this, "MemberDB.db", null, 1)

    }//end of onCreate
    fun onClickView(view: View?) {
        var num: Int = 0

        when(view?.id) {
            R.id.btnUpdateSearch -> {
                val id = binding.edtUpdateId.text
                if(id.length ==  0) {
                    Toast.makeText(this, "아이디 입력요망", Toast.LENGTH_SHORT).show()
                }else {
                  var member = dbHelper.selectMember(id.toString())
                    if(member != null) {
                        num = member.num
                        binding.edtUpdateId2.setText(member.id)
                        binding.edtUpdatePassWord.setText(member.pass)
                        binding.edtUpdateName.setText(member.name)
                        binding.edtUpdateId.setText("")
                    }else {
                        Toast.makeText(this, "해당아이디없음", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.btnUpdateSave -> {
                val id = binding.edtUpdateId2.text.toString()
                val pass = binding.edtUpdatePassWord.text.toString()
                val name = binding.edtUpdateName.text.toString()
                if(id.length == 0 || pass.length == 0 || name.length == 0) {
                    Toast.makeText(this, "정보를 기입하세요", Toast.LENGTH_SHORT).show()
                }else {
                    var member = Member(num, id, pass, name)
                    if(dbHelper.updataMember(member)) {
                        Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show()

                    }else {
                        Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.btnUpdateCancel -> {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
            }

        }
    }//end of onClickView

}//end of class