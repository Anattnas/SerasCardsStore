package com.example.serascardsstore

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton //-> Imagen para boton retroceso no borrar
import com.example.serascardsstore.Opciones_Login.Login_email
import com.example.serascardsstore.databinding.ActivityRegistroEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


// Clase principal de la actividad de registro de email
// Hereda de AppCompatActivity para tener compatibilidad con Android moderno
class Registro_email : AppCompatActivity() {
    //------------------------------------------
    // binding: permite acceder a las vistas del layout de forma segura
    //------------------------------------------
    private lateinit var binding: ActivityRegistroEmailBinding
    // lateinit: inicialización tardía; la variable será inicializada en onCreate

    //------------------------------------------
    // firebaseAuth: instancia de Firebase para manejar la autenticación de usuarios
    // progressDialog: cuadro de diálogo que muestra progreso mientras se realiza registro
    //------------------------------------------
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    //------------------------------------------
    // onCreate: metodo que se ejecuta al iniciar la actividad
    //------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializamos el binding para enlazar las vistas del layout
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root) // Mostramos el layout en pantalla
        // Inicializamos FirebaseAuth para poder usar sus métodos de registro
        firebaseAuth = FirebaseAuth.getInstance()
        // Configuramos el ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")     // Título del cuadro de diálogo
        progressDialog.setCanceledOnTouchOutside(false) // No se cierra al tocar fuera del diálogo

        //------------------------------------------
        // Listener del botón de registrar
        //------------------------------------------
        binding.BtnRegistrar.setOnClickListener {
            validarInfo()  // Llama a la función que valida los datos del usuario
        }

    // -----------------------------------------------------------------------------------
        // Bloque necesario para el boton de retroceso mantener por ahora.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //---------------------------
        // Botón de retroceso
        //---------------------------\\
        val backButton = findViewById<ImageButton>(R.id.Btn_Back)
        backButton.setOnClickListener {
            finish() // Cierra esta activity y regresa al login
        }
    // -----------------------------------------------------------------------------------
    }
    //------------------------------------------
    // Variables para almacenar los datos ingresados por el usuario
    //------------------------------------------
    private var email = ""      // Email ingresado
    private var password = ""   // Contraseña ingresada
    private var r_password = "" // Confirmación de contraseña

    //------------------------------------------
    // Función para validar los datos del usuario
    //------------------------------------------
    private fun validarInfo() {
        // Obtenemos los datos de las vistas y eliminamos espacios al inicio y final
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()
        r_password = binding.EtRPassword.text.toString().trim()

        //------------------------------------------
        // Validaciones del email y las contraseñas
        //------------------------------------------
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // Si el email no tiene un formato válido, se muestra error en el EditText
            binding.EtEmail.error = "Email Invalido"
            binding.EtEmail.requestFocus() // Coloca el foco en el campo con error
        }
        else if (email.isEmpty()){
            binding.EtEmail.error = "Ingrese un Email"
            binding.EtEmail.requestFocus()
        }
        else if (password.isEmpty()){
            binding.EtPassword.error = "Ingrese el Password"
            binding.EtPassword.requestFocus()
        }
        else if (r_password.isEmpty()){
            binding.EtRPassword.error = "Repita el Password"
            binding.EtRPassword.requestFocus()
        }
        else if (password != r_password){
            binding.EtRPassword.error = "No Coinciden"
            binding.EtRPassword.requestFocus()
        }
        else {
            // Si todas las validaciones pasan, se procede a registrar al usuario
            registrarUsuario()
        }
    }

    //------------------------------------------
    // Función para registrar al usuario en Firebase Authentication
    //------------------------------------------
    private fun registrarUsuario(){
        // Configuramos el mensaje del ProgressDialog y lo mostramos
        progressDialog.setMessage("Creando Cuenta")
        progressDialog.show()

        //------------------------------------------
        // Metodo de Firebase para crear usuario con email y contraseña
        //------------------------------------------
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Si el registro es exitoso, se llama a la función para llenar la BD
                llenarInfoBD()
            }
            .addOnFailureListener { exception ->
                // Si ocurre un error, cerramos el diálogo y mostramos un Toast con el error
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se registro el usuario debido a ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    //------------------------------------------
    // Función para llenar información adicional del usuario en la BD
    //------------------------------------------
    private fun llenarInfoBD() {
        // Configuramos mensaje del ProgressDialog mientras se guarda información
        progressDialog.setMessage("Guardando Informacion")

        // Obtenemos tiempo actual en milisegundos
        val tiempo = Constantes.obtenerTiempoDis()
        // Obtenemos email del usuario registrado
        val emailUsuario = firebaseAuth.currentUser!!.email
        // Obtenemos UID único asignado por Firebase
        val uidUsuario = firebaseAuth.uid

        //------------------------------------------
        // HashMap para almacenar la información del usuario en Firebase
        //------------------------------------------
        val hashMap = HashMap<String, Any>()
        hashMap["nombres"] = ""                  // Nombre del usuario
        hashMap["codigoTelefono"] = ""           // Código de teléfono
        hashMap["telefono"] = ""                 // Número de teléfono
        hashMap["urlImagenPerfil"] = ""          // URL de imagen de perfil
        hashMap["proveedor"] = "Email"           // Proveedor de login (email, Google, etc.)
        hashMap["escribiendo"] = ""              // Estado de “escribiendo” para chat
        hashMap["tiempo"] = tiempo               // inicializa con el valor de la variable tiempo
        hashMap["online"] = true                 // inicializa con true en conexion
        hashMap["email"] = "${emailUsuario}"     // inicializa con emailUsuario aqui)
        hashMap["uid"] = "${uidUsuario}"         // inicializa con uidUsuario aqui)
        hashMap["fecha_nac"] = ""                // inicializa vacio la fecha de nacimiento

        // Se obtiene una referencia a la base de datos de Firebase, específicamente al nodo llamado "Usuarios". Si este nodo (o la base de datos) no existe, Firebase lo creará automáticamente.
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        // Usamos el identificador único (uidUsuario) como hijo dentro de "Usuarios". El operador `!!` asegura que no sea nulo; si es nulo lanzará una excepción. Luego, se establece el valor del nodo con los datos del hashMap.
        ref.child(uidUsuario!!)
            .setValue(hashMap)
            // addOnSuccessListener: Se ejecuta si la operación en la base de datos fue exitosa.
            .addOnSuccessListener {
                // Se cierra el cuadro de diálogo de progreso (progressDialog).
                progressDialog.dismiss()
                // Se inicia la pagina para ingresar correo (para que entre con el regustro nuevo).
                startActivity(Intent(this, Login_email::class.java))
                // Se finalizan todas las actividades anteriores para evitar que el usuario
                finishAffinity()
            }
            // addOnFailureListener: Se ejecuta si ocurre algún error en la operación de la base de datos.
            .addOnFailureListener { exception ->
                // Se cierra el cuadro de diálogo de progreso.
                progressDialog.dismiss()
                // Se muestra un mensaje emergente (Toast) indicando que no se registró
                // e incluyendo el detalle del error recibido en `exception.message`.
                Toast.makeText(
                    this,
                    "No se registro debido a ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}