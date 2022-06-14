package com.example.chapter17mp3test.MemberActivityCollection

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter17mp3test.R
import com.example.chapter17mp3test.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }//end of onCreate

    //굳이 binding.btn.setonClick 할필요없이 펑션을 하나만들어 코드를 간결하게 합니다.
    fun  adminOnClickView(view: View?) {
        when(view?.id) {
            //======================회원삭제 버튼===================================
            R.id.linearLayoutAdminRemove -> {
                val intent = Intent(this, DeleteActivity::class.java)
                startActivity(intent)
            }
            //=====================================================================

            //======================회원수정 버튼===================================
            R.id.linearLayoutAdminCorrection -> {
                val intent = Intent(this, UpdateActivity::class.java)
                startActivity(intent)
            }
            //=====================================================================

            //======================회원목록 버튼===================================
            R.id.linearLayoutAdminList -> {
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            }
            //=====================================================================

            //=======================로그인화면으로 가기 =============================
            R.id.btnAdminCancel -> {
                //Intent하고 this, MainActivity :: class.java해서 화면이동을합니다.
                val intent = Intent(this, LoginActivity::class.java)
                //돌아올게 아니니 intent를 넣어줍니다?
                startActivity(intent)
            }
            //=====================================================================
        }
    }

}//end of class