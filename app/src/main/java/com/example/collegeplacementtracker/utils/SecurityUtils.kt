package com.example.collegeplacementtracker.utils

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecurityUtils {

    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATION_COUNT = 10000
    private const val KEY_LENGTH = 256

    /**
     * Hash password using PBKDF2 with salt
     */
    fun hashPassword(password: String): Pair<String, String> {
        val salt = generateSalt()
        val hash = hashWithSalt(password, salt)
        return Pair(hash, salt)
    }

    /**
     * Verify password against stored hash and salt
     */
    fun verifyPassword(password: String, storedHash: String, salt: String): Boolean {
        val hash = hashWithSalt(password, salt)
        return hash == storedHash
    }

    private fun hashWithSalt(password: String, salt: String): String {
        val spec =
            PBEKeySpec(password.toCharArray(), salt.toByteArray(), ITERATION_COUNT, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val hash = factory.generateSecret(spec).encoded
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    /**
     * Simple hash for backward compatibility
     */
    fun simpleHash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
