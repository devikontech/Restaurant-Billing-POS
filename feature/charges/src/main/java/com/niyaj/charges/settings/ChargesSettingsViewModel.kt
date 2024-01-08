package com.niyaj.charges.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.model.Charges
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChargesSettingsViewModel @Inject constructor(
    private val chargesRepository: ChargesRepository,
    private val analyticsHelper: AnalyticsHelper,
): BaseViewModel() {

    val charges = snapshotFlow { mSearchText.value }.flatMapLatest {
        chargesRepository.getAllCharges(it)
    }.mapLatest { list ->
        totalItems = list.map { it.chargesId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<Charges>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<Charges>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: ChargesSettingsEvent) {
        when(event) {
            is ChargesSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = charges.value
                    } else {
                        val list = mutableListOf<Charges>()

                        mSelectedItems.forEach { id ->
                            val charges = charges.value.find { it.chargesId == id }

                            if (charges != null) {
                                list.add(charges)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }

                    analyticsHelper.logExportedChargesToFile(_exportedItems.value.size)
                }
            }

            is ChargesSettingsEvent.OnImportChargesItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.chargesId }
                        _importedItems.value = event.data

                        analyticsHelper.logImportedChargesFromFile(event.data.size)
                    }
                }
            }

            is ChargesSettingsEvent.ImportChargesItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap {chargesId ->
                            _importedItems.value.filter { it.chargesId == chargesId }
                        }
                    }else {
                        _importedItems.value
                    }

                    when(val result = chargesRepository.importChargesItemsToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items has been imported successfully"))
                            analyticsHelper.logImportedChargesToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}


internal fun AnalyticsHelper.logImportedChargesFromFile(totalCharges: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "charges_imported_from_file",
            extras = listOf(
                AnalyticsEvent.Param("charges_imported_from_file", totalCharges.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logImportedChargesToDatabase(totalCharges: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "charges_imported_to_database",
            extras = listOf(
                AnalyticsEvent.Param("charges_imported_to_database", totalCharges.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logExportedChargesToFile(totalCharges: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "charges_exported_to_file",
            extras = listOf(
                AnalyticsEvent.Param("charges_exported_to_file", totalCharges.toString()),
            ),
        ),
    )
}