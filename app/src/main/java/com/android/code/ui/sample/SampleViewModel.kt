package com.android.code.ui.sample

import androidx.lifecycle.LiveData
import com.android.code.App
import com.android.code.R
import com.android.code.models.repository.MarvelRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.livedata.SafetyMutableLiveData
import kotlinx.coroutines.async
import java.security.MessageDigest

class SampleViewModel(private val marvelRepository: MarvelRepository) : BaseViewModel(),
    SampleViewModelInput, SampleViewModelOutput {

    val inputs: SampleViewModelInput = this
    val outputs: SampleViewModelOutput = this

    private val _responseData = SafetyMutableLiveData<List<SampleData>>()
    override val responseData: LiveData<List<SampleData>>
        get() = _responseData

    private val _clickData = SafetyMutableLiveData<SampleData>()
    override val clickData: LiveData<SampleData>
        get() = _clickData

    private val _toggle = SafetyMutableLiveData<SampleData>()
    override val toggle: LiveData<SampleData>
        get() = _toggle

    override fun initData() {
        launchDataLoad(

            onLoad = {
                val timestamp = System.currentTimeMillis()
                val hash = marvelHash(timestamp)

                val taskOne = async {
                    kotlin.runCatching {
                        marvelRepository.characters(
                            apikey = App.instance.getString(R.string.marvel_public_key),
                            timestamp = timestamp, hash = hash
                        )
                    }.getOrNull()?.data?.results?.map { SampleLeftData(it) }
                }

                val taskTwo = async {
                    kotlin.runCatching {
                        marvelRepository.characters(
                            apikey = App.instance.getString(R.string.marvel_public_key),
                            timestamp = timestamp, hash = hash
                        )
                    }.getOrNull()?.data?.results?.map { SampleRightData(it) }
                }

                val list = (taskOne.await() ?: emptyList()) +
                    (taskTwo.await() ?: emptyList())
                _responseData.setValueSafety(list)
            },
            onError = {
                _error.setValueSafety(it)
            }
        )
    }

    private fun marvelHash(timestamp: Long): String {
        val digest = MessageDigest.getInstance("MD5")
        val hashString =
            timestamp.toString() + App.instance.getString(R.string.marvel_private_key) + App.instance.getString(
                R.string.marvel_public_key
            )
        digest.update(hashString.encodeToByteArray())

        return digest.digest().fold("", { acc, byte -> acc + String.format("%02x", byte) })
    }

    override fun clickData(sampleData: SampleData) {
        _clickData.setValueSafety(sampleData)
    }

    override fun switchData(sampleData: SampleData) {
        _toggle.setValueSafety(sampleData)
    }
}

interface SampleViewModelInput {
    fun initData()
    fun clickData(sampleData: SampleData)
    fun switchData(sampleData: SampleData)
}

interface SampleViewModelOutput {
    val responseData: LiveData<List<SampleData>>
    val clickData: LiveData<SampleData>
    val toggle: LiveData<SampleData>
}