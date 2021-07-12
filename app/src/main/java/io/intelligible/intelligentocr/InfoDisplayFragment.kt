package io.intelligible.intelligentocr

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import io.intelligible.intelligentocr.databinding.FragmentInfoDisplayBinding

class InfoDisplayFragment : Fragment(R.layout.fragment_info_display) {

    lateinit var binding: FragmentInfoDisplayBinding
    private val infoDisplayFragmentArgs: InfoDisplayFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentInfoDisplayBinding.bind(view)
        val text = infoDisplayFragmentArgs.text
        binding.textView.append(text)
        val entityExtractor =
            EntityExtraction.getClient(
                EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH)
                    .build()
            )
        entityExtractor
            .downloadModelIfNeeded()
            .addOnSuccessListener { _ ->

                binding.textView.append("\n" + "===================== Entities ===============" + "\n")
                val params =
                    EntityExtractionParams.Builder(text).build()
                entityExtractor
                    .annotate(params)
                    .addOnSuccessListener { entityAnnotation ->
                        for (entitiy in entityAnnotation) {
                            val listOfEntities = entitiy.entities
                            for (entity in listOfEntities) {

                                when (entity.type) {
                                    Entity.TYPE_ADDRESS -> {
                                        binding.textView.append("address " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_DATE_TIME -> {
                                        binding.textView.append("date time " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_EMAIL -> {
                                        binding.textView.append("email " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_FLIGHT_NUMBER -> {
                                        binding.textView.append("flight number " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_IBAN -> {
                                        binding.textView.append("Iban " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_ISBN -> {
                                        binding.textView.append("isbn " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_MONEY -> {
                                        binding.textView.append("money " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_PAYMENT_CARD -> {
                                        binding.textView.append("payment card " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_PHONE -> {
                                        binding.textView.append("phone " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_TRACKING_NUMBER -> {
                                        binding.textView.append("tracking number " + entitiy.annotatedText + "\n")
                                    }
                                    Entity.TYPE_URL -> {
                                        binding.textView.append("type url " + entitiy.annotatedText + "\n")
                                    }
                                    else -> {
                                        binding.textView.append(entitiy.annotatedText + "\n")
                                    }
                                }


                            }


                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Entity Extraction failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { _ ->
                Toast.makeText(requireContext(), "Model Download failed", Toast.LENGTH_SHORT).show()
            }


    }
}