package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.BottomSheetLayoutBinding
import com.example.flo.databinding.FragmentLockerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding

    private val information = arrayListOf("저장한곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        val activity = requireActivity() as MainActivity
        val bottomSheetBinding: BottomSheetLayoutBinding = BottomSheetLayoutBinding.inflate(inflater, container, false)
        // 전체 선택 클릭 시 BottomSheetDialog 나타남
        binding.lockerAllSelectTv.setOnClickListener {
            //activity.BottomSheetDialog()

            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

            val bottomSheetDeleteIv: ImageView = bottomSheetView.findViewById(R.id.bottom_sheet_delete_iv)
            bottomSheetDeleteIv.setOnClickListener {


                var songDB = SongDatabase.getInstance(requireContext())!!
                songDB.songDao().updateAllLikedSongsToFalse()

                Log.d("되나", "클릭되었음")
                // BottomSheetDialog를 닫기
                bottomSheetDialog.dismiss()
            }
        }



        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt", 0)
    }

    private fun initView() {
        val jwt: Int = getJwt()
        if (jwt == 0) {
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity,LoginActivity::class.java))
            }
        } else {
            binding.lockerLoginTv.text = "로그아웃"
            binding.lockerLoginTv.setOnClickListener{
                // 로그아웃 진행
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun logout(){
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()
        editor.remove("jwt")
        editor.apply()
    }



}