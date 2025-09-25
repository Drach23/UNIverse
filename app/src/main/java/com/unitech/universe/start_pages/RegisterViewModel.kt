package com.unitech.universe.start_pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance();
    private val db = FirebaseFirestore.getInstance();

    //Sealed class para representa estados de la aplicacion
    sealed class RegisterState {
        object Loading : RegisterState()
        data class Success(val message: String) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun registerUser(user: User, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _registerState.value = RegisterState.Error(
                "Por favor, ingresa un correo electrónico y una contraseña."

            );
            return
        }
        if (!isValidEmail(email)) {
            _registerState.value = RegisterState.Error(
                "No es un correo con dominio UDG"
            )
            return
        }
        if (!isValidPassword(password)) {
            _registerState.value = RegisterState.Error(
                "La contraseña debe tener al menos 8 caracteres, " +
                        "una mayúscula, una minúscula, un número y un carácter especial."
            )
            return
        }

        if(user.firstName.isEmpty() || user.lastName.isEmpty()){
            _registerState.value = RegisterState.Error(
                "Por favor, ingresa tu nombre y/o apellido"
            )
            return
        }
        if(user.phone.isEmpty()){
            _registerState.value = RegisterState.Error(
                "Por favor, ingresa tu número de teléfono"
            )
            return
        }
        if(user.username.isEmpty()){
            _registerState.value = RegisterState.Error(
                "Por favor, ingresa tu nombre de usuario"
            )
            return
        }

        if (user.gender == "Selecciona tu género") {
            _registerState.value = RegisterState.Error(
                "Por favor, selecciona un género válido"
            )
            return
        }

        _registerState.value = RegisterState.Loading

        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        saveUserToFirestore(it.uid, user);
                    } ?: run {
                        _registerState.value = RegisterState.Error(
                            "Error al obtener el usuario actual"
                        )
                    }
                }else{
                    _registerState.value = RegisterState.Error(
                        "Error al registrar: ${task.exception?.message}"
                    )
                }
            }
    }

    private fun saveUserToFirestore(userId: String, user: User) {
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                _registerState.value = RegisterState.Success("Registro exitoso")
            }
            .addOnFailureListener {
                _registerState.value = RegisterState.Error("Error al registrar: ${it.message}")
            }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("[a-zA-Z0-9._%+-]+@alumnos\\.udg\\.mx")
        return emailPattern.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern =
            Regex(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$"
            )
        return passwordPattern.matches(password);
    }
}