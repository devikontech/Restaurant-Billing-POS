@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("popos.android.library")
    id("popos.android.library.jacoco")
    id("popos.android.hilt")
}

android {
    namespace = "com.niyaj.core.worker"
}

dependencies {

    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:notifications"))
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.work.ktx)
    kapt(libs.hilt.ext.compiler)

    testImplementation(project(":core:testing"))
}
