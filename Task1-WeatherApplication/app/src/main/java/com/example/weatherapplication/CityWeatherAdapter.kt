package com.example.weatherapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CityWeatherAdapter(
    context: Context,
    items: List<CityWeatherItem>
) : ArrayAdapter<CityWeatherItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val item = getItem(position) ?: return view
        val title = view.findViewById<TextView>(android.R.id.text1)
        val subtitle = view.findViewById<TextView>(android.R.id.text2)

        title.text = "${item.name} - ${item.currentTemperature}"
        subtitle.text = "${item.country} - ${item.currentSummary}"
        return view
    }
}
