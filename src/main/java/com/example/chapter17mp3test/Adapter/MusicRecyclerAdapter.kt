package com.example.chapter17mp3test.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chapter17mp3test.Mp3ActivityCollection.PlaymusicActivity
import com.example.chapter17mp3test.DBHelper2
import com.example.chapter17mp3test.Mp3ActivityCollection.FavoriteActivity
import com.example.chapter17mp3test.Music
import com.example.chapter17mp3test.R
import com.example.chapter17mp3test.databinding.ItemRecyclerBinding
import java.text.SimpleDateFormat

//1. 매개변수로 두개를 받습니다. 1. Context 2, CollectionFrameWork
//내부클래스니까 Adapter<"여기에MusicRecyclerAdapter.CustomViewHolder을 넣어줍니다">()
class MusicRecyclerAdapter(val context: Context, var musicList: MutableList<Music>?): RecyclerView.Adapter<MusicRecyclerAdapter.CustomViewHolder>() {
    var ALBUM_IMAGE_SIZE = 70

    //dbHelper 객체를 만들면(실행하면) 데이터베이스 파일이 있으면 또 만들지 않습니다.
    //만들어진 데이터베이스 객체만 전달합니다.
    val dbHelper2: DBHelper2 by lazy {
        DBHelper2(context, "musicDB", 1)
    }
    //3.오버라이딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = (holder as CustomViewHolder).binding
        val music = musicList?.get(position)

        binding.tvArtist.text = music?.artist    //가수명을 아티스트에 넘겨줍니다.
        binding.tvTitle.text = music?.title //노래명을 MainActivity의 title에 넘겨줍니다.
        binding.tvDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)  //경과 시간을 알리는
        val bitmap: Bitmap? = music?.getAlbumImage(context, ALBUM_IMAGE_SIZE)

        if (bitmap != null) {
            binding.ivAlbumPicture.setImageBitmap(bitmap)
        } else {
            //앨범 이미지가 없을때 기본 사진으로
            binding.ivAlbumPicture.setImageResource(R.drawable.music_collection_24)
        }
        binding.root.setOnClickListener {
            //한마디로 해당되는 ItemView를 클릭을 하면 PlayMusicActivity에 musicList를 Parcelable를 통해 변환된
            //ArrayList와, 해당 뷰 위치값을 전송해줍니다.
            //musicList를 인텐트로 전달하기 위해 Parcelable ArrayList에 저장합니다.
            val playList: ArrayList<Parcelable>? = musicList as ArrayList<Parcelable>
            val intent = Intent(binding.root.context, PlaymusicActivity::class.java)
            //위에서 음악 리스트를 가져오면서 음악 순서(position)가져오기
            intent.putExtra("position", position)
            //전체의 플레이리스트를 다 보내줍니다.
            intent.putExtra("playList", playList)
            binding.root.context.startActivity(intent)
        }
            when (music?.favorite) {
                0 -> {
                    binding.ivStar.setImageResource(R.drawable.star_empty_24)
                }

                1 -> {
                    binding.ivStar.setImageResource(R.drawable.star_full_24)
                }
            }
            binding.ivStar.setOnClickListener {
                when (music?.favorite) {
                    0 -> {
                        music.favorite = 1
                        if (dbHelper2.updateFavorite(music)) {
                            Toast.makeText(context, "업데이트 성공", Toast.LENGTH_SHORT).show()
                        }
                        //이 포지션이 변경되었으니 확인
                        notifyItemChanged(position)
                    }
                    1 -> {
                        music.favorite = 0
                        if (dbHelper2.updateFavorite(music)) {
                            Toast.makeText(context, "즐겨찾기 삭제", Toast.LENGTH_SHORT).show()
                        }
                        //리사이클러뷰한테 전체 다 확인하라는 명령
                        notifyDataSetChanged()
                    }
                }
            }
        }
        override fun getItemCount(): Int {
            //MutableList의 musicList의 사이즈를 넣습니다.
            //뮤직리스트가 null이 아니면 사이즈를주고 null이면 0을 줍니다.
            return musicList?.size ?: 0
        }

        //2.뷰홀더를 하나 생성합니다. 내부클래스든 외부클래스든 상관없이 선언합니다. 그리고 바인딩하여 데이터를 불러와야합니다.
        class CustomViewHolder(val binding: ItemRecyclerBinding) :
            RecyclerView.ViewHolder(binding.root)
    }