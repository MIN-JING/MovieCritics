package com.jim.moviecritics.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.databinding.FragmentDetailBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.ui.theme.MovieCriticsTheme

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> {
        getVmFactory(DetailFragmentArgs.fromBundle(requireArguments()).movie)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentDetailBinding.inflate(inflater, container, false)
        // The toolbar and bottom navigation, which normally take the system bar insets, are
        // hidden on this page. DetailScreen's Scaffold keeps its content clear of the bars.
        binding.composeViewDetail.setContent {
            MovieCriticsTheme {
                DetailScreen(viewModel = viewModel)
            }
        }

        viewModel.navigateToPending.observe(viewLifecycleOwner) {
            it?.let {
                when (viewModel.isLoggedIn) {
                    true -> {
                        findNavController()
                            .navigate(NavigationDirections.navigateToPendingDialog(it))
                        viewModel.onPendingNavigated()
                    }
                    false -> {
                        viewModel.onPendingNavigated()
                        viewModel.navigateToLogin()
                    }
                }
            }
        }

        viewModel.navigateToReport.observe(viewLifecycleOwner) {
            it?.let {
                when (viewModel.isLoggedIn) {
                    true -> {
                        findNavController()
                            .navigate(NavigationDirections.navigationToReportDialog(it))
                        viewModel.onReportNavigated()
                    }
                    false -> {
                        viewModel.onReportNavigated()
                        viewModel.navigateToLogin()
                    }
                }
            }
        }

        viewModel.navigateToUserInfo.observe(viewLifecycleOwner) {
            it?.let {
                when (viewModel.isLoggedIn) {
                    true -> {
                        findNavController()
                            .navigate(NavigationDirections.navigationToFollowDialog(it))
                        viewModel.onUserInfoNavigated()
                    }
                    false -> {
                        viewModel.onUserInfoNavigated()
                        viewModel.navigateToLogin()
                    }
                }
            }
        }

        viewModel.navigateToTrailer.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(NavigationDirections.navigationToTrailerDialog(it))
                viewModel.onTrailerNavigated()
            }
        }

        viewModel.navigateToLogin.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(NavigationDirections.navigationToLoginDialog())
                viewModel.onLoginNavigated()
            }
        }

        viewModel.leave.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        }
        return binding.root
    }
}
