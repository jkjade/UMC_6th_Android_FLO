package com.example.flo


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson
import me.relex.circleindicator.CircleIndicator3

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    var currentPosition = 0
    private var albumDatas = ArrayList<Album>()

    val handler = Handler(Looper.getMainLooper()){
        setPage()
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 7주차 과제 추가=========================================================
        val songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())

        // ======================================================================



        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }

            // 앨범의 플레이 버튼을 누르면 미니 플레이어의 제목, 가수 명 변경
            override fun onPlaySong(album: Album) {
                val activity = requireActivity() as MainActivity

                //activity.onDataReceived("Lilac", "아이유 (IU)")

                // 플레이 버튼을 누르면 수록곡 하나가 미니 플레이어와 SongActivity에도 새로 저장이 되게 해야함
                // 수록곡을 어떻게 데베에 저장할 것인가.
                // activity.onDataReceived(album.title.toString(), album.singer.toString())

                // albumIdx를 사용하여 수록곡 불러오기
                // ex) 클릭된 앨범의 인덱스가 1이면 albumIdx == 1 인 수록곡 모두 재생(미니플레이어와 SongActivity에 추가)


                // 플레이 버튼이 눌린 앨범의 albumId의 값을 albumIdx로 가진 Song들을 includedSongs 배열에 추가함
                val includedSongs = ArrayList<Song>()
                val ids = ArrayList<Int>()

                includedSongs.addAll(songDB.songDao().getIncludedSongs(album.id))


                for (i in 0 until includedSongs.size) {
                    ids.add(includedSongs[i].id)
                }

                val gson = Gson()
                val includedSongIdJson = gson.toJson(ids)

                activity.onDataReceived(includedSongIdJson)

                Log.d("albumIdCheck", ids.toString())

            }

        })

        // BannerVPAdapter 클래스를 bannerAdapter 변수에 대입함
        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter

        // 수평을 기준으로 뷰페이저가 넘어가게 함
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        // pannelFragment 뷰페이저와 연결
        val pannelAdapter = PannelVPAdapter(this)
        binding.homePannelBackgroundVp.adapter = pannelAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // indicator 구현
        val indicator : CircleIndicator3 = binding.indicator
        indicator.setViewPager(binding.homePannelBackgroundVp)
        indicator.createIndicators(3, 0)
        indicator.animatePageSelected(0)

        val thread = Thread(PagerRunnable())
        thread.start()

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    fun setPage(){
        if(currentPosition==3) currentPosition=0
        binding.homePannelBackgroundVp.setCurrentItem(currentPosition,true)
        currentPosition+=1
    }

    // 2초에 한 번씩 넘어가는 자동 슬라이드
    inner class PagerRunnable:Runnable{
        override fun run() {
            while(true) {
                Thread.sleep(3000)
                handler.sendEmptyMessage(0)
            }
        }
    }

}