import androidx.paging.PagingSource
import androidx.paging.PagingState
import jp.speakbuddy.edisonandroidexercise.data.local.FactEntity
import jp.speakbuddy.edisonandroidexercise.model.Fact

class FactPagingSource(private val originalSource: PagingSource<Int, FactEntity>) : PagingSource<Int, Fact>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Fact> {
        return when (val result = originalSource.load(params)) {
            is LoadResult.Error -> LoadResult.Error(result.throwable)
            is LoadResult.Invalid -> LoadResult.Invalid()
            is LoadResult.Page -> LoadResult.Page(
                data = result.data.map { it.toFact() },
                prevKey = result.prevKey,
                nextKey = result.nextKey
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Fact>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun FactEntity.toFact(): Fact {
        return Fact(
            text = this.fact,
            length = this.fact.length,
            id = this.id
        )
    }
}