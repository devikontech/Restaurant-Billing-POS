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

package com.niyaj.customer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.niyaj.common.tags.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_LIST
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NOT_AVAILABLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SCREEN_TITLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_TAG
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.SEARCH_BAR_CLEAR_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_FAB_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_SEARCH_BAR
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.Customer
import com.niyaj.model.searchCustomer
import com.niyaj.ui.components.NAV_DELETE_BTN
import com.niyaj.ui.components.NAV_EDIT_BTN
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NAV_SELECT_ALL_BTN
import com.niyaj.ui.components.NAV_SETTING_BTN
import com.niyaj.ui.event.UiState
import com.niyaj.ui.event.UiState.Empty
import com.niyaj.ui.event.UiState.Loading
import com.niyaj.ui.event.UiState.Success
import com.niyaj.ui.parameterProvider.CustomerPreviewData
import kotlinx.collections.immutable.toPersistentList
import org.junit.Rule
import org.junit.Test

class CustomerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val itemList = mutableStateOf(CustomerPreviewData.customerList)

    private val selectedItems = mutableStateListOf<Int>()
    private var showSearchBar = mutableStateOf(false)
    private var searchText = mutableStateOf("")

    @Test
    fun loadingState_whenStateIsLoading_showLoadingIndicator() {
        composeTestRule.apply {
            setContent {
                CustomerScreen(uiState = Loading)
            }

            onNodeWithText(CUSTOMER_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithContentDescription("loadingIndicator").assertIsDisplayed()
        }
    }

    @Test
    fun emptyState_whenStateIsEmpty_thenShowEmptyState() {
        composeTestRule.setContent {
            CustomerScreen(uiState = Empty)
        }

        composeTestRule.onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsDisplayed()
    }

    @Test
    fun successState_whenStateIsSuccess_thenShowList() {
        composeTestRule.apply {
            val customerList = itemList.value.take(4)

            setContent {
                CustomerScreen(uiState = Success(customerList))
            }

            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()

            customerList.forEach {
                onNodeWithTag(CUSTOMER_TAG.plus(it.customerId))
                    .assertIsDisplayed()
                    .assertHasClickAction()
            }

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(STANDARD_FAB_BUTTON)
                .assertIsDisplayed()
                .assertContentDescriptionEquals(CREATE_NEW_CUSTOMER)
        }
    }

    @Test
    fun itemSelected_whenPerformLongClick_thenShowSelectionCount() {
        composeTestRule.apply {
            setContent {
                CustomerScreen(
                    uiState = Success(itemList.value),
                    selectedItems = listOf(1),
                )
            }

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun itemSelected_whenPerformSelectAll_thenShowTotalSelectionCount() {
        composeTestRule.apply {
            setContent {
                CustomerScreen(
                    uiState = Success(itemList.value),
                    selectedItems = selectedItems.toList(),
                    onSelectAllClick = {
                        selectAllItems()
                    },
                    onDeselect = {
                        selectedItems.clear()
                    },
                    onItemClick = {
                        if (selectedItems.contains(it)) {
                            selectedItems.remove(it)
                        } else {
                            selectedItems.add(it)
                        }
                    },
                )
            }

            onNodeWithTag(CUSTOMER_TAG.plus(1))
                .assertIsDisplayed()
                .assertHasClickAction()
                .performTouchInput {
                    longClick()
                }

            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithText("${itemList.value.size} Selected").assertIsDisplayed()
            onNodeWithTag(NAV_EDIT_BTN).assertIsNotDisplayed()
            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed()

            onNodeWithTag(CLEAR_ICON)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithText(CUSTOMER_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun onSearch_whenClickSearch_thenShowSearchBarAndResult() {
        composeTestRule.apply {
            setContent {
                CustomerScreen(
                    uiState = Success(itemList.value),
                    searchText = searchText.value,
                    showSearchBar = showSearchBar.value,
                    onSearchClick = {
                        showSearchBar.value = true
                    },
                    onCloseSearchBar = {
                        // Close search bar
                        showSearchBar.value = false
                    },
                    onSearchTextChanged = {
                        onSearchTextChanged(it)
                    },
                )
            }

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertTextContains(CUSTOMER_SEARCH_PLACEHOLDER)
                .assertIsDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("John")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Composable
    private fun CustomerScreen(
        uiState: UiState<List<Customer>>,
        selectedItems: List<Int> = emptyList(),
        showSearchBar: Boolean = false,
        searchText: String = "",
        onItemClick: (Int) -> Unit = {},
        onCreateNewClick: () -> Unit = {},
        onEditClick: (Int) -> Unit = {},
        onDeleteClick: () -> Unit = {},
        onSettingsClick: () -> Unit = {},
        onSelectAllClick: () -> Unit = {},
        onClearSearchClick: () -> Unit = {},
        onSearchClick: () -> Unit = {},
        onSearchTextChanged: (String) -> Unit = {},
        onCloseSearchBar: () -> Unit = {},
        onBackClick: () -> Unit = {},
        onDeselect: () -> Unit = {},
    ) {
        PoposRoomTheme {
            CustomerScreenContent(
                uiState = uiState,
                selectedItems = selectedItems,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onClickSelectItem = onItemClick,
                onSearchTextChanged = onSearchTextChanged,
                onCloseSearchBar = onCloseSearchBar,
                onNavigateToScreen = {},
                onClickSearchIcon = onSearchClick,
                onClickClear = onClearSearchClick,
                onClickSelectAll = onSelectAllClick,
                onClickDeselect = onDeselect,
                onClickDelete = onDeleteClick,
                onClickBack = onBackClick,
                onClickCreateNew = onCreateNewClick,
                onClickEdit = onEditClick,
                onClickSettings = onSettingsClick,
                onNavigateToDetails = {},
            )
        }
    }

    private fun selectAllItems() {
        selectedItems.clear()
        selectedItems.addAll(itemList.value.map { it.customerId })
    }

    private fun onSearchTextChanged(text: String) {
        searchText.value += text
        itemList.value = itemList.value.searchCustomer(text).toPersistentList()
    }
}
