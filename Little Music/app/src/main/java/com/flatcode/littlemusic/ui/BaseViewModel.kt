package com.flatcode.littlemusic.ui

import androidx.lifecycle.ViewModel
import timber.log.Timber

open class BaseViewModel : ViewModel() {
    init {
        Timber.d("ViewModel Created: ${this.javaClass.simpleName}")
    }

    override fun onCleared() {
        Timber.d("ViewModel Cleared: ${this.javaClass.simpleName}")
    }
}