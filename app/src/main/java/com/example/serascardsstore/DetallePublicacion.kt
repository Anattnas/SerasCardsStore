package com.example.serascardsstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.serascardsstore.Adaptadores.AdaptadorImagenDetalle
import com.example.serascardsstore.Modelo.ModeloPublicacion
import com.example.serascardsstore.databinding.ActivityDetallePublicacionBinding

class DetallePublicacion : AppCompatActivity() {

    private lateinit var binding: ActivityDetallePublicacionBinding
    private lateinit var adaptadorImagen: AdaptadorImagenDetalle
    private lateinit var publicacion: ModeloPublicacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallePublicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibir publicación
        publicacion = intent.getParcelableExtra("publicacion")!!

        // Adaptador de imágenes
        adaptadorImagen = AdaptadorImagenDetalle(this, publicacion.imagenes)
        binding.viewPagerImagenes.adapter = adaptadorImagen
    }
}

