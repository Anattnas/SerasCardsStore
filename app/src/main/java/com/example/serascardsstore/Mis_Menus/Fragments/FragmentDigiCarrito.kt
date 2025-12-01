package com.example.serascardsstore.Mis_Menus.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serascardsstore.Adaptadores.AdaptadorPublicacion
import com.example.serascardsstore.DetallePublicacion
import com.example.serascardsstore.Modelo.ModeloPublicacion
import com.example.serascardsstore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentDigiCarrito : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var adaptador: AdaptadorPublicacion
    private var listaCompras = ArrayList<ModeloPublicacion>()

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_digi_carrito, container, false)

        rv = view.findViewById(R.id.rvMisCompras)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adaptador = AdaptadorPublicacion(
            requireContext(),
            listaCompras,
            "CARRITO",        // Tipo de fragmento: controla visibilidad de botones
            "MisCompras" ,    // Nodo de Firebase desde donde se eliminarán los elementos
        ) { publicacion ->

            val intent = Intent(requireContext(), DetallePublicacion::class.java)
            intent.putExtra("publicacion", publicacion)
            startActivity(intent)
        }

        rv.adapter = adaptador

        cargarCompras()

        return view
    }

    private fun cargarCompras() {
        val uid = firebaseAuth.uid ?: return

        val ref = database.child("MisCompras").child(uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaCompras.clear()
                for (dataSnap in snapshot.children) {
                    val publicacion = dataSnap.getValue(ModeloPublicacion::class.java)
                    publicacion?.let { listaCompras.add(it) }
                }
                adaptador.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // manejar error si se requiere
            }
        })
    }
}
