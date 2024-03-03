package ru.netology.nmedia.repository

import android.net.http.HttpException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import java.io.IOException

class PostPagingSource(
    private val apiService: ApiService,
) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val response = when (params) {
                is LoadParams.Refresh -> {
                    apiService.getLatest(params.loadSize)
                }

                is LoadParams.Append -> {
                    apiService.getBefore(id = params.key, count = params.loadSize)
                }

                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key
                )
                
            }
            if (!response.isSuccessful) {
                return LoadResult.Error(IOException("Error PostPagingSource"))
            }
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )
            return LoadResult.Page(
                data = body,
                prevKey = params.key,
                nextKey = body.lastOrNull()?.id
            )
        } catch (e: IOException){
            return LoadResult.Error(e)
        }
    }

}
