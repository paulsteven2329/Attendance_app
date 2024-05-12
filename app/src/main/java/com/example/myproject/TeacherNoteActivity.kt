package com.example.myproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TeacherNoteActivity : AppCompatActivity() {

    private lateinit var editTextNote: EditText
    private lateinit var buttonSaveNote: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_note)

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Initialize views
        editTextNote = findViewById(R.id.editTextNote)
        buttonSaveNote = findViewById(R.id.buttonSaveNote)

        // Set OnClickListener for save note button
        buttonSaveNote.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val userId = firebaseAuth.currentUser?.uid
        val noteText = editTextNote.text.toString().trim()

        if (userId != null && noteText.isNotEmpty()) {
            // Save note to Firebase Realtime Database under the teacher's ID
            val noteRef = databaseReference.child("teacher_notes").child(userId).push()
            noteRef.setValue(noteText)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after saving
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show()
        }
    }
}
