package com.jim.moviecritics.pending


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jim.moviecritics.databinding.FragmentPendingBinding
import com.jim.moviecritics.ext.getVmFactory

class PendingFragment : Fragment() {

    private val viewModel by viewModels<PendingViewModel> { getVmFactory() }

//    companion object {
//        fun newInstance() = PendingFragment()
//    }
//
//    private lateinit var viewModel: PendingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentPendingBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
//        return inflater.inflate(R.layout.fragment_pending, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(PendingViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}