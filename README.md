# H1 Kotlin(Android Studio)프로그램을 이용한 Mp3Player
기존 핸드폰에 등록된 Mp3파일을 로그인식으로 만들고,
다양한 기능을 추가하여 보다 편하게 음악을 들을 수 있도록 만들은 프로그램입니다.

1. Mp3 로그인
2. 회원가입, 회원목록, 수정, 삭제 기능 추가
3. MediaPlayer 출력
4. 검색
5. 즐겨찾기(Favorite)
6. 음악 재생 화면
7. 이전곡, 다음곡

![mp3 03png](https://user-images.githubusercontent.com/100817617/173474730-de4613b4-7c6b-4e48-aab0-daa3ee8abcb7.png)

![mp3 04](https://user-images.githubusercontent.com/100817617/173474904-c94c2476-a790-4734-b33c-6a756823ef2e.png)
## H2 Mp3MainActivity의 중요 기능
```
class Mp3MainActivity : AppCompatActivity()
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
```
![mp3 01](https://user-images.githubusercontent.com/100817617/173474736-bb53356b-b29f-4146-85f2-b61982f60f2d.png)
## H2 RecyclerView에 출력되는 별(즐겨찾기)
```
DB를 가져옵니다.
val dbHelper2: DBHelper2 by lazy {
        DBHelper2(context, "musicDB", 1)
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
    
   
```




![mp3 02](https://user-images.githubusercontent.com/100817617/173474738-2edf74f5-1764-4a9d-a5e0-17e1c1037b07.png)
![mp3 05](https://user-images.githubusercontent.com/100817617/173474870-f683320d-1487-4692-8b48-b4be9fd6e790.PNG)
## H2 이전곡 다음곡으로 가는 기능
```
private var position: Int = 0

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
```

