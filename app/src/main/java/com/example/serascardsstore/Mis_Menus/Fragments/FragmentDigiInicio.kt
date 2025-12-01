package com.example.serascardsstore.Mis_Menus.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.serascardsstore.Adaptadores.AdaptadorPublicacion
import com.example.serascardsstore.DetallePublicacion
import com.example.serascardsstore.Modelo.ModeloPublicacion
import com.example.serascardsstore.R
import com.example.serascardsstore.databinding.FragmentDigiInicioBinding
import com.google.firebase.database.*


private const val ARG_NODO = "nodoTCG"

class FragmentDigiInicio : Fragment() {

    private var nodoTCG: String? = null
    private var _binding: FragmentDigiInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var publicacionList: ArrayList<ModeloPublicacion>
    private lateinit var adaptador: AdaptadorPublicacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nodoTCG = it.getString(ARG_NODO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDigiInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar RecyclerView
        binding.rvPublicaciones.layoutManager = LinearLayoutManager(requireContext())
        publicacionList = ArrayList()
        adaptador = AdaptadorPublicacion(
            requireContext(),
            publicacionList,
            "INICIO",         // Tipo de fragmento: controla visibilidad de botones
            nodoTCG ?: "digipublicaciones" // Nodo real en Firebase para eliminar
        ) { publicacion ->
            val intent = Intent(requireContext(), DetallePublicacion::class.java)
            intent.putExtra("publicacion", publicacion)
            startActivity(intent)
        }


        binding.rvPublicaciones.adapter = adaptador

        // Cargar publicaciones desde Firebase
        cargarPublicaciones()

        // BOTÓN MODIFICAR
        binding.btnModificar.setOnClickListener {
            val seleccionadas = adaptador.getPublicacionesSeleccionadas()

            when {
                seleccionadas.isEmpty() -> {
                    Toast.makeText(requireContext(), "Debe seleccionar una publicación", Toast.LENGTH_SHORT).show()
                }
                seleccionadas.size > 1 -> {
                    Toast.makeText(requireContext(), "Solo debe seleccionar una a la vez", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Correcto", Toast.LENGTH_SHORT).show()
                    // A partir de aqui esta la lógica de modificación

                    val fragmentModificar = FragmentModificar()
                    val bundle = Bundle()
                    bundle.putParcelable("publicacion", seleccionadas[0])
                    fragmentModificar.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.DigiFragmentContainer, fragmentModificar) // Asegúrate que tu Activity tenga un FrameLayout con id fragment_container
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

    }

    private fun cargarPublicaciones() {
        nodoTCG?.let { nodo ->
            val ref = FirebaseDatabase.getInstance().getReference(nodo)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    publicacionList.clear()
                    for (dataSnap in snapshot.children) {
                        val publicacion = dataSnap.getValue(ModeloPublicacion::class.java)
                        publicacion?.let { publicacionList.add(it) }
                    }
                    adaptador.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores si es necesario
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(nodoTCG: String) =
            FragmentDigiInicio().apply {
                arguments = Bundle().apply {
                    putString(ARG_NODO, nodoTCG)
                }
            }
    }
}
