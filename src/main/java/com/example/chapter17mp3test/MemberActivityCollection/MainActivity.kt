package com.example.chapter17mp3test.MemberActivityCollection

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter17mp3test.R
import com.example.chapter17mp3test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }//end of onCreate

    fun viewClick(view: View) {
        when(view?.id) {
            R.id.ivMainPicture -> {
                val intent = Intent(this, LoginActivity::class.java )
                startActivity(intent)
            }
        }
    }//end of viewClick

}//end of class