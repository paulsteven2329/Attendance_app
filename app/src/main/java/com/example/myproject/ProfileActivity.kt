package com.example.myproject

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    // Views
    private lateinit var textViewName: TextView
    private lateinit var textViewAge: TextView
    private lateinit var textViewEducationalQualifications: TextView
    private lateinit var textViewSubjectTaught: TextView
    private lateinit var buttonLogout: Button

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("teachers")

        // Initialize views
        textViewName = findViewById(R.id.textViewName)
        textViewAge = findViewById(R.id.textViewAge)
        textViewEducationalQualifications = findViewById(R.id.textViewQualification)
        textViewSubjectTaught = findViewById(R.id.textViewSubject)
        buttonLogout = findViewById(R.id.buttonLogout)

        // Get current user's email
        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email

        // Check if email is not null or empty
        if (!email.isNullOrEmpty()) {
            loadUserProfile(email)
        } else {
            // Show a toast message and finish the activity if email is not found
            Toast.makeText(this, "Email not found for current user", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Set OnClickListener for logout button
        buttonLogout.setOnClickListener {
            // Logout user
            firebaseAuth.signOut()
            // Redirect to LoginActivity
            finish()
        }
    }

    private fun loadUserProfile(email: String) {
        // Query the database to get the user data based on email
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (snapshot in dataSnapshot.children) {
                            // Get user data from snapshot
                            val name = snapshot.child("name").getValue(String::class.java) ?: ""
                            val age = snapshot.child("age").getValue(Long::class.java)?.toString() ?: ""
                            val educationalQualifications = snapshot.child("educationalQualifications").getValue(String::class.java) ?: ""
                            val subjectTaught = snapshot.child("subjectTaught").getValue(String::class.java) ?: ""

                            // Update TextViews with user's profile data
                            textViewName.text = "Name: $name"
                            textViewAge.text = "Age: $age"
                            textViewEducationalQualifications.text = "Educational Qualifications: $educationalQualifications"
                            textViewSubjectTaught.text = "Subject Taught: $subjectTaught"
                        }
                    } else {
                        // Show a toast message if user data is not found
                        Toast.makeText(this@ProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@ProfileActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
