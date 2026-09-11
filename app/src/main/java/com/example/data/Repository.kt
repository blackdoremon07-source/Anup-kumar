package com.example.data

import com.example.model.AdmitCard
import com.example.model.AnswerKey
import com.example.model.GovernmentScheme
import com.example.model.Job
import com.example.model.JobCategory
import com.example.model.Notification
import com.example.model.Result
import com.example.model.Tool
import com.example.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AppRepository {
    fun getJobs(): Flow<List<Job>>
    fun getResults(): Flow<List<Result>>
    fun getAdmitCards(): Flow<List<AdmitCard>>
    fun getAnswerKeys(): Flow<List<AnswerKey>>
    fun getNotifications(): Flow<List<Notification>>
    fun getSchemes(): Flow<List<GovernmentScheme>>
    fun getTools(): Flow<List<Tool>>
    fun getCurrentUser(): Flow<User>
    fun toggleSaveJob(jobId: String)
    fun searchAll(query: String): SearchResults
}

data class SearchResults(
    val query: String,
    val matchedJobs: List<Job>,
    val matchedResults: List<Result>,
    val matchedAdmitCards: List<AdmitCard>,
    val matchedTools: List<Tool>,
    val matchedNotifications: List<Notification>
) {
    val totalCount: Int
        get() = matchedJobs.size + matchedResults.size + matchedAdmitCards.size + matchedTools.size + matchedNotifications.size
}

class InMemoryAppRepository : AppRepository {
    private val _jobs = MutableStateFlow(MockDataProvider.sampleJobs)
    private val _results = MutableStateFlow(MockDataProvider.sampleResults)
    private val _admitCards = MutableStateFlow(MockDataProvider.sampleAdmitCards)
    private val _answerKeys = MutableStateFlow(MockDataProvider.sampleAnswerKeys)
    private val _notifications = MutableStateFlow(MockDataProvider.sampleNotifications)
    private val _schemes = MutableStateFlow(MockDataProvider.sampleSchemes)
    private val _tools = MutableStateFlow(MockDataProvider.toolsList)
    private val _user = MutableStateFlow(User())

    override fun getJobs(): Flow<List<Job>> = _jobs.asStateFlow()
    override fun getResults(): Flow<List<Result>> = _results.asStateFlow()
    override fun getAdmitCards(): Flow<List<AdmitCard>> = _admitCards.asStateFlow()
    override fun getAnswerKeys(): Flow<List<AnswerKey>> = _answerKeys.asStateFlow()
    override fun getNotifications(): Flow<List<Notification>> = _notifications.asStateFlow()
    override fun getSchemes(): Flow<List<GovernmentScheme>> = _schemes.asStateFlow()
    override fun getTools(): Flow<List<Tool>> = _tools.asStateFlow()
    override fun getCurrentUser(): Flow<User> = _user.asStateFlow()

    override fun toggleSaveJob(jobId: String) {
        val currentSaved = _user.value.savedJobIds.toMutableList()
        if (currentSaved.contains(jobId)) {
            currentSaved.remove(jobId)
        } else {
            currentSaved.add(jobId)
        }
        _user.value = _user.value.copy(savedJobIds = currentSaved)
    }

    override fun searchAll(query: String): SearchResults {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) {
            return SearchResults(query, emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        }

        val jobs = _jobs.value.filter {
            it.title.lowercase().contains(trimmed) ||
            it.organization.lowercase().contains(trimmed) ||
            it.category.displayName.lowercase().contains(trimmed)
        }
        val results = _results.value.filter {
            it.title.lowercase().contains(trimmed) ||
            it.organization.lowercase().contains(trimmed)
        }
        val admitCards = _admitCards.value.filter {
            it.title.lowercase().contains(trimmed) ||
            it.organization.lowercase().contains(trimmed)
        }
        val tools = _tools.value.filter {
            it.title.lowercase().contains(trimmed) ||
            it.description.lowercase().contains(trimmed) ||
            it.category.lowercase().contains(trimmed)
        }
        val notifications = _notifications.value.filter {
            it.title.lowercase().contains(trimmed) ||
            it.organization.lowercase().contains(trimmed)
        }

        return SearchResults(
            query = query,
            matchedJobs = jobs,
            matchedResults = results,
            matchedAdmitCards = admitCards,
            matchedTools = tools,
            matchedNotifications = notifications
        )
    }
}
