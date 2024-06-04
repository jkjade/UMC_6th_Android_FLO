package com.example.flo.ui.song

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.R
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivitySongBinding
import com.example.flo.ui.main.MainActivity
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initPlayList()
        initSong()
        initClickListener()
    }

    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause(){
        super.onPause()
        setPlayerStatus(false)
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime)/100)/1000

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터


        editor.putInt("songId", songs[nowPos].id)

        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null // 미디어플레이어 해제
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initSong() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID", songs[nowPos].id.toString())
        startTimer()

        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for(i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song) {
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        if (song.isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        setPlayerStatus(song.isPlaying)
    }

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener {

            val song = Song(
                binding.songMusicTitleTv.text.toString(),
                binding.songSingerNameTv.text.toString()
            )

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("title", song.title)
            startActivity(intent)

            finish()
        }

        // 재생 버튼
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }

        // 정지 버튼
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        // 다음 곡 버튼
        binding.songNextIv.setOnClickListener{
            moveSong(+1)
        }

        // 이전 곡 버튼
        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        // 반복 버튼
        binding.songRepeatIv.setOnClickListener {
            setRepeatStatus(false)
        }
        binding.songRepeatActiveIv.setOnClickListener {
            setRepeatStatus(true)
        }

        // 랜덤 버튼
        binding.songRandomIv.setOnClickListener {
            setRandomStatus(false)
        }
        binding.songRandomActiveIv.setOnClickListener {
            setRandomStatus(true)
        }

        // 좋아요 버튼
        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }



    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if(!isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            Toast.makeText(this, "좋아요 반영되었습니다", Toast.LENGTH_SHORT).show()
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            Toast.makeText(this, "좋아요 취소되었습니다", Toast.LENGTH_SHORT).show()
        }
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

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])

    }





    fun setPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

         if (isPlaying) {
             binding.songMiniplayerIv.visibility = View.GONE
             binding.songPauseIv.visibility = View.VISIBLE
             mediaPlayer?.start()
         } else {
             binding.songMiniplayerIv.visibility = View.VISIBLE
             binding.songPauseIv.visibility = View.GONE
             if(mediaPlayer?.isPlaying == true){
                 mediaPlayer?.pause()
             }
         }
    }

    fun setRepeatStatus(isPlaying: Boolean) {
        if (isPlaying) {
            binding.songRepeatIv.visibility = View.VISIBLE
            binding.songRepeatActiveIv.visibility = View.GONE
        } else {
            binding.songRepeatIv.visibility = View.GONE
            binding.songRepeatActiveIv.visibility = View.VISIBLE
        }
    }

    fun setRandomStatus(isPlaying: Boolean) {
        if (isPlaying) {
            binding.songRandomIv.visibility = View.VISIBLE
            binding.songRandomActiveIv.visibility = View.GONE
        } else {
            binding.songRandomIv.visibility = View.GONE
            binding.songRandomActiveIv.visibility = View.VISIBLE
        }
    }

    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int,var isPlaying: Boolean = true):Thread(){
        private var second : Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try{
                while(true){
                    if(second >= playTime){
                        break
                    }
                    if(isPlaying){
                        sleep(50)
                        mills += 50

                        runOnUiThread{
                            binding.songProgressSb.progress = ((mills / playTime)*100).toInt()
                        }

                        if(mills % 1000 == 0f){
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            }catch (e: InterruptedException){
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }

        }
    }
}
