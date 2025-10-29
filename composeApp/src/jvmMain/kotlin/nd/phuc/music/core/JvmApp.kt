package nd.phuc.music.core

object JvmApp {
    private val onAppDestroyListener = mutableListOf<() -> Unit>()
    fun getOnAppDestroyListener(): List<() -> Unit> = onAppDestroyListener

    fun addOnAppDestroyListener(listener: () -> Unit) {
        onAppDestroyListener.add(listener)
    }
}