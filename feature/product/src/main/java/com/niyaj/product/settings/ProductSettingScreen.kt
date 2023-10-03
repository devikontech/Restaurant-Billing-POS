package com.niyaj.product.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.product.destinations.DecreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ExportProductScreenDestination
import com.niyaj.product.destinations.ImportProductScreenDestination
import com.niyaj.product.destinations.IncreaseProductPriceScreenDestination
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProductSettingScreen(
    navController: NavController,
    exportRecipient: ResultRecipient<ExportProductScreenDestination, String>,
    importRecipient: ResultRecipient<ImportProductScreenDestination, String>,
    increaseRecipient: ResultRecipient<IncreaseProductPriceScreenDestination, String>,
    decreaseRecipient: ResultRecipient<DecreaseProductPriceScreenDestination, String>,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    exportRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }
    importRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }
    increaseRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }
    decreaseRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        title = "Product Settings",
        snackbarHostState = snackbarState,
        showBackButton = true,
        showBottomBar = false,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ){
            item("IncreaseProductPrice") {
                SettingsCard(
                    title = "Increase Product Price",
                    subtitle = "Click here to increase product price.",
                    icon = Icons.Default.ControlPoint,
                    onClick = {
                        navController.navigate(IncreaseProductPriceScreenDestination)
                    }
                )
            }

            item("decreaseProductPrice") {
                SettingsCard(
                    title = "Decrease Product Price",
                    subtitle = "Click here to decrease product price.",
                    icon = Icons.Default.RemoveCircleOutline,
                    onClick = {
                        navController.navigate(DecreaseProductPriceScreenDestination)
                    }
                )
            }

            item("ImportProduct") {
                SettingsCard(
                    title = "Import Product",
                    subtitle = "Click here to import product from file.",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportProductScreenDestination())
                    }
                )
            }

            item("ExportProduct") {
                SettingsCard(
                    title = "Export Product",
                    subtitle = "Click here to export products to file.",
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(ExportProductScreenDestination())
                    }
                )
            }
        }
    }
}