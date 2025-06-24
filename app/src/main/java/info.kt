package com.example.fitnessapp


import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class info : AppCompatActivity() {

    private lateinit var btnFeet: Button
    private lateinit var btnInches: Button
    private lateinit var btnWeight: Button
    private lateinit var spinnerGender: Spinner
    private lateinit var btnSubmit: Button

    private var selectedHeightUnit = ""
    private var selectedWeightValue = ""
    private var selectedGender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        initViews()
        setupClickListeners()
        setupSpinner()
    }

    private fun initViews() {
        btnFeet = findViewById(R.id.btnFeet)
        btnInches = findViewById(R.id.btnInches)
        btnWeight = findViewById(R.id.btnWeight)
        spinnerGender = findViewById(R.id.spinnerGender)
        btnSubmit = findViewById(R.id.btnSubmit)
    }

    private fun setupClickListeners() {
        // Height unit selection
        btnFeet.setOnClickListener {
            selectHeightUnit("ft", btnFeet, btnInches)
        }

        btnInches.setOnClickListener {
            selectHeightUnit("in", btnInches, btnFeet)
        }

        // Weight button click
        btnWeight.setOnClickListener {
            showWeightDialog()
        }

        // Submit button
        btnSubmit.setOnClickListener {
            submitForm()
        }
    }

    private fun selectHeightUnit(unit: String, selectedButton: Button, otherButton: Button) {
        selectedHeightUnit = unit

        // Update button states
        selectedButton.isSelected = true
        otherButton.isSelected = false

        // Update visual appearance
        updateButtonAppearance(selectedButton, true)
        updateButtonAppearance(otherButton, false)
    }

    private fun updateButtonAppearance(button: Button, isSelected: Boolean) {
        if (isSelected) {
            button.setBackgroundResource(R.drawable.button_selector_enhanced)
            button.setTextColor(ContextCompat.getColor(this, R.color.button_selected_text))
        } else {
            button.setBackgroundResource(R.drawable.button_selector_enhanced)
            button.setTextColor(ContextCompat.getColor(this, R.color.button_unselected_text))
        }
    }

    private fun showWeightDialog() {
        val weightOptions = arrayOf("40 kg", "45 kg", "50 kg", "55 kg", "60 kg", "65 kg", "70 kg", "75 kg", "80 kg", "85 kg", "90 kg", "95 kg", "100 kg", "105 kg", "110 kg")

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Weight")
        builder.setItems(weightOptions) { _, which ->
            selectedWeightValue = weightOptions[which]
            btnWeight.text = selectedWeightValue
        }
        builder.show()
    }

    private fun setupSpinner() {
        val genderOptions = arrayOf("Select your gender", "Male", "Female", "Other", "Prefer not to say")

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderOptions) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(ContextCompat.getColor(this@info, R.color.spinner_text_color))
                textView.textSize = 18f
                textView.setPadding(20, 20, 20, 20)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(ContextCompat.getColor(this@info, android.R.color.black))
                textView.setPadding(16, 16, 16, 16)
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter

        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Skip the first "Select your gender" option
                    selectedGender = genderOptions[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun submitForm() {
        // Validate form
        if (selectedHeightUnit.isEmpty()) {
            Toast.makeText(this, "Please select height unit", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedWeightValue.isEmpty()) {
            Toast.makeText(this, "Please select weight", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            return
        }

        // Form is valid, process the data
        val message = "Height Unit: $selectedHeightUnit\nWeight: $selectedWeightValue\nGender: $selectedGender"

        Toast.makeText(this, "Form submitted successfully!", Toast.LENGTH_LONG).show()

        // Here you can add code to save data to database, send to server, etc.
        // For now, we'll just show the collected data
        showSubmissionDialog(message)
    }

    private fun showSubmissionDialog(data: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Submitted Data")
        builder.setMessage(data)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            // You can add navigation to next screen here
        }
        builder.show()
    }
}