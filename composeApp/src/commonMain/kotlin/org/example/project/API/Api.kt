package org.example.project.API
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.Models.Listresponcies
import org.example.project.Models.Student
import org.example.project.Models.User
import org.example.project.Models.UserVacancies
import org.example.project.Models.Vacancy
import io.ktor.client.call.body;
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.builtins.ListSerializer
import org.example.project.Models.AllUserprofile
import org.example.project.Models.CardVacancy
import org.example.project.Models.Proffesions
import org.example.project.Models.Skills
import org.example.project.Models.University
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import io.ktor.http.ContentType
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.IO
import org.example.project.Models.Filters
import org.example.project.Models.Formats
import org.example.project.Models.Sferes

class ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    private val BASE_URL = "http://10.0.2.2:5131/api/"
    suspend fun saveUserProfile(profile: AllUserprofile): Boolean {
        return try {
            val url = "${BASE_URL}User/SaveUserProfile"
            println("Saving profile to: $url")

            val response = client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(profile)  // Ktor сам сериализует, не нужно вызывать encodeToString
            }

            println("Response status: ${response.status}")

            if (response.status.value != 200) {
                val errorBody = response.bodyAsText()
                println("Error body: $errorBody")
            }

            response.status.value in 200..299
        } catch (e: Exception) {
            println("Exception: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    suspend fun login(user: User): User? {
        return try {
            val url = "${BASE_URL}User/Authorization/${user.emailUser}/${user.passwordUser}"
            println(url)
            val response = client.post(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<User>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun deleteuser(id: Int): Student? {
        return try {
            val url = "${BASE_URL}User/DeleteUser/${id}"
            println(url)
            val response = client.put(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<Student>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getUserArhciveVacancies(id: Int): List<Listresponcies>? {
        return try {
            val url = "${BASE_URL}User/GetUserArchiveVacancies/${id}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<List<Listresponcies>>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getUser(user: User): Student? {
        return try {
            val url = "${BASE_URL}User/GetUser/${user.emailUser}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<Student>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun isFavourite(id_vacancy: Int, id_user:Int): Boolean? {
        return try {
            val url = "${BASE_URL}User/IfFavoutity/${id_user}/${id_vacancy}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<Boolean>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun deletefavourites(id_vacancy: Int, id_user:Int): Boolean? {
        return try {
            val url = "${BASE_URL}User/DeleteFaivourites/${id_user}/${id_vacancy}"
            println(url)
            val response = client.delete(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<Boolean>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun addfavourites(id_vacancy: Int, id_user:Int): Boolean? {
        return try {
            val url = "${BASE_URL}User/AddFaivourites/${id_user}/${id_vacancy}"
            println(url)
            val response = client.post(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<Boolean>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getUniversity(): List<University>? {
        return try {
            val url = "${BASE_URL}University/GetAllUniversity"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<List<University>>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getSkills(): List<Skills>? {
        return try {
            val url = "${BASE_URL}Skill/GetAllSkills"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<List<Skills>>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getProffesions(): List<Proffesions>? {
        return try {
            val url = "${BASE_URL}Proffesion/GetAllProffesions"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<List<Proffesions>>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getSferes(): List<Sferes>? {
        return try {
            val url = "${BASE_URL}Sfere/GetAllSferes"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<List<Sferes>>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getFormats(): List<Formats>? {
        return try {
            val url = "${BASE_URL}Format/GetAllFormats"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<List<Formats>>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getVacancies(user: Student): List<UserVacancies>? {
        return try {
            val url = "${BASE_URL}User/UserVacancy/${user.idStudent}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<List<UserVacancies>>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getVacancy(id: Int): CardVacancy? {
        return try {
            val url = "${BASE_URL}Vacancy/GetVacancy/${id}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<CardVacancy>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getCountUsermessages(user: Student): Int? {
        return try {
            val url = "${BASE_URL}User/GetUserMessages/${user.idStudent}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<Int>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    suspend fun getAllUserProfile(userid:Int): AllUserprofile? {
        return try {
            val url = "${BASE_URL}User/GetAllUserProfile/${userid}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<AllUserprofile>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun register(user: User): String? {
        return try {
            val response = client.post("${BASE_URL}User/Registration") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(user)
            }

            when (response.status.value) {
                in 200..299 -> null
                400 -> "Пользователь с таким email уже существует"
                else -> "Ошибка сервера: ${response.status.value}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Проверьте интернет-соединение"
        }
    }
    suspend fun getrejectedVacancy(user: Student): List<Listresponcies>? {
        return try {
            val url = "${BASE_URL}Responces/GetUserRejectedIntership/${user.idStudent}"

            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Listresponcies>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getRecommendationVacancy(id: Int): List<Listresponcies>? {
        return try {
            val url = "${BASE_URL}Responces/GetRecommendationsIntership/${id}"

            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Listresponcies>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getfilteredVacancy(filters: Filters): List<Listresponcies>? {
        return try {
            val response = client.post("${BASE_URL}Responces/GetFilterIntership") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(filters)
            }
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Listresponcies>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getuserfavourities(user: Student): List<Listresponcies>? {
        return try {
            val url = "${BASE_URL}User/GetUserFaivorities/${user.idStudent}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Listresponcies>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getconsiderVacancy(user: Student): List<Listresponcies>? {
        return try {
            val url = "${BASE_URL}Responces/GetUserConsiderIntership/${user.idStudent}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Listresponcies>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getinvitedVacancy(user: Student): List<Listresponcies>? {
        return try {
            val url = "${BASE_URL}Responces/GetUserInvitedIntership/${user.idStudent}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Listresponcies>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

