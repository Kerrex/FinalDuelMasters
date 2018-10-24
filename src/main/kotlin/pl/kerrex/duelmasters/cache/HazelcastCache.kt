package pl.riscosoftware.cache

import com.hazelcast.config.Config
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.instance.HazelcastInstanceFactory

class HazelcastCache {
    val hazelcastInstance: HazelcastInstance

    init {
        val cfg = Config("hazelcastInstance")
        val network = cfg.networkConfig
        network.join.tcpIpConfig.isEnabled = false
        network.join.multicastConfig.isEnabled = false

        hazelcastInstance = HazelcastInstanceFactory.getOrCreateHazelcastInstance(cfg)
    }

    companion object {
        const val GAMES = "games"
    }
}