package de.halfbit.g1.overview

import io.reactivex.Single
import magnet.Instance

interface GithubOverviewApi {
    fun getRepos(page: Int): Single<List<Repo>>
}

@Instance(type = GithubOverviewApi::class)
internal class DefaultGithubOverviewApi(
    private val githubOverviewEndpoint: GithubOverviewEndpoint
) : GithubOverviewApi {

    override fun getRepos(page: Int): Single<List<Repo>> =
        githubOverviewEndpoint.getRepos(page)
            .map { it.items.map { jsonRepo -> jsonRepo.toRepo() } }

}

private const val UNKNOWN = "unknown"

private fun JsonRepos.JsonRepo.toRepo(): Repo =
    Repo(
        name = name ?: UNKNOWN,
        author = owner.login ?: UNKNOWN,
        description = description ?: UNKNOWN,
        language = language ?: UNKNOWN,
        stars = stargazers_count?.toIntOrNull() ?: 0
    )