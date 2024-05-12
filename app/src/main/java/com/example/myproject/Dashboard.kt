package com.example.myproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        val profileImageView = findViewById<ImageView>(R.id.imageViewProfile)
        val takeAttendanceImageView = findViewById<ImageView>(R.id.imageViewTakeAttendance)
        val generateReportImageView = findViewById<ImageView>(R.id.imageViewGenerateReport)
        val logoutImageView = findViewById<ImageView>(R.id.imageViewLogout)
        val teacherNoteImageView = findViewById<ImageView>(R.id.imageViewTeacherNote)

        profileImageView.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        takeAttendanceImageView.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        generateReportImageView.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        teacherNoteImageView.setOnClickListener {
            startActivity(Intent(this, TeacherNoteActivity::class.java))
        }

        logoutImageView.setOnClickListener {
            Toast.makeText(this, "Logout Clicked", Toast.LENGTH_SHORT).show()
            finishAffinity()
        }
    }
}
