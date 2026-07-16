package com.example.scholarix

import com.example.scholarix.repository.UserRepo
import com.example.scholarix.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UserViewModelTest {
    @Test
    fun login_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login success")
            null
        }.`when`(repo).login(eq("test@gmail.com"), eq("123456"), any())

        var successResult = false
        var messageResult = ""

        viewModel.login("test@gmail.com", "123456") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Login success", messageResult)
        verify(repo).login(eq("test@gmail.com"), eq("123456"), any())
    }

    @Test
    fun register_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Registration successful", "uid123")
            null
        }.`when`(repo).register(eq("new@gmail.com"), eq("123456"), any())

        var successResult = false
        var messageResult = ""
        var uidResult = ""

        viewModel.register("new@gmail.com", "123456") { success, msg, uid ->
            successResult = success
            messageResult = msg
            uidResult = uid
        }

        assertTrue(successResult)
        assertEquals("Registration successful", messageResult)
        assertEquals("uid123", uidResult)
        verify(repo).register(eq("new@gmail.com"), eq("123456"), any())
    }
}