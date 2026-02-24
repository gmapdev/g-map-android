package com.ibigroup.mobile.otp.android.manager

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SecurityManager {

    private fun SecKey(): ByteArray {
        return shift("93bcead916b779be26d69fb61a33d9a7f51c027805f46bda71a94bcf00000000")
    }

    private fun SecIV(): ByteArray{
        return shift("47ae599c354da66c2fa14abfc87b126a")
    }

    companion object  {
        var shared = SecurityManager()

        @JvmStatic
        public fun decrypt(encryptedContent: String) : String? {
            return shared.decryptData(encryptedContent)
        }

        @JvmStatic
        public fun encrypt(originalContent: String) : String? {
            return shared.encryptData(originalContent)
        }
    }

    private fun hexStringToByteArray(s: String): ByteArray? {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private fun byteArrayToHexString(bytes: ByteArray): String? {
        var retHexString = ""
        for (b in bytes) {
            retHexString += String.format("%02X", b)
        }
        return retHexString
    }

    private fun shift(value: String): ByteArray {
        return value.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

    private fun process(data: ByteArray?, isEncryption:Boolean) : ByteArray? {
        data?.let { data ->
            val preParameter = SecretKeySpec(SecKey(), "AES")
            val nxtParameter = IvParameterSpec(SecIV())

            val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
            cipher.init(if (isEncryption) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, preParameter, nxtParameter)
            return cipher.doFinal(data)
        }
        return null
    }

    public fun decryptData(encryptedContent: String) : String? {
        if(encryptedContent.trim().isEmpty()) {
            return ""
        }

        val base64Byte = hexStringToByteArray(encryptedContent)

        shared.process(base64Byte, false)?.let{ decrypted ->
            val base64 = String(decrypted)
            Base64.decode(base64)?.let {
                return it
            }
        }
        return null
    }

    public fun encryptData(originalContent: String) : String? {
        if(originalContent.trim().isEmpty()) {
            return ""
        }

        val base64 = Base64.encode(originalContent)
        val base64ByteArray = base64.toByteArray(Charsets.UTF_8)

        shared.process(base64ByteArray, true)?.let { encrypted ->
            byteArrayToHexString(encrypted)?.let {
                return it
            }
            return null
        }
        return null
    }

}