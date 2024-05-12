package com.example.myproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AttendanceActivity : AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextRollNumber: EditText
    private lateinit var editTextSubject: EditText
    private lateinit var buttonSelectDate: Button
    private lateinit var buttonSelectTime: Button
    private lateinit var toggleButtonAttendance: ToggleButton
    private lateinit var buttonSubmit: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("students")

        editTextName = findViewById(R.id.editTextName) // Initialize editTextName
        editTextRollNumber = findViewById(R.id.editTextRollNumber)
        editTextSubject = findViewById(R.id.editTextSubject)
        buttonSelectDate = findViewById(R.id.buttonSelectDate)
        buttonSelectTime = findViewById(R.id.buttonSelectTime)
        toggleButtonAttendance = findViewById(R.id.toggleButtonAttendance)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        buttonSelectDate.setOnClickListener {
            selectDate()
        }

        buttonSelectTime.setOnClickListener {
            selectTime()
        }

        buttonSubmit.setOnClickListener {
            submitAttendance()
        }
    }

    private fun selectDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                // Handle date selection
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                buttonSelectDate.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun selectTime() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                // Handle time selection
                val selectedTime = "$selectedHour:$selectedMinute"
                buttonSelectTime.text = selectedTime
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun submitAttendance() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val teacherUID = currentUser.uid
            val name = editTextName.text.toString().trim() // Retrieve the name from the user's authentication data
            val rollNumber = editTextRollNumber.text.toString().trim()
            val subject = editTextSubject.text.toString().trim()
            val date = buttonSelectDate.text.toString().trim()
            val time = buttonSelectTime.text.toString().trim()
            val attendance = if (toggleButtonAttendance.isChecked) "Present" else "Absent"

            // Check if any field is empty
            if (name.isNullOrEmpty() || rollNumber.isEmpty() || subject.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return
            }

            // Store attendance in Firebase Realtime Database under the teacher's UID
            val attendanceRef = databaseReference.child(teacherUID).push()
            attendanceRef.child("name").setValue(name)
            attendanceRef.child("roll_number").setValue(rollNumber)
            attendanceRef.child("subject").setValue(subject)
            attendanceRef.child("date").setValue(date)
            attendanceRef.child("time").setValue(time)
            attendanceRef.child("attendance").setValue(attendance)

            Toast.makeText(this, "Attendance recorded successfully", Toast.LENGTH_SHORT).show()

            // Clear all fields
            editTextName.setText("") // Clear name field
            editTextRollNumber.setText("")
            editTextSubject.setText("")
            buttonSelectDate.text = "Select Date"
            buttonSelectTime.text = "Select Time"
            toggleButtonAttendance.isChecked = false
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
