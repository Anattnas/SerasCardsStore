package com.example.serascardsstore.Mis_Menus.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.serascardsstore.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serascardsstore.Adaptadores.AdaptadorPublicacion
import com.example.serascardsstore.DetallePublicacion
import com.example.serascardsstore.Modelo.ModeloPublicacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class FragmentDigiFavoritos : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var adaptador: AdaptadorPublicacion
    private var listaFavoritos = ArrayList<ModeloPublicacion>()
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_digi_favoritos, container, false)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance()
            .getReference("MisFavoritos")
            .child(auth.currentUser!!.uid)

        rv = view.findViewById(R.id.rvMisFavoritos)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adaptador = AdaptadorPublicacion(
            requireContext(),
            listaFavoritos,
            false   // NO mostrar botones en Favoritos
        ) { publicacion ->
            // Luego abrimos el detalle

            val intent = Intent(requireContext(), DetallePublicacion::class.java)
            intent.putExtra("publicacion", publicacion)
            startActivity(intent)
        }


        rv.adapter = adaptador

        cargarMisFavoritos()

        return view

    }

    private fun cargarMisFavoritos() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaFavoritos.clear()

                for (dato in snapshot.children) {
                    val publicacion = dato.getValue(ModeloPublicacion::class.java)
                    if (publicacion != null) {
                        listaFavoritos.add(publicacion)
                    }
                }

                adaptador.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object {

    }
}