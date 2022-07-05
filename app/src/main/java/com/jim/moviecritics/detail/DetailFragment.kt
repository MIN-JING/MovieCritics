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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (viewModel.user.value == null) {
//            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
//            mainViewModel.user.value?.let { viewModel.takeDownUser(it) }
//            Logger.i("Detail mainViewModel.user.value = ${mainViewModel.user.value}")
//            Logger.i("Detail viewModel.user.value = ${viewModel.user.value}")
//        }
//
//
//        viewModel.movie.value?.imdbID?.let {
//            viewModel.user.value?.id?.let { userId ->
//                viewModel.getLiveScoreResult(imdbID = it, userID = userId)
//            }
//            viewModel.getLiveCommentsResult(imdbID = it)
//        }
    }

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

        binding.recyclerviewDetailReview.adapter = ReviewAdapter(
            ReviewAdapter.OnClickListener {
                Logger.i("ReviewAdapter.OnClickListener it = $it")
            }
        )

        viewModel.movie.observe(viewLifecycleOwner) {
            Logger.i("DetailViewModel.movie = $it")
        }

        viewModel.mutableScore.observe(viewLifecycleOwner) {
            Logger.i("DetailViewModel.liveScore = $it")
            if (it != null) {
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

        viewModel.liveComments.observe(viewLifecycleOwner) {
            Logger.i("DetailViewModel.liveComments = $it")
            it?.let {
                binding.viewModel = viewModel
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

        viewModel.leave.observe(viewLifecycleOwner) {
            Logger.i(" DetailViewModel.leaveDetail = $it")
            it?.let {
                if (it) findNavController().popBackStack()
            }
        }
        return binding.root
    }
}