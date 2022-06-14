package com.example.chapter17mp3test.Mp3ActivityCollection

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chapter17mp3test.*
import com.example.chapter17mp3test.Adapter.MusicRecyclerAdapter
import com.example.chapter17mp3test.MemberActivityCollection.InsertActivity
import com.example.chapter17mp3test.MemberActivityCollection.LoginActivity
import com.example.chapter17mp3test.databinding.ActivityMainmp3Binding

class Mp3MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainmp3Binding

    var searchedMusicList: MutableList<Music> = mutableListOf()
    var displayMusicList: MutableList<Music> = mutableListOf()

    companion object {
        val DB_NAME = "musicDB"
        val VERSION = 1
    }

    //승인받아야할 항목 퍼미션을 허락을 받아야합니다.
    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val REQUEST_READ = 999

    //2. 데이터베이스 객체화
    val dbHelper2: DBHelper2 by lazy { DBHelper2(this, DB_NAME, VERSION)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainmp3Binding.inflate(layoutInflater)

        //왜 binding.root를 하는 이유는 제일 전체적인 큰 뷰를 말하기 때문
        setContentView(binding.root)

        //승인이 되었으면 음악파일을 가져오는것이고, 승인이 안되었으면 재요청을 합니다.
        if (isPermitted() == true) {
            startProcess()
            //실행하면 되고, 외부파일을 가져와서, ContextResolver로 가져와서 그 정보를 MutableList, CollectionFrame에 저장하고
            //그 어댑터를 불러오면 다 들어가는 구조입니다.
        } else {
            //만약 안되있으면 다시 isPermitted에 승인요청을 해줘야합니다. : android.Manifest.permission.READ_EXTERNAL_STORAGE
            //permission을 REQUEST_READ로 승인요청합니다.
            //요청 승인이 되면 콜백함수(onRequestPermissionsResult)로 승인 결과값을 알려줍니다.
            ActivityCompat.requestPermissions(this, permission, REQUEST_READ)
        }
        //==========================================액션바==============================
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //============================================================================

    }//end of onCreate

    //======================================옵션바===================================================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchMenu = menu?.findItem(R.id.search_menu)
        val searchView = searchMenu?.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object:androidx.appcompat.widget.SearchView.OnQueryTextListener {
           //검색 버튼 클릭 시 검색 결과만 화면에 출력
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            //검색 텍스트 변화시 검색 결과만 화면에 출력
            override fun onQueryTextChange(query: String?): Boolean {
                if(!query.isNullOrBlank()) {
                    searchedMusicList = dbHelper2.selectMusic(query)
                    binding.mainRecyclerView.adapter = MusicRecyclerAdapter(this@Mp3MainActivity, searchedMusicList!!)
                }else {
                    //검색 텍스트가 비었을 경우 전체 데이터 출력
                    displayMusicList = dbHelper2.selectMusicAll()!!
                    binding.mainRecyclerView.adapter = MusicRecyclerAdapter(this@Mp3MainActivity, displayMusicList!!)
                    searchedMusicList.clear()
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }
    //==============================================================================================

    //==================옵션바 아이템 선택시==========================================================
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.favorites -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
            }
            //=======툴바에 생긴 Back 버튼 활성화===========================
            android.R.id.home -> {
                var dialog = AlertDialog.Builder(this)
                dialog.setTitle("Mp3Play")
                dialog.setIcon(R.drawable.cuteapp)
                dialog.setMessage("첫 화면으로 이동하시겠습니까?")

                fun enter() {
                    Toast.makeText(this, "화면이동합니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
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
            }
            //=========================================
        }
        return super.onOptionsItemSelected(item)
    }
    //==============================================================================================

    //승일 요청 하였을대 승인 결과에 대한 콜백함수이다.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //실행하면 되고, 외부파일을 가져와서, ContextResolver로 가져와서 그 정보를 MutableList, CollectionFrame에 저장하고
                //그 어댑터를 불러오면 다 들어가는 구조입니다.
                startProcess()
            } else {
                Toast.makeText(this, "권한요청 승인 없이 앱 실행이 안됩니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    //========================외부파일 읽기 승인요청===================================================
    fun isPermitted(): Boolean {
        //한가지의 퍼미션을 적었으니
        if(ContextCompat.checkSelfPermission(this, permission[0]) != PackageManager.PERMISSION_GRANTED) {
            return false
        }else {
            return true
        }
    }//end of isPermitted
    //==============================================================================================
    //외부파일로부터 모든 음악정보를 가져오는 함수입니다.
    private fun startProcess() {
        //데이터베이스 기능을 설정합니다.
        var musicList: MutableList<Music>? = mutableListOf<Music>()

        //데이터베이스 뮤직테이블에서 자료를 가져옵니다. (있으면 -> 리사이클러뷰를 보여주고,
        // 없으면 -> getMusicList 정보를 가져오고 그리고 DB의 musicTable에 저장하고 리사이클러뷰에 보여줍니다.
        //자료가있으면 밑 if가 실행안하고 없으면 if에 가서 하나하나 씩 집어 넣어줍니다.
        musicList = dbHelper2.selectMusicAll()

        if(musicList == null || musicList.size <= 0 ) {
            //getMusicList(외부장치에서 음악정보를 가져오는 기능을 담당합니다.)
            musicList = getMusicList()
            //가져온 뮤직리스트의 정보를 데이터베이스 테이블에 저장합니다.
            if(musicList != null) {
                for(i in 0..musicList!!.size -1) {
                    val music = musicList.get(i)
                    if(dbHelper2.insertMusic(music) == false) {
                        Log.d("shin", "삽입오류 ${music.toString()}")
                    }
                }
            }

            Log.d("shin", "테이블에 없어서 getMusicList()")
        }else {
            Log.d("shin", "테이블에 있어서 내용을 가져와서 보여줍니다.")
        }

        //3. 어댑터를 만들고, MutableList를 제공합니다.
        val musicRecyclerAdapter = MusicRecyclerAdapter(this, musicList)
        binding.mainRecyclerView.adapter = musicRecyclerAdapter
        binding.mainRecyclerView.addItemDecoration(MyDecoration(this))
        //4. 화면에 출력합니다.
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun getMusicList(): MutableList<Music>? {
        //1. 외부파일의 음악정보주소
        val listUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        //2.요청해야될 음원정보 컬럼들
        val proj = arrayOf(
            MediaStore.Audio.Media._ID,     //음악 id
            MediaStore.Audio.Media.TITLE,    //음악 타이틀
            MediaStore.Audio.Media.ARTIST,  //가수명
            MediaStore.Audio.Media.ALBUM_ID, //음악이미지
            MediaStore.Audio.Media.DURATION //음악 시간
        )
        //3.Content Resolver을 통해 쿼리에 Uri, 요청음원정보컬럼을 요구하고 결과값을 cursor로 변환합니다.
        val cursor = contentResolver.query(listUri, proj, null, null, null)
        val musicList: MutableList<Music>?= mutableListOf<Music>()
        while(cursor?.moveToNext() == true) {
            //얘내는 파일에서 데이터를 가져오는것
            val id = cursor.getString(0)
            val title = cursor.getString(1).replace("'", "")
            var artist = cursor.getString(2).replace("'", "")
            var albumId = cursor.getString(3)
            var duration = cursor.getInt(4)
            //데이터클래스로 만듭니다.
            val music = Music(id, title, artist, albumId, duration, 0)
            musicList?.add(music)
        }
        cursor?.close()
        return musicList
    }
}//end of class