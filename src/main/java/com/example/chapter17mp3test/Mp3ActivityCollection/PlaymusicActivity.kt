package com.example.chapter17mp3test.Mp3ActivityCollection

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.chapter17mp3test.DBHelper2
import com.example.chapter17mp3test.Music
import com.example.chapter17mp3test.R
import com.example.chapter17mp3test.databinding.ActivityPlaymusicBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlaymusicActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlaymusicBinding

    //1. 뮤직플레이어 변수를 선언합니다
    private var mediaPlayer: MediaPlayer? = null
    //2.음악정보객체 변수를 선언합니다.
    private var music: Music? = null
    private var playList: ArrayList<Parcelable>? = null

    private var position: Int = 0

    //3. 음악앨범이미지 사이즈
    private val ALBUM_IMAGE_SIZE = 150
    //4. 코루틴 스코프 launch
    private var playerJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityPlaymusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playList = intent.getParcelableArrayListExtra("playList")
        position = intent.getIntExtra("position", 0)
        music = playList?.get(position) as Music

        startMusic(music)


    }//end of onCreate

    fun startMusic(music: Music?) {
        if(music != null) {
            binding.tvPlayTitle.text = music?.title
            binding.tvPlayArtist.text = music?.artist
            binding.tvPlayTimerL.text = "00:00"
            //duration이 int니까 toInt를 따로 적어줍니다.
            binding.tvPlayTimerR.text = SimpleDateFormat("mm:ss").format(music?.duration)
            val bitmap: Bitmap? = music?.getAlbumImage(this, ALBUM_IMAGE_SIZE)
            if(bitmap != null) {
                binding.ivPlayPicture.setImageBitmap(bitmap)
            }else {
                binding.ivPlayPicture.setImageResource(R.drawable.music_collection_24)
            }
            //음원실행 생성 및 재생 여기에 음악을 구현합니다.
            mediaPlayer = MediaPlayer.create(this, music?.getMusicUri() )
            binding.seekBar.max = music?.duration!!.toInt()

            //SeekBar 이벤트를 설정합니다. -> 노래와 같이 동기화 처리가 됩니다.
            binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                //시크바를 터치하고 이동할때 발생되는 이벤트입니다.
                //fromUser : 유저에 의한 터치유무
                override fun onProgressChanged(seekBar : SeekBar?, progress : Int, fromUser : Boolean) {
                    if(fromUser) {
                        //노래에 대한 모든 정보는 mediaPlayer에 있습니다.
                        //미디어플레이어에 음악위치를 시크바에서 값을가져와서 셋팅합니다.
                        mediaPlayer?.seekTo(progress)
                    }
                }
                //시크바를 터치하는 순간 이벤트가 발생합니다.
                override fun onStartTrackingTouch(p0: SeekBar?) {

                }
                //시크바를 터치하고 손을 떼는 순간 이벤트 발생
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
        }//end of if
    }

    fun onClickView(view: View?) {
        when(view?.id) {
            R.id.ivPlayList -> {    //음악을 정지하고 스레드도 코루틴도 취소가되고, 음악 객체를 해제시키고, //음악객체에 null을 줘야합니다.
                mediaPlayer?.stop()
                playerJob?.cancel()
                finish()
            }
            R.id.ivPlay -> {
                if (mediaPlayer?.isPlaying == true) {
                    binding.ivPlay.setImageResource(R.drawable.play_circle_outline_24)
                    binding.seekBar.progress = mediaPlayer?.currentPosition!!
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                    binding.ivPlay.setImageResource(R.drawable.pause_circle_outline_24)
                    //음악 시작 (시크바, 진행시간 코루틴으로 진행)
                    val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                    playerJob = backgroundScope.launch {
                        //노래 진행 사항을 시크바와 시작진행시간 값에 넣어주기
                        //사용자가 만든 스레드에서 화면에 뷰 값을 변경하게 되면 오류 발생
                        //해결방법 : 스레드 안에서 뷰 값을 변경하고 싶으면 runOnUiThread{  } 사용
                        while (mediaPlayer?.isPlaying == true) {
                            //노래 진행 위치를 시크바에 적용
                            runOnUiThread {
                                var currentPosition = mediaPlayer?.currentPosition!!
                                binding.seekBar.progress = currentPosition
                                binding.tvPlayTimerL.text =
                                    SimpleDateFormat("mm:ss").format(currentPosition)
                            }
                            try {
                                delay(500)
                            } catch (e: Exception) {
                                Log.d("shin", "delay : ${e.toString()}")
                            }
                        }//end of while
                        //음악이 끝났으면 seekBar 초기화
                        runOnUiThread {
                            if(mediaPlayer!!.currentPosition >= binding.seekBar.max - 1000) {
                                binding.seekBar.progress = 0
                                binding.tvPlayTimerL.text = "00:00"
                            }
                        }
                        try {
                            binding.ivPlay.setImageResource(R.drawable.play_circle_outline_24)
                        }catch (e:Exception) {

                        }
                    }//end of playerJob
                }//end of if
            }//end of R.id.ivPlay
            R.id.ivPlayStop -> {
                mediaPlayer?.stop()
                playerJob?.cancel()
                //현재 음악을 다시 가져옵니다.
                mediaPlayer =  MediaPlayer.create(this, music?.getMusicUri())
                binding.seekBar.progress = 0
                mediaPlayer?.seekTo(0)
                binding.tvPlayTimerL.text = "00:00"
                binding.ivPlay.setImageResource(R.drawable.play_circle_outline_24)
            }
            R.id.ivLeft -> {
                //음악 정지, 코루틴 정지, 음악 객체 해제
                mediaPlayer?.stop()
                playerJob?.cancel()

                position = position -1
                if(position < 0) {
                    position = playList!!.size -1
                }
                music = playList?.get(position) as Music
                startMusic(music)
            }
            R.id.ivRight -> {
                mediaPlayer?.stop()
                playerJob?.cancel()

                position = position +1
                if(position > playList!!.size -1) {
                    position = 0
                }
                music = playList?.get(position) as Music
                startMusic(music)
            }
        }// end of when
    }//end of onClickView

}//end of class