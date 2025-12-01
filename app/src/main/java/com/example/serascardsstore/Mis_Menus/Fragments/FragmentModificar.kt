package com.example.serascardsstore.Mis_Menus.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.serascardsstore.Adaptadores.AdaptadorImagenSeleccionada
import com.example.serascardsstore.Modelo.ModeloImagenSeleccionada
import com.example.serascardsstore.Modelo.ModeloPublicacion
import com.example.serascardsstore.databinding.FragmentModificarBinding
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import android.app.AlertDialog
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FragmentModificar : Fragment() {

    private var _binding: FragmentModificarBinding? = null
    private val binding get() = _binding!!

    private lateinit var imagenList: ArrayList<ModeloImagenSeleccionada>
    private lateinit var imagenAdapter: AdaptadorImagenSeleccionada
    private lateinit var storageRef: FirebaseStorage

    // Publicación que vamos a modificar
    private lateinit var publicacion: ModeloPublicacion

    // Launcher para cámara y galería
    private val galeriaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val imagenUri = data?.data
            if (imagenUri != null && imagenList.size < 3) {
                imagenList.add(ModeloImagenSeleccionada(UUID.randomUUID().toString(), imagenUri))
                imagenAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Máximo 3 imágenes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val camaraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data")
            val uri = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap as android.graphics.Bitmap, "IMG_${System.currentTimeMillis()}", null)
            if (uri != null && imagenList.size < 3) {
                imagenList.add(ModeloImagenSeleccionada(UUID.randomUUID().toString(), Uri.parse(uri)))
                imagenAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Máximo 3 imágenes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentModificarBinding.inflate(inflater, container, false)
        storageRef = FirebaseStorage.getInstance()

        // Recuperar la publicación desde argumentos
        arguments?.let {
            publicacion = it.getParcelable("publicacion")!!
        }

        // Cargar datos en los campos
        cargarDatosPublicacion()

        // RecyclerView de imágenes
        imagenList = ArrayList()
        publicacion.imagenes.forEach { url ->
            imagenList.add(ModeloImagenSeleccionada(UUID.randomUUID().toString(), Uri.parse(url)))
        }
        imagenAdapter = AdaptadorImagenSeleccionada(requireContext(), imagenList)
        binding.RVImagenes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.RVImagenes.adapter = imagenAdapter

        // Botón agregar imagen
        binding.btnAgregarImagen.setOnClickListener { mostrarOpcionesImagen() }

        // Botón guardar cambios
        binding.btnGuardarCambios.setOnClickListener { guardarCambios() }

        return binding.root
    }

    private fun cargarDatosPublicacion() {
        binding.etNombre.setText(publicacion.nombre)
        binding.etPrecio.setText(publicacion.precio.toString())
        binding.etCondicion.setText(publicacion.condicion)
        binding.etDescripcion.setText(publicacion.descripcion)
        binding.etTCG.setText(publicacion.tcg) // solo lectura
    }

    private fun mostrarOpcionesImagen() {
        if (imagenList.size >= 3) {
            Toast.makeText(requireContext(), "Máximo 3 imágenes", Toast.LENGTH_SHORT).show()
            return
        }

        val opciones = arrayOf("Tomar Foto", "Elegir de Galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una opción")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> { // Cámara
                        val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        camaraLauncher.launch(intentCamara)
                    }
                    1 -> { // Galería
                        val intentGaleria = Intent(Intent.ACTION_PICK)
                        intentGaleria.type = "image/*"
                        galeriaLauncher.launch(intentGaleria)
                    }
                }
            }
            .show()
    }

    private fun guardarCambios() {
        val nombre = binding.etNombre.text.toString().trim()
        val precio = binding.etPrecio.text.toString().trim()
        val condicion = binding.etCondicion.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()

        if (nombre.isEmpty() || precio.isEmpty() || condicion.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Separar URLs existentes y nuevas imágenes
        val urlsExistentes = ArrayList<String>()
        val nuevasUris = ArrayList<Uri>()

        for (img in imagenList) {
            val uriStr = img.imagenUri.toString()
            if (uriStr.startsWith("http://") || uriStr.startsWith("https://")) {
                urlsExistentes.add(uriStr) // Ya estaba en Firebase
            } else {
                img.imagenUri?.let { nuevasUris.add(it) } // Nueva imagen
            }
        }

        if (nuevasUris.isEmpty()) {
            // No hay imágenes nuevas, actualizamos solo con las existentes
            actualizarFirebase(nombre, precio, condicion, descripcion, urlsExistentes)
        } else {
            // Subir solo las nuevas imágenes
            subirImagenesStorage(nuevasUris) { urlsNuevas ->
                val listaFinal = ArrayList<String>()
                listaFinal.addAll(urlsExistentes)
                listaFinal.addAll(urlsNuevas)
                actualizarFirebase(nombre, precio, condicion, descripcion, listaFinal)
            }
        }
    }

    private fun subirImagenesStorage(listaUris: ArrayList<Uri>, callback: (ArrayList<String>) -> Unit) {
        val listaUrlsImagenes = ArrayList<String>()
        for (uri in listaUris) {
            val nombreImagen = "publicaciones/${System.currentTimeMillis()}.jpg"
            val referenciaImagen = storageRef.reference.child(nombreImagen)

            referenciaImagen.putFile(uri)
                .addOnSuccessListener {
                    referenciaImagen.downloadUrl.addOnSuccessListener { url ->
                        listaUrlsImagenes.add(url.toString())
                        if (listaUrlsImagenes.size == listaUris.size) callback(listaUrlsImagenes)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun actualizarFirebase(nombre: String, precio: String, condicion: String, descripcion: String, urlsImagenes: ArrayList<String>) {
        val ref = FirebaseDatabase.getInstance().getReference(getNodoFirebase(publicacion.tcg))
        val datos = HashMap<String, Any>()
        datos["nombre"] = nombre
        datos["precio"] = precio
        datos["condicion"] = condicion
        datos["descripcion"] = descripcion
        datos["imagenes"] = urlsImagenes

        ref.child(publicacion.id).updateChildren(datos)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                // Regresar a FragmentDigiInicio
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al guardar cambios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getNodoFirebase(tcg: String): String {
        return when (tcg) {
            "Digimon" -> "digipublicaciones"
            "Pokemon" -> "pokepublicaciones"
            "YuGiOh" -> "yugipublicaciones"
            "Magic" -> "magicpublicaciones"
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
