package com.example.serascardsstore.Mis_Menus

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.serascardsstore.Anuncios.CrearAnuncio
import com.example.serascardsstore.Mis_Menus.Fragments.FragmentDigiInicio
import com.example.serascardsstore.Mis_Menus.Fragments.FragmentDigiCarrito
import com.example.serascardsstore.Mis_Menus.Fragments.FragmentDigiVender
import com.example.serascardsstore.Mis_Menus.Fragments.FragmentDigiFavoritos
import com.example.serascardsstore.Mis_Menus.Fragments.FragmentDigiPerfil
import com.example.serascardsstore.Mis_Menus.MenuTCG
import com.example.serascardsstore.R
import com.example.serascardsstore.databinding.ActivityDigiMainBinding
import com.example.serascardsstore.databinding.ActivityMenuTcgBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class DigiMain : AppCompatActivity() {
    private lateinit var binding: ActivityDigiMainBinding    // Variable para manejar el binding de la vista principal (conecta el XML con el código Kotlin)
    private lateinit var firebaseAuth: FirebaseAuth         // Variable para la autenticacion con Firebase
    private lateinit var firebaseStorage: FirebaseStorage   // Variable para la autenticacion con Firebase

    override fun onCreate(savedInstanceState: Bundle?) {    // Metodo principal que se ejecuta al crear la actividad
        super.onCreate(savedInstanceState)
        binding = ActivityDigiMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibe el nodo a mostrar desde MenuTCG
        val nodoSeleccionado = intent.getStringExtra("nodoTCG") ?: "digipublicaciones"


        // Ajustes de ventana para padding automático
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencia al botón
        val backButton = findViewById<ImageButton>(R.id.BtnBackDigi00)
        // -----------------------------
        // Acción para volver a MenuTCG
        backButton.setOnClickListener {
            val intent = Intent(this@DigiMain, MenuTCG::class.java)
            startActivity(intent)
            finish() // opcional, para que al presionar "Atrás" no regrese aquí
        }

        // Mostrar fragmento inicial (Inicio) con el nodo seleccionado
        //verFragment(FragmentDigiInicio.newInstance(nodoSeleccionado, ""))
        verFragment(FragmentDigiInicio.newInstance(nodoSeleccionado))

        // Configurar BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.BottomDigiNV)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Item_digiInicio -> {
                    //verFragment(FragmentDigiInicio.newInstance(nodoSeleccionado, ""))
                    verFragment(FragmentDigiInicio.newInstance(nodoSeleccionado))
                    true
                }
                R.id.Item_digiCarrito -> {
                    verFragment(FragmentDigiCarrito())
                    true
                }
                R.id.Item_digiVender -> {
                    verFragment(FragmentDigiVender())
                    true
                }
                R.id.Item_digiFavoritos -> {
                    verFragment(FragmentDigiFavoritos())
                    true
                }
                R.id.Item_digiPerfil -> {
                    verFragment(FragmentDigiPerfil())
                    true
                }
                else -> false
            }
        }

        //binding.FAB.setOnClickListener {
            //startActivity(Intent(this, CrearAnuncio::class.java))
        //}
    }

        // Función para cambiar fragmentos dinámicamente
        private fun verFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.DigiFragmentContainer, fragment)
                .commit()
        }
}




 /*

-Explicacion boton
findViewById<ImageButton>(R.id.BtnBack)
Conecta el botón que en el layout (activity_digi_main.xml) con el código.
setOnClickListener { ... }
Programa la acción cuando el usuario toca el botón.
Intent(this@DigiMain, MenuTCG::class.java)
Define que al tocarlo se abra la actividad MenuTCG.
finish()
Cierra DigiMain para que no quede en la pila de actividades, evitando que el usuario regrese a ella al presionar “Atrás”.

- Explicacion Fragments
Conservamos tu botón BtnBackDigi00 para volver a MenuTCG.
Agregamos un BottomNavigationView (BottomDigiNV) que detecta qué ítem se toca.
Creamos la función verFragment(fragment) para reemplazar el contenido del contenedor DigiFragmentContainer.
Al abrir DigiMain, se carga FragmentDigiInicio como pantalla inicial.

 */