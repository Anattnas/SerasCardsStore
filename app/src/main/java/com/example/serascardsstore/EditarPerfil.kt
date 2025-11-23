package com.example.serascardsstore

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.serascardsstore.databinding.ActivityEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EditarPerfil : AppCompatActivity() {  // Clase principal de la actividad EditarPerfil

    private lateinit var binding: ActivityEditarPerfilBinding // Permite acceder a las vistas mediante ViewBinding
    private lateinit var firebaseAuth: FirebaseAuth           // Instancia para manejar autenticación con Firebase
    private lateinit var progressDialog: ProgressDialog       // Cuadro de diálogo para mostrar progreso
    private var imageUri: Uri? = null                         // Variable para almacenar la Uri de la imagen tomada con la cámara; puede ser null si no se ha tomado ninguna foto

    override fun onCreate(savedInstanceState: Bundle?) {                  // Metodo principal que se ejecuta al iniciar la actividad
        super.onCreate(savedInstanceState)                                // Llama al metodo de la superclase AppCompatActivity
        setContentView(R.layout.activity_editar_perfil)                   // Asigna el layout XML a la actividad (puede eliminarse si se usa binding.root)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)     // Inicializa el binding con el diseño de la vista
        setContentView(binding.root)                                      // Asigna la vista raíz del binding (forma recomendada con ViewBinding)

        firebaseAuth = FirebaseAuth.getInstance()                         // Obtiene la instancia actual de FirebaseAuth (usuario activo)
        progressDialog = ProgressDialog(this)                    // Crea un ProgressDialog asociado a esta actividad
        progressDialog.setTitle("Por Favor espere")                       // Título del cuadro de diálogo
        progressDialog.setCanceledOnTouchOutside(false)                   // Evita que el usuario cierre el cuadro al tocar fuera de él

        cargarInfo()                                                      // Llama a la función que cargará la información del usuario desde Firebase

        binding.BtnActualizar.setOnClickListener {
            validarInfo()
        }

        binding.FABCambiarImg.setOnClickListener {
            selec_imagen_de()
        }
    }
    private var nombres = ""
    private var f_nac = ""
    private var codigo = ""
    private var telefono = ""

    private fun validarInfo() {
        nombres = binding.EtNombres.text.toString().trim()
        f_nac = binding.EtFNac.text.toString().trim()
        codigo = binding.selectorCod.selectedCountryCodeWithPlus
        telefono = binding.EtTelefono.text.toString().trim()

        if (nombres.isEmpty()){
            Toast.makeText(this, "Ingrese sus nombres", Toast.LENGTH_SHORT).show()
        }else if (f_nac.isEmpty()){
            Toast.makeText(this, "Ingrese su fecha de nacimiento", Toast.LENGTH_SHORT).show()
        }else if (codigo.isEmpty()){
            Toast.makeText(this, "Seleccione un codigo", Toast.LENGTH_SHORT).show()
        }else if (telefono.isEmpty()){
            Toast.makeText(this, "Ingrese su numero de telefono", Toast.LENGTH_SHORT).show()
        }else{
            actualizarInfo()
        }
    }

    private fun actualizarInfo(){
        progressDialog.setMessage("Actualizando Informacion")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["nombres"] = "${nombres}"
        hashMap["fecha_nac"] = "${f_nac}"
        hashMap["codigoTelefono"] = "${codigo}"
        hashMap["telefono"] = "${telefono}"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Informacion Actualizada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función que obtiene los datos del usuario desde Firebase y los muestra en los campos de la pantalla Editar Perfil en tiempo real
    private fun cargarInfo() {                                                             // Función para obtener los datos del usuario desde Firebase y mostrarlos en pantalla
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")           // Referencia a la rama "Usuarios" de la base de datos
        ref.child("${firebaseAuth.uid}")                                       // Se obtiene el nodo del usuario actual usando su UID de Firebase
            .addValueEventListener(object : ValueEventListener {                  // Se agrega un listener para escuchar cambios en tiempo real
                override fun onDataChange(snapshot: DataSnapshot) {                        // Se ejecuta cuando hay cambios o se carga por primera vez
                    val nombres = "${snapshot.child("nombres").value}"              // Obtiene el valor del campo "nombres"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"       // Obtiene la URL de la imagen de perfil
                    val f_nac = "${snapshot.child("fecha_nac").value}"              // Obtiene la fecha de nacimiento
                    val telefono = "${snapshot.child("telefono").value}"            // Obtiene el número de teléfono
                    val codTelefono = "${snapshot.child("codigoTelefono").value}"   // Obtiene el código del país (por ejemplo, +52)

                    // Establecer los valores en los campos del formulario
                    binding.EtNombres.setText(nombres)                      // Muestra el nombre en el campo correspondiente
                    binding.EtFNac.setText(f_nac)                           // Muestra la fecha de nacimiento
                    binding.EtTelefono.setText(telefono)                    // Muestra el teléfono sin el código

                    try {
                        if (!this@EditarPerfil.isDestroyed && !this@EditarPerfil.isFinishing) {     // Verifica que la Activity siga activa (no destruida ni a punto de cerrarse) antes de cargar la imagen
                            Glide.with(this@EditarPerfil)                                  // Usa Glide con el contexto de la Activity (correcto para cargar imágenes en sus vistas)
                                .load(imagen)                                               // Carga la imagen desde la URL obtenida
                                .placeholder(R.drawable.img_perfil)                     // Imagen por defecto si no hay foto disponible
                                .into(binding.imgPerfil)                                     // Coloca la imagen en el ImageView del perfil
                        }
                    } catch (e: Exception) {
                        // Muestra error si Glide falla
                        Toast.makeText(this@EditarPerfil, "${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    try {
                        val codigo = codTelefono.replace("+", "").toInt()   // Elimina el signo "+" del código y lo convierte a entero
                        binding.selectorCod.setCountryForPhoneCode(codigo)                       // Establece el código de país en el selector (por ejemplo, México → 52)
                    } catch (e: Exception) {
                        // Muestra error si hay problema con el código
                        // Toast.makeText(this@EditarPerfil, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) { // Se ejecuta si ocurre un error al leer la base de datos
                    print("ERROR")
                }
            })
    }
    // Permita subir una imagen seleccionada hacia FireBase Storage para que posteriormente se pueda subir a FireBase Database
    private fun subirImagenStorage() {
        progressDialog.setMessage("Subiendo imagen de Storage") // Muestra un mensaje al usuario mientras se sube la imagen
        progressDialog.show() // Abre el cuadro de diálogo de progreso para indicar que el proceso está en curso

        // Detectar con que metodo inicio sesion el usuario
        val provider = firebaseAuth.currentUser?.providerData?.get(1)?.providerId

        // val rutaImagen = "imagenesPerfil/" + firebaseAuth.uid  // Esta línea define la ruta (carpeta) en el Storage donde se guardará la imagen del usuario.

        // Elegir carpeta según el proveedor de acceso
        val rutaImagen = if (provider == "google.com") {
            "imagenesPerfilG/${firebaseAuth.uid}"
        } else {
            "imagenesPerfilC/${firebaseAuth.uid}"
        }
        // val storageReference = FirebaseStorage.getInstance().getReference(rutaImagen) // Obtiene una referencia al lugar donde se almacenará la imagen en Firebase Storage

        // Crear referencia al Storage
        val storage = FirebaseStorage.getInstance().reference

        // Crear carpeta según el proveedor
        val carpetaRef = if (provider == "google.com") {
            storage.child("imagenesPerfilG")
        } else {
            storage.child("imagenesPerfilC")
        }

        // Crear nombre único de imagen
        val nombreImagen = firebaseAuth.uid + ".jpg"
        val imagenRef = carpetaRef.child(nombreImagen)
        // storageReference.putFile(imageUri!!)               // Sube el archivo de imagen (la URI seleccionada previamente) a la ruta definida
            imagenRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    imagenRef.downloadUrl.addOnSuccessListener { uri ->
                        val urlImagenCargada = uri.toString()
                        actualizarImagenBD(urlImagenCargada)
                    }.addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Error al obtener URL: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
    }
    // Función que actualiza la URL de la imagen del usuario en la base de datos
    private fun EditarPerfil.actualizarImagenBD(urlImagenCargada: String) {
        progressDialog.setMessage("Actualizando imagen")    // Cambia el mensaje del ProgressDialog para indicar que se está actualizando la imagen
        progressDialog.show()                               // Muestra el ProgressDialog mientras se realiza la actualización

        val hashMap : HashMap<String, Any> = HashMap()      // Crea un HashMap para almacenar los datos que se van a actualizar en Firebase
        if (imageUri != null){                              // Verifica que exista una imagen seleccionada antes de intentar subirla
            hashMap["urlImagenPerfil"] = urlImagenCargada   // Agrega al HashMap la clave “urlImagenPerfil” con la URL de la imagen subida
        }

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios") // Obtiene una referencia al nodo “Usuarios” en Firebase Realtime Database
        ref.child(firebaseAuth.uid!!)                                // Accede al nodo del usuario actual usando su UID de Firebase
            .updateChildren(hashMap)                                    // Actualiza los datos del usuario con el contenido del HashMap
            .addOnSuccessListener {                                              // Se ejecuta si la actualización en Firebase fue exitosa
                progressDialog.dismiss()                                         // Cierra el ProgressDialog porque la operación terminó correctamente
                Toast.makeText(this, "Su imagen de perfil se ha actualizado", Toast.LENGTH_SHORT).show() // Muestra un mensaje confirmando la actualización
            }
            .addOnFailureListener { e ->                                                                  // Se ejecuta si ocurre un error durante la actualización
                progressDialog.dismiss()                                                                  // Cierra el ProgressDialog aunque haya fallado
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show() // Muestra el mensaje de error en un Toast
            }
    }
    // Función para mostrar un PopUpMenu que permite elegir entre cámara o galería
    private fun selec_imagen_de() {
        val popupMenu = PopupMenu(this, binding.FABCambiarImg)  // Crea un PopupMenu ligado al botón flotante de cambiar imagen

        popupMenu.menu.add(Menu.NONE, 1, 1, "Camara")  // Agrega el primer ítem al menú: "Cámara" con ID 1
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galeria") // Agrega el segundo ítem al menú: "Galería" con ID 2

        popupMenu.show() // Muestra el PopUpMenu en pantalla

        popupMenu.setOnMenuItemClickListener { item ->       // Define qué pasa cuando se selecciona un ítem del menú
            val itemId = item.itemId                         // Obtiene el ID del ítem seleccionado
            if (itemId == 1) {                               // Si el usuario seleccionó "Cámara"
                //Funcionalidad para la camara
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {   // Si la versión de Android es 33 o superior (Tiramisu)
                    concederPermisosCamara.launch(arrayOf(android.Manifest.permission.CAMERA)) // Solicita permiso de cámara
                } else { // Si la versión de Android es menor a 33
                    // Solicita permisos de camara, y de almacenamiento
                    concederPermisosCamara.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            } else if (itemId == 2) { // Si el usuario seleccionó "Galería"
                //Funcionalidad para la galeria
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imagenGaleria() // Llama a la función que abre la galería para seleccionar imagen
                } else {
                    // Solicita permiso de almacenamiento en versiones menores a 33
                    concederPermisosAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            return@setOnMenuItemClickListener true  // Indica que se consumió el click del menú
        }
    }
    // Crea un ActivityResultLauncher que solicita múltiples permisos con un
    // Callback que se ejecuta cuando el usuario responde a la solicitud de permisos
    private val concederPermisosCamara = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
        var concedidoTodos = true                          // Variable que verifica si todos los permisos fueron concedidos
        for (seConcede in resultado.values) {              // Recorre los valores de los permisos solicitados
            concedidoTodos = concedidoTodos && seConcede   // Si algún permiso es falso, concedidoTodos será false
        }
        if (concedidoTodos) {                              // Si todos los permisos fueron concedidos
            imagenCamara()                                 // Llama a la función que abre la cámara
        } else {                                           // Si algún permiso fue denegado
            // Si no, se muestra el siguiente mensaje al usuario
            Toast.makeText(this,"El permiso de la camarao almacenamiento se denegaron",Toast.LENGTH_SHORT).show()
        }
    }
    // Función de extensión para abrir la cámara y guardar la imagen
    private fun EditarPerfil.imagenCamara() {
        val contentValues = ContentValues()                                                                 // Crea un contenedor para metadatos de la imagen
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")                                   // Asigna un título a la imagen
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")                        // Asigna una descripción a la imagen
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) // Inserta la imagen en MediaStore y obtiene su Uri

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)                                        // Crea un Intent para abrir la cámara
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)                                   // Indica dónde guardar la foto capturada
        resultadoCamara_ARL.launch(intent)                                                                  // Lanza la cámara usando ActivityResultLauncher
    }
    // Registrar un ActivityResult para manejar el resultado de la cámara, si se tomo bien o si no
    private val resultadoCamara_ARL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == RESULT_OK) {  // Verifica si la captura de imagen fue exitosa
            subirImagenStorage() // Llamamon a la funcion subirImagenStorage()
            /*try {
                Glide.with(this)                                     // Usar Glide para cargar la imagen en un ImageView
                    .load(imageUri)                                  // Cargar la imagen desde la Uri obtenida de la cámara
                    .placeholder(R.drawable.img_perfil)              // Mostrar imagen por defecto mientras se carga la imagen capturada
                    .into(binding.imgPerfil)                         // Colocar la imagen en el ImageView de perfil
            } catch (e: Exception) {                                 // Captura cualquier excepción al cargar la imagen
            }*/
        } else {
            // Mostrar mensaje si el usuario cancela la captura de la cámara
            Toast.makeText(this, "La captura de imagen se cancelo", Toast.LENGTH_SHORT).show()
        }
    }
    // Registrar el ActivityResult para solicitar permiso de almacenamiento
    private val concederPermisosAlmacenamiento = registerForActivityResult(ActivityResultContracts.RequestPermission()) { esConcedido ->
        if (esConcedido) {      // Verificar si el permiso fue concedido
            imagenGaleria()     // Si se concedió, ejecutar la función para abrir la galería
        } else {                // Si el permiso fue denegado se muestra el mensaje de alerta
            Toast.makeText(this, "El permiso de almacenamiento se denego", Toast.LENGTH_SHORT)
                .show()
        }
    }
    // Función para abrir la galería y seleccionar una imagen
    private fun EditarPerfil.imagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)  // Crear un Intent para seleccionar un ítem del almacenamiento
        intent.type = "image/*"                          // Filtrar para que solo se muestren imágenes
        resultadoGaleria_ARL.launch(intent)              // Lanzar el ActivityResult para obtener el resultado de la selección
    }
    // Registro del ActivityResult para recibir el resultado de la selección de la galería
    private val resultadoGaleria_ARL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == RESULT_OK) {   // Verificar si el usuario seleccionó una imagen correctamente
            val data = resultado.data              // Obtener los datos devueltos por la galería
            imageUri = data!!.data                 // Guardar la URI de la imagen seleccionada en la variable imageUri
            subirImagenStorage()                   // Llamamos a la funcoon subirImagenStorage()
          /*try {                                                     // Intentamos mostrar la imagen usando Glide
                Glide.with(this)                                      // Contexto de la actividad
                    .load(imageUri)                                   // Cargar la imagen desde la URI
                    .placeholder(R.drawable.img_perfil)               // Imagen temporal mientras carga
                    .into(binding.imgPerfil)                          // Mostrar la imagen en el ImageView del perfil
            } catch (e: Exception) {                                  // Capturar cualquier error al cargar la imagen
            }*/
        } else { // Si el usuario canceló la selección se muestra el mensaje de cancelación
            Toast.makeText(this, "La seleccion de imagen se cancelo", Toast.LENGTH_SHORT).show()
        }
    }

} //Llave de cierre de la clase principal




