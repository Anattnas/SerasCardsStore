package com.example.serascardsstore.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.serascardsstore.R

class AdaptadorImagenDetalle(
    private val context: Context,
    private val imagenes: ArrayList<String>
) : RecyclerView.Adapter<AdaptadorImagenDetalle.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_imagen_detalle, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val url = imagenes[position]

        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.agregar_img)
            .into(holder.imagen)
    }

    override fun getItemCount(): Int = imagenes.size

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.ivDetalleImagen)
    }
}
