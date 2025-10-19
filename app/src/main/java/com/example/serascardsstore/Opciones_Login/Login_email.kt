package com.example.serascardsstore.Opciones_Login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.serascardsstore.Mis_Menus.MenuTCG   // Importa tu MenuTCG
import com.example.serascardsstore.R
import com.example.serascardsstore.Registro_email
import com.example.serascardsstore.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth
//import kotlin.io.root
import kotlin.toString

class Login_email : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding

    private lateinit var firebaseAuth: FirebaseAuth // variable para manejar autenticacion con firebase
    private lateinit var progressDialog: ProgressDialog // variable para mostrar un cuadro de dialogo de progreso

    // ---------------------------------------------------------- \\

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance() // inicializa firebaseAuth con la instancia actual de firebase

        progressDialog = ProgressDialog(this) // crea un cuadro de dialogo de progreso en el contexto actual
        progressDialog.setTitle("Espere por favor") // define el titulo del cuadro de dialogo
        progressDialog.setCanceledOnTouchOutside(false) // evita que se cierre al tocar fuera del cuadro

        binding.BtnIngresar.setOnClickListener { // asigna un evento de click al boton ingresar
            validarInfo() // llama al metodo validarInfo cuando el usuario presiona el boton
        }

        binding.TxtRegistrarme.setOnClickListener {
            startActivity(Intent(this@Login_email, Registro_email::class.java))
        }

    }

    private var email = "" // variable para almacenar el email ingresado
    private var password = "" // variable para almacenar el password ingresado
    private fun validarInfo() { // metodo para validar la informacion del formulario
        email = binding.EtEmail.text.toString().trim() // obtiene el texto del campo email y elimina espacios
        password = binding.EtPassword.text.toString().trim() // obtiene el texto del campo password y elimina espacios

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){ // verifica si el email no coincide con el patron de emails validos
            binding.EtEmail.error = "Email Invalido" // muestra un error en el campo email
            binding.EtEmail.requestFocus() // enfoca el campo email
        }
        else if (email.isEmpty()){ // verifica si el email esta vacio
            binding.EtEmail.error = "Ingrese un Email" // muestra un error en el campo email
            binding.EtEmail.requestFocus() // enfoca el campo email
        }
        else if (password.isEmpty()){ // verifica si el password esta vacio
            binding.EtPassword.error = "Ingrese el Password" // muestra un error en el campo password
            binding.EtPassword.requestFocus() // enfoca el campo password
        }
        else{
            loginUsuario() // si todo es correcto entra a loginUsuario
        }
    }

    private fun loginUsuario() { // metodo para iniciar sesion con firebase
        progressDialog.setMessage("Ingresando") // metodo para iniciar sesion con firebase
        progressDialog.show() // muestra el cuadro de dialogo de progreso

        firebaseAuth.signInWithEmailAndPassword(email, password) // intenta iniciar sesion con email y password
            .addOnSuccessListener { // se ejecuta si el inicio de sesion fue exitoso
                progressDialog.dismiss() // cierra el cuadro de dialogo
                startActivity(Intent(this, MenuTCG::class.java)) // abre la actividad principal MenuTCG
                finishAffinity() // cierra la actividad actual y evita volver atras
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show() // muestra mensaje de bienvenida
            }
            .addOnFailureListener { e-> // se ejecuta si ocurre un error al iniciar sesion
                progressDialog.dismiss() // cierra el cuadro de dialogo
                Toast.makeText(this, "No se pudo iniciar sesion debido a ${e.message}", Toast.LENGTH_SHORT).show() // muestra mensaje de error con detalle
            }
    }
}