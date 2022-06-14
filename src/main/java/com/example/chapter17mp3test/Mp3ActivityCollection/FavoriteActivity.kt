package com.example.chapter17mp3test.Mp3ActivityCollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chapter17mp3test.DBHelper2
import com.example.chapter17mp3test.Music
import com.example.chapter17mp3test.Adapter.MusicRecyclerAdapter
import com.example.chapter17mp3test.MyDecoration
import com.example.chapter17mp3test.R
import com.example.chapter17mp3test.databinding.ActivityFavoriteBinding
import java.text.FieldPosition

class FavoriteActivity : AppCompatActivity() {
    lateinit var binding: ActivityFavoriteBinding
    lateinit var  dbHelper2: DBHelper2
    var timeValue: Long = 0
    var favoriteList: MutableList<Music>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityFavoriteBinding.inflate(layoutInflater)
        dbHelper2 = DBHelper2(this, "musicDB", 1)
        setContentView(binding.root)

        favoriteList = dbHelper2.selectFavorite()
        val musicRecyclerAdapter = MusicRecyclerAdapter(this, favoriteList)
        binding.favoriteRecyclerView.adapter = musicRecyclerAdapter
        binding.favoriteRecyclerView.addItemDecoration(MyDecoration(this))
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(this)

    }//end of onCreate

    fun viewOnClick(view: View?) {
        when(view?.id) {
            R.id.ivFavoriteBack -> {
                val intent = Intent(this, Mp3MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - timeValue >= 3000) {
            Toast.makeText(this, "한번더 누를시 Main으로 이동합니다.(데이터 전달x)", Toast.LENGTH_SHORT).show()
            timeValue = System.currentTimeMillis()
        }else {
            super.onBackPressed()
        }
    }
}//end of class