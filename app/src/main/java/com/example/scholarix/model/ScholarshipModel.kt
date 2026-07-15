package com.example.scholarix.model

data class ScholarshipModel(
    val id: String = "",
    val providerId: String = "",
    val title: String = "",
    val description: String = "",
    val eligibility: String = "",
    val amount: String = "",
    val deadline: String = "",
    val applicationLink: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "providerId" to providerId,
            "title" to title,
            "description" to description,
            "eligibility" to eligibility,
            "amount" to amount,
            "deadline" to deadline,
            "applicationLink" to applicationLink,
            "createdAt" to createdAt
        )
    }
}