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
import org.example.project.Models.Adress
import org.example.project.Models.Chat
import org.example.project.Models.ChatDTO
import org.example.project.Models.Complaint
import org.example.project.Models.ComplaintDTO
import org.example.project.Models.CreateVacancyDTO
import org.example.project.Models.Currency
import org.example.project.Models.Director
import org.example.project.Models.DirectorDTO
import org.example.project.Models.FeedbacksUser
import org.example.project.Models.FeedbacksUserDTO
import org.example.project.Models.Filters
import org.example.project.Models.Formats
import org.example.project.Models.MoneyType
import org.example.project.Models.PricesForUser
import org.example.project.Models.ResponciesDTO
import org.example.project.Models.Response
import org.example.project.Models.Sferes
import org.example.project.Models.UserPrice

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
    suspend fun getUserResponseCount(userId: Int): Int? {
        return try {
            val url = "${BASE_URL}User/GetUserCountResponce/${userId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                jsonString.toIntOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getAllPrices(): List<PricesForUser>? {
        return try {
            val url = "${BASE_URL}UserPrices/GetAllPrices"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<PricesForUser>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addNewUserPrice(userPrice: UserPrice): UserPrice? {
        return try {
            val url = "${BASE_URL}UserPrices/AddNewUserPrice"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(userPrice)
            }
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<UserPrice>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getChatMessagesCompany(userId: Int, companyId: Int, vacancyId: Int): List<ChatDTO>? {
        return try {
            val url = "${BASE_URL}User/GetChatMessagesCompany/${userId}/${companyId}/${vacancyId}"
            println("URL: $url")
            val response = client.get(url)
            println("Response status: ${response.status}")
            val jsonString = response.bodyAsText()
            println("Response body: $jsonString")
            if (response.status.value in 200..299) {
                Json.decodeFromString<List<ChatDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun getChatMessagesAdmin(userId: Int, emailAdmin: String, ): List<ChatDTO>? {
        return try {
            val url = "${BASE_URL}User/GetChatMessagesAdmin/${userId}/${emailAdmin}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<ChatDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getCompanyCard(companyId: Int): DirectorDTO? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyCard/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<DirectorDTO>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun sendMessage(chat: Chat): Boolean {
        return try {
            val url = "${BASE_URL}User/SendMessage"
            println("Sending message to: $url")
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(chat)
            }
            println("Response status: ${response.status}")
            if (response.status.value != 200) {
                val error = response.bodyAsText()
                println("Error: $error")
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            println("Exception: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    suspend fun getUserFeedbacks(userId: Int): List<FeedbacksUserDTO>? {
        return try {
            val url = "${BASE_URL}FeedbacksUser/GetUsersFeedbacks/${userId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<FeedbacksUserDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getFeedbackById(feedbackId: Int): FeedbacksUserDTO? {
        return try {
            val url = "${BASE_URL}FeedbacksUser/GetFeedback/${feedbackId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<FeedbacksUserDTO>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getUserComplaints(userId: Int): List<ComplaintDTO>? {
        return try {
            val url = "${BASE_URL}Complaint/GetAll/${userId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<ComplaintDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun addComplaint(complaint: Complaint): Complaint? {
        return try {
            val url = "${BASE_URL}Complaint/AddComplaint"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(complaint)
            }
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<Complaint>(jsonString)
            } else {
                val error = response.bodyAsText()
                println("Error: $error")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun deleteFeedback(feedbackId: Int): Boolean {
        return try {
            val url = "${BASE_URL}FeedbacksUser/DeleteFeedbackUser/${feedbackId}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteComplaint(complaintId: Int): Boolean {
        return try {
            val url = "${BASE_URL}Complaint/DeleteComplaint/${complaintId}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun getCompanyFeedback(companyId: Int): List<FeedbacksUser>? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyFeedback/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<FeedbacksUser>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun addResponse(response: Response): Response? {
        return try {
            val url = "${BASE_URL}Responces/AddResponse"
            println(url)
            val responseBody = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(response)
            }
            println(responseBody)
            if (responseBody.status.value in 200..299) {
                val jsonString = responseBody.bodyAsText()
                Json.decodeFromString<Response>(jsonString)
            } else {
                val error = responseBody.bodyAsText()
                println("Error: $error")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getUserPrice(userId: Int): UserPrice? {
        return try {
            val url = "${BASE_URL}User/GetUserPrice/${userId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<UserPrice>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun addFeedback(feedback: FeedbacksUser): FeedbacksUser? {
        return try {
            val url = "${BASE_URL}FeedbacksUser/AddFeedbackUser"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(feedback)
            }
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<FeedbacksUser>(jsonString)
            } else {
                val error = response.bodyAsText()
                println("Error: $error")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getComplaintById(complaintId: Int): ComplaintDTO? {
        return try {
            val url = "${BASE_URL}Complaint/GetComplaint/${complaintId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<ComplaintDTO>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun saveUserProfile(profile: AllUserprofile): Boolean {
        return try {
            val url = "${BASE_URL}User/SaveUserProfile"
            println("Saving profile to: $url")

            val response = client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(profile)
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
    suspend fun getMoneyTypes(): List<MoneyType>? {
        return try {
            val url = "${BASE_URL}MoneyTypes/GetMoneyTypes"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<MoneyType>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getCurrencies(): List<Currency>? {
        return try {
            val url = "${BASE_URL}Currency/GetCurrency"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Currency>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getResponsesByCompany(companyId: Int): List<Response>? {
        return try {
            val url = "${BASE_URL}Responces/GetResponsesByCompany/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Response>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun updateResponseStatus(responseId: Int, status: String): Boolean {
        return try {
            val url = "${BASE_URL}Responces/UpdateResponseStatus/${responseId}/${status}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun createVacancy(vacancy: CreateVacancyDTO): Boolean {
        return try {
            val url = "${BASE_URL}Vacancy/CreateVacancy"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(vacancy)
            }
            println(response)
            if (response.status.value in 200..299) {
                true
            } else {
                val error = response.bodyAsText()
                println("Error: $error")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun getUserById(id: Int): Student? {
        return try {
            val url = "${BASE_URL}User/GetUserById/${id}"
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
    suspend fun getCompany(user: User): Director? {
        return try {
            val url = "${BASE_URL}Company/GetCompany/${user.emailUser}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<Director>()
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
    suspend fun getCountCompanymessages(user: Director): Int? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyMessages/${user.idDirector}"
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
    suspend fun getCompanyVacancies(user: Director): List<Listresponcies>? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyVacancies/${user.idDirector}"
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
        } as List<Listresponcies>?
    }
    suspend fun getCompanyResponcies(user: Director): List<ResponciesDTO>? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyResponcies/${user.idDirector}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                response.body<List<ResponciesDTO>>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } as List<ResponciesDTO>?
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
    suspend fun getUserChats(userId: Int): List<ChatDTO>? {
        return try {
            val url = "${BASE_URL}User/GetUserChats/$userId"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<ChatDTO>>(jsonString)
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

