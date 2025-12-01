package com.example.serascardsstore.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.serascardsstore.Modelo.ModeloImagenSeleccionada
import com.example.serascardsstore.R
import com.example.serascardsstore.databinding.ItemImagenSeleccionadaBinding

class AdaptadorImagenSeleccionada(
    private val context: Context,
    private val imagenList: ArrayList<ModeloImagenSeleccionada>
) : RecyclerView.Adapter<AdaptadorImagenSeleccionada.HolderImagen>() {

    private lateinit var binding: ItemImagenSeleccionadaBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagen {
        binding = ItemImagenSeleccionadaBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return HolderImagen(binding.root)
    }

    override fun onBindViewHolder(holder: HolderImagen, position: Int) {
        val modelo = imagenList[position]

        Glide.with(context)
            .load(modelo.imagenUri)
            .placeholder(R.drawable.agregar_img)
            .into(holder.itemImagen)

        holder.btnCerrar.setOnClickListener {
            imagenList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = imagenList.size

    inner class HolderImagen(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImagen = binding.itemImagen
        val btnCerrar = binding.cerrarItem
    }
}
