package com.example.flo.ui.main

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.main.locker.LockerFragment
import com.example.flo.ui.main.look.LookFragment
import com.example.flo.R
import com.example.flo.ui.main.search.SearchFragment
import com.example.flo.ui.song.SongActivity
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    private var mediaPlayer: MediaPlayer? = null

    fun onDataReceived(jsonData: String) {
        // HomeFragment에서 받은 includedSongs의 id값으로 songs의 맨 뒤에 추가함
        // 포커스를 includedSongs의 첫 번째 곡으로 설정할 것


        val gson = Gson()
        val idArray = gson.fromJson(jsonData, Array<Int>::class.java)
        val idArrayList: ArrayList<Int> = ArrayList(idArray.toList())

        songDB = SongDatabase.getInstance(this)!!

        nowPos = songs.size

        for(i in 0 until idArrayList.size) {
            songs.add(songDB.songDao().getSong(idArrayList[i]))
        }

        setMiniPlayer(songs[nowPos])

        Log.d("mainAlbumId", songs.toString())

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Album 과 Song 더미 데이터를 만드는 순서에 주의할 것
        inputDummyAlbums()
        inputDummySongs()
        initBottomNavigation()

        binding.mainPlayerCl.setOnClickListener() {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        if(intent.hasExtra("title")){
            var message = intent.getStringExtra("title")
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // 7주차 과제 추가함==================================================================

        // 재생 버튼
        binding.mainMiniplayerBtn.setOnClickListener {
            setPlayerStatus(true)
        }

        // 정지 버튼
        binding.mainPauseBtn.setOnClickListener {
            setPlayerStatus(false)
        }

        // 다음 곡 버튼
        binding.mainNextIv.setOnClickListener {
            moveSong(+1)
        }

        // 이전 곡 버튼
        binding.mainPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        Log.d("MAIN/JWT_TO_SERVER", getJwt().toString())


    }

    private fun moveSong(direct: Int){
        if(nowPos + direct < 0){
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if(nowPos + direct >= songs.size){
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        setMiniPlayer(songs[nowPos])

    }

    private fun setPlayerStatus(isPlaying: Boolean){
        songs[nowPos].isPlaying = isPlaying

        if (isPlaying) {
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            //mediaPlayer?.start()
        } else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
//            if(mediaPlayer?.isPlaying == true){
//                mediaPlayer?.pause()
//            }
        }
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for(i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setMiniPlayer(song: Song){
        binding.mainPlayerTitleTv.text = song.title
        binding.mainPlayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second*100000)/song.playTime

        //val music = resources.getIdentifier(song.music, "raw", this.packageName)
        //mediaPlayer = MediaPlayer.create(this, music)

        setPlayerStatus(song.isPlaying)

    }

    private fun getJwt(): String? {
        val spf = this.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)

        return spf!!.getString("jwt", "")
    }

    override fun onStart() {
        super.onStart()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())

        nowPos = getPlayingSongPosition(songId)

        songs[nowPos] = if (songId == 0){
            songDB.songDao().getSong(1)
        } else {
            songDB.songDao().getSong(songId)
        }

        Log.d("song ID", songs[nowPos].id.toString())
        setMiniPlayer(songs[nowPos])

    }

    private fun inputDummyAlbums(){
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if(albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                1,
                "IU 5th Album",
                "아이유 (IU)",
                R.drawable.img_album_exp2

            )
        )

        songDB.albumDao().insert(
            Album(
                2,
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp

            )
        )

        songDB.albumDao().insert(
            Album(
                3,
                "Remixes",
                "에스파 (AESPA)",
                R.drawable.img_album_exp3

            )
        )

        songDB.albumDao().insert(
            Album(
                4,
                "SOUL PERSONA",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp4

            )
        )

        songDB.albumDao().insert(
            Album(
                5,
                "GREAT!",
                "모모랜드",
                R.drawable.img_album_exp5

            )
        )

        val _albums = songDB.albumDao().getAlbums()
        Log.d("album Data", _albums.toString())
    }


    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if(songs.isNotEmpty()) return


        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                230,
                false,
                "music_flu",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_boy",
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                230,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )

        songDB.songDao().insert(
            Song(
                "Savage",
                "에스파 (AESPA)",
                0,
                230,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )

        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                false,
                5
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }



}