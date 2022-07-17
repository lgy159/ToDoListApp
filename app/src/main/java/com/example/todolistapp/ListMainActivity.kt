package com.example.todolistapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.todolistapp.adapter.TodoAdapter
import com.example.todolistapp.database.TodoDatabase
import com.example.todolistapp.databinding.ActivityListMainBinding
import com.example.todolistapp.databinding.ActivityMainBinding
import com.example.todolistapp.databinding.DialogEditBinding
import com.example.todolistapp.model.TodoInfo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListMainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityListMainBinding
    private lateinit var todoAdapter : TodoAdapter
    private lateinit var roomDatabase : TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 모바일 광고 SDK 초기화
        MobileAds.initialize(this) {}

        // 하단 배너 광고 로드
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        // 어댑터 인스턴스 생성
        todoAdapter = TodoAdapter()

        // 라사이클러뷰에 어댑터 세팅
        binding.rvTodo.adapter = todoAdapter

        // 룸데이터베이스 초기화//  applicationContext?
        roomDatabase = TodoDatabase.getInstance(applicationContext)!! // non-null

        // 전체 데이터 load (비동기 - 비순서적으로 하나의 코드가 끝날떄까지 기다려주지않고 다른 코드를 이어서 실행시켜놓고 완료됨과 상관없이 쭉)
        CoroutineScope(Dispatchers.IO).launch {
            val lstTodo = roomDatabase.todoDao().getAllReadData() as ArrayList<TodoInfo>
            for (todoItem in lstTodo) {
                todoAdapter.addListItem(todoItem)
            }
            // UI Thread 에서 처리
            runOnUiThread {
                todoAdapter.notifyDataSetChanged()
            }
        }

        // 작성하기 버튼 클릭
        binding.btnWrtie.setOnClickListener {
            val bindingDialog = DialogEditBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)

            AlertDialog.Builder(this).setTitle("To-Do남기기").setView(bindingDialog.root)
                .setPositiveButton("작성완료", DialogInterface.OnClickListener { dialogInterface, i ->
                    val todoItem = TodoInfo()
                    todoItem.todoContent = bindingDialog.etMemo.text.toString()
                    todoItem.todoDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    todoAdapter.addListItem(todoItem)   // 어댑터의 전역변수의 arrayList 쪽 아이템 추가하기 위한 메소드 호출
                    CoroutineScope(Dispatchers.IO).launch {
                        roomDatabase.todoDao().insertTodoData(todoItem) // 데이터베이스 또한 클래스 데이터 삽입
                        runOnUiThread {
                            todoAdapter.notifyDataSetChanged()  // 새로고침 로직
                        }
                    }
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->

                }).show()
        }
    }
}