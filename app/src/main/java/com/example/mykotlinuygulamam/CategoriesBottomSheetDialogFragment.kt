package com.example.mykotlinuygulamam

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoriesBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var listener: CategorySelectListener? = null
    private lateinit var categories: MutableList<String>
    private lateinit var subCategories: Map<String, List<String>>
    private lateinit var adapter: ArrayAdapter<String>

    interface CategorySelectListener {
        fun onCategorySelected(category: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as CategorySelectListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement CategorySelectListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_categories, container, false)

        val categoriesListView = view.findViewById<ListView>(R.id.list_categories)

        categories = mutableListOf(
            "Bilgisayarlar", "Telefonlar", "Tekerlekler", "Haplar", "Fareler",
            "Peçeteler", "Kalemler", "Defterler", "Soğutucular", "Kemer",
            "Cüzdanlar", "Okul İhtiyaçları", "Masalar", "Ağız Bakım Ürünleri",
            "Okuma Kitapları", "Tarım İhtiyaçları", "Tabletler", "Fare Bezi",
            "Hayvan İlaçları", "Parfümler", "Battaniyeler", "Seccadeler",
            "Araç Süsleri", "Işıklar", "Kablolar", "Kulaklıklar", "Klimalar",
            "Televizyonlar"
        )

        subCategories = mapOf(
            "Bilgisayarlar" to listOf("Dell", "HP", "Lenovo", "Asus", "Acer"),
            "Telefonlar" to listOf("Samsung", "Apple", "Huawei", "Xiaomi", "Oppo"),
            "Tekerlekler" to listOf("Michelin", "Pirelli"),
            "Haplar" to listOf("Pfizer", "Roche"),
            "Fareler" to listOf("Logitech", "Razer"),
            "Peçeteler" to listOf("Selpak", "Solo"),
            "Kalemler" to listOf("Faber-Castell", "Pilot"),
            "Defterler" to listOf("Moleskine", "Leuchtturm1917"),
            "Soğutucular" to listOf("Arçelik", "Beko"),
            "Kemer" to listOf("Gucci", "Prada"),
            "Cüzdanlar" to listOf("Louis Vuitton", "Montblanc"),
            "Okul İhtiyaçları" to listOf("Faber-Castell", "Staedtler"),
            "Masalar" to listOf("Ikea", "Steelcase"),
            "Ağız Bakım Ürünleri" to listOf("Colgate", "Oral-B"),
            "Okuma Kitapları" to listOf("Penguin Books", "HarperCollins"),
            "Tarım İhtiyaçları" to listOf("John Deere", "Case IH"),
            "Tabletler" to listOf("Apple iPad", "Samsung Galaxy Tab"),
            "Fare Bezi" to listOf("SteelSeries", "Razer"),
            "Hayvan İlaçları" to listOf("Zoetis", "Bayer"),
            "Parfümler" to listOf("Chanel", "Dior"),
            "Battaniyeler" to listOf("Ikea", "H&M"),
            "Seccadeler" to listOf("Madame Coco", "English Home"),
            "Araç Süsleri" to listOf("3M", "Meguiar's"),
            "Işıklar" to listOf("Philips", "Osram"),
            "Kablolar" to listOf("Belkin", "Anker"),
            "Kulaklıklar" to listOf("Sony", "Bose"),
            "Klimalar" to listOf("Daikin", "Mitsubishi Electric"),
            "Televizyonlar" to listOf("Samsung", "LG")
        )

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        categoriesListView.adapter = adapter

        categoriesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]
            if (subCategories.containsKey(selectedCategory) && subCategories[selectedCategory]?.isNotEmpty() == true) {
                showSubCategories(selectedCategory)
            } else {
                Toast.makeText(requireContext(), "Maalesef, henüz $selectedCategory adında ürün bulunamadı.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun showSubCategories(category: String) {
        val subCategoryList = subCategories[category]
        if (subCategoryList != null && subCategoryList.isNotEmpty()) {
            val subCategoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subCategoryList)
            val subCategoryListView = ListView(requireContext())
            subCategoryListView.adapter = subCategoryAdapter
            val subCategoryDialog = AlertDialog.Builder(requireContext())
                .setTitle(category)
                .setView(subCategoryListView)
                .setNegativeButton("Geri Dön") { dialog, _ -> dialog.dismiss() }
                .create()
            subCategoryListView.setOnItemClickListener { _, _, subPosition, _ ->
                val selectedSubCategory = subCategoryList[subPosition]
                moveCategoryToTop(selectedSubCategory)
                listener?.onCategorySelected(selectedSubCategory)
                subCategoryDialog.dismiss()
                dismiss()
            }
            subCategoryDialog.show()
        }
    }

    private fun moveCategoryToTop(category: String) {
        categories.remove(category)
        categories.add(0, category)
        adapter.notifyDataSetChanged()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
