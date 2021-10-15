package com.satyrlabs.pennystack.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HomeViewModel : ViewModel() {

    private val amountEarned = MutableLiveData<String>().apply {
        value = "$0.00"
    }
    val currentAmountEarned: LiveData<String> = amountEarned

    private var earningDisposable: Disposable? = null

    fun startCounting(millisecondsPerPenny: Long) {
        earningDisposable?.dispose()

        earningDisposable = Observable.interval(0, millisecondsPerPenny, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updateValue) { error ->
                print(error)
            }
    }

    fun updateValue(newValue: Long) {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        val newString = numberFormat.format(newValue / 100.0)
        amountEarned.postValue(newString)

    }
}