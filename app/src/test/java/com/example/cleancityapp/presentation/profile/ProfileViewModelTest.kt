package com.example.cleancityapp.presentation.profile

import android.content.SharedPreferences
import com.example.cleancityapp.data.remote.AccountDto
import com.example.cleancityapp.data.remote.AuthApi
import com.example.cleancityapp.data.remote.MeResponse
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.data.repository.DeviceRegistrationRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val authApi = mockk<AuthApi>()
    private val deviceRepository = mockk<DeviceRegistrationRepository>(relaxed = true)
    private val prefs = mockk<SharedPreferences>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { prefs.getString("access_token", null) } returns "token"
        every { prefs.edit() } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadUser_success_usesApiProfileOnly() = runTest(dispatcher) {
        coEvery { authApi.getMe("Bearer token") } returns Response.success(meResponse(userProfile = sampleUser()))

        val vm = viewModel()
        advanceUntilIdle()

        assertEquals("Ada", vm.state.value.user?.name)
        assertEquals("ada@city.test", vm.state.value.user?.email)
        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun loadUser_successWithNoProfiles_setsErrorAndClearsUser() = runTest(dispatcher) {
        coEvery { authApi.getMe("Bearer token") } returns Response.success(meResponse(userProfile = null))

        val vm = viewModel()
        advanceUntilIdle()

        assertNull(vm.state.value.user)
        assertFalse(vm.state.value.isLoading)
        assertEquals("Unable to load your profile.", vm.state.value.error)
    }

    @Test
    fun loadUser_httpError_setsApiErrorAndClearsUser() = runTest(dispatcher) {
        coEvery { authApi.getMe("Bearer token") } returns errorResponse(401, """{"error":"Unauthorized","message":"Please sign in again."}""")

        val vm = viewModel()
        advanceUntilIdle()

        assertNull(vm.state.value.user)
        assertFalse(vm.state.value.isLoading)
        assertEquals("Unauthorized\nPlease sign in again.", vm.state.value.error)
    }

    @Test
    fun loadUser_missingToken_setsErrorWithoutLoadingData() = runTest(dispatcher) {
        every { prefs.getString("access_token", null) } returns null

        val vm = viewModel()
        advanceUntilIdle()

        assertNull(vm.state.value.user)
        assertFalse(vm.state.value.isLoading)
        assertEquals("Unable to load your profile.", vm.state.value.error)
    }

    @Test
    fun updateProfile_success_usesResponseBody() = runTest(dispatcher) {
        coEvery { authApi.getMe("Bearer token") } returns Response.success(meResponse(userProfile = sampleUser()))
        coEvery { authApi.updateProfile("Bearer token", any()) } returns Response.success(sampleUser(name = "Ada Lovelace"))

        val vm = viewModel()
        advanceUntilIdle()

        vm.updateProfile("Ada Lovelace", "999", "Street 1")
        advanceUntilIdle()

        assertEquals("Ada Lovelace", vm.state.value.user?.name)
        assertTrue(vm.state.value.isUpdateSuccess)
        assertFalse(vm.state.value.isSaving)
        assertNull(vm.state.value.error)
    }

    @Test
    fun updateProfile_httpError_doesNotApplyLocalEdits() = runTest(dispatcher) {
        coEvery { authApi.getMe("Bearer token") } returns Response.success(meResponse(userProfile = sampleUser()))
        coEvery { authApi.updateProfile("Bearer token", any()) } returns errorResponse(
            404,
            """{"error":"Not Found","message":"Profile update is unavailable."}"""
        )

        val vm = viewModel()
        advanceUntilIdle()

        vm.updateProfile("Local Name", "000", "Fake address")
        advanceUntilIdle()

        assertEquals("Ada", vm.state.value.user?.name)
        assertFalse(vm.state.value.isUpdateSuccess)
        assertFalse(vm.state.value.isSaving)
        assertEquals("Not Found\nProfile update is unavailable.", vm.state.value.error)
    }

    private fun viewModel() = ProfileViewModel(
        authApi = authApi,
        deviceRepository = deviceRepository,
        sharedPreferences = prefs,
        ioDispatcher = dispatcher,
    )

    private fun sampleUser(name: String = "Ada") = UserDto(
        id = "u1",
        name = name,
        email = "ada@city.test",
        phone = "123",
        roles = listOf("USER"),
        role = "USER",
        rewardPoints = 12,
        isVerified = true,
        createdAt = "2026-01-01",
        updatedAt = "2026-01-02",
        reportsFiled = 3,
        reportsResolved = 1,
        address = "Ward 4",
        profileImage = null,
    )

    private fun meResponse(userProfile: UserDto?) = MeResponse(
        account = AccountDto(id = "a1", email = "ada@city.test", phone = "123", status = "ACTIVE"),
        roles = listOf("USER"),
        userProfile = userProfile,
        driverProfile = null,
    )

    private fun <T> errorResponse(code: Int, body: String): Response<T> {
        return Response.error(code, body.toResponseBody("application/json".toMediaType()))
    }
}
