package cn.qiuye.gtmoremachine.utils

import cn.qiuye.gtmoremachine.GTmm

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI
import dev.ftb.mods.ftbteams.api.Team

import java.util.*
import java.util.function.Function

object TeamUtils {
    @JvmStatic
    fun getTeamUUID(playerUUID: UUID): UUID {
        if (GTmm.Mods.isFTBTeamsLoaded() && FTBTeamsAPI.api().isManagerLoaded) {
            val team = FTBTeamsAPI.api().manager.getTeamForPlayerID(playerUUID)
            return team.map(Function { obj: Team? -> obj!!.teamId }).orElse(playerUUID)
        } else {
            return playerUUID
        }
    }

    @JvmStatic
    fun getName(player: Player): Component {
        if (GTmm.Mods.isFTBTeamsLoaded() && FTBTeamsAPI.api().isManagerLoaded) {
            val team = FTBTeamsAPI.api().manager.getTeamForPlayerID(player.getUUID())
            if (team.isPresent) return team.get().name
        }
        return player.name
    }

    @JvmStatic
    fun getName(level: Level, playerUUID: UUID): Component {
        if (GTmm.Mods.isFTBTeamsLoaded() && FTBTeamsAPI.api().isManagerLoaded) {
            val team = FTBTeamsAPI.api().manager.getTeamForPlayerID(playerUUID)
            if (team.isPresent) {
                return team.get().name
            }
        }
        val player = level.getPlayerByUUID(playerUUID)
        if (player != null) return player.name
        return Component.literal(playerUUID.toString())
    }

    @JvmStatic
    fun hasOwner(level: Level, playerUUID: UUID): Boolean {
        if (GTmm.Mods.isFTBTeamsLoaded() && FTBTeamsAPI.api().isManagerLoaded) {
            val team = FTBTeamsAPI.api().manager.getTeamForPlayerID(playerUUID)
            return if (team.isPresent) {
                true
            } else {
                (level.getPlayerByUUID(playerUUID) != null)
            }
        } else {
            return (level.getPlayerByUUID(playerUUID) != null)
        }
    }
}
