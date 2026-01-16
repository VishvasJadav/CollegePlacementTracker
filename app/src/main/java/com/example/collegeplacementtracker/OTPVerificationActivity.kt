package com.example.collegeplacementtracker

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.collegeplacementtracker.utils.OTPUtils

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var otpEditTexts: Array<EditText>
    private lateinit var verifyButton: Button
    private lateinit var resendOtpTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var otpDescriptionTextView: TextView

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var resendCounter = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        initializeViews()
        setupOTPInputs()
        setupListeners()

        // Get the identifier from intent and update description
        val identifier = intent.getStringExtra("identifier") ?: ""
        updateDescription(identifier)

        startResendTimer()
    }

    private fun initializeViews() {
        otpEditTexts = arrayOf(
            findViewById(R.id.otpEditText1),
            findViewById(R.id.otpEditText2),
            findViewById(R.id.otpEditText3),
            findViewById(R.id.otpEditText4),
            findViewById(R.id.otpEditText5),
            findViewById(R.id.otpEditText6)
        )
        verifyButton = findViewById(R.id.verifyButton)
        resendOtpTextView = findViewById(R.id.resendOtpTextView)
        timerTextView = findViewById(R.id.timerTextView)
        otpDescriptionTextView = findViewById(R.id.otpDescriptionTextView)
    }

    private fun setupOTPInputs() {
        for (i in otpEditTexts.indices) {
            val editText = otpEditTexts[i]

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < otpEditTexts.size - 1) {
                        otpEditTexts[i + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: android.text.Editable?) {
                    // Move to next field when 1 digit is entered
                    if (s?.length == 1 && i < otpEditTexts.size - 1) {
                        otpEditTexts[i + 1].requestFocus()
                    }
                }
            })
        }
    }

    private fun setupListeners() {
        verifyButton.setOnClickListener {
            verifyOTP()
        }

        resendOtpTextView.setOnClickListener {
            if (!isTimerRunning) {
                resendOTP()
            }
        }
    }

    private fun updateDescription(identifier: String) {
        val descriptionText = if (identifier.contains("@")) {
            "Enter the 6-digit OTP sent to your email: $identifier"
        } else {
            "Enter the 6-digit OTP sent to your phone: $identifier"
        }
        otpDescriptionTextView.text = descriptionText
    }

    private fun verifyOTP() {
        val otp = getOTPFromInputs()

        if (otp.length != 6) {
            Toast.makeText(this, "Please enter complete 6-digit OTP", Toast.LENGTH_SHORT).show()
            return
        }

        // Verify the OTP using OTPUtils
        val identifier = OTPUtils.getOTPIdentifier(this) ?: ""
        if (OTPUtils.verifyOTP(this, identifier, otp)) {
            // OTP is valid, navigate to reset password screen
            val intent = Intent(this, ResetPasswordActivity::class.java)
            intent.putExtra("identifier", identifier)

            // Check if this is for password change (from ChangePasswordActivity)
            val prefs = getSharedPreferences("temp_password_change", MODE_PRIVATE)
            val tempNewPassword = prefs.getString("new_password", null)

            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getOTPFromInputs(): String {
        return otpEditTexts.joinToString("") { it.text.toString() }
    }

    private fun resendOTP() {
        val identifier = intent.getStringExtra("identifier") ?: ""

        if (identifier.isEmpty()) {
            Toast.makeText(this, "No identifier found", Toast.LENGTH_SHORT).show()
            return
        }

        // Resend OTP using OTPUtils
        if (OTPUtils.sendOTP(this, identifier)) {
            Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            startResendTimer()
        } else {
            Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startResendTimer() {
        // Cancel any existing timer
        countDownTimer?.cancel()

        // Reset timer
        resendCounter = 30
        timerTextView.text = "Resend in ${resendCounter}s"
        resendOtpTextView.isEnabled = false
        isTimerRunning = true

        // Start new countdown timer
        countDownTimer = object : CountDownTimer((resendCounter + 1) * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                resendCounter--
                timerTextView.text = "Resend in ${resendCounter}s"
            }

            override fun onFinish() {
                timerTextView.text = "Resend OTP"
                resendOtpTextView.isEnabled = true
                isTimerRunning = false
            }
        }
        countDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Clear OTP when user goes back to prevent reuse
        OTPUtils.clearOTP(this)
    }
}