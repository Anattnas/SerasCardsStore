package com.example.serascardsstore

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale


//------------------------------------------
// Declara un objeto singleton llamado Constantes.
// Un objeto en Kotlin es similar a una clase que solo tiene una instancia.
// Esto es útil para funciones o variables que queremos que sean globales.
//------------------------------------------
object Constantes {
    // Declara una función llamada obtenerTiempoDis que regresa un valor de tipo Long.
    // Esta función obtiene el tiempo actual en milisegundos.
    fun obtenerTiempoDis(): Long {
        // Llama al metodo System.currentTimeMillis(), que devuelve
        // el número de milisegundos transcurridos desde la medianoche del 1 de enero de 1970.
        // Esto es útil para marcar fechas y horas de forma numérica, por ejemplo en bases de datos.
        //------------------------------------------
        return System.currentTimeMillis()
    }

    fun obtenerFecha(tiempo: Long) : String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy", calendario).toString()
    }
}