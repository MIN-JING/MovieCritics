package com.jim.moviecritics.detail



import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.databinding.FragmentDetailBinding
import com.jim.moviecritics.util.Logger

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> { getVmFactory(DetailFragmentArgs.fromBundle(requireArguments()).movie) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        Logger.i("Detail Fragment onCreateView()")
        val binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.recyclerviewDetailCast.adapter = CastAdapter(
            CastAdapter.OnClickListener {
                Logger.i("CastAdapter.OnClickListener it = $it")
            }
        )

        viewModel.movie.observe(viewLifecycleOwner, Observer {
            Logger.i("DetailViewModel.movie = $it")
        })

        viewModel.liveScore.observe(viewLifecycleOwner, Observer {
            Logger.i("DetailViewModel.liveScore = $it")
            if (it != null) {
                val radarData = viewModel.movie.value?.let { it1 ->
                    viewModel.setRadarData(
                        averageLeisure = it1.voteAverage,
                        averageHit = it1.voteAverage,
                        averageCast = it1.voteAverage,
                        averageMusic = it1.voteAverage,
                        averageStory = it1.voteAverage,
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
                val radarData = viewModel.movie.value?.let { it1 ->
                    viewModel.setRadarData(
                        averageLeisure = it1.voteAverage,
                        averageHit = it1.voteAverage,
                        averageCast = it1.voteAverage,
                        averageMusic = it1.voteAverage,
                        averageStory = it1.voteAverage,
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
        })


        viewModel.navigateToPending.observe(viewLifecycleOwner, Observer {
            Logger.i("DetailViewModel.navigateToPending = $it")
            Logger.i("DetailViewModel.navigateToPending runTime = ${it?.runtime}")
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToPendingDialog(it))
                viewModel.onPendingNavigated()
            }
        })

        viewModel.leaveDetail.observe(viewLifecycleOwner, Observer {
            Logger.i(" DetailViewModel.leaveDetail = $it")
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })

        return binding.root
    }
}