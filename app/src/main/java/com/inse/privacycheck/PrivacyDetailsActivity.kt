package com.inse.privacycheck

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class PrivacyDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_details_activity)

        val jsonData = intent.getStringExtra("privacyDetails")
        val jsonArray = JSONArray(jsonData)
        val listView = findViewById<ListView>(R.id.privacy_list_view)
        val items = mutableListOf<PrivacyItem>()
        for (i in 0 until jsonArray.length()) {
            val itemObject = jsonArray.getJSONObject(i)
            val title = itemObject.getString("category")
            val rating = itemObject.get("rating").toString()
            val summary = itemObject.getString("summary")
            items.add(PrivacyItem(title, summary, rating))
        }
        val adapter = PrivacyItemAdapter(this, items)
        listView.adapter = adapter
    }


}

data class PrivacyItem(
    val category: String,
    val summary: String,
    val rating: String
)