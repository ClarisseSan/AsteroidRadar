package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.AsteroidRecyclerViewAdapter
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.network.AsteroidApiFilter

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)
        ).get(MainViewModel::class.java)
    }

    private val asteroidRecyclerAdapter =
        AsteroidRecyclerViewAdapter(AsteroidRecyclerViewAdapter.OnClickListener {
            viewModel.displayAsteroidDetails(it)
        })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = asteroidRecyclerAdapter



        setHasOptionsMenu(true)


        viewModel.asteroids.observe(viewLifecycleOwner, Observer {
            asteroidRecyclerAdapter.submitList(it)
        })


        //navigate to DetailFragment
        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_week -> AsteroidApiFilter.SHOW_WEEK
                R.id.show_today -> AsteroidApiFilter.SHOW_TODAY
                R.id.show_all -> AsteroidApiFilter.SHOW_ALL
                else -> AsteroidApiFilter.SHOW_ALL
            }
        )
        return true
    }
}