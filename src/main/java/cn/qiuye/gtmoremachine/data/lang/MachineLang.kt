package cn.qiuye.gtmoremachine.data.lang

import com.tterrag.registrate.providers.RegistrateLangProvider

object MachineLang {

    fun init(provider: RegistrateLangProvider) {
        provider.add("gtmoremachine.universal.disabled", "Multiblock Sharing §4Disabled")
        provider.add(
            "block.gtmoremachine.capacity_component.tooltip_empty",
            "§7For filling structural gaps in the Wireless Energy Storage Module",
        )
        provider.add("block.gtmoremachine.capacity_component.tooltip_filled", "§cCapacity component capacity: §f%d EU")
        provider.add(
            "block.gtmoremachine.capacity_component.tooltip_passive_drain",
            "§cCapacity component passive energy consumption: §f%d EU",
        )
        provider.add(
            "gtmoremachine.machine.huge_item_bus.import.tooltip",
            "Inputs items for multiblock structures, with each slot able to store up to 2^31-1 items.",
        )
        provider.add(
            "gtmoremachine.machine.huge_item_bus.export.tooltip",
            "Outputs items for multiblock structures, with each slot able to store up to 2^31-1 items.",
        )
        provider.add("gtmoremachine.machine.huge_item_bus.tooltip.1", "Returns all items to the container in front.")
        provider.add("gtmoremachine.machine.huge_item_bus.tooltip.2", "Item slots: %s/%s")
        provider.add("gtmoremachine.machine.huge_item_bus.tooltip.3", "Empty")
        provider.add("gtmoremachine.machine.huge_dual_hatch.tooltip.2", "Fluid slots: %s/%s")
        provider.add("gui.gtmoremachine.share_inventory.title", "Catalyst")
        provider.add("gui.gtmoremachine.share_inventory.desc.0", "Open Catalyst Slot")
        provider.add(
            "gui.gtmoremachine.share_inventory.desc.1",
            "In the catalyst slot, only non-consumable items can participate in the synthesis.",
        )
        provider.add(
            "gui.gtmoremachine.share_inventory.desc.2",
            "Common items can be automatically input by placing a container in front of the input bus.",
        )
        provider.add("gui.gtmoremachine.me_export_buffer.item_status.full", "Item storage space is full.")
        provider.add("gui.gtmoremachine.me_export_buffer.fluid_status.full", "Fluid storage space is full.")
        provider.add("gtmoremachine.machine.wireless_monitor.tooltip.0", "Owner: %s")
        provider.add("gtmoremachine.machine.wireless_monitor.tooltip.net_power", "Average Net Power: %s")
        provider.add("gtmoremachine.machine.wireless_monitor.tooltip.net_cwu", "Average Net CWU: %s")
        provider.add(
            "gtmoremachine.machine.wireless_monitor.tooltip.statistics.energy",
            "Electricity Statistics：%s     Display Format：%s    Power status：%s\nSorting rules：%s",
        )
        provider.add(
            "gtmoremachine.machine.wireless_monitor.tooltip.statistics.cwu",
            "CWU Statistics：%s     Display Format：%s    CWU status：%s",
        )
        provider.add("gtmoremachine.machine.wireless_energy_hatch.tooltip.bind", "Bind to: %s")
        provider.add("gtmoremachine.machine.wireless_energy_hatch.tooltip.unbind", "Unbind!")
        provider.add("gtmoremachine.machine.wireless_energy_hatch.tooltip.1", "No owner.")
        provider.add("gtmoremachine.machine.wireless_energy_hatch.tooltip.2", "Bind to: %s")
        provider.add("gtmoremachine.machine.wireless_energy_hatch.tooltip.3", "Bind to unknow user: %s")
        provider.add(
            "gtmoremachine.machine.wireless_energy_monitor.tooltip",
            "You can monitor the total energy and useage.",
        )
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.1", "Total Energy: %s EU (%s A %s§r)")
        provider.add(
            "gtmoremachine.machine.wireless_energy_monitor.tooltip.2",
            "Single Transfer Limit：%s EU/t (%s A %s§r)",
        )
        provider.add(
            "gtmoremachine.machine.wireless_energy_monitor.tooltip.last_minute",
            "Last minute: §a%s EU/t (%s A %s§r)",
        )
        provider.add(
            "gtmoremachine.machine.wireless_energy_monitor.tooltip.last_hour",
            "Last hour: §a%s EU/t (%s A %s§r)",
        )
        provider.add(
            "gtmoremachine.machine.wireless_energy_monitor.tooltip.last_day",
            "Last day: §a%s EU/t (%s A %s§r)",
        )
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.now", "Now: §a%s EU/t (%sA %s§r)")
        provider.add(
            "gtmoremachine.machine.wireless_energy_monitor.tooltip.time_to_fill",
            "You will never be satisfied with this for the rest of your life",
        )
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.all", "Global")
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.team", "Team")
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.science", "Science")
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.unit", "Unit")
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_all", "ALL")
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_in", "IN")
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.power_out", "OUT")
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.ascending", "Ascending")
        provider.add("gtmoremachine.machine.wireless_energy_monitor.tooltip.descendingorder", "Descendingorder")
        provider.add("gtmoremachine.machine.wireless_cwu_monitor.tooltip.1", "Total CWU: %s CWU")
        provider.add("gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_minute", "Last minute: %s CWU")
        provider.add("gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_hour", "Last hour: %s CWU")
        provider.add("gtmoremachine.machine.wireless_cwu_monitor.tooltip.last_day", "Last day: %s CWU")
        provider.add("gtmoremachine.machine.wireless_cwu_monitor.tooltip.now", "Now: %s CWU")
        provider.add(
            "gtmoremachine.machine.wireless_energy_interface.tooltip",
            "Receives energy and sends it to the power network",
        )
        provider.add("gtmoremachine.machine.wireless_energy_cover.tooltip.1", "The Wireless Energy Reciver unbind!")
        provider.add("gtmoremachine.machine.wireless_energy_cover.tooltip.2", "The Wireless Energy Reciver bind to: %s")
        provider.add(
            "gtmoremachine.machine.wireless_energy_cover.tooltip.3",
            "The Wireless Energy Reciver bind to unknow user: %s",
        )
        provider.add("gtmoremachine.machine.energy_hatch.input.tooltip", "Energy Input for Multiblocks")
        provider.add("gtmoremachine.machine.energy_hatch.output.tooltip", "Energy Output for Multiblocks")
        provider.add(
            "gtmoremachine.machine.energy_hatch.target.tooltip",
            "Large Amount Of Energy Input for Multiblocks",
        )
        provider.add(
            "gtmoremachine.machine.energy_hatch.source.tooltip",
            "Large Amount Of Energy Output for Multiblocks",
        )
        provider.add(
            "gtmoremachine.machine.wireless_energy_hatch.input.tooltip",
            "You can bind or change the owner by left-click the Energy Hatch with Data Stick,or right-click to unbind.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_energy_hatch.output.tooltip",
            "You can bind or change the owner by left-click the Dynoma Hatch with Data Stick,or right-click to unbind.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_energy_hatch.target.tooltip",
            "You can bind or change the owner by left-click the Laser Target Hatch with Data Stick,or right-click to unbind.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_energy_hatch.source.tooltip",
            "You can bind or change the owner by left-click the Laser Source Hatch with Data Stick,or right-click to unbind.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_computation_transmitter_hatch.tooltip.1",
            "Output computational power data from the multiblock structure.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_computation_transmitter_hatch.tooltip.2",
            "Need to bind the wireless computational power target Hatch and the wireless computational power source Hatch by right-clicking with a flash memory.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_computation_transmitter_hatch.tobind",
            "Source Hatch data reading completed, please right-click the target Hatch to bind.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_computation_receiver_hatch.tooltip.1",
            "Input computational power data for the multiblock structure.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_computation_receiver_hatch.tooltip.2",
            "gtmoremachine.machine.wireless_computation_receiver_hatch.tooltip.2",
        )
        provider.add(
            "gtmoremachine.machine.wireless_computation_receiver_hatch.tobind",
            "Target Hatch data reading completed, please right-click the source Hatch to bind.",
        )
        provider.add(
            "gtmoremachine.machine.wireless_computation_hatch.binded",
            "Wireless computational power data Hatch binding completed.",
        )
        provider.add("gtmoremachine.machine.transmitter_hatch.bind", "Already bound to the receiving Hatch (%s).")
        provider.add("gtmoremachine.machine.transmitter_hatch.unbind", "Not bound to the receiving Hatch.")
        provider.add("gtmoremachine.machine.receiver_hatch.bind", "Already bound to the launch Hatch (%s).")
        provider.add("gtmoremachine.machine.receiver_hatch.unbind", "Not bound to the launch Hatch")
    }
}
