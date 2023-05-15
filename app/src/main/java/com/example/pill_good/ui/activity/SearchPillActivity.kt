package com.example.pill_good.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.pill_good.R

class SearchPillActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_search_pill)

        val searchButton: Button = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            var searchPillResultIntent = Intent(this, SearchPillResultActivity::class.java)
            startActivity(searchPillResultIntent)
        }
    }
}