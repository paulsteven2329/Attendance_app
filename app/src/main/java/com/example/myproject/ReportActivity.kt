package com.example.myproject

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ReportActivity : AppCompatActivity() {

    private lateinit var studentListLayout: LinearLayout
    private lateinit var buttonDownloadReport: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("students")

        // Initialize views
        studentListLayout = findViewById(R.id.studentListLayout)

        // Load students' data
        loadStudentsData()

        // Set OnClickListener for download report button

    }

    private fun loadStudentsData() {
        // Clear previous data
        studentListLayout.removeAllViews()

        // Retrieve students' data from Firebase Realtime Database
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val teacherUID = user.uid
            val teacherReference = databaseReference.child(teacherUID)
            teacherReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (studentSnapshot in dataSnapshot.children) {
                        val name = studentSnapshot.child("name").value.toString()
                        val rollNumber = studentSnapshot.child("roll_number").value.toString()
                        val subject = studentSnapshot.child("subject").value.toString()
                        val date = studentSnapshot.child("date").value.toString()
                        val time = studentSnapshot.child("time").value.toString()
                        val attendance = studentSnapshot.child("attendance").value.toString()

                        // Create TextView to display student details
                        val textViewStudent = TextView(this@ReportActivity)
                        textViewStudent.text = "Name: $name\nRoll Number: $rollNumber\nSubject: $subject\nDate: $date\nTime: $time\nAttendance: $attendance\n\n"
                        textViewStudent.setPadding(20, 20, 20, 20)

                        // Create delete button for each student
                        val deleteButton = Button(this@ReportActivity)
                        deleteButton.text = "Delete"
                        deleteButton.setOnClickListener {
                            showConfirmationDialog(studentSnapshot)
                        }

                        // Add TextView and delete button to LinearLayout
                        val linearLayout = LinearLayout(this@ReportActivity)
                        linearLayout.orientation = LinearLayout.VERTICAL
                        linearLayout.addView(textViewStudent)
                        linearLayout.addView(deleteButton)

                        // Add LinearLayout to parent layout
                        studentListLayout.addView(linearLayout)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@ReportActivity, "Failed to load data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showConfirmationDialog(studentSnapshot: DataSnapshot) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this student record?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Perform deletion from the Realtime Database
            studentSnapshot.ref.removeValue()
            // Reload the student list after deletion
            loadStudentsData()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }




}
