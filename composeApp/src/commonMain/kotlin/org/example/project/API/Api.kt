package org.example.project.API
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.Models.Listresponcies
import org.example.project.Models.Student
import org.example.project.Models.User
import org.example.project.Models.UserVacancies
import io.ktor.client.call.body;
import io.ktor.client.statement.bodyAsText
import org.example.project.Models.AllUserprofile
import org.example.project.Models.CardVacancy
import org.example.project.Models.Proffesions
import org.example.project.Models.Skills
import org.example.project.Models.University
import io.ktor.http.ContentType
import org.example.project.Models.AdminSearchFilters
import org.example.project.Models.AdminSearchResult
import org.example.project.Models.Chat
import org.example.project.Models.ChatDTO
import org.example.project.Models.CompanyPaymentChartDTO
import org.example.project.Models.CompanyPrice
import org.example.project.Models.CreateRequestCompany
import org.example.project.Models.CreateRequestUser
import org.example.project.Models.CreateVacancyDTO
import org.example.project.Models.Currency
import org.example.project.Models.CurrentUser
import org.example.project.Models.DashboardStatsDTO
import org.example.project.Models.Director
import org.example.project.Models.DirectorDTO
import org.example.project.Models.FeedbacksCompany
import org.example.project.Models.FeedbacksUser
import org.example.project.Models.FeedbacksUserDTO
import org.example.project.Models.Filters
import org.example.project.Models.Formats
import org.example.project.Models.Intership
import org.example.project.Models.ModerationItems
import org.example.project.Models.MoneyType
import org.example.project.Models.NewUsersChartDTO
import org.example.project.Models.PaymentChartDTO
import org.example.project.Models.PricesForCompany
import org.example.project.Models.PricesForUser
import org.example.project.Models.PriorityResponseDTO
import org.example.project.Models.RequestsCompany
import org.example.project.Models.RequestsUser
import org.example.project.Models.ResponciesDTO
import org.example.project.Models.Response
import org.example.project.Models.Sferes
import org.example.project.Models.StudentPaymentChartDTO
import org.example.project.Models.StudentSearchFilters
import org.example.project.Models.StudentSearchResult
import org.example.project.Models.SuccessInternshipChartDTO
import org.example.project.Models.TopCompanyDTO
import org.example.project.Models.TopStudentAdminDTO
import org.example.project.Models.TopStudentDTO
import org.example.project.Models.TopUniversityDTO
import org.example.project.Models.UserChurnChartDTO
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

    // В ApiClient.kt добавь эти методы:

    // Топ-3 компании
    suspend fun getTopCompanies(): List<TopCompanyDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetTopCompanies"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<TopCompanyDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Топ-3 студента
    suspend fun getTopStudentsAdmin(): List<TopStudentAdminDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetTopStudents"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<TopStudentAdminDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Топ-3 университета
    suspend fun getTopUniversities(): List<TopUniversityDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetTopUniversities"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<TopUniversityDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // График оплат (общий)
    suspend fun getPaymentsChart(): List<PaymentChartDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetPaymentsChart"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<PaymentChartDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // График оплат студентов
    suspend fun getStudentPaymentsChart(): List<StudentPaymentChartDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetStudentPaymentsChart"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<StudentPaymentChartDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // График оплат компаний
    suspend fun getCompanyPaymentsChart(): List<CompanyPaymentChartDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetCompanyPaymentsChart"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<CompanyPaymentChartDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    // В ApiClient.kt добавь:

    // Получить все сущности на модерации
    suspend fun getModerationItems(): ModerationItems? {
        return try {
            val url = "${BASE_URL}Admin/GetModerationItems"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<ModerationItems>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // График новых пользователей
    suspend fun getNewUsersChart(): List<NewUsersChartDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetNewUsersChart"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<NewUsersChartDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // График ухода пользователей
    suspend fun getUserChurnChart(): List<UserChurnChartDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetUserChurnChart"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<UserChurnChartDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
// В ApiClient.kt добавь:

    // Изменение статуса пользователя (студента)
    suspend fun updateUserStatus(userId: Int, status: String): Boolean {
        return try {
            val url = "${BASE_URL}Admin/UpdateUserStatus/${userId}/${status}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Изменение статуса компании
    suspend fun updateCompanyStatus(companyId: Int, status: String): Boolean {
        return try {
            val url = "${BASE_URL}Admin/UpdateCompanyStatus/${companyId}/${status}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Изменение статуса вакансии
    suspend fun updateVacancyStatus(vacancyId: Int, status: String): Boolean {
        return try {
            val url = "${BASE_URL}Admin/UpdateVacancyStatus/${vacancyId}/${status}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Изменение статуса отзыва о студенте (FeedbacksCompany)
    suspend fun updateFeedbackCompanyStatus(feedbackId: Int, status: String): Boolean {
        return try {
            val url = "${BASE_URL}Admin/UpdateFeedbackCompanyStatus/${feedbackId}/${status}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Изменение статуса отзыва о компании (FeedbacksUser)
    suspend fun updateFeedbackUserStatus(feedbackId: Int, status: String): Boolean {
        return try {
            val url = "${BASE_URL}Admin/UpdateFeedbackUserStatus/${feedbackId}/${status}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Поиск для админа
    suspend fun adminSearch(filters: AdminSearchFilters): List<AdminSearchResult>? {
        return try {
            val url = "${BASE_URL}Admin/Search"
            println("Admin search: $url")
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(filters)
            }
            println("Response: ${response.status}")
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<AdminSearchResult>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // Получить вакансию по ID для админа
    suspend fun getVacancyById(vacancyId: Int): CardVacancy? {
        return try {
            val url = "${BASE_URL}Vacancy/GetVacancy/${vacancyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<CardVacancy>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    // График успешных стажировок
    suspend fun getSuccessInternshipsChart(): List<SuccessInternshipChartDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetSuccessInternshipsChart"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<SuccessInternshipChartDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Общая статистика
    suspend fun getDashboardStats(): DashboardStatsDTO? {
        return try {
            val url = "${BASE_URL}Admin/GetDashboardStats"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<DashboardStatsDTO>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun sendVerificationCode(email: String): Boolean {
        return try {
            val url = "${BASE_URL}User/SendVerificationCode"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun verifyCode(email: String, code: String): Boolean {
        return try {
            val url = "${BASE_URL}User/VerifyCode"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "code" to code))
            }
            println(response)
            if (response.status.value in 200..299) {
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun register(user: User): User? {
        return try {
            val url = "${BASE_URL}User/Register"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<User>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createStudent(student: Student): Boolean {
        return try {
            val url = "${BASE_URL}User/CreateStudent"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(student)
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun createDirector(director: Director): Boolean {
        return try {
            val url = "${BASE_URL}Company/CreateDirector"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(director)
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun resetPassword(email: String, newPassword: String): Boolean {
        return try {
            val url = "${BASE_URL}User/ResetPassword"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "newPassword" to newPassword))
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
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
    suspend fun getChatMessagesCompanyAdmin(companyId: Int, emailAdmin: String): List<ChatDTO>? {
        return try {
            val url = "${BASE_URL}User/GetChatMessagesCompanyAdmin/${companyId}/${emailAdmin}"
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
    suspend fun markMessageAsRead(chatId: Int): Boolean {
        return try {
            val url = "${BASE_URL}Chat/MessageRead/${chatId}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun getUserIntership(userId: Int, companyId: Int, vacancyId: Int): Intership? {
        return try {
            val url = "${BASE_URL}Intership/UserIntership/${userId}/${companyId}/${vacancyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<Intership>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun startInternship(internship: Intership): Intership? {
        return try {
            val url = "${BASE_URL}Intership/NewInterShip"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(internship)
            }
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<Intership>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun endInternship(internshipId: Int): Intership? {
        return try {
            val url = "${BASE_URL}Intership/EndInterShip/${internshipId}"
            println(url)
            val response = client.put(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<Intership>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getAllCompanyTariffs(): List<PricesForCompany>? {
        return try {
            val url = "${BASE_URL}Company/GetAllCompanyTarrifs"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<PricesForCompany>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getPriorityResponses(companyId: Int): List<PriorityResponseDTO>? {
        return try {
            val url = "${BASE_URL}Company/GetPriorityResponses/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<PriorityResponseDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getCompanyResponsesCount(companyId: Int): Int? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyResponsesCount/${companyId}"
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
    suspend fun getTopStudents(companyId: Int): List<TopStudentDTO>? {
        return try {
            val url = "${BASE_URL}Company/GetTopStudents/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<TopStudentDTO>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun searchStudents(filters: StudentSearchFilters): List<StudentSearchResult>? {
        return try {
            val url = "${BASE_URL}Company/SearchStudents"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(filters)
            }
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<StudentSearchResult>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getCompanyTariff(companyId: Int): CompanyPrice? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyTarriff/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<CompanyPrice>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addNewCompanyPrice(companyPrice: CompanyPrice): CompanyPrice? {
        return try {
            val url = "${BASE_URL}Company/AddNewCompanyPrice"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(companyPrice)
            }
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<CompanyPrice>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getInternshipDays(internshipId: Int): Int? {
        return try {
            val url = "${BASE_URL}Intership/GetInternshipDays/${internshipId}"
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
    suspend fun getCompanyInterships(companyId: Int): List<Intership>? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyIntership/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<Intership>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addCompanyFeedback(feedback: FeedbacksCompany): Boolean {
        return try {
            val url = "${BASE_URL}Company/CompanyFeddback"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(feedback)
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
    suspend fun deleteCompanyFeedback(feedbackId: Int): Boolean {
        return try {
            val url = "${BASE_URL}Company/DeleteGetCompanyFeedbackToUser/${feedbackId}"
            println(url)
            val response = client.delete(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun getCompanyFeedbackToUser(companyId: Int): List<FeedbacksCompany>? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyFeedbackToUser/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<FeedbacksCompany>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun saveCompanyProfile(director: Director): Boolean {
        return try {
            val url = "${BASE_URL}Company/SaveCompanyProfile/${director.idDirector}"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(director)
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
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
    suspend fun getUserFeedbacksCompany(userId: Int): List<FeedbacksCompany>? {
        return try {
            val url = "${BASE_URL}User/GetUserFeedback/${userId}"
            println("Getting user feedbacks: $url")
            val response = client.get(url)
            println("Response: ${response.status}")

            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<FeedbacksCompany>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error getting user feedbacks: ${e.message}")
            e.printStackTrace()
            null
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

    // Создать жалобу на пользователя (без отзыва)
    suspend fun createDirectUserRequest(
        targetUserId: Int,
        description: String
    ): Boolean {
        return try {
            val url = "${BASE_URL}Request/CreateDirectUserRequest"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "idStudent" to (CurrentUser.id ?: 0),
                        "targetUserId" to targetUserId,
                        "descriptionRequest" to description
                    )
                )
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    // Получить отзыв о компании (FeedbacksUser) по ID
    suspend fun getUserFeedbackById(feedbackId: Int): FeedbacksUser? {
        return try {
            val url = "${BASE_URL}FeedbacksUser/GetUserFeedback/${feedbackId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<FeedbacksUser>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Создать жалобу на компанию (без отзыва)
    suspend fun createDirectCompanyRequest(
        targetCompanyId: Int,
        description: String
    ): Boolean {
        return try {
            val url = "${BASE_URL}Request/CreateDirectCompanyRequest"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "idCompany" to (CurrentUser.id ?: 0),
                        "targetCompanyId" to targetCompanyId,
                        "descriptionRequset" to description
                    )
                )
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Получить жалобы пользователя
    suspend fun getUserRequests(userId: Int): List<RequestsUser>? {
        return try {
            val url = "${BASE_URL}Request/GetUserRequests/${userId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<RequestsUser>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Получить жалобы компании
    suspend fun getCompanyRequests(companyId: Int): List<RequestsCompany>? {
        return try {
            val url = "${BASE_URL}Request/GetCompanyRequests/${companyId}"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<RequestsCompany>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Создать жалобу от пользователя
    suspend fun createUserRequest(request: CreateRequestUser): Boolean {
        return try {
            val url = "${BASE_URL}Request/CreateUserRequest"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Создать жалобу от компании
    suspend fun createCompanyRequest(request: CreateRequestCompany): Boolean {
        return try {
            val url = "${BASE_URL}Request/CreateCompanyRequest"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun getAllUserRequests(): List<RequestsUser>? {
        return try {
            val url = "${BASE_URL}Request/GetAllUserRequests"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<RequestsUser>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllCompanyRequests(): List<RequestsCompany>? {
        return try {
            val url = "${BASE_URL}Request/GetAllCompanyRequests"
            println(url)
            val response = client.get(url)
            println(response)
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                Json.decodeFromString<List<RequestsCompany>>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun updateRequestStatus(requestId: Int, status: String, type: String): Boolean {
        return try {
            val url = "${BASE_URL}Request/UpdateRequestStatus/${requestId}/${status}/${type}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun login(user: User): User? {
        return try {
            val url = "${BASE_URL}User/Authorization"
            println(url)
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
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
    suspend fun deleteCompany(companyId: Int): Boolean {
        return try {
            val url = "${BASE_URL}Company/DeleteCompany/${companyId}"
            println(url)
            val response = client.delete(url)
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
    suspend fun getCountAdminmessages(email: String?): Int? {
        return try {
            val url = "${BASE_URL}Admin/GetAdminMessages/${email}"
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
    suspend fun getAdminChats(email: String?): List<ChatDTO>? {
        return try {
            val url = "${BASE_URL}Admin/GetAdminChats/$email"
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
    suspend fun archiveVacancy(vacancyId: Int): Boolean {
        return try {
            val url = "${BASE_URL}Vacancy/ArchiveVacancy/${vacancyId}"
            println(url)
            val response = client.put(url)
            println(response)
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun getCompanyChats(userId: Int): List<ChatDTO>? {
        return try {
            val url = "${BASE_URL}Company/GetCompanyChats/$userId"
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

