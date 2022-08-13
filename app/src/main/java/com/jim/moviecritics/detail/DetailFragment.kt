package com.jim.moviecritics.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.R
import com.jim.moviecritics.databinding.FragmentDetailBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger

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

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.recyclerviewDetailCast.adapter = CastAdapter(
            CastAdapter.OnClickListener {
                Logger.i("CastAdapter.OnClickListener it = $it")
            }
        )

        val reviewAdapter = ReviewAdapter(
            ReviewAdapter.OnClickListener {
                Logger.i("ReviewAdapter.OnClickListener it = $it")
            },
            viewModel
        )

        binding.recyclerviewDetailReview.adapter = reviewAdapter

        showRadarChart(
            binding.radarChartRating,
            setRatings(viewModel.averageRatings, viewModel.userRatings)
        )

        viewModel.liveScore.observe(viewLifecycleOwner) { score ->
            Logger.i("DetailViewModel.liveScore = $score")
            score?.let {
                viewModel.setRadarEntry(it)
                showRadarChart(
                    binding.radarChartRating,
                    setRatings(viewModel.averageRatings, viewModel.userRatings)
                )
            }
        }

        viewModel.liveComments.observe(viewLifecycleOwner) { comments ->
            comments?.let { commentList ->
                val list = mutableListOf<String>()
                commentList.forEach { comment ->
                    list.add(comment.userID)
                }
                val distinctUser = list.distinct()
                Logger.i("DetailViewModel.liveComment userIDs = $distinctUser")
                viewModel.getUsersResult(distinctUser)
                viewModel.isUsersMapReady.observe(viewLifecycleOwner) { boolean ->
                    Logger.i("DetailViewModel.isUsersMapReady = $boolean")
                    reviewAdapter.submitList(comments)
                }
            }
        }

        viewModel.navigateToPending.observe(viewLifecycleOwner) {
            it?.let {
                when (viewModel.isLoggedIn) {
                    true -> {
                        viewModel.checkUser()
                        findNavController()
                            .navigate(NavigationDirections.navigateToPendingDialog(it))
                        viewModel.onPendingNavigated()
                    }
                    false -> {
                        viewModel.navigateToLogin()
                    }
                }
            }
        }

        viewModel.navigateToReport.observe(viewLifecycleOwner) {
            it?.let {
                when (viewModel.isLoggedIn) {
                    true -> {
                        viewModel.checkUser()
                        findNavController()
                            .navigate(NavigationDirections.navigationToReportDialog(it))
                        viewModel.onReportNavigated()
                    }
                    false -> {
                        viewModel.navigateToLogin()
                    }
                }
            }
        }

        viewModel.navigateToUserInfo.observe(viewLifecycleOwner) {
            it?.let {
                when (viewModel.isLoggedIn) {
                    true -> {
                        viewModel.checkUser()
                        findNavController()
                            .navigate(NavigationDirections.navigationToFollowDialog(it))
                        viewModel.onUserInfoNavigated()
                    }
                    false -> {
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

    private fun setRatings(
        averageRatings: ArrayList<RadarEntry>,
        userRatings: ArrayList<RadarEntry>
    ): RadarData {
        val averageRatingsSet = RadarDataSet(
            averageRatings, "TMDB ratings with linear transformation"
        )
        averageRatingsSet.lineWidth = 2F
        averageRatingsSet.isDrawHighlightCircleEnabled = true
        averageRatingsSet.setDrawHighlightIndicators(false)

        val userRatingsSet = RadarDataSet(userRatings, "Ratings by you")
        userRatingsSet.color = R.color.secondary
        userRatingsSet.lineWidth = 2F
        userRatingsSet.isDrawHighlightCircleEnabled = true
        userRatingsSet.setDrawHighlightIndicators(false)

        val totalRatingsSet = ArrayList<IRadarDataSet>()
        totalRatingsSet.add(averageRatingsSet)
        totalRatingsSet.add(userRatingsSet)

        val radarData = RadarData(totalRatingsSet)
        radarData.setValueTextSize(10F)
        return radarData
    }

    private fun showRadarChart(radarChart: RadarChart, radarData: RadarData) {
        radarChart.description.isEnabled = false
        radarChart.isRotationEnabled = true
        val labels: Array<String> = arrayOf("Leisure", "Hit", "Cast", "Music", "Story")
        radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        radarChart.xAxis.textSize = 15F
        radarChart.yAxis.axisMinimum = 0F
        radarChart.yAxis.axisMaximum = 5F
        radarChart.yAxis.setLabelCount(5, true)
        // NOT show yAxis label
        radarChart.yAxis.setDrawLabels(false)
        radarChart.scaleX = 1.05F
        radarChart.scaleY = 1.05F
        radarChart.data = radarData
        radarChart.invalidate()
    }
}