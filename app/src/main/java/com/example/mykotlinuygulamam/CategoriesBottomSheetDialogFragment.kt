package com.example.mykotlinuygulamam

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoriesBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var listener: CategorySelectListener? = null

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
        val categories = arrayOf("Tekerlekler", "Haplar", "Fareler", "Bilgisayarlar", "Peçeteler", "Kalemler", "Defterler", "Soğutucular", "Kemer", "Cüzdanlar", "Okul İhtiyaçları", "Masalar", "Ağız Bakım Ürünleri", "Telefonlar", "Okuma Kitapları", "Tarım İhtiyaçları", "Tabletler", "Fare Bezi", "Hayvan İlaçları", "Parfümler", "Battaniyeler", "Seccadeler", "Araç Süsleri", "Işıklar", "Kablolar", "Kulaklıklar", "Klimalar", "Televizyonlar")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        categoriesListView.adapter = adapter

        categoriesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]
            listener?.onCategorySelected(selectedCategory)
            dismiss()
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
