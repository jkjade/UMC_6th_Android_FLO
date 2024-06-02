package com.example.flo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentDetailBinding
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment() {

    lateinit var binding: FragmentSongBinding

    fun onTransmitData(){
        val spf = requireContext().getSharedPreferences("song", Context.MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("songTitle", binding.songListTitle01Tv.text.toString())
        editor.putString("songSinger", binding.songSingerName01Tv.text.toString())
        editor.apply()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSongBinding.inflate(inflater, container, false)

        binding.songMixoffTg.setOnClickListener {
            setMixStatus(false)
        }
        binding.songMixonTg.setOnClickListener {
            setMixStatus(true)
        }

        // 7주차 과제 추가==========
        /*val songDB = SongDatabase.getInstance(requireContext())!!

        val bundle = Bundle().apply {
            putString("title01", binding.songMusicTitle01Tv.text.toString())
            putString("singer01", binding.songSingerName01Tv.text.toString())
        }
        HomeFragment().apply {
            arguments = bundle
        }*/




        return binding.root
    }

    fun setMixStatus(isPlaying : Boolean) {
        if(isPlaying) {
            binding.songMixoffTg.visibility = View.VISIBLE
            binding.songMixonTg.visibility = View.GONE
        }
        else {
            binding.songMixoffTg.visibility = View.GONE
            binding.songMixonTg.visibility = View.VISIBLE
        }
    }

}