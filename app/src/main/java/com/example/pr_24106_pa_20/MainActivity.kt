package com.example.pr_24106_pa_20

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var edName: EditText
    private lateinit var edSName: EditText
    private lateinit var edMail: EditText
    private lateinit var btnSave: Button
    private lateinit var btnRead: Button
    private lateinit var tvOutput: TextView
    private lateinit var database: DatabaseReference
    private val USERS_KEY = "users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        database = FirebaseDatabase.getInstance().getReference(USERS_KEY)
        btnSave.setOnClickListener { saveUserToFirebase() }
        btnRead.setOnClickListener { readUsersFromFirebase() }
    }

    private fun initViews() {
        edName = findViewById(R.id.edName)
        edSName = findViewById(R.id.edSName)
        edMail = findViewById(R.id.edMail)
        btnSave = findViewById(R.id.btnSave)
        btnRead = findViewById(R.id.btnRead)
        tvOutput = findViewById(R.id.tvOutput)
    }

    private fun saveUserToFirebase() {
        val edName = edName.text.toString().trim()
        val lastName = edSName.text.toString().trim()
        val email = edMail.text.toString().trim()
        if (edName.isEmpty() || lastName.isEmpty() || email.isEmpty()
        ) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            return
        }


        val user = User(edName, edSName, edMail)
        val userId = database.push().key ?: return
        database.child(userId).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Пользователь сохранён!", Toast.LENGTH_SHORT).show()
                clearInputFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun readUsersFromFirebase() {
        tvOutput.text = "Загрузка..."

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    tvOutput.text = "Нет данных в Firebase"
                    return
                }

                val result = StringBuilder()

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        result.append("👤 Имя: ${user.edName}\n")
                        result.append("📛 Фамилия: ${user.edSName}\n")
                        result.append("📧 Email: ${user.edMail}\n")
                    }
                }

                tvOutput.text = result.toString()
                Toast.makeText(this@MainActivity, "Данные загружены", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                tvOutput.text = "Ошибка: ${error.message}"
                Toast.makeText(this@MainActivity, "Ошибка загрузки: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearInputFields() {
        edName.text.clear()
        edSName.text.clear()
        edMail.text.clear()
        edName.requestFocus()
    }
}