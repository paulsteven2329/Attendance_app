package com.example.myproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextEducation: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextSubject: EditText
    private lateinit var buttonRegister: Button

    // Firebase
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference.child("teachers")

        editTextName = findViewById(R.id.editTextName)
        editTextAge = findViewById(R.id.editTextAge)
        editTextEducation = findViewById(R.id.editTextEducation)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextSubject = findViewById(R.id.editTextSubject)
        buttonRegister = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            // Get user input
            val name = editTextName.text.toString()
            val age = editTextAge.text.toString().toInt()
            val education = editTextEducation.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val subject = editTextSubject.text.toString()

            // Store user data in Firebase
            val userId = database.push().key // Generate a unique key for the user
            if (userId != null) {
                val user = User(name, age, education, email, password, subject)
                database.child(userId).setValue(user)
                    .addOnSuccessListener {
                        // Display registration success message
                        Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()

                        // Redirect to dashboard activity
                        val intent = Intent(this@RegisterActivity, Dashboard::class.java)
                        startActivity(intent)
                        finish() // Optional: Finish current activity to prevent returning to it when pressing back
                    }
                    .addOnFailureListener {
                        // Handle failure
                        Toast.makeText(this@RegisterActivity, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Handle error
                Toast.makeText(this@RegisterActivity, "Registration Failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
// User data model
data class User(
    val name: String = "",
    val age: Int = 0,
    val educationalQualifications: String = "",
    val email: String = "",
    val password: String = "",
    val subjectTaught: String = ""
)