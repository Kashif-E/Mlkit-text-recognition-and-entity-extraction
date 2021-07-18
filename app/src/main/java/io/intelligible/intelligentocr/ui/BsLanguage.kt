package io.intelligible.intelligentocr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import io.intelligible.intelligentocr.adapter.LanguagesAdapter
import io.intelligible.intelligentocr.databinding.BsLanguagesBinding

typealias languageClickListener = (language: String) -> Unit

class BsLanguage(val languageChangeListener: languageChangeListener) : BottomSheetDialogFragment() {
    lateinit var binding: BsLanguagesBinding
    private val languageList = listOf(
        "Arabic",
        "Portuguese",
        "English (US, UK)",
        "Dutch",
        "French",
        "German",
        "Italian",
        "Japanese",
        "Korean",
        "Polish",
        "Russian",
        "Chinese (Simplified, Traditional)",
        "Spanish",
        "Thai",
        "Turkish"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BsLanguagesBinding.inflate(layoutInflater)
        binding.rvlanaguages.adapter = LanguagesAdapter { selectedLanguage ->
          val option =  when (selectedLanguage) {
                "Arabic" -> {
                    EntityExtractorOptions.ARABIC
                }
                "Portuguese" -> {
                    EntityExtractorOptions.PORTUGUESE
                }
                "English (US, UK)" -> {
                    EntityExtractorOptions.ENGLISH
                }
                "Dutch" -> {
                    EntityExtractorOptions.DUTCH
                }
                "French" -> {
                    EntityExtractorOptions.FRENCH
                }
                "German" -> {
                    EntityExtractorOptions.GERMAN
                }
                "Italian" -> {
                    EntityExtractorOptions.ITALIAN
                }
                "Japanese" -> {
                    EntityExtractorOptions.JAPANESE
                }
                "Korean" -> {
                    EntityExtractorOptions.KOREAN
                }
                "Polish" -> {
                    EntityExtractorOptions.POLISH
                }
                "Russian" -> {
                    EntityExtractorOptions.RUSSIAN
                }
                "Chinese (Simplified, Traditional)" -> {
                    EntityExtractorOptions.CHINESE
                }
                "Spanish" -> {
                    EntityExtractorOptions.SPANISH
                }
                "Thai" -> {
                    EntityExtractorOptions.THAI
                }
                "Turkish" -> {
                    EntityExtractorOptions.TURKISH
                }
                else -> {
                    EntityExtractorOptions.ENGLISH
                }
            }

            languageChangeListener(option)

            this.dismiss()
        }.apply {
            differ.submitList(languageList)
        }


        return binding.root
    }


    companion object {

        fun newInstance(
            languageChangeListener: languageChangeListener
        ): BsLanguage =
            BsLanguage (languageChangeListener).apply {

            }
    }

}