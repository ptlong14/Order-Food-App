package com.longpt.projectll1

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.longpt.projectll1.databinding.XxxActivityAdminAddFoodBinding
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.model.OptionGroup
import com.longpt.projectll1.domain.model.OptionItem

class AdminAddFood : AppCompatActivity() {
    private lateinit var binding: XxxActivityAdminAddFoodBinding
    private val db = Firebase.firestore
    private val categories = listOf("snacks", "meal", "vegan", "dessert", "drinks")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = XxxActivityAdminAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupCategorySpinner()
        setupAddGroupButton()
        setupSubmitButton()
    }
    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = adapter
    }

    private fun setupAddGroupButton() {
        binding.btnAddGroup.setOnClickListener {
            addGroupView()
        }
    }

    private fun addGroupView() {
        val groupView = layoutInflater.inflate(R.layout.xxx_item_option_group, binding.layoutOptionGroups, false)
        val btnAddOption = groupView.findViewById<Button>(R.id.btnAddOptionItem)
        val layoutOptions = groupView.findViewById<LinearLayout>(R.id.layoutOptionItems)

        btnAddOption.setOnClickListener {
            val optionView = layoutInflater.inflate(R.layout.xxx_item_option_item, layoutOptions, false)
            layoutOptions.addView(optionView)
        }

        binding.layoutOptionGroups.addView(groupView)
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val price = binding.etPrice.text.toString().toDoubleOrNull()?: 0.0
            val imgUrl = binding.etImageUrl.text.toString().trim()
            val category = binding.spCategory.selectedItem.toString()
            val rating = binding.etRating.text.toString().toDoubleOrNull() ?: 0.0
            val sold = binding.etSold.text.toString().toIntOrNull() ?: 0

            val optionGroups = mutableListOf<OptionGroup>()
            for (i in 0 until binding.layoutOptionGroups.childCount) {
                val groupView = binding.layoutOptionGroups.getChildAt(i)
                val etGroupName = groupView.findViewById<EditText>(R.id.etGroupName)
                val etMaxChoose = groupView.findViewById<EditText>(R.id.etMaxChoose)
                val cbRequire = groupView.findViewById<CheckBox>(R.id.cbRequire)
                val layoutOptions = groupView.findViewById<LinearLayout>(R.id.layoutOptionItems)

                val items = mutableListOf<OptionItem>()
                for (j in 0 until layoutOptions.childCount) {
                    val itemView = layoutOptions.getChildAt(j)
                    val etOptionName = itemView.findViewById<EditText>(R.id.etOptionName)
                    val etExtraCost = itemView.findViewById<EditText>(R.id.etExtraCost)
                    items.add(OptionItem(etOptionName.text.toString(), etExtraCost.text.toString().toDoubleOrNull() ?: 0.0))
                }

                optionGroups.add(
                    OptionGroup(
                        groupName = etGroupName.text.toString(),
                        maxChoose = etMaxChoose.text.toString().toIntOrNull() ?: 1,
                        require = cbRequire.isChecked,
                        optionItem = items
                    )
                )
            }

            val food = Food("",name, price, rating, imgUrl, category, desc, optionGroups, sold)

            val docRef = db.collection("foods").document() // Tạo doc có id random
            val foodWithId = food.copy(id = docRef.id)     // Gán id đó vào model

            docRef.set(foodWithId)
                .addOnSuccessListener {
                    Toast.makeText(this, "Đã thêm món!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}