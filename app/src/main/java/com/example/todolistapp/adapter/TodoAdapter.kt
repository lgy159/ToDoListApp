package com.example.todolistapp.adapter

import android.app.Activity
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.database.TodoDatabase
import com.example.todolistapp.databinding.ActivityMainBinding
import com.example.todolistapp.databinding.DialogEditBinding
import com.example.todolistapp.databinding.ListItemTodoBinding
import com.example.todolistapp.model.TodoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ToDoViewHolder>(){
    private var lstTodo : ArrayList<TodoInfo> = ArrayList()
    private lateinit var roomDatabase : TodoDatabase

    init {
    }

    fun addListItem(todoItem: TodoInfo){
        // add는 배열의 마지막에 추가됨, 하지만 우리가 만들 앱은 새로 생성되는건 맨 앞에 생성되게 해야함.
        lstTodo.add(0, todoItem)
    }
                                        // 아래 onCreateViewHolder에서 받은 Binding, // 내부 클래스 ToDoViewHolder 역시 상속을 받아야 함.
    inner class ToDoViewHolder(private val binding: ListItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todoItem: TodoInfo){
            // 리스트 뷰 데이터를 UI에 연동
            binding.tvContent.setText(todoItem.todoContent)
            binding.tvDate.setText(todoItem.todoDate)

            // 리스트 삭제 버튼 연동
            binding.btnRemove.setOnClickListener {
                // 쓰레기통 이미지 버튼 클릭시 내부 로직 수행
                AlertDialog.Builder(binding.root.context).setTitle("[주의]")
                    .setMessage("제거하시면 데이터는 복구되지 않습니다!\n정말 제거하시겠습니까?")
                    .setPositiveButton("제거", DialogInterface.OnClickListener { dialogInterface, i ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val innerLstTodo = roomDatabase.todoDao().getAllReadData()
                            for (item in innerLstTodo) {
                                if (item.todoContent == todoItem.todoContent && item.todoDate == todoItem.todoDate) {
                                    roomDatabase.todoDao().deleteTodoData(item)
                                }
                            }
                            // ui remove
                            lstTodo.remove(todoItem)
                            (binding.root.context as Activity).runOnUiThread {
                                notifyDataSetChanged()
                                Toast.makeText(binding.root.context, "제거되었습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    .show()
            }

            // 리스트 수정 클릭 연동
            binding.root.setOnClickListener {
                val bindingDialog = DialogEditBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)
                // 기존 작성된 데이터 보여주기
                bindingDialog.etMemo.setText(todoItem.todoContent)
                AlertDialog.Builder(binding.root.context).setTitle("To-Do남기기").setView(bindingDialog.root)
                    .setPositiveButton("수정완료", DialogInterface.OnClickListener { dialogInterface, i ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val innerLstTodo = roomDatabase.todoDao().getAllReadData()
                            for (item in innerLstTodo) {
                                if (item.todoContent == todoItem.todoContent && item.todoDate == todoItem.todoDate) {
                                    item.todoContent = bindingDialog.etMemo.text.toString()
                                    item.todoDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                                    roomDatabase.todoDao().updateTodoData(item)
                                }
                            }
                            todoItem.todoContent = bindingDialog.etMemo.text.toString()
                            todoItem.todoDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                            // array list 수정
                            lstTodo.set(adapterPosition, todoItem)
                            (binding.root.context as Activity).runOnUiThread {
                                notifyDataSetChanged()
                            }
                        }


                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, i ->
                    }).show()
            }
        }
    }

    // 뷰홀더가 생성됨 (각 리스트 아이템 1개씩 구성될 때 마다 이 오버라이드 메소드가 호출 됨)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.ToDoViewHolder {
        // ListItemTodoBinding : list_item_todo.xml을 생성해놨기 때문.
        val binding = ListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        roomDatabase = TodoDatabase.getInstance(binding.root.context)!! // non-null
        return ToDoViewHolder(binding) // binding 생성한걸 ToDoViewHolder(클래스) 생성자에 던짐
    }

    // 뷰홀더가 바인딩 (껼합) 이루저일 때 해줘야 될 처리들을 구현.
    override fun onBindViewHolder(holder: TodoAdapter.ToDoViewHolder, position: Int) {
        holder.bind(lstTodo[position])
    }

    // 리스트 총 개수
    override fun getItemCount(): Int {
        return lstTodo.size
    }
}