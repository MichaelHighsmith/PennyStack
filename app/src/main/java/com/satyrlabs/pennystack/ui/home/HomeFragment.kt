package com.satyrlabs.pennystack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.robinhood.ticker.TickerUtils
import com.satyrlabs.pennystack.R
import com.satyrlabs.pennystack.databinding.FragmentHomeBinding
import com.satyrlabs.pennystack.ui.notifications.NotificationsViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeBinding: FragmentHomeBinding

    private val disposables = CompositeDisposable()
    private var earningDisposable: Disposable? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        homeBinding = FragmentHomeBinding.inflate(layoutInflater)
        val view = homeBinding.root

        homeBinding.circleView.setOnClickListener { startCounting() }
        return view
    }

    override fun onStart() {
        super.onStart()
        tickerView.setCharacterLists(TickerUtils.provideNumberList());
        addPenny(0)
    }

    private fun startCounting() {
        earningDisposable?.dispose()

        if (homeBinding.hourlyWageEditText.text.isEmpty()) {
            Toast.makeText(context, "Please enter an hourly wage", Toast.LENGTH_SHORT).show()
            return
        }
        val hourlyWageString = homeBinding.hourlyWageEditText.text.toString()
        val hourlyWage = hourlyWageString.toFloat()
        val penniesPerHour = hourlyWage * 100
        val penniesPerMinute = penniesPerHour / 60
        val penniesPerSecond = penniesPerMinute / 60
        val secondsPerPenny = 60 / penniesPerMinute
        val millisecondsPerPenny = (secondsPerPenny * 1000).toLong()

        earningDisposable = Observable.interval(0, millisecondsPerPenny, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::addPenny) { error ->
                print(error)
            }
    }

    private fun addPenny(newNumber: Long) {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        val newString = numberFormat.format(newNumber / 100.0)
        homeBinding.tickerView.text = newString
    }



}