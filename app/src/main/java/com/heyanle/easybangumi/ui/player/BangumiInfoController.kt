package com.heyanle.easybangumi.ui.player

import androidx.compose.runtime.mutableStateOf
import com.heyanle.bangumi_source_api.api.entity.BangumiDetail
import com.heyanle.bangumi_source_api.api.entity.BangumiSummary
import com.heyanle.easybangumi.R
import com.heyanle.easybangumi.db.EasyDB
import com.heyanle.easybangumi.db.entity.BangumiStar
import com.heyanle.easybangumi.source.AnimSourceFactory
import com.heyanle.easybangumi.ui.home.star.AnimStarViewModel
import com.heyanle.easybangumi.utils.stringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by HeYanLe on 2023/2/4 23:55.
 * https://github.com/heyanLE
 */
class BangumiInfoController(
    val bangumiSummary: BangumiSummary
) {

    private val scope = MainScope()

    private val _infoStatus = MutableStateFlow<BangumiInfoState>(
        BangumiInfoState.None
    )
    val flow: StateFlow<BangumiInfoState> = _infoStatus

    val isBangumiStar = mutableStateOf(false)

    fun load() {
        scope.launch {
            _infoStatus.emit(BangumiInfoState.Loading)
            kotlin.runCatching {
                var bangumiDetail: BangumiDetail? = null

                AnimSourceFactory.requireDetail(bangumiSummary.source)
                    .detail(bangumiSummary)
                    .complete {
                        val isStar = withContext(Dispatchers.IO) {
                            EasyDB.database.bangumiStarDao().getBySourceDetailUrl(
                                it.data.source,
                                it.data.detailUrl
                            ) != null
                        }
                        isBangumiStar.value = isStar
                        bangumiDetail = it.data
                    }.error {
                        it.throwable.printStackTrace()
                        _infoStatus.emit(
                            BangumiInfoState.Error(
                                if (it.isParserError) stringRes(R.string.source_error) else stringRes(
                                    R.string.loading_error
                                ),
                                it.throwable
                            )
                        )
                    }

                val detail = bangumiDetail ?: return@launch

                AnimSourceFactory.requirePlay(bangumiSummary.source)
                    .getPlayMsg(bangumiSummary)
                    .complete {
                        _infoStatus.emit(
                            BangumiInfoState.Info(
                                it.data,
                                detail
                            )
                        )
                    }.error {
                        _infoStatus.emit(
                            BangumiInfoState.Error(
                                if (it.isParserError) stringRes(
                                    R.string.source_error
                                ) else stringRes(R.string.loading_error), it.throwable
                            )
                        )
                        return@launch
                    }
            }.onFailure {
                _infoStatus.emit(
                    BangumiInfoState.Error(
                        stringRes(R.string.loading_error),
                        it
                    )
                )
            }
        }
    }

    suspend fun setBangumiStar(isStar: Boolean, bangumiDetail: BangumiDetail) {
        if (isStar) {
            withContext(Dispatchers.IO) {
                EasyDB.database.bangumiStar.apply {
                    val old = getBySourceDetailUrl(bangumiDetail.source, bangumiDetail.detailUrl)
                    if (old == null) {
                        insert(BangumiStar.fromBangumi(bangumiDetail))
                    } else {

                        modify(
                            old.copy(
                                name = bangumiDetail.name,
                                cover = bangumiDetail.cover,
                                source = bangumiDetail.source,
                                createTime = System.currentTimeMillis()
                            )
                        )
                    }
                }

            }
            AnimStarViewModel.refresh()
            isBangumiStar.value = true
        } else {
            withContext(Dispatchers.IO) {
                EasyDB.database.bangumiStarDao()
                    .deleteBySourceDetailUrl(bangumiDetail.source, bangumiDetail.detailUrl)
            }
            AnimStarViewModel.refresh()
            isBangumiStar.value = false
        }
    }

    fun release() {
        scope.cancel()
    }

}

sealed class BangumiInfoState {
    object None : BangumiInfoState()

    object Loading : BangumiInfoState()

    class Info(
        val playMsg: LinkedHashMap<String, List<String>>,
        val detail: BangumiDetail
    ) : BangumiInfoState()

    class Error(
        val errorMsg: String,
        val throwable: Throwable?
    ) : BangumiInfoState()
}