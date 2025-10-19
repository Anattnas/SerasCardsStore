package com.example.serascardsstore

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.serascardsstore.Mis_Menus.MenuTCG
import com.example.serascardsstore.Opciones_Login.Login_email
import com.example.serascardsstore.databinding.ActivityOpcionesLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class OpcionesLogin : AppCompatActivity() {

    private lateinit var binding: ActivityOpcionesLoginBinding
    // Variable para manejar la autenticacion con Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mGooleSigninInClient: GoogleSignInClient // variable para manejar el login con cuentas de Google
    private lateinit var progressDialog: ProgressDialog // variable para mostrar barra de progreso en pantalla

    // ------------------------------------------------------------------- \\

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this) // inicializa el progressDialog en la actividad actual
        progressDialog.setTitle("Espere por favor") // establece el titulo que se mostrara en el dialogo
        progressDialog.setCanceledOnTouchOutside(false) // evita que el dialogo se cierre al tocar fuera de el

        // Inicializamos FirebaseAuth con la instancia actual
        firebaseAuth = FirebaseAuth.getInstance()
        // Llamamos a la funcion que comprueba la sesion del usuario
        comprobarSesion()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // construye las opciones de inicio de sesion con Google usando el modo por defecto
            .requestIdToken(getString(R.string.default_web_client_id)) // solicita el idToken necesario para autenticar en Firebase usando el client_id guardado en strings.xml
            .requestEmail() // solicita acceso al email de la cuenta seleccionada
            .build() // finaliza la construccion del objeto de configuracion

        mGooleSigninInClient = GoogleSignIn.getClient(this, gso) // inicializa el cliente de GoogleSignIn con la configuracion definida

        binding.IngresarGoogle.setOnClickListener { // evento que se activa al presionar el boton "Ingresar con Google"
            googleLogin() // llama a la funcion googleLogin para iniciar el proceso de autenticacion con Google
        }

        binding.IngresarEmail.setOnClickListener {
            startActivity(Intent(this@OpcionesLogin, Login_email::class.java))
        }
    }
    private fun googleLogin(){ // funcion para iniciar el proceso de login con Google
        val googleSignInIntent = mGooleSigninInClient.signInIntent // crea un intent con la configuracion del cliente de Google
        googleSignInARL.launch(googleSignInIntent) // lanza el intent para mostrar las cuentas de Google disponibles en el dispositivo
    }
    // propiedad para manejar el resultado del intent de Google
    private val googleSignInARL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado->
        if (resultado.resultCode == RESULT_OK){ // verifica si el resultado fue exitoso
            val data = resultado.data // obtiene los datos regresados por el intent
            val task = GoogleSignIn.getSignedInAccountFromIntent(data) // obtiene la cuenta seleccionada de Google a partir de los datos
            try {
                val cuenta = task.getResult(ApiException::class.java) // intenta obtener la cuenta de Google validando si hubo error
                autenticacionGoogle(cuenta.idToken) // envia el idToken de la cuenta para autenticarse en Firebase
            }catch (e: Exception){
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show() // muestra un mensaje en caso de error
            }
        }
    }
    private fun autenticacionGoogle(idToken: String?) { // funcion para autenticar al usuario con Firebase usando el token de Google
        val credential = GoogleAuthProvider.getCredential(idToken, null) // crea una credencial de Firebase a partir del idToken recibido
        firebaseAuth.signInWithCredential(credential) // inicia sesion en Firebase con la credencial
            .addOnSuccessListener { resultadoAuth-> // si la autenticacion fue exitosa
                if (resultadoAuth.additionalUserInfo!!.isNewUser) { // verifica si el usuario es nuevo en la base de datos
                    llenarInfoBD() // si es nuevo, llama a la funcion para guardar su informacion en la base de datos
                }else{
                    actualizarInfoBD() // Siempre actualizar información, aunque el usuario ya exista
                }
            }
            .addOnFailureListener { e-> // si ocurre un error durante la autenticacion
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show() // muestra el mensaje de error en pantalla
            }
    }
    private fun actualizarInfoBD() {
        progressDialog.setMessage("Actualizando información")
        progressDialog.show()

        val tiempo = Constantes.obtenerTiempoDis()
        val emailUsuario = firebaseAuth.currentUser!!.email
        val uidUsuario = firebaseAuth.uid
        val nombreUsuario = firebaseAuth.currentUser?.displayName

        // Solo actualizamos los campos necesarios
        val hashMap = HashMap<String, Any>()
        hashMap["tiempo"] = tiempo
        hashMap["nombres"] = nombreUsuario ?: ""
        hashMap["email"] = emailUsuario ?: ""
        hashMap["online"] = true
        hashMap["uid"] = uidUsuario ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidUsuario!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MenuTCG::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al actualizar info: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun llenarInfoBD() { // funcion para guardar informacion de un usuario nuevo en la base de datos
        progressDialog.setMessage("Guardando Informacion") // cambia el mensaje del cuadro de progreso

        val tiempo = Constantes.obtenerTiempoDis() // obtiene el tiempo actual desde la clase Constantes
        val emailUsuario = firebaseAuth.currentUser!!.email // obtiene el email del usuario autenticado
        val uidUsuario = firebaseAuth.uid // obtiene el uid unico del usuario en Firebase
        val nombreUsuario = firebaseAuth.currentUser?.displayName // obtiene el nombre mostrado del usuario

        val hashMap = HashMap<String, Any>()     // crea un hashmap para almacenar los datos del usuario
        hashMap["nombres"] = "${nombreUsuario}"  // guarda el nombre del usuario
        hashMap["codigoTelefono"] = ""           // inicializa vacio el codigo de telefono
        hashMap["telefono"] = ""                 // inicializa vacio el telefono
        hashMap["urlImagenPerfil"] = ""          // inicializa vacio la url de la imagen de perfil
        hashMap["proveedor"] = "Google"          // especifica que el proveedor es Google
        hashMap["escribiendo"] = ""              // inicializa vacio el estado de escritura
        hashMap["tiempo"] = tiempo               // inicializa con el valor de la variable tiempo
        hashMap["online"] = true                 // inicializa con true en conexion
        hashMap["email"] = "${emailUsuario}"     // inicializa con emailUsuario aqui)
        hashMap["uid"] = "${uidUsuario}"         // inicializa con uidUsuario aqui)
        hashMap["fecha_nac"] = ""                // inicializa vacio la fecha de nacimiento

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios") // referencia a la ruta "Usuarios" en la base de datos
        ref.child(uidUsuario!!) // crea un nodo con el uid del usuario
            .setValue(hashMap) // guarda el mapa de datos en ese nodo
            .addOnSuccessListener { // si el guardado fue exitoso
                progressDialog.dismiss() // cierra el cuadro de progreso
                startActivity(Intent(this, MenuTCG::class.java)) // abre la actividad principal
                finishAffinity() // cierra las demas actividades para que no se pueda volver atras
            }
            .addOnFailureListener { exception -> // si ocurre un error
                progressDialog.dismiss() // cierra el cuadro de progreso
                Toast.makeText(this, "No se registro debido a ${exception.message}", Toast.LENGTH_SHORT).show() // muestra el error en pantalla
            }
    }

    // Funcion que comprueba si hay un usuario con sesion activa
    private fun comprobarSesion(){
        // Si el usuario actual de Firebase no es nulo significa que ya inicio sesion
        if (firebaseAuth.currentUser != null){
            // Redirigimos al usuario a MenuTCG
            startActivity(Intent(this, MenuTCG::class.java))
            // Cerramos todas las actividades anteriores
            finishAffinity()
        }
    }
}