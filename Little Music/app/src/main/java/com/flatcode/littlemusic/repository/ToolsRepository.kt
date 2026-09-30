package com.flatcode.littlemusic.repository

import com.flatcode.littlemusic.db.SettingDao
import com.flatcode.littlemusic.db.SliderDao
import com.flatcode.littlemusic.model.SettingEntity
import com.flatcode.littlemusic.model.SliderEntity
import com.flatcode.littlemusic.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolsRepository @Inject constructor(
    private val sliderDao: SliderDao,
    private val settingDao: SettingDao
) {

    private val database = FirebaseDatabase.getInstance()

    fun getPrivacyPolicy(): Flow<String> = callbackFlow {
        val repositoryScope = CoroutineScope(Dispatchers.IO)
        val localJob = repositoryScope.launch {
            settingDao.getSetting(DATA.PRIVACY_POLICY).collect { cached ->
                if (!cached.isNullOrEmpty()) {
                    trySend(cached)
                }
            }
        }

        val reference = database.getReference(DATA.TOOLS).child(DATA.PRIVACY_POLICY)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value?.toString().orEmpty()
                if (value.isNotEmpty()) {
                    trySend(value)
                    repositoryScope.launch {
                        settingDao.insertSetting(SettingEntity(DATA.PRIVACY_POLICY, value))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "getPrivacyPolicy failed")
            }
        }
        reference.addValueEventListener(listener)
        awaitClose {
            reference.removeEventListener(listener)
            localJob.cancel()
        }
    }

    fun getSliderImages(): Flow<List<String>> {
        syncSliderImages()
        return sliderDao.getSliderImages().map { list ->
            list.map { it.image }
        }
    }

    private fun syncSliderImages() {
        database.getReference(DATA.SLIDER_SHOW)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sliderList = mutableListOf<SliderEntity>()
                    var index = 0
                    for (child in snapshot.children) {
                        val id = child.key ?: index.toString()
                        val url = child.child(DATA.IMAGE).value?.toString()
                            ?: (child.value as? String)
                        if (!url.isNullOrEmpty() && url != "null") {
                            sliderList.add(SliderEntity(id, url, index++))
                        }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        sliderDao.deleteAllSliderImages()
                        sliderDao.insertSliderImages(sliderList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncSliderImages failed")
                }
            })
    }
}
