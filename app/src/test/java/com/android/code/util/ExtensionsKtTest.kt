package com.android.code.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ExtensionsKtTest {
    data class TestData(
        val a: String = "a",
        val b: Int = 1,
        val c: List<Double> = listOf(1.0, 2.0)
    )

    private val testDataJson = "{\"a\":\"a\",\"b\":1,\"c\":[1.0,2.0]}"

    @Test
    fun fromJson() {
        assertEquals(testDataJson.fromJson() as TestData, TestData())
    }

    @Test
    fun fromJsonWithTypeToken() {
        assertEquals(testDataJson.fromJsonWithTypeToken() as TestData, TestData())
    }

    @Test
    fun toJson() {
        assertEquals(TestData().toJson(), testDataJson)
    }

    @Test
    fun empty() {
        assertEquals(String.empty(), "")
    }

    @Test
    fun space() {
        assertEquals(String.space(), " ")
    }

    @Test
    fun zero() {
        assertEquals(Int.zero(), 0)
        assertEquals(Long.zero(), 0L)
    }
}