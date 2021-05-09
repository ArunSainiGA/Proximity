package com.asp.proximitylabs.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.asp.proximitylabs.*
import com.asp.proximitylabs.databinding.FragmentDetailBinding
import com.asp.proximitylabs.di.DependencyInjector
import com.asp.proximitylabs.model.AirQualityModel
import com.asp.proximitylabs.ui.viewmodel.ViewModel
import com.asp.proximitylabs.utils.ViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*


class DetailFragment : Fragment() {

    private lateinit var model: AirQualityModel

    private lateinit var graph: LineChart

    companion object {
        private const val KEY_AQI_MODEL = "KeyAQIModel"

        fun newInstance(model: AirQualityModel) = DetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_AQI_MODEL, model)
            }
        }
    }

    private lateinit var binding: FragmentDetailBinding

    private lateinit var viewModel: ViewModel

    private val mapEntry = mutableMapOf<Float, Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_detail,
            container,
            false
        )

        viewModel = ViewModelProvider(this, ViewModelFactory {
            ViewModel(DependencyInjector.getAirQualityClient())
        }).get(ViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateViews(view)
        parseArguments()
        updateUI()
        observe()
    }

    private fun inflateViews(view: View){
        graph = view.findViewById(R.id.graph)
    }

    private fun observe(){
        viewModel.viewStateObservable.observe(viewLifecycleOwner, {
            if(it != null)
                render(it)
        })
    }

    private fun updateUI() {
        val xAxis: XAxis = graph.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1F

        xAxis.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val milli = mapEntry[value]
                val sdf = SimpleDateFormat("HH:mm:ss")
                return sdf.format(Date(milli!!))
            }
        }

        val desc = Description()
        desc.text = model.city
        graph.description = desc;

        graph.setTouchEnabled(true)
        graph.setPinchZoom(false)
        graph.isDoubleTapToZoomEnabled = false
        graph.setVisibleXRangeMaximum(10F)
        graph.moveViewToX(10F)

        graph.data = LineData(setUpLineData(generateChartDataPoints()))

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
        list?.forEach {
            if(it.city == model.city) {
                model.updateAQI(it.aqi)

                graph.data = LineData(setUpLineData(generateChartDataPoints()))
                graph.notifyDataSetChanged()
                graph.invalidate()
            }
        }
    }

    private fun setUpLineData(dataPoints:  List<Entry>): LineDataSet{
        val set = LineDataSet(dataPoints, "")
        set.color = Color.GREEN
        set.lineWidth = 1.5f
        set.fillAlpha = 65
        set.fillColor = Color.GRAY
        return set
    }

    private fun generateChartDataPoints(): List<Entry>{
        val dataPoints = mutableListOf<Entry>()
        mapEntry.clear()
        model.getHistory().entries.forEachIndexed { index, entry ->
            val xAxis = (index + 1).toFloat()
            mapEntry[xAxis] = entry.key
            dataPoints.add(Entry(xAxis, entry.value.toFloat()))
        }
        return dataPoints
    }

    private fun parseArguments(){
        model = arguments?.getParcelable(KEY_AQI_MODEL)!!
    }
}