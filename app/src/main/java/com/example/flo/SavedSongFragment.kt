package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentSavedsongBinding

class SavedSongFragment : Fragment() {

    lateinit var binding : FragmentSavedsongBinding
    private var savedSongDatas = ArrayList<SavedSong>()

    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedsongBinding.inflate(inflater, container, false)


        // 더미 데이터
//        savedSongDatas.apply {
//            add(SavedSong("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
//            add(SavedSong("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
//            add(SavedSong("Next level", "에스파 (AESPA)", R.drawable.img_album_exp3))
//            add(SavedSong("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
//            add(SavedSong("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
//            add(SavedSong("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
//            add(SavedSong("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
//            add(SavedSong("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
//            add(SavedSong("Next level", "에스파 (AESPA)", R.drawable.img_album_exp3))
//            add(SavedSong("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
//            add(SavedSong("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
//            add(SavedSong("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
//        }
//
//        val savedSongRVAdapter = SavedSongRVAdapter(savedSongDatas)
//        binding.savedsongSongRv.adapter = savedSongRVAdapter
//        binding.savedsongSongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//
//        savedSongRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
//            override fun onRemoveSong(position: Int) {
//                savedSongRVAdapter.removeItem(position)
//            }
//
//        })


        // 추가===============================================================

        songDB = SongDatabase.getInstance(requireContext())!!
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

    private fun initRecyclerView(){
        binding.savedsongSongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdapter = SavedSongRVAdapter()

        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener{
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false,songId)
            }
        })


        binding.savedsongSongRv.adapter = songRVAdapter

        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
    }
}