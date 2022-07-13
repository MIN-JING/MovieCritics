package com.jim.moviecritics.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.MovieApplication
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.databinding.FragmentDetailBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> { getVmFactory(DetailFragmentArgs.fromBundle(requireArguments()).movie) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.isLiveDataDesign = MovieApplication.instance.isLiveDataDesign()
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

//        binding.recyclerviewDetailReview.adapter = ReviewAdapter(
//            ReviewAdapter.OnClickListener {
//                Logger.i("ReviewAdapter.OnClickListener it = $it")
//            },
//            viewModel
//        )

        viewModel.movie.observe(viewLifecycleOwner) {
            Logger.i("DetailViewModel.movie = $it")
        }

        viewModel.liveScore.observe(viewLifecycleOwner) {
            Logger.i("DetailViewModel.liveScore = $it")
            if (it != null) {
                Logger.i("viewModel.liveScore != null")
                val radarData = viewModel.movie.value?.let { movie ->
                    viewModel.setRadarData(
                        averageLeisure = movie.voteAverage,
                        averageHit = movie.voteAverage,
                        averageCast = movie.voteAverage,
                        averageMusic = movie.voteAverage,
                        averageStory = movie.voteAverage,
                        userLeisure = it.leisure,
                        userHit = it.hit,
                        userCast = it.cast,
                        userMusic = it.music,
                        userStory = it.story
                    )
                }
                if (radarData != null) {
                    viewModel.showRadarChart(binding.radarChartRating, radarData)
                }

            } else {
                Logger.i("viewModel.liveScore == null")
                val radarData = viewModel.movie.value?.let { movie ->
                    viewModel.setRadarData(
                        averageLeisure = movie.voteAverage,
                        averageHit = movie.voteAverage,
                        averageCast = movie.voteAverage,
                        averageMusic = movie.voteAverage,
                        averageStory = movie.voteAverage,
                        userLeisure = 0F,
                        userHit = 0F,
                        userCast = 0F,
                        userMusic = 0F,
                        userStory = 0F
                    )
                }
                if (radarData != null) {
                    viewModel.showRadarChart(binding.radarChartRating, radarData)
                }
            }
        }

        viewModel.liveComments.observe(viewLifecycleOwner) { comments ->
            Logger.i("DetailViewModel.liveComments = $comments")
            comments?.let {
                val list = mutableListOf<String>()
                for (value in it) {
                    list.add(value.userID)
                }
                Logger.i("DetailViewModel.liveComment userID = $list")
                viewModel.getUsersResult(false, list)

                viewModel.isUsersMapReady.observe(viewLifecycleOwner) { boolean ->
                    Logger.i("DetailViewModel.isUsersMapReady = $boolean")
                    reviewAdapter.submitList(comments)
                }
            }
        }


        viewModel.navigateToPending.observe(viewLifecycleOwner) {
            Logger.i("DetailViewModel.navigateToPending = $it")
            Logger.i("DetailViewModel.navigateToPending runTime = ${it?.runtime}")
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToPendingDialog(it))
                viewModel.onPendingNavigated()
            }
        }

        viewModel.navigateToReport.observe(viewLifecycleOwner) {
            Logger.i("ReportViewModel.navigateToReport = $it")
            it?.let {
                findNavController().navigate(NavigationDirections.navigationToReportDialog(it))
                viewModel.onReportNavigated()
            }
        }

        viewModel.leave.observe(viewLifecycleOwner) {
            Logger.i(" DetailViewModel.leaveDetail = $it")
            it?.let {
                if (it) findNavController().popBackStack()
            }
        }
        return binding.root
    }
}