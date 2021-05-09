package com.asp.proximitylabs.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.asp.proximitylabs.*
import com.asp.proximitylabs.databinding.FragmentListBinding
import com.asp.proximitylabs.di.DependencyInjector
import com.asp.proximitylabs.model.AirQualityModel
import com.asp.proximitylabs.ui.adapter.Adapter
import com.asp.proximitylabs.ui.viewmodel.ViewModel
import com.asp.proximitylabs.utils.ViewModelFactory

class ListFragment: Fragment() {
    private var recyclerView: RecyclerView? = null
    private var adapter =  Adapter(ArrayList(), ::listener)

    companion object {
        fun newInstance() = ListFragment()
    }

    private lateinit var viewModel: ViewModel
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_list, container, false)

        viewModel = ViewModelProvider(this, ViewModelFactory {
            ViewModel(DependencyInjector.getAirQualityClient())
        }).get(ViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateViews(view)
        setupViews()
        observe()
    }

    private fun inflateViews(view: View){
        recyclerView = view.findViewById(R.id.recyclerView)
    }

    private fun setupViews(){
        recyclerView?.adapter = adapter
    }

    private fun observe(){
        viewModel.viewStateObservable.observe(viewLifecycleOwner, {
            if(it != null)
                render(it)
        })
    }

    private fun render(viewState: ViewModel.ViewState) {
        when(viewState){
            is ViewModel.ViewState.Loading -> {

            }
            is ViewModel.ViewState.Error -> {
                Toast.makeText(context, viewState.message, Toast.LENGTH_SHORT).show()
            }
            is ViewModel.ViewState.DataFetched -> {
                onDataReceived(viewState.data)
            }
        }
    }

    private fun onDataReceived(list: ArrayList<AirQualityModel>?){
        adapter.updateContent(list)
    }

    fun listener(position: Int, item: AirQualityModel){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, DetailFragment.newInstance(item))
        transaction.addToBackStack(null)
        transaction.commit()
    }
}