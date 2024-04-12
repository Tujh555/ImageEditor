package api

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

@OptIn(InternalCoroutinesApi::class)
object CommonApiHolder {
    private var _api: CommonApi? = null
    private var _dependencies: PlatformDependencies? = null
    private val synchronizedObject = SynchronizedObject()

    internal val dependencies: PlatformDependencies
        get() = requireNotNull(_dependencies) {
            "Call init first"
        }

    fun get(): CommonApi = requireNotNull(_api) {
        "Call init first"
    }

    fun init(dependenciesFactory: DependenciesFactory) {
        if (_api != null) {
            return
        }

        synchronized(synchronizedObject) {
            if (_api == null) {
                val dependencies = dependenciesFactory.create()
                val api = CommonApiImpl(dependencies)
                _api = api
            }
        }
    }
}