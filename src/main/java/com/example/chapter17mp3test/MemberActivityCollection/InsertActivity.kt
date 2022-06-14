package com.example.chapter17mp3test.MemberActivityCollection

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter17mp3test.DBHelper
import com.example.chapter17mp3test.Member
import com.example.chapter17mp3test.databinding.ActivitySignupBinding

class InsertActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //여기 화면이니 this를 줍니다. 팩토리는 null을 주고 버젼은 1을 넣어줍니다.
        //데이터베이스 기능(DBHelper)이 필요하면 밑 코드를 작성합니다.
        var dbHelper: DBHelper = DBHelper(this, "MemberDB.db", null, 1)

        //=================회원추가 저장하기버튼==================================================
        binding.btnSignUpSave.setOnClickListener {
            //입력된 내용을 가져와서 테이블에 삽입하면 된다.
            val id = binding.edtSignUpId.text.toString()
            val pass = binding.edtSignUpPassWord.text.toString()
            val name = binding.edtSignUpName.text.toString()

            if(id.length == 0 || pass.length == 0 || name.length == 0) {
                Toast.makeText(this, "정보를 기입하세요", Toast.LENGTH_SHORT).show()
            }else {
                val member = Member(0, id, pass, name)
                if(dbHelper.insertMember(member)) {
                    Toast.makeText(this, "입력 완료", Toast.LENGTH_SHORT).show()
                    binding.edtSignUpId.setText("")
                    binding.edtSignUpPassWord.setText("")
                    binding.edtSignUpName.setText("")
                }else {
                    Toast.makeText(this, "입력 실패", Toast.LENGTH_SHORT).show()
                }
            }
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        //==================================================================================

        //========================뒤로가기========================
        binding.btnSignUpCancel.setOnClickListener {
           val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //=======================================================

    }//end of onCreate
}//end of class