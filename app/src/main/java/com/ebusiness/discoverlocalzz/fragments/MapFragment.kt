package com.ebusiness.discoverlocalzz.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.activities.MainActivity
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.models.Address
import com.ebusiness.discoverlocalzz.database.models.Location
import com.ebusiness.discoverlocalzz.database.models.LocationInterest
import com.ebusiness.discoverlocalzz.helpers.Base64
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

/**
 * Fragment zur Darstellung einer interaktiven Karte für Veranstaltungsorte.
 */
class MapFragment : Fragment() {
    private lateinit var map: MapView
    private var geocoder: Geocoder? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var dialogView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val imageUri = data?.data
                if (imageUri != null) {
                    if (dialogView == null) {
                        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_location, null)
                    }
                    dialogView?.let {
                        val uploadedImage = it.findViewById<ImageView>(R.id.uploaded_image)
                        uploadedImage.setImageURI(imageUri)
                        it.findViewById<ConstraintLayout>(R.id.uploaded_image_view).visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    /**
     * Erstellt die Ansicht für das Kartenfragment und initialisiert die Suchleiste.
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        MainActivity.setupSearchView(root)
        root.findViewById<SearchView>(R.id.searchView)
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    onSearchClicked()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

        root.findViewById<FloatingActionButton>(R.id.add_location_button).setOnClickListener {
            showAddLocationDialog(requireContext())
        }
        requestPermissions()

        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext()),
        )
        map = root.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.minZoomLevel = MIN_ZOOM_LEVEL
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.controller.run {
            zoomTo(DEFAULT_ZOOM_LEVEL)
            animateTo(GeoPoint(START_LATITUDE, START_LONGITUDE))
        }
        map.overlays.add(
            MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
                setPersonAnchor(PERSON_ANCHOR, PERSON_ANCHOR)
                setPersonIcon(
                    ContextCompat
                        .getDrawable(requireContext(), R.drawable.ic_map_location)
                        ?.toBitmap(PERSON_SIZE, PERSON_SIZE),
                )
                enableMyLocation()
                runOnFirstFix {
                    requireActivity().runOnUiThread {
                        map.controller.animateTo(myLocation)
                    }
                }
            },
        )

        CoroutineScope(Dispatchers.Main).launch {
            geocoder = Geocoder(requireContext(), Locale.getDefault())
            for ((location, address) in AppDatabase.getInstance(requireContext()).locationDao()
                .getAll()) {
                geocoder?.getFromLocationName(
                    address.toString(resources),
                    1,
                )?.firstOrNull()?.let { address ->
                    map.overlays.add(
                        Marker(map).apply {
                            position = GeoPoint(address.latitude, address.longitude)
                            title = location.title
                            icon = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_circle_local_activity
                            )
                        },
                    )
                }
            }
        }

        return root
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                0,
            )
        }
    }

    /**
     * Aktualisiert die Kartenansicht.
     */
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    /**
     * Pausiert die Kartenansicht.
     */
    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    companion object {
        private const val MIN_ZOOM_LEVEL = 5.0
        private const val DEFAULT_ZOOM_LEVEL = 15.0
        private const val START_LATITUDE = 49.0135165
        private const val START_LONGITUDE = 8.4018601
        private const val PERSON_SIZE = 48
        private const val PERSON_ANCHOR = 0.2f
    }

    fun onSearchClicked() {
        val searchView = requireView().findViewById<SearchView>(R.id.searchView)
        val searchQuery = searchView.query.toString()


        if (searchQuery.isEmpty()) {
            Toast.makeText(context, requireContext().getString(R.string.search_empty_message), Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val locations = withContext(Dispatchers.IO) {
                    geocoder?.getFromLocationName(searchQuery, 1)
                }

                locations?.firstOrNull()?.let { location ->
                    val searchPoint = GeoPoint(location.latitude, location.longitude)

                    map.controller.apply {
                        animateTo(searchPoint)
                        zoomTo(DEFAULT_ZOOM_LEVEL)
                    }

                    map.overlays.add(
                        Marker(map).apply {
                            position = searchPoint
                            title = searchQuery
                            snippet = location.getAddressLine(0)
                            icon = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_map_location
                            )
                        }
                    )

                    map.invalidate()
                } ?: run {
                    Toast.makeText(
                        context,
                        requireContext().getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    requireContext().getString(R.string.search_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showAddLocationDialog(context: Context) {
        if (dialogView == null) {
            dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_location, null)
        } else {
            resetDialogView()
        }
        val database = AppDatabase.getInstance(context)
        val cancelButton = dialogView?.findViewById<Button>(R.id.cancel_button)
        val addButton = dialogView?.findViewById<Button>(R.id.add_button)
        val categoriesList = dialogView?.findViewById<Spinner>(R.id.categories_list)
        val uploadImageButton = dialogView?.findViewById<Button>(R.id.upload_image_button)
        val uploadedImage = dialogView?.findViewById<ImageView>(R.id.uploaded_image)
        val uploadedImageView = dialogView?.findViewById<ConstraintLayout>(R.id.uploaded_image_view)
        val deleteImageButton = dialogView?.findViewById<ImageButton>(R.id.delete_image_button)
        uploadedImageView?.visibility = View.GONE
        val streetEt = dialogView?.findViewById<EditText>(R.id.street_et)
        val streetNumEt = dialogView?.findViewById<EditText>(R.id.number_et)
        val zipCodeEt = dialogView?.findViewById<EditText>(R.id.zip_code_et)
        val cityEt = dialogView?.findViewById<EditText>(R.id.city_et)
        val titleEt = dialogView?.findViewById<EditText>(R.id.title_et)
        val description = dialogView?.findViewById<EditText>(R.id.description_et)

        uploadImageButton?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }

        deleteImageButton?.setOnClickListener {
            uploadedImage?.setImageDrawable(null)
            uploadedImageView?.visibility = View.GONE
        }

        CoroutineScope(Dispatchers.Main).launch {
            val allCategories = database.interestDao().getAllInterests()
            val categoryNames = allCategories.map { it.name }

            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            categoriesList?.adapter = adapter
        }

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        cancelButton?.setOnClickListener {
            (dialogView?.parent as? ViewGroup)?.removeView(dialogView)
            alertDialog.dismiss()
        }

        addButton?.setOnClickListener {
            val street = streetEt?.text.toString()
            val streetNumber = streetNumEt?.text.toString()
            val zipCode = zipCodeEt?.text.toString()
            val city = cityEt?.text.toString()
            val title = titleEt?.text.toString()

            var isValid = true

            fun highlightEditText(editText: EditText?, highlight: Boolean) {
                if (highlight) {
                    editText?.apply {
                        setTypeface(typeface, Typeface.BOLD)
                        setHintTextColor(ContextCompat.getColor(context, R.color.error_hint_color))
                    }
                } else {
                    editText?.apply {
                        setTypeface(typeface, Typeface.NORMAL)
                        setHintTextColor(ContextCompat.getColor(context, R.color.normal_hint_color))
                    }
                }
            }

            if (title.isBlank()) {
                titleEt?.apply {
                    error = "Titel darf nicht leer sein"
                    highlightEditText(this, true)
                }
                isValid = false
            } else {
                highlightEditText(titleEt, false)
            }

            val addressFields = listOf(streetEt, streetNumEt, zipCodeEt, cityEt)
            addressFields.forEach { editText ->
                if (editText?.text.toString().isBlank()) {
                    highlightEditText(editText, true)
                    editText?.error = "Adresse darf nicht leer sein."
                    isValid = false
                } else {
                    highlightEditText(editText, false)
                }
            }

            if (!isValid) {
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val descriptionText = description?.text.toString()
                val selectedCategory = categoriesList?.selectedItem?.toString()

                val uploadedImageDrawable = uploadedImage?.drawable
                val base64Image = if (uploadedImageDrawable is BitmapDrawable) {
                    val bitmap = uploadedImageDrawable.bitmap
                    Base64.encodeImageToBase64(bitmap)
                } else {
                    null
                }

                val addressId = database.addressDao().insert(Address(street, zipCode, streetNumber, city))
                val location = Location(
                    Preferences.getUserId(requireContext()),
                    title,
                    addressId,
                    descriptionText,
                    base64Image ?: Base64.getFromAssets(context, "sample_bar.jpg"),
                )

                val locationId = database.locationDao().insert(location)
                database.interestDao().getInterestIdByName(selectedCategory ?: "")?.let { id ->
                    database.locationInterestDao().insert(LocationInterest(locationId, id))
                }
                Toast.makeText(requireContext(), "Location erflogreich hinzugefügt", Toast.LENGTH_SHORT).show()
            }
            (dialogView?.parent as? ViewGroup)?.removeView(dialogView)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun resetDialogView() {
        dialogView?.findViewById<EditText>(R.id.street_et)?.setText("")
        dialogView?.findViewById<EditText>(R.id.number_et)?.setText("")
        dialogView?.findViewById<EditText>(R.id.zip_code_et)?.setText("")
        dialogView?.findViewById<EditText>(R.id.city_et)?.setText("")
        dialogView?.findViewById<EditText>(R.id.title_et)?.setText("")
        dialogView?.findViewById<EditText>(R.id.description_et)?.setText("")

        dialogView?.findViewById<Spinner>(R.id.categories_list)?.setSelection(0)
        dialogView?.findViewById<ImageView>(R.id.uploaded_image)?.setImageDrawable(null)

        val editTexts = listOf<EditText?>(
            dialogView?.findViewById(R.id.street_et),
            dialogView?.findViewById(R.id.number_et),
            dialogView?.findViewById(R.id.zip_code_et),
            dialogView?.findViewById(R.id.city_et),
            dialogView?.findViewById(R.id.title_et)
        )

        editTexts.forEach { editText ->
            editText?.error = null

            editText?.setTypeface(null, Typeface.NORMAL)
            editText?.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.normal_hint_color))
        }
    }

}
