package com.example.chapter17mp3test

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.io.IOException

//mp3DB에들어갈 내용입니다.
@Parcelize
class Music(var id: String,var title: String?,var artist: String?,var albumId: String?,var duration: Int?, var favorite: Int?):
    //PlayMusicActivity에 인텐트로 객체를 전송하기 위해서, serializable -> Parcelable로 사용합니다.
    //그 이유는 속도처리와, 용량처리가 용이하기 때문에
    //PlayMusicActivity 객체를 넘기기 위해 serializable 말고 Parcelabe사용
    Parcelable {
    companion object : Parceler<Music> {

        override fun Music.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(title)
            parcel.writeString(artist)
            parcel.writeString(albumId)
            parcel.writeInt(duration!!)
            parcel.writeInt(favorite!!)
        }

        override fun create(parcel: Parcel): Music {
            return Music(parcel)
        }
    }
    //생성자 일반객체내용을 전송하기 위해서 parcel 변환시켜줍니다.
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )
    fun getMusicUri(): Uri {
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    //앨범의 URI을 가져오는 방법이 있습니다. 콘텐츠 리졸버를 이용해서 음악파일에 URI 정보를 가져오기 위한
    //방법이 있습니다. id가지고 음악파일의 URI을 얻어와서 그 음악에 대한 모든것을 가져올 수 있게 합니다.
    //"컨텐츠리졸버를 이용해서 음악파일을 가져오고 해당되는 음악파일결로는 Uri"
    fun getAlbumUri(): Uri {
        return Uri.parse("content://media/external/audio/albumart/"+albumId)
    }

    //해당되는 음악의 이미지를 내가 원하는 사이즈로 비트맵을 만들어 돌려줍니다.
    //여기에는 비트맵을 구하는곳 입니다.(해당되는 음악에 비트맵 만들기)
    fun getAlbumImage(context: Context, albumImageSize: Int): Bitmap? {
        //비트맵을 구하려면 컨텐츠 리졸버를 하나 가져와야합니다.
        val contentResolver: ContentResolver = context.getContentResolver()
        //앨범에 대한 경로
        //이 uri을 통해서 비트맵을 만들고 원하는 사이즈로 만듭니다.
        val uri = getAlbumUri()
        //앨범에 대한 경로를 저장하기위한 옵션
        val options = BitmapFactory.Options()
        // != null의 뜻은 "uri가 널이 아니라면" 이라고 해석
        if (uri != null) {
            var parceFileDescriptor: ParcelFileDescriptor? = null
            try {
                //해당된 파일을 컨텐츠리졸버를 통해서 읽어와야합니다.
                //외부파일에 있는 이미지 정보를 가져오기 위한 스트림입니다.
                parceFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                //decodeFileDescriptor은 파일을 해석해주는것.
                var bitmap = BitmapFactory.decodeFileDescriptor(parceFileDescriptor!!.fileDescriptor, null, options)
                //비트맵을 가져와서 사이즈를 결정합니다.
                if(bitmap != null) {
                    //비트맵이 널이 아니면 원하는 사이즈로 변경하게 합니다.
                    //내가 원하는 사이즈하고 맞지 않을 경우 원하는 사이즈로 다시 만듭니다.
                    if(options.outHeight !== albumImageSize || options.outWidth !== albumImageSize) {
                        //원본을 내가 원하는 앨범 사이즈로 변경하겠다.
                        val tempBitmap = Bitmap.createScaledBitmap(bitmap, albumImageSize, albumImageSize, true)
                        bitmap.recycle()
                        bitmap = tempBitmap
                    }
                }
                return bitmap

            }catch (e: Exception) {
                Log.d("shin", "getAlbumImage() ${e.toString()}")
            }finally {
                try {
                    parceFileDescriptor?.close()
                }catch (e: IOException) {
                    Log.d("shin", "parceFileDescriptor?.close() ${e.toString()}")
                }
            }
        }//end of if
        return null
    }//end of getAlbumImage

}//end of Parcelabe
