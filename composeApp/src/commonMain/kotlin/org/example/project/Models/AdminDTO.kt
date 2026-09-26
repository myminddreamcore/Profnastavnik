package org.example.project.Models

import kotlinx.serialization.Serializable
// В AdminModels.kt добавь:

@Serializable
data class AdminSearchResult(
    val type: String, // "vacancy", "user", "company", "feedback"
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val date: String? = null,
    val status: String? = null,
    val photo: String? = null
)
// В AdminModels.kt добавь:

@Serializable
data class ModerationItems(
    val users: List<ModerationUser>,
    val companies: List<ModerationCompany>,
    val vacancies: List<ModerationVacancy>,
    val feedbacksToUsers: List<ModerationFeedbackCompany>,
    val feedbacksToCompanies: List<ModerationFeedbackUser>
)

@Serializable
data class ModerationUser(
    val id: Int,
    val fullName: String,
    val email: String,
    val university: String?,
    val course: Int?,
    val status: String
)

@Serializable
data class ModerationCompany(
    val id: Int,
    val name: String,
    val email: String,
    val city: String?,
    val status: String
)

@Serializable
data class ModerationVacancy(
    val id: Int,
    val name: String,
    val companyName: String,
    val description: String?,
    val status: String
)

// ModerationFeedbackCompany (отзыв о студенте на модерации)
@Serializable
data class ModerationFeedbackCompany(
    val id: Int,
    val studentName: String,
    val companyName: String,
    val description: String,
    val rating: Int,
    val status: String,
    val date: String? = null  // <-- ДОБАВЛЕНО
)

// ModerationFeedbackUser (отзыв о компании на модерации)
@Serializable
data class ModerationFeedbackUser(
    val id: Int,
    val studentName: String,
    val companyName: String,
    val description: String,
    val rating: Int,
    val status: String,
    val date: String? = null  // <-- ДОБАВЛЕНО
)
@Serializable
data class AdminSearchFilters(
    val query: String? = null,
    val type: String? = null, // "all", "vacancy", "user", "company", "feedback"
    val id: Int? = null
)
@Serializable
data class TopCompanyDTO(
    val companyId: Int,
    val companyName: String,
    val rating: Double,
    val completedInternships: Int
)
// Models/DashboardStatsDTO.kt
@Serializable
data class DashboardStatsDTO(
    val totalUsers: Int,           // Всего пользователей в системе
    val totalStudents: Int,        // Всего студентов (активных)
    val totalCompanies: Int,       // Всего компаний (активных)
    val totalInternships: Int,     // Всего стажировок (все статусы)
    val completedInternships: Int, // Завершенных стажировок
    val activeInternships: Int,    // Активных (идущих) стажировок
    val successRate: Double        // Процент успешных стажировок (%)
)
@Serializable
data class TopStudentAdminDTO(
    val studentId: Int,
    val fullName: String,
    val universityName: String,
    val completedInternships: Int,
    val skillsCount: Int,
    val professionsCount: Int,
    val photo: String? = null
)

@Serializable
data class TopUniversityDTO(
    val universityId: Int,
    val universityName: String,
    val studentCount: Int
)

@Serializable
data class PaymentChartDTO(
    val month: String,
    val tariffType: String,
    val amount: Double
)

@Serializable
data class StudentPaymentChartDTO(
    val month: String,
    val tariffType: String,
    val amount: Double
)

@Serializable
data class CompanyPaymentChartDTO(
    val month: String,
    val tariffType: String,
    val amount: Double
)

@Serializable
data class NewUsersChartDTO(
    val month: String,
    val count: Int
)

@Serializable
data class UserChurnChartDTO(
    val month: String,
    val count: Int
)

@Serializable
data class SuccessInternshipChartDTO(
    val month: String,
    val count: Int
)