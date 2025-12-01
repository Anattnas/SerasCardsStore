package com.example.serascardsstore.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.serascardsstore.Modelo.ModeloPublicacion
import com.example.serascardsstore.R
import com.example.serascardsstore.databinding.ItemPublicacionBinding
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.app.AlertDialog


class AdaptadorPublicacion(
    private val context: Context,
    private val publicaciones: ArrayList<ModeloPublicacion>,
    private val mostrarBotones: Boolean,
    private val listener: (ModeloPublicacion) -> Unit

) : RecyclerView.Adapter<AdaptadorPublicacion.Holder>() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private lateinit var binding: ItemPublicacionBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        binding = ItemPublicacionBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(binding.root)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val publicacion = publicaciones[position]

        if (!mostrarBotones) {
            binding.btnFavorito.visibility = View.GONE
            binding.btnCarrito.visibility = View.GONE
            binding.checkPublicacion.visibility = View.GONE
            binding.btnEliminarFavoritos.visibility = View.VISIBLE
        } else {
            binding.btnEliminarFavoritos.visibility = View.GONE
        }

        binding.tvNombre.text = publicacion.nombre
        binding.tvPrecio.text = "$${publicacion.precio}"
        binding.tvTcg.text = publicacion.tcg
        binding.tvCondicion.text = publicacion.condicion

        // Cargar primera imagen
        if (publicacion.imagenes.isNotEmpty()) {
            Glide.with(context)
                .load(publicacion.imagenes[0])
                .placeholder(R.drawable.agregar_img)
                .into(binding.ivImagen)
        }

        // CLICK EN TODA LA PUBLICACIÓN
        holder.itemView.setOnClickListener {
            listener(publicacion)
        }

        // BOTÓN FAVORITOS
        binding.btnFavorito.setOnClickListener {
            val uid = firebaseAuth.uid ?: return@setOnClickListener
            val refFav = database.child("MisFavoritos").child(uid).child(publicacion.id)

            refFav.setValue(publicacion)
                .addOnSuccessListener {
                    android.widget.Toast.makeText(context, "Agregado a Favoritos", android.widget.Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    android.widget.Toast.makeText(context, "Error al guardar favorito", android.widget.Toast.LENGTH_SHORT).show()
                }
        }

        // BOTÓN CARRITO
        binding.btnCarrito.setOnClickListener {
            val uid = firebaseAuth.uid ?: return@setOnClickListener
            val refCompra = database.child("MisCompras").child(uid).child(publicacion.id)

            refCompra.setValue(publicacion)
                .addOnSuccessListener {
                    android.widget.Toast.makeText(context, "Agregado al Carrito", android.widget.Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    android.widget.Toast.makeText(context, "Error al guardar en carrito", android.widget.Toast.LENGTH_SHORT).show()
                }
        }

        // CHECK
        binding.checkPublicacion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(context, "Check exitoso", Toast.LENGTH_SHORT).show()
            }
        }

        // BOTÓN ELIMINAR DE FAVORITOS (SOLO EN FRAGMENTO FAVORITOS)
        if (!mostrarBotones) {
            binding.btnEliminarFavoritos.visibility = View.VISIBLE

            binding.btnEliminarFavoritos.setOnClickListener {
                val uid = firebaseAuth.uid ?: return@setOnClickListener

                AlertDialog.Builder(context)
                    .setTitle("Eliminar de favoritos")
                    .setMessage("¿Deseas eliminar esta publicación de tus favoritos?")
                    .setPositiveButton("Sí") { _, _ ->

                        val refEliminar = database
                            .child("MisFavoritos")
                            .child(uid)
                            .child(publicacion.id)

                        refEliminar.removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()

                                val pos = holder.adapterPosition
                                if (pos != RecyclerView.NO_POSITION) {
                                    publicaciones.removeAt(pos)
                                    notifyItemRemoved(pos)
                                }

                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

    }


    override fun getItemCount(): Int = publicaciones.size

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
