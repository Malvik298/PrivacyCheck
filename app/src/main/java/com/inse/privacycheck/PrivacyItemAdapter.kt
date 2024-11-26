package com.inse.privacycheck

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class PrivacyItemAdapter(context: Context, items: List<PrivacyItem>) :
    ArrayAdapter<PrivacyItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_view, parent, false)

        val item = getItem(position)

        val categoryTextView = view.findViewById<TextView>(R.id.titleTextView)
        val summaryTextView = view.findViewById<TextView>(R.id.summaryTextView)
        val scoreTextView = view.findViewById<TextView>(R.id.scoreTextView)

        categoryTextView.text = item?.category
        summaryTextView.text = item?.summary
        scoreTextView.text = item?.rating


        // Set category text color based on rating
        val value = when (item?.rating) {
            "Worst" -> categoryTextView.setTextColor(Color.RED)
            "Good" -> categoryTextView.setTextColor(Color.parseColor("#FFA500"))
            "Best" -> categoryTextView.setTextColor(Color.GREEN)
            else -> categoryTextView.setTextColor(Color.BLACK) // Default color
        }
        return view
    }
}