package br.unistanta.aplicativoroom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.unistanta.aplicativoroom.database.AppDatabase
import br.unistanta.aplicativoroom.model.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    fun getUser(userId: Int): LiveData<User?> = userDao.getUser(userId)

    fun insert(user: User) = viewModelScope.launch {
        userDao.insert(user)
    }

    fun update(user: User) = viewModelScope.launch {
        userDao.update(user)
    }

    fun delete(user: User) = viewModelScope.launch {
        userDao.delete(user)
    }
}