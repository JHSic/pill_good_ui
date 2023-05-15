package com.example.pill_good.ui.activity

import android.os.Bundle
import com.example.pill_good.R

class SearchPillResultActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_search_pill_result)
    }
}