package com.jim.moviecritics.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jim.moviecritics.NavigationDirections
import com.jim.moviecritics.data.Movie
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.databinding.FragmentHomeBinding
import com.jim.moviecritics.util.Logger


class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }

//    companion object {
//        fun newInstance() = HomeFragment()
//    }
//
//    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.homeItems.observe(viewLifecycleOwner, Observer {
            Logger.i("HomeViewModel.homeItems = $it")
//            for (value in it) {
//                Logger.i("value in List<HomeItem> value.id = ${value.id}")
//            }
        })

        binding.recyclerviewPopular.adapter = HomeAdapter(
            HomeAdapter.OnClickListener {
                Logger.i("HomeAdapter.OnClickListener it = $it")
                Logger.i("HomeAdapter.OnClickListener it.id = ${it.id}")
//                viewModel.getMovieDetail(isInitial = true, id = it.id)
//                viewModel.getMovieCredit(isInitial = true, id = it.id)
                var movie = Movie()
                viewModel.getMovieFull(it.id)
//                viewModel.navigateToDetail(movie)
            }
        )

        viewModel.detailItem.observe(viewLifecycleOwner, Observer {
            Logger.i("HomeViewModel.detailItem = $it")
//            viewModel.navigateToDetail(
//                Movie(
//                    id = it.id,
//                    casts = listOf(),
//                    imdbID = it.imdbID,
//                    awards = null,
//                    country = null,
//                    director = null,
//                    genres = it.genres,
//                    overview = it.overview,
//                    posterUri = "https://image.tmdb.org/t/p/w185" + it.posterUri,
//                    released = it.releaseDate,
//                    runTime = it.runTime,
//                    sales = null,
//                    salesTaiwan = null,
//                    title = it.title,
//                    trailerUri = null,
//                    writer = null,
//                    ratings = listOf()))
        })

        viewModel.creditItem.observe(viewLifecycleOwner, Observer {
            Logger.i("HomeViewModel.creditItem = $it")
        })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            Logger.i("HomeViewModel.navigateToDetail = $it")
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        })

        viewModel.comments.observe(viewLifecycleOwner, Observer {
            Logger.i("HomeViewModel.testComments = $it")
        })

        return binding.root
//        return inflater.inflate(R.layout.home_fragment, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}