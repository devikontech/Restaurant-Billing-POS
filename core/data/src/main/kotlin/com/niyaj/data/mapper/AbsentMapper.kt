/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.data.mapper

import com.niyaj.common.utils.toDate
import com.niyaj.database.model.AbsentEntity
import com.niyaj.model.Absent

fun Absent.toEntity(): AbsentEntity {
    return AbsentEntity(
        absentId = this.absentId,
        employeeId = this.employeeId,
        absentReason = this.absentReason,
        absentDate = this.absentDate,
        createdAt = this.createdAt.toDate,
        updatedAt = this.updatedAt?.toDate
    )
}