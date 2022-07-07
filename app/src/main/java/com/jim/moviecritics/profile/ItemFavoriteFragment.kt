package com.jim.moviecritics.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jim.moviecritics.databinding.ItemProfileFavoriteBinding
import com.jim.moviecritics.ext.getVmFactory
import com.jim.moviecritics.util.Logger


class ItemFavoriteFragment : Fragment() {

    private val viewModel by viewModels<ItemFavoriteViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding = ItemProfileFavoriteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.recyclerProfileFavoriteChild.adapter = FavoriteItemAdapter(
            FavoriteItemAdapter.OnClickListener {
                Logger.i("FavoriteItemAdapter.OnClickListener it = $it")
            }
        )

//        viewModel.livePersonalFavorites.observe(viewLifecycleOwner) {
//            Logger.i("viewModel.livePersonalFavorites = $it")
//            viewModel.getFavoritesFull(it)
//        }
        return binding.root
    }
}