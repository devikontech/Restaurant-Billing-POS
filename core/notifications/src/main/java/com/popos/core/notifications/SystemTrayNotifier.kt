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

package com.popos.core.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.popos.core.notifications.utils.DELETION_NOTIFICATION_ID
import com.popos.core.notifications.utils.ORDER_PRINT_NOTIFICATION_ID
import com.popos.core.notifications.utils.REPORT_NOTIFICATION_ID
import com.popos.core.notifications.utils.deletionWorkNotification
import com.popos.core.notifications.utils.printOrderNotification
import com.popos.core.notifications.utils.reportWorkNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 */
@Singleton
class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun showDataDeletionNotification() = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(DELETION_NOTIFICATION_ID, deletionWorkNotification())
    }

    override fun showReportGenerationNotification() = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(REPORT_NOTIFICATION_ID, reportWorkNotification())
    }

    override fun showPrintingErrorNotification(orderId: Int) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(ORDER_PRINT_NOTIFICATION_ID, printOrderNotification(orderId))
    }
}
