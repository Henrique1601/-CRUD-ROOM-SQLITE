package br.unistanta.aplicativoroom.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import br.unistanta.aplicativoroom.dao.UserDao
import br.unistanta.aplicativoroom.database.AppDatabase
import br.unistanta.aplicativoroom.databinding.ActivityMainBinding
import br.unistanta.aplicativoroom.model.User
import br.unistanta.aplicativoroom.viewmodel.UserViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db:AppDatabase
    private lateinit var viewModel: UserViewModel
    private var userId: Int = 1  // Exemplo de ID fixo para teste
    private lateinit var userDao: UserDao

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val imagePath = saveImageToInternalStorage(it) // Copia a imagem para armazenamento interno
            if (imagePath != null) {
                binding.imgProfile.setImageURI(it) // Exibe a imagem escolhida
                binding.imgProfile.tag = imagePath // Armazena o caminho para uso futuro
            } else {
                Toast.makeText(this, "Erro ao salvar a imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(filesDir, "profile_photo.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath // Retorna o caminho absoluto da imagem salva
        } catch (e: Exception) {
            e.printStackTrace()
            null // Retorna null se der erro
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.imgProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "database-unisanta"
        ).fallbackToDestructiveMigration()
            .build()
        userDao = db.userDao()

        binding.btnSalvar.setOnClickListener{
            val name = binding.edtFname.text.toString()
            val email = binding.etEmail.text.toString()
            val photo = binding.imgProfile.tag?.toString() ?: ""

            val user = User(id = userId, name = name, email = email, profilePhoto = photo)
            viewModel.insert(user)
            Toast.makeText(this, "Usuário salvo", Toast.LENGTH_SHORT).show()

        }
        binding.btnDelete.setOnClickListener {
            viewModel.getUser(userId).observe(this) { user ->
                user?.let {
                    viewModel.delete(it)
                    Toast.makeText(this, "Usuário excluído", Toast.LENGTH_SHORT).show()
                }
            }

        }
        // Carrega os dados do usuário ao abrir o app
        viewModel.getUser(userId).observe(this) { user ->
            user?.let {
                binding.edtFname.setText(it.name)
                binding.etEmail.setText(it.email)
                try {
                    val photoPath = it.profilePhoto
                    if (photoPath.isNotEmpty()) {
                        val file = File(photoPath)
                        if (file.exists()) {
                            binding.imgProfile.setImageURI(file.toUri()) // Carrega a imagem salva
                        } else {
                            Toast.makeText(this, "Foto não encontrada", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Erro ao carregar a foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}