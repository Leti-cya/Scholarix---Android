package com.example.scholarix

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.scholarix.ui.screens.auth.ChooseAccountActivity
import com.example.scholarix.ui.screens.auth.ForgotPasswordActivity
import com.example.scholarix.ui.screens.auth.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testCreateAccountLink_navigatesToChooseAccount() {
        composeRule.onNodeWithTag("register")
            .performClick()
        Intents.intended(hasComponent(ChooseAccountActivity::class.java.name))
    }

    @Test
    fun testForgotPasswordLink_navigatesToForgotPassword() {
        composeRule.onNodeWithTag("forgotPassword")
            .performClick()
        Intents.intended(hasComponent(ForgotPasswordActivity::class.java.name))
    }
}