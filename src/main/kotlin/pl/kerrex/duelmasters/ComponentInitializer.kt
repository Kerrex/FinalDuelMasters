package pl.kerrex.duelmasters

import com.hazelcast.core.HazelcastInstance
import org.kodein.di.Kodein
import org.kodein.di.conf.global
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import pl.kerrex.duelmasters.common.repository.GameRepository
import pl.kerrex.duelmasters.lobby.handler.AvailableGamesHandler
import pl.kerrex.duelmasters.lobby.handler.JoinGameHandler
import pl.kerrex.duelmasters.lobby.handler.RegistrationHandler
import pl.kerrex.duelmasters.lobby.service.CardProvider
import pl.kerrex.duelmasters.lobby.service.LobbyService
import pl.riscosoftware.Configuration
import pl.riscosoftware.cache.HazelcastCache

fun initContext() {
    Kodein.global.addConfig {
        registerSingletons()
        registerServices()
        registerHandlers()
        registerRepositories()
    }

}

fun Kodein.MainBuilder.registerRepositories() {
    bind<GameRepository>() with provider { GameRepository(instance()) }
}

private fun Kodein.MainBuilder.registerHandlers() {
    bind<RegistrationHandler>() with provider { RegistrationHandler(instance(), instance()) }
    bind<JoinGameHandler>() with provider { JoinGameHandler(instance(), instance()) }
    bind<AvailableGamesHandler>() with provider { AvailableGamesHandler(instance()) }
}

private fun Kodein.MainBuilder.registerServices() {
    bind<CardProvider>() with provider { CardProvider() }
    bind<LobbyService>() with provider { LobbyService(instance()) }
}

private fun Kodein.MainBuilder.registerSingletons() {
    bind<HazelcastInstance>() with singleton { HazelcastCache().hazelcastInstance }
    bind<Configuration>() with singleton { Configuration() }
}


