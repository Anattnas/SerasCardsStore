package com.example.serascardsstore.Mis_Menus.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.serascardsstore.R
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.serascardsstore.databinding.FragmentDigiVenderBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.serascardsstore.Adaptadores.AdaptadorImagenSeleccionada
import com.example.serascardsstore.Modelo.ModeloImagenSeleccionada
import android.app.AlertDialog
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import java.util.UUID
import com.google.firebase.database.FirebaseDatabase




class FragmentDigiVender : Fragment() {

    // Firebase Storage
    private lateinit var storageRef: com.google.firebase.storage.StorageReference

    // Aquí se guardarán las URLs finales de las imágenes
    private val listaUrlsImagenes = ArrayList<String>()

    private var _binding: FragmentDigiVenderBinding? = null
    private val binding get() = _binding!!

    private lateinit var imagenList: ArrayList<ModeloImagenSeleccionada>
    private lateinit var imagenAdapter: AdaptadorImagenSeleccionada

    // ---------------------------
    // GALERÍA
    private val galeriaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val imagenUri = data?.data

            if (imagenUri != null && imagenList.size < 3) {
                imagenList.add(
                    ModeloImagenSeleccionada(
                        UUID.randomUUID().toString(),
                        imagenUri
                    )
                )
                imagenAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Máximo 3 imágenes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------------------------
    // CÁMARA
    private val camaraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data")
            val uri = MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                bitmap as android.graphics.Bitmap,
                "IMG_${System.currentTimeMillis()}",
                null
            )

            if (uri != null && imagenList.size < 3) {
                imagenList.add(
                    ModeloImagenSeleccionada(
                        UUID.randomUUID().toString(),
                        Uri.parse(uri)
                    )
                )
                imagenAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Máximo 3 imágenes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentDigiVenderBinding.inflate(inflater, container, false)
        storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference

        // ---------------------------
        // RECYCLERVIEW DE IMÁGENES
        imagenList = ArrayList()
        imagenAdapter = AdaptadorImagenSeleccionada(requireContext(), imagenList)

        binding.RVImagenes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.RVImagenes.adapter = imagenAdapter

        // ---------------------------
        // DROPDOWN CONDICIÓN
        val opcionesCondicion = arrayOf("Sellado", "Abierto")
        val adapterCondicion = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            opcionesCondicion
        )
        binding.etCondicion.setAdapter(adapterCondicion)

        // ---------------------------
        // DROPDOWN TCG
        val opcionesTCG = arrayOf("Digimon", "Pokemon", "YuGiOh", "Magic")
        val adapterTCG = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            opcionesTCG
        )
        binding.etTCG.setAdapter(adapterTCG)

        // ---------------------------
        // BOTÓN AGREGAR IMAGEN (por ahora solo aviso)
        binding.btnAgregarImagen.setOnClickListener {

            if (imagenList.size >= 3) {
                Toast.makeText(requireContext(), "Solo puedes subir 3 imágenes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val opciones = arrayOf("Tomar Foto", "Elegir de Galería")

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Selecciona una opción")
            builder.setItems(opciones) { _, which ->

                when (which) {
                    0 -> { // CÁMARA
                        val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        camaraLauncher.launch(intentCamara)
                    }

                    1 -> { // GALERÍA
                        val intentGaleria = Intent(Intent.ACTION_PICK)
                        intentGaleria.type = "image/*"
                        galeriaLauncher.launch(intentGaleria)
                    }
                }
            }

            builder.show()
        }


        // ---------------------------
        // BOTÓN CREAR ANUNCIO (aún no guarda)
        binding.btnCrearAnuncio.setOnClickListener {
            if (imagenList.isEmpty()) {
                Toast.makeText(requireContext(), "Agrega al menos una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val listaUris = ArrayList<Uri>()
            for (img in imagenList) {
                img.imagenUri?.let { listaUris.add(it) }
            }
            subirImagenesStorage(listaUris) { urlsFinales ->
                guardarPublicacionEnFirebase(urlsFinales)
            }
        }

        return binding.root
    }

    private fun subirImagenesStorage(listaUris: ArrayList<android.net.Uri>, callback: (ArrayList<String>) -> Unit) {

        listaUrlsImagenes.clear()

        for (uri in listaUris) {
            val nombreImagen = "publicaciones/${System.currentTimeMillis()}.jpg"
            val referenciaImagen = storageRef.child(nombreImagen)

            referenciaImagen.putFile(uri)
                .addOnSuccessListener {
                    referenciaImagen.downloadUrl.addOnSuccessListener { url ->
                        listaUrlsImagenes.add(url.toString())

                        if (listaUrlsImagenes.size == listaUris.size) {
                            callback(listaUrlsImagenes)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun guardarPublicacionEnFirebase(urlsImagenes: ArrayList<String>) {

        val nombre = binding.etNombre.text.toString().trim()
        val precio = binding.etPrecio.text.toString().trim()
        val condicion = binding.etCondicion.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val tcg = binding.etTCG.text.toString().trim()

        if (nombre.isEmpty() || precio.isEmpty() || condicion.isEmpty() || descripcion.isEmpty() || tcg.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Determinar el nodo según el TCG seleccionado
        val nodo = when (tcg) {
            "Digimon" -> "digipublicaciones"
            "Pokemon" -> "pokepublicaciones"
            "YuGiOh" -> "yugipublicaciones"
            "Magic" -> "magicpublicaciones"
            else -> ""
        }

        if (nodo.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona un TCG válido", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference(nodo)
        val idPublicacion = ref.push().key!!

        val datos = HashMap<String, Any>()
        datos["id"] = idPublicacion
        datos["nombre"] = nombre
        datos["precio"] = precio
        datos["condicion"] = condicion
        datos["descripcion"] = descripcion
        datos["tcg"] = tcg
        datos["imagenes"] = urlsImagenes
        datos["tiempo"] = System.currentTimeMillis()

        ref.child(idPublicacion).setValue(datos)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Publicación creada correctamente", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al guardar publicación", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limpiarCampos() {
        binding.etNombre.setText("")
        binding.etPrecio.setText("")
        binding.etCondicion.setText("")
        binding.etDescripcion.setText("")
        binding.etTCG.setText("")

        imagenList.clear()
        imagenAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
