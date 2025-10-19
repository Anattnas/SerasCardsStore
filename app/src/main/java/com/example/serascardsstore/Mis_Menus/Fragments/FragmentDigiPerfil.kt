package com.example.serascardsstore.Mis_Menus.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.serascardsstore.Constantes
import com.example.serascardsstore.EditarPerfil
import com.example.serascardsstore.OpcionesLogin
import com.example.serascardsstore.R
import com.example.serascardsstore.databinding.FragmentDigiPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
class FragmentDigiPerfil : Fragment() {

    private lateinit var binding: FragmentDigiPerfilBinding  // referencia al binding del layout del fragmento
    private lateinit var firebaseAuth: FirebaseAuth  // referencia a la autenticacion de Firebase
    private lateinit var mContext: Context // variable para almacenar el contexto del fragmento

    // ------------------------------------------------------------------------------------------------------------- //

    override fun onAttach(context: Context) {
        mContext = context   // asigna el contexto recibido al fragmento
        super.onAttach(context)  // llama al metodo padre
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {  // metodo llamado despues de que la vista fue creada
        super.onViewCreated(view, savedInstanceState)  // llama al metodo de la clase padre

        firebaseAuth = FirebaseAuth.getInstance()  // obtiene la instancia de FirebaseAuth

        leerInfo() // llama a la función que obtiene la información del usuario

        binding.BtnEditarPerfil.setOnClickListener {  // Asigna un listener al botón "Editar Perfil"
            startActivity(Intent(mContext, EditarPerfil::class.java))  // Inicia la actividad EditarPerfil al hacer clic
        }

        binding.BtnCerrarSesion.setOnClickListener {  // define el click listener del boton cerrar sesion
            // aqui se agregara la logica para cerrar sesion
            firebaseAuth.signOut()  // cierra la sesion en Firebase
            startActivity(Intent(mContext, OpcionesLogin::class.java))  // abre la actividad de login
            activity?.finishAffinity()  // cierra la actividad actual y las anteriores
        }
    }

    private fun leerInfo() {                                                                // función para leer datos de Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")            // referencia a la tabla "Usuarios"
        ref.child("${firebaseAuth.uid}")                                        // accede al nodo del usuario actual
            .addValueEventListener(object : ValueEventListener {                  // escucha cambios en los datos
                override fun onDataChange(snapshot: DataSnapshot) {                        // llamado cuando hay datos
                    val nombres = "${snapshot.child("nombres").value}"              // obtiene el nombre
                    val email = "${snapshot.child("email").value}"                  // obtiene el email
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"       // obtiene la URL de la imagen de perfil
                    val f_nac = "${snapshot.child("fecha_nac").value}"              // obtiene la fecha de nacimiento
                    var tiempo = "${snapshot.child("tiempo").value}"                // obtiene tiempo de registro (timestamp)
                    val telefono = "${snapshot.child("telefono").value}"            // obtiene teléfono
                    val codTelefono = "${snapshot.child("codigoTelefono").value}"   // obtiene código telefónico
                    val proveedor = "${snapshot.child("proveedor").value}"          // obtiene proveedor de cuenta (Email/Google)

                    val cod_tel = codTelefono + telefono  // concatena lada + número

                    if (tiempo == "null"){  // valida si tiempo es null
                        tiempo = "0"  // asigna valor por defecto
                    }

                    val for_tiempo = Constantes.obtenerFecha(tiempo.toLong())     // convierte timestamp a fecha legible

                    binding.TvEmail.text = email                                           // asigna email al TextView
                    binding.TvNombres.text = nombres                                       // asigna nombres al TextView
                    binding.TvNacimiento.text = f_nac                                      // asigna fecha de nacimiento
                    binding.TvTelefono.text = cod_tel                                      // asigna teléfono completo
                    binding.TvMiembro.text = for_tiempo                                    // asigna fecha de registro formateada

                    try {
                        Glide.with(mContext)                                            // inicia Glide con contexto del fragment
                            .load(imagen)                                       // carga la imagen desde URL
                            .placeholder(R.drawable.img_perfil)             // imagen temporal mientras carga
                            .into(binding.TvPerfil)                              // asigna imagen al ImageView
                    }catch (e: Exception){                                              // captura errores al cargar la imagen
                        Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show() // muestra mensaje de error
                    }

                    if (proveedor == "Email"){                                          // si el proveedor es Email
                        val esVerificado = firebaseAuth.currentUser!!.isEmailVerified   // verifica si el correo está validado
                        if (esVerificado){
                            binding.TvEstadoCuenta.text = "Verificado"                  // muestra "Verificado"
                        }else{
                            binding.TvEstadoCuenta.text = "No Verificado"               // muestra "No Verificado"
                        }
                    }else{
                        binding.TvEstadoCuenta.text = "Verificado"                      // cualquier otro proveedor se considera verificado
                    }
                }

                override fun onCancelled(error: DatabaseError) {                        // llamado si hay error en la base de datos
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDigiPerfilBinding.inflate(layoutInflater, container, false) // infla el layout usando binding
        return binding.root                                                                                   // devuelve la vista raiz del layout inflado
    }
}