package com.example.serascardsstore.Menu_Hamburgesa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.serascardsstore.R  // Importamos R del paquete raíz, debido a que android no encontraba la referencia "R"
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.serascardsstore.databinding.ActivityMapaUbicacionBinding
import com.google.firebase.database.FirebaseDatabase


class MapaUbicacion : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapaUbicacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapaUbicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtenemos el fragmento del mapa desde el XML y lo vinculamos al callback OnMapReady
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Se llama cuando el mapa ya está listo para usarse
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Coordenadas de Hermosillo, Sonora
        val hermosillo = LatLng(29.121807974969588, -110.9914956134914)
        mMap.addMarker(MarkerOptions().position(hermosillo).title("Seras Cards Store"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hermosillo, 16f)) // Zoom más cercano

        // Llamado de la funcion para guardar ubicacion en firebase
        guardarUbicacionEnFirebase(hermosillo.latitude, hermosillo.longitude)

    }

    // Funcion para guardar la ubicacion en firebase en un nodo si este no existe ya
    private fun guardarUbicacionEnFirebase(lat: Double, lng: Double) {
        // Referencia a la base de datos de Firebase
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("UbicacionTienda")

        // Comprobamos si ya existe la ubicación en Firebase
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Si ya existe, no se sobreescribe
                println("La ubicación ya existe en Firebase, no se modificará.")
            } else {
                // Si no existe, se crea con las coordenadas nuevas
                val ubicacion = mapOf(
                    "latitud" to lat,
                    "longitud" to lng
                )

                ref.setValue(ubicacion)
                    .addOnSuccessListener {
                        println("Ubicación guardada correctamente en Firebase.")
                    }
                    .addOnFailureListener { e ->
                        println("Error al guardar la ubicación: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            println("Error al verificar la ubicación: ${e.message}")
        }
    }



}
