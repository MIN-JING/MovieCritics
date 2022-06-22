package com.jim.moviecritics.detail



import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.R
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.databinding.FragmentDetailBinding
import com.jim.moviecritics.util.Logger

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> { getVmFactory(DetailFragmentArgs.fromBundle(requireArguments()).movie) }

//    companion object {
//        fun newInstance() = DetailFragment()
//    }
//
//    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.recyclerviewCast.adapter = CastAdapter(
            CastAdapter.OnClickListener {
                Logger.i("CastAdapter.OnClickListener it = $it")
            }
        )


        viewModel.movie.observe(viewLifecycleOwner, Observer {
            Logger.i("DetailViewModel.movie = $it")
            Logger.i("DetailViewModel.movie casts = ${it.casts}")
        })

        viewModel.scores.observe(viewLifecycleOwner, Observer {
            Logger.i("DetailViewModel.scores = $it")
            Logger.i("DetailViewModel.scores it.first() = ${it.first()}")
            val scoresFirst = it.first()

            Logger.i("DetailViewModel.scores it.first() = ${scoresFirst.leisure}")
            Logger.i("DetailViewModel.scores it.first() = ${scoresFirst.hit}")
            Logger.i("DetailViewModel.scores it.first() = ${scoresFirst.cast}")
            Logger.i("DetailViewModel.scores it.first() = ${scoresFirst.music}")
            Logger.i("DetailViewModel.scores it.first() = ${scoresFirst.story}")

            val radarData = setRadarData(
                averageLeisure = 4.3F,
                averageHit = 3.4F,
                averageCast = 4.9F,
                averageMusic = 2.1F,
                averageStory = 3.7F,
                userLeisure = scoresFirst.leisure,
                userHit = scoresFirst.hit,
                userCast = scoresFirst.cast,
                userMusic = scoresFirst.music,
                userStory = scoresFirst.story
            )

//            binding.radarChartRating.data = radarData
            showRadarChart(binding.radarChartRating, radarData)
        })

        viewModel.score.observe(viewLifecycleOwner, Observer {
            Logger.i("DetailViewModel.score = $it")
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

//        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    private fun setRadarData(
        averageLeisure: Float,
        averageHit: Float,
        averageCast: Float,
        averageMusic: Float,
        averageStory: Float,
        userLeisure: Float,
        userHit: Float,
        userCast: Float,
        userMusic: Float,
        userStory: Float,
    ): RadarData {
        val averageRatingsList: ArrayList<RadarEntry>
            = arrayListOf(
                RadarEntry(averageLeisure),
                RadarEntry(averageHit),
                RadarEntry(averageCast),
                RadarEntry(averageMusic),
                RadarEntry(averageStory)
            )
        val userRatingsList: ArrayList<RadarEntry>
            = arrayListOf(
                RadarEntry(userLeisure),
                RadarEntry(userHit),
                RadarEntry(userCast),
                RadarEntry(userMusic),
                RadarEntry(userStory)
            )

        val averageRatingsSet = RadarDataSet(averageRatingsList, "Average Ratings")
        averageRatingsSet.color = R.color.teal_200
        averageRatingsSet.fillColor = R.color.teal_700
        averageRatingsSet.fillAlpha = 160
        averageRatingsSet.lineWidth = 2F
        averageRatingsSet.isDrawHighlightCircleEnabled = true
        averageRatingsSet.setDrawHighlightIndicators(false)

        val userRatingsSet = RadarDataSet(userRatingsList, "Ratings By you")
        userRatingsSet.color = R.color.gray_999999
        userRatingsSet.fillColor = R.color.black_3f3a3a
        userRatingsSet.fillAlpha = 160
        userRatingsSet.lineWidth = 2F
        userRatingsSet.isDrawHighlightCircleEnabled = true
        userRatingsSet.setDrawHighlightIndicators(false)

        val totalRatingsSet = ArrayList<IRadarDataSet>()
        totalRatingsSet.add(averageRatingsSet)
        totalRatingsSet.add(userRatingsSet)

        val radarData = RadarData(totalRatingsSet)
        radarData.setDrawValues(true)
        radarData.setValueTextSize(10F)
        radarData.setValueTextColor(R.color.purple_200)

        return radarData
    }

    private fun showRadarChart(radarChart: RadarChart, radarData: RadarData) {
        radarChart.description.text = ""
        radarChart.description.setPosition(750F, 70F)
        radarChart.description.textSize = 50F
        radarChart.setDrawWeb(true)
//        radarChart.setBackgroundColor(Color.rgb(255, 102, 0))
//        radarChart.webLineWidth = 1f
//        radarChart.webColor = Color.rgb(0, 0, 0)
//        radarChart.webColorInner = Color.rgb(0, 0, 0)
//        radarChart.webLineWidthInner = 1f
        radarChart.isRotationEnabled = true

        val labels: Array<String> = arrayOf("Leisure", "Hit", "Cast", "Music", "Story")
        radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        radarChart.yAxis.axisMinimum = 0F
        radarChart.yAxis.axisMaximum = 5F
        radarChart.data = radarData
        radarChart.invalidate()
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}


//        scoresFirst?.let {
//            setRadarData(
//                averageLeisure = 4.3F,
//                averageHit = 3.4F,
//                averageCast = 4.9F,
//                averageMusic = 2.1F,
//                averageStory = 3.7F,
//                userLeisure = it.leisure,
//                userHit = it.hit,
//                userCast = it.cast,
//                userMusic = it.music,
//                userStory = it.story)
//        }?.let {
//            showRadarChart(binding.radarChartRating, it)
//        }

//        showRadarChart(
//            binding.radarChartRating,
//            setRadarData(
//                averageLeisure = 4.3F,
//                averageHit = 3.4F,
//                averageCast = 4.9F,
//                averageMusic = 2.1F,
//                averageStory = 3.7F,
//                userLeisure = 3.5F,
//                userHit = 2.5F,
//                userCast = 2.0F,
//                userMusic = 3.5F,
//                userStory = 5.0F
//            )
//        )