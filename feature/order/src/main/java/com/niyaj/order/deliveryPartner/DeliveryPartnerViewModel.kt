/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.order.deliveryPartner

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.OrderRepository
import com.niyaj.model.DeliveryReport
import com.niyaj.model.TotalDeliveryPartnerOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryPartnerViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var partnerId = savedStateHandle.get<Int?>("partnerId")

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    val allOrders = _selectedDate.flatMapLatest {
        orderRepository.getDeliveryPartnerOrders(it)
    }.map { items ->
        if (items.isEmpty()) PartnerState.Empty else PartnerState.Success(items)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PartnerState.Loading,
    )

    val deliveryReports = _selectedDate.combine(snapshotFlow { partnerId }) { date, partnerId ->
        orderRepository.getPartnerDeliveryReports(date, partnerId)
    }.flatMapLatest {
        it.map { items ->
            if (items.isEmpty()) PartnerReportState.Empty else PartnerReportState.Success(items)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PartnerReportState.Loading,
    )

    fun selectDate(date: String) {
        viewModelScope.launch {
            _selectedDate.update { date }
        }
    }

    fun getSharablePartnerOrders() {
        viewModelScope.launch {
            partnerId = null
        }
    }
}

sealed interface PartnerState {
    data object Loading : PartnerState
    data object Empty : PartnerState
    data class Success(val orders: List<TotalDeliveryPartnerOrder>) : PartnerState
}

sealed interface PartnerReportState {
    data object Loading : PartnerReportState
    data object Empty : PartnerReportState
    data class Success(val orders: List<DeliveryReport>) : PartnerReportState
}
