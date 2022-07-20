package com.jim.moviecritics.trailer

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jim.moviecritics.R

class TrailerDialog : Fragment() {

    companion object {
        fun newInstance() = TrailerDialog()
    }

    private lateinit var viewModel: TrailerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.dialog_trailer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TrailerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}