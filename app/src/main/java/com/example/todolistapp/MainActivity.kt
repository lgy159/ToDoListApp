package com.example.todolistapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todolistapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 액티비티가 생성될 때 호출되며 사용자에게 인터페이스 초기화 할 때 구현
        println("hii onCreate !!")

        binding.tvName.setText("안녕하세요")
        binding.tvTest.setText("화이팅")
    }

    // 생명주기 매우 중요 !!
    override fun onStart() {
        super.onStart()
        // 액티비티가 사용자에게 보여지기 직전 호출 됨
        println("hii onStart !!")
    }

    override fun onResume() {
        super.onResume()
        // 액티비티가 사용자와 상호작용 하기 직전에 호출 됨
        println("hii onResume !!")
    }

    override fun onPause() {
        super.onPause()
        // 다른 액티비티가 보여질때 호출 됨 (중지 상태)
        println("hii onPause !!")
    }

    override fun onStop() {
        super.onStop()
        // 액티비가 사용자에게 완전히 보여지지 않을때 사용 됨
        println("hii onStop !!")
    }

    override fun onDestroy() {
        super.onDestroy()
        // 액티비티가 완전히 소멸될 때 호출 됨
        println("hii onDestroy !!")
    }

    override fun onRestart() {
        super.onRestart()
        // 액티비티가 멈췄다가 다시 시작될때 호출 됨
        println("hii onRestart !!")
    }
}