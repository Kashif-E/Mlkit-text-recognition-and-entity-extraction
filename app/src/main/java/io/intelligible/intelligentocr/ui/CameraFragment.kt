package io.intelligible.intelligentocr.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import com.snatik.storage.Storage
import io.intelligible.intelligentocr.constants.Constants.Companion.RATIO_16_9_VALUE
import io.intelligible.intelligentocr.constants.Constants.Companion.RATIO_4_3_VALUE
import io.intelligible.intelligentocr.databinding.FragmentCameraBinding
import io.intelligible.intelligentocr.utils.TextAnalyser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.abs
import   io.intelligible.intelligentocr.R
import io.intelligible.intelligentocr.customviews.ProgressDialog
import io.intelligible.intelligentocr.extensions.snack
import kotlin.math.max

import kotlin.math.min


typealias CameraTextAnalyzerListener = (text: String) -> Unit
typealias languageChangeListener = (language : String ) -> Unit
class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraInfo: CameraInfo
    private var currentLanguage = EntityExtractorOptions.ENGLISH
    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }
    private lateinit var progressDialog: ProgressDialog
    lateinit var binding: FragmentCameraBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCameraBinding.bind(view)

        progressDialog = ProgressDialog(requireContext(), false)
        binding.btnchangeLanguage.setOnClickListener {
            BsLanguage.newInstance {
                currentLanguage = it
              binding.root.snack("Language has been changed to $it", getString(R.string.ok) )
            }.show(
                childFragmentManager,
                BsLanguage::class.java.simpleName
            )
        }

        binding.viewFinder.post {
            startCamera()
        }
        binding.ivImageCapture.setOnClickListener {
            progressDialog.show()
            takePicture()
        }

    }


    @Suppress("SameParameterValue")
    private fun createFile(baseFolder: File, format: String, extension: String) =
        File(
            baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension
        )

    private fun takePicture() {

        val file = createFile(
            getOutputDirectory(
                requireContext()
            ),
            "yyyy-MM-dd-HH-mm-ss-SSS",
            ".png"
        )
        val outputFileOptions =
            ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {


                    // sending the captured image for analysis
                    GlobalScope.launch(Dispatchers.IO) {
                        TextAnalyser({ result ->
                            if (result.isEmpty()) {

                                progressDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "No Text Detected",
                                    Toast.LENGTH_SHORT
                                ).show()


                            } else {

                                progressDialog.dismiss()
                                findNavController().navigate(
                                    R.id.action_cameraFragment_to_infoDisplayFragment,
                                    Bundle().apply {
                                        putString("text", result)
                                        putString("language", currentLanguage)
                                    })
                            }

                        }, requireContext(), Uri.fromFile(file)).analyseImage()

                    }
                }

                override fun onError(exception: ImageCaptureException) {

                    progressDialog.dismiss()
                    Log.e("error", exception.localizedMessage!!)
                }
            })
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun startCamera() {


        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        val rotation = binding.viewFinder.display.rotation


        // Bind the CameraProvider to the LifeCycleOwner
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            // CameraProvider
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation
                .setTargetRotation(rotation)

                .build()

            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            // ImageCapture
            imageCapture = initializeImageCapture(screenAspectRatio, rotation)

            // ImageAnalysis


            cameraProvider.unbindAll()

            try {
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                cameraControl = camera.cameraControl
                cameraInfo = camera.cameraInfo
                cameraControl.setLinearZoom(0.5f)


            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))


    }

    private fun getOutputDirectory(context: Context): File {

        val storage = Storage(context)
        val mediaDir = storage.internalCacheDirectory?.let {
            File(it, "Intelligible OCR").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }


    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    private fun initializeImageCapture(
        screenAspectRatio: Int,
        rotation: Int
    ): ImageCapture {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
    }

    override fun onResume() {
        super.onResume()
        binding.root.snack(getString(R.string.pointing_message), getString(R.string.ok))

    }
}