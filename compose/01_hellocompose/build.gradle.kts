plugins {
  id("comm.app-compose-module")
}

android {
  defaultConfig {
    namespace = "com.hsicen.hellocompose"
  }
}

dependencies {
  implementation(Deps.accompanistInsets)
  implementation(Deps.accompanistPager)
    implementation("androidx.compose.material3:material3:1.4.0")
}