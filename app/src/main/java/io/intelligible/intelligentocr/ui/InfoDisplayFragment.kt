package io.intelligible.intelligentocr.ui

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.mlkit.nl.entityextraction.*
import io.intelligible.intelligentocr.R
import io.intelligible.intelligentocr.databinding.FragmentInfoDisplayBinding
import io.intelligible.intelligentocr.extensions.snack


class InfoDisplayFragment : Fragment(R.layout.fragment_info_display) {

    private lateinit var entityExtractor: EntityExtractor
    lateinit var binding: FragmentInfoDisplayBinding
    private val infoDisplayFragmentArgs: InfoDisplayFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentInfoDisplayBinding.bind(view)

        binding.rawtext.append(infoDisplayFragmentArgs.text)

        entityExtractor =
            EntityExtraction.getClient(
                EntityExtractorOptions.Builder(infoDisplayFragmentArgs.language)
                    .build()
            )
        binding.btnSave.setOnClickListener {

            if (binding.tvMobile.text.toString()
                    .isEmpty()
            ) {
                binding.root.snack(
                    message = getString(R.string.phone_number_needed),
                    action = getString(R.string.ok)
                )
                return@setOnClickListener
            }
            val intent = Intent(ContactsContract.Intents.Insert.ACTION)

            intent.type = ContactsContract.RawContacts.CONTENT_TYPE


            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, binding.tvemail.text.toString())
                .putExtra(
                    ContactsContract.Intents.Insert.EMAIL_TYPE,
                    CommonDataKinds.Email.TYPE_WORK
                ).putExtra(
                    ContactsContract.Intents.Insert.NAME, binding.tvname.text.toString())
                .putExtra(
                    ContactsContract.Intents.Insert.PHONE,
                    binding.tvMobile.text.toString()
                )
                .putExtra(
                    ContactsContract.Intents.Insert.PHONE_TYPE,
                    Phone.TYPE_MOBILE,
                )
                .putExtra(
                    ContactsContract.Intents.Insert.SECONDARY_PHONE,
                    binding.tvhome.text.toString()
                )
                .putExtra(
                    ContactsContract.Intents.Insert.PHONE_TYPE,
                    Phone.TYPE_HOME
                )

            startActivity(intent)
        }
        extractEntities()


    }

    private fun extractEntities() {
        entityExtractor
            .downloadModelIfNeeded()
            .addOnSuccessListener { _ ->


                val params =
                    EntityExtractionParams.Builder(infoDisplayFragmentArgs.text).build()
                entityExtractor
                    .annotate(params)
                    .addOnSuccessListener { entityAnnotation ->

                        categoriesEntities(entityAnnotation)
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

    private fun categoriesEntities(entityAnnotation: MutableList<EntityAnnotation>) {
        binding.rawtext.append("\n ======= Other Entities ======= \n")
        for (entitiy in entityAnnotation) {
            val listOfEntities = entitiy.entities
            for (entity in listOfEntities) {


                when (entity.type) {

                    Entity.TYPE_ADDRESS -> {
                        binding.tvaddress.append(entitiy.annotatedText)
                    }
                    Entity.TYPE_DATE_TIME -> {
                        binding.tvdate.append(entitiy.annotatedText)
                    }
                    Entity.TYPE_EMAIL -> {
                        binding.tvemail.setText(entitiy.annotatedText)
                    }
                    Entity.TYPE_FLIGHT_NUMBER -> {
                        binding.rawtext.append("flight number " + entitiy.annotatedText + "\n")
                    }
                    Entity.TYPE_IBAN -> {
                        binding.rawtext.append("Iban " + entitiy.annotatedText + "\n")
                    }
                    Entity.TYPE_ISBN -> {
                        binding.rawtext.append("isbn " + entitiy.annotatedText + "\n")
                    }
                    Entity.TYPE_MONEY -> {
                        binding.rawtext.append("money " + entitiy.annotatedText + "\n")
                    }
                    Entity.TYPE_PAYMENT_CARD -> {
                        binding.rawtext.append("payment card " + entitiy.annotatedText + "\n")
                    }
                    Entity.TYPE_PHONE -> {
                        if (binding.tvMobile.text.toString().isEmpty()) {
                            binding.tvMobile.setText(entitiy.annotatedText)
                        } else if (binding.tvhome.text.toString().isEmpty()) {
                            binding.tvhome.setText(entitiy.annotatedText)
                        }
                    }
                    Entity.TYPE_TRACKING_NUMBER -> {
                        binding.rawtext.append("tracking number " + entitiy.annotatedText + "\n")
                    }
                    Entity.TYPE_URL -> {
                        binding.tvUrl.setText(entitiy.annotatedText)
                    }
                    else -> {
                        binding.rawtext.append(entitiy.annotatedText + "\n")
                    }
                }


            }


        }
    }
}