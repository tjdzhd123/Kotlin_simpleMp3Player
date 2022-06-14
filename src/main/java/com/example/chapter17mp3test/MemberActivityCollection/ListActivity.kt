package com.example.chapter17mp3test.MemberActivityCollection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chapter17mp3test.Adapter.CustomAdapter
import com.example.chapter17mp3test.DBHelper
import com.example.chapter17mp3test.databinding.ActivityListBinding


class ListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //데이터베이스 기능(DBHelper)이 필요하면 밑 코드를 작성합니다.
        var dbHelper: DBHelper = DBHelper(this, "MemberDB.db", null, 1)
        var memberList = dbHelper.selectAll()

        val customAdapter = CustomAdapter(this, memberList)
        binding.listRecyclerView.adapter = customAdapter

        val layoutManager = LinearLayoutManager(this)
        binding.listRecyclerView.layoutManager = layoutManager

        binding.btnListCancel.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

    }//end of onCreate

}//end of class