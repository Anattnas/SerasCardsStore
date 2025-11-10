package com.example.serascardsstore.Mis_Menus

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.serascardsstore.OpcionesLogin
import com.example.serascardsstore.R
import com.example.serascardsstore.databinding.ActivityMainBinding
import com.example.serascardsstore.databinding.ActivityMenuTcgBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import android.widget.PopupMenu
import android.widget.Toast
import com.example.serascardsstore.Menu_Hamburgesa.MapaUbicacion


class MenuTCG : AppCompatActivity() {
    // Variable para manejar el binding de la vista principal (conecta el XML con el código Kotlin)
    private lateinit var binding: ActivityMenuTcgBinding
    // Variable para la autenticacion con Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    // ------------------------------------------------------------------ \\

    // Metodo principal que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Vinculamos la vista con el archivo XML mediante view binding
        binding = ActivityMenuTcgBinding.inflate(layoutInflater)
        // Establecemos el layout raíz como contenido de la pantalla
        setContentView(binding.root)

        // Ajuste de padding automático para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializamos FirebaseAuth con la instancia actual de Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        // Comprobamos si el usuario tiene sesión iniciada
        comprobarSesion()


        // Referencia al botón DigitTCG
        val digitButton = findViewById<ImageButton>(R.id.btnDigit)
        digitButton.setOnClickListener {   // Listener para abrir DigiMain
            val intent = Intent(this@MenuTCG, DigiMain::class.java)
            startActivity(intent)
        }
        // Referencia al botón YugiTCG
        val yugiButton = findViewById<ImageButton>(R.id.btnYugi)
        yugiButton.setOnClickListener {   // Listener para abrir DigiMain
            val intent = Intent(this@MenuTCG, DigiMain::class.java)
            startActivity(intent)
        }
        // Referencia al botón pokeTCG
        val pokeButton = findViewById<ImageButton>(R.id.btnPoke)
        pokeButton.setOnClickListener {   // Listener para abrir DigiMain
            val intent = Intent(this@MenuTCG, DigiMain::class.java)
            startActivity(intent)
        }
        // Referencia al botón magicTCG
        val magicButton = findViewById<ImageButton>(R.id.btnMagic)
        magicButton.setOnClickListener {   // Listener para abrir DigiMain
            val intent = Intent(this@MenuTCG, DigiMain::class.java)
            startActivity(intent)
        }

        // Referencia al botón del menú hamburgesa
        val menuButton = findViewById<ImageButton>(R.id.btnMenuHamburguesa)

        menuButton.setOnClickListener {                                         // Cuando el usuario presione el botón del menú
            val popupMenu = PopupMenu(this, menuButton)       // Crear el PopupMenu y enlazarlo al botón
            popupMenu.menuInflater.inflate(R.menu.menu_hamburgesa, popupMenu.menu)  // Inflar (cargar) el menú desde res/menu/menu_hamburgesa.xml
            popupMenu.show()                                                    // Mostrar el menú

            popupMenu.setOnMenuItemClickListener { item ->                      // Escuchar las opciones seleccionadas
                when (item.itemId) {
                    R.id.opcion_ubicacion -> {
                        val intent =
                            Intent(this, MapaUbicacion::class.java)  // Abrir la actividad del mapa
                        startActivity(intent)
                        true
                    }
                    R.id.opcion_contacto -> {
                    Toast.makeText(this, "Opción de contacto seleccionada", Toast.LENGTH_SHORT).show()
                    true
                    }else -> false
                }
            }
        }


    }

    // Función que comprueba si existe un usuario con sesión activa
    private fun comprobarSesion(){
        // Verificamos si el usuario actual de Firebase es nulo (es decir, nadie ha iniciado sesión)
        if (firebaseAuth.currentUser == null){
            // Si no hay usuario, se redirige a la actividad de OpcionesLogin
            startActivity(Intent(this, OpcionesLogin::class.java))
            // Cerramos todas las actividades anteriores para evitar que el usuario regrese con "back"
            finishAffinity()
        }
    }
}