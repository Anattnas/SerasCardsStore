package com.example.serascardsstore.Modelo

import android.net.Uri

class ModeloImagenSeleccionada {

    var id = ""
    var imagenUri: Uri? = null

    constructor()

    constructor(id: String, imagenUri: Uri?) {
        this.id = id
        this.imagenUri = imagenUri
    }
}
