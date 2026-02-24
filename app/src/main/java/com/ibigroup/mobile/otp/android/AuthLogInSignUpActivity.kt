/*
 * DemoActivity.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ibigroup.mobile.otp.android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.lock.AuthenticationCallback
import com.auth0.android.lock.InitialScreen
import com.auth0.android.lock.Lock
import com.auth0.android.lock.LockCallback
import com.auth0.android.lock.UsernameStyle
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar
import com.ibigroup.mobile.otp.android.utility.Configurations

class AuthLogInSignUpActivity : AppCompatActivity() {
    // Configured instances
    private var lock: Lock? = null
    private lateinit var rootLayout: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth0_log_in_sign_up)
        rootLayout = findViewById(R.id.rl_auth)

        showClassicLock()
    }

    private fun showClassicLock() {
        val builder = Lock.newBuilder(account, callback)
            .withScheme("https")
            .closable(true)
            .useLabeledSubmitButton(true)
            .loginAfterSignUp(true)
            .allowLogIn(true)
            .allowSignUp(true)
            .allowForgotPassword(true)
            .allowedConnections(generateConnections())
            .hideMainScreenTitle(false)
            .withUsernameStyle(UsernameStyle.EMAIL)
            .initialScreen(InitialScreen.LOG_IN)
            .setDefaultDatabaseConnection("Username-Password-Authentication")

        lock?.onDestroy(this)
        // Create a new instance with the updated configuration
        lock = builder.build(this)
        startActivity(lock!!.newIntent(this))
    }

    private val account: Auth0 by lazy {
        Auth0(Configurations.getString("auth0_client_id", ""), Configurations.getString("auth0_domain", ""))
    }

    public override fun onDestroy() {
        super.onDestroy()
        lock?.onDestroy(this)
    }

    internal fun showResult(message: String) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show()
    }

    private val callback: LockCallback = object : AuthenticationCallback() {
        override fun onAuthentication(credentials: Credentials) {
            showResult("OK > " + credentials.accessToken)
        }

        override fun onError(error: AuthenticationException) {
            if (error.isCanceled) {
                showResult("User pressed back.")
            } else {
                showResult(error.getDescription())
            }
        }
    }

    private fun generateConnections(): List<String> {
        val connections: MutableList<String> = ArrayList()
        connections.add("Username-Password-Authentication")
        connections.add("email")
        if (connections.isEmpty()) {
            connections.add("no-connection")
        }
        return connections
    }
}