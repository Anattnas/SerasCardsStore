package com.example.serascardsstore.Modelo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModeloPublicacion(
    var id: String = "",
    var nombre: String = "",
    var precio: String = "",
    var tcg: String = "",
    var condicion: String = "",
    var descripcion: String = "",
    var tiempo: Long = 0,
    var imagenes: ArrayList<String> = arrayListOf()
) : Parcelable
