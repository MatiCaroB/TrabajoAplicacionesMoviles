package com.example.ataprial.ui.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ataprial.data.database.AppDatabase
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.data.model.Usuario
import com.example.ataprial.data.model.UsuarioErrores
import com.example.ataprial.data.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class UsuarioViewModel(application: Application) : AndroidViewModel(application) {
    private val _usuarioLogueado = MutableStateFlow<Usuario?>(null)
    val usuarioLogueado: StateFlow<Usuario?> = _usuarioLogueado.asStateFlow()
    var loginCorreo by mutableStateOf("")
    var loginClave by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)
    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()

    // Estado del formulario
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado.asStateFlow()


    val listaUsuarios: StateFlow<List<Usuario>> = usuarioDao.obtenerTodosLosUsuarios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Actualizar campos del formulario en vivo (Filtra números en el nombre)
    fun onNombreChange(valor: String) {
        val textoSinNumeros = valor.filter { !it.isDigit() }
        _estado.update { it.copy(nombre = textoSinNumeros, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onRolChange(valor: String) {
        _estado.update { it.copy(rol = valor, errores = it.errores.copy(rol = null)) }
    }

    // Validar entradas
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val nombreError = when {
            estadoActual.nombre.isBlank() -> "Campo obligatorio"
            estadoActual.nombre.any { it.isDigit() } -> "El nombre no puede contener números"
            else -> null
        }

        val errores = UsuarioErrores(
            nombre = nombreError,
            correo = if (!estadoActual.correo.contains("@")) "Correo inválido" else null,
            clave = if (estadoActual.clave.length < 6) "Contraseña de al menos 6 caracteres" else null,
            rol = if (estadoActual.rol.isBlank()) "Debe seleccionar un rol" else null
        )

        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(errores.nombre, errores.correo, errores.clave, errores.rol).isEmpty()
    }

    fun iniciarSesion(onSuccess: (Usuario) -> Unit) {
        if (loginCorreo.isBlank() || loginClave.isBlank()) {
            loginError = "Por favor, completa todos los campos"
            return
        }
        viewModelScope.launch {
            // 💡 BUSQUEDA DIRECTA EN SQLITE: Ya no depende de si el Flow de la UI cargó o no
            val usuarioEncontrado = usuarioDao.buscarUsuarioPorCorreo(loginCorreo.trim())

            if (usuarioEncontrado != null && usuarioEncontrado.clave == loginClave) {
                _usuarioLogueado.value = usuarioEncontrado
                loginError = null
                loginCorreo = ""
                loginClave = ""
                onSuccess(usuarioEncontrado)
            } else {
                loginError = "Credenciales incorrectas o usuario no registrado"
            }
        }
    }


    // Cerrar sesión de forma segura
    fun cerrarSesion(onNavigateOut: () -> Unit) {
        _usuarioLogueado.value = null
        onNavigateOut()
    }


    // 💾 GUARDAR EN SQLITE (Modificado para permitir autoregistro)
    fun registrarUsuario(): Boolean {
        if (!validarFormulario()) return false


        val actualUsuario = _usuarioLogueado.value
        val esAutoregistro = actualUsuario == null
        val esJefe = actualUsuario?.rol == TipoUsuario.JEFATURA

        if (!esAutoregistro && !esJefe) {

            return false
        }

        val estadoActual = _estado.value
        val tipoUsuario = runCatching { TipoUsuario.valueOf(estadoActual.rol) }
            .getOrDefault(TipoUsuario.OPERARIO)

        val nuevoUsuario = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = estadoActual.nombre,
            correo = estadoActual.correo,
            clave = estadoActual.clave,
            rol = tipoUsuario
        )

        // Lanzamos la Coroutine para escribir de forma asíncrona en SQLite mediante Room
        viewModelScope.launch {
            usuarioDao.insertarUsuario(nuevoUsuario)
        }

        // Limpiar los campos del formulario de la interfaz
        _estado.update { UsuarioUiState() }
        return true
    }


    // ELIMINAR DE SQLITE (Protegido por Rol desde el ViewModel xd)
    fun eliminarUsuario(usuario: Usuario, rolDelEjecutor: TipoUsuario) {
        //Solo JEFATURA puede gatillar la eliminación física
        if (rolDelEjecutor == TipoUsuario.JEFATURA) {
            viewModelScope.launch {
                usuarioDao.eliminarUsuario(usuario)
            }
        }
    }
}
