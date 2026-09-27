plugins {
    id("com.android.application") version "9.3.3" apply false
    // Com AGP 9 a compilação de Kotlin é feita pelo built-in Kotlin (o plugin
    // org.jetbrains.kotlin.android não deve ser aplicado nos módulos).
    // Este plugin base é declarado apenas (apply false) para colocar o
    // Kotlin Gradle Plugin 2.4.20 no classpath — o AGP usa a versão mais
    // alta disponível. Mantenha esta versão igual à do plugin do Compose abaixo.
    id("org.jetbrains.kotlin") version "2.4.20" apply false
    // O plugin do Compose Compiler deve ter a MESMA versão do Kotlin acima.
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20" apply false
    id("com.google.devtools.ksp") version "2.3.12" apply false
}
