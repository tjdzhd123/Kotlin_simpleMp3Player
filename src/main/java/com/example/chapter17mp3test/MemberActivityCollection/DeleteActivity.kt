package com.example.chapter17mp3test.MemberActivityCollection

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter17mp3test.DBHelper
import com.example.chapter17mp3test.databinding.ActivityRemoveBinding


class DeleteActivity : AppCompatActivity() {
    lateinit var binding: ActivityRemoveBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityRemoveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var dbHelper: DBHelper = DBHelper(this, "MemberDB.db", null, 1)
        //해당되지 않는 아이디를 가지고 삭제기능을 구현합니다.

        //================삭제 완료=======================================
        binding.btnRemove.setOnClickListener {
            //해당되는 아이디를 가지고 삭제기능을 구현합니다.
            val id = binding.edtRemoveId.text
            if(id.length != 0) {
                if(dbHelper.memberDelete(id.toString())){
                    Toast.makeText(this , "삭제성공", Toast.LENGTH_SHORT).show()
                    binding.edtRemoveId.setText("")
                }else {
                    Toast.makeText(this , "삭제점검", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //==============================================================

        binding.btnRemoveCancel.setOnClickListener {
            //뒤로가는것을 구현합니다.
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

    }//end of onCreate
}//end of class