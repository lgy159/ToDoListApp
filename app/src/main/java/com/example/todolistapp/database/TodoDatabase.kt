package com.example.todolistapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todolistapp.model.TodoInfo

@Database(entities = [TodoInfo::class], version = 1)
// abstract: 추상, 메소드 같은거 만들어놓고 실제로 사용은 하지 않음.
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object { // 클래스를 따로 생성하지않고, 메소드처럼 사용할수있게 함. ( 고정 메모리라는 개념, 앱마다 인스턴스를 생성하지 않고 싱글 턴 패턴으로 한번만 호출 )
        private var instance: TodoDatabase? = null
        @Synchronized // 동시 호출 방지 (액티비티가 여러개인 경우)
        fun getInstance(context: Context) : TodoDatabase? {
            if (instance == null) {
                synchronized(TodoDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TodoDatabase::class.java,"todo-database"
                    ).build()
                }
            }
            return instance
        }
    }
}