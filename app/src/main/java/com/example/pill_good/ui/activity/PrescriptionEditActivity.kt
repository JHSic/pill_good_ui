package com.example.pill_good.ui.activity

import android.os.Bundle
import com.example.pill_good.R

class PrescriptionEditActivity : CustomActionBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addCustomView(R.layout.activity_prescription_edit_pill_item)
    }
}