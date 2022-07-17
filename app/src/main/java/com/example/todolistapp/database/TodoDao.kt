package com.example.todolistapp.database

import androidx.room.*
import com.example.todolistapp.model.TodoInfo

@Dao
interface TodoDao {

    // db table 삽입 (추가)
    @Insert
    fun insertTodoData(todoInfo: TodoInfo)

    // db table 수정
    @Update
    fun updateTodoData(todoInfo: TodoInfo)

    // db table 삭제
    @Delete
    fun deleteTodoData(todoInfo: TodoInfo)

    // db table 전체 데이터 가지고 옴. (조회)
    // SELECT : 조회, * : all, DESC 내림차순
    @Query("SELECT * FROM TodoInfo ORDER BY todoDate")
    fun getAllReadData(): List<TodoInfo>
}