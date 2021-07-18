package io.intelligible.intelligentocr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.intelligible.intelligentocr.databinding.ViewholderLanguagesBinding
import io.intelligible.intelligentocr.ui.languageClickListener


class LanguagesAdapter(val languageClickListener: languageClickListener) : RecyclerView.Adapter<LanguagesAdapter.LangugaeViewHolder>() {


    inner class LangugaeViewHolder(private val itemViewBinding: ViewholderLanguagesBinding) :
        RecyclerView.ViewHolder(
            itemViewBinding.root
        ) {

        fun bindView(lanaguageItem: String) {
            itemViewBinding.apply {
                tvlang.text = lanaguageItem
            }
            itemViewBinding.root.setOnClickListener {
                languageClickListener(lanaguageItem)
            }


        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.length == newItem.length
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LangugaeViewHolder {
        return LangugaeViewHolder(

            ViewholderLanguagesBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: LangugaeViewHolder, position: Int) {

        val languageItem = differ.currentList[position]
        holder.bindView(languageItem)
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}