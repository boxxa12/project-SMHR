// priority: -100
// Package 2 revision 2. Full item gating. No stage grants.
(function () {
  if (!global.ProgressionStages || !global.ProgressionStages.isValidStage("era_10")) throw new Error("[package2] Missing Package 1 stage: era_10");
  if (!global.Package2EraGatingStatus) throw new Error("[package2] Missing package2_validation.js");
  if (typeof AStages === "undefined") throw new Error("[package2] AStages unavailable");
  var registry = Java.loadClass("net.minecraftforge.registries.ForgeRegistries").ITEMS;
  var RL = Java.loadClass("net.minecraft.resources.ResourceLocation");
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:adaptive_biomass"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_adaptive_biomass", "era_10", "kubejs:adaptive_biomass").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:adaptive_biomass");
  }
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:incomplete_nano_alloy"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_incomplete_nano_alloy", "era_10", "kubejs:incomplete_nano_alloy").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:incomplete_nano_alloy");
  }
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:nano_alloy"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_nano_alloy", "era_10", "kubejs:nano_alloy").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:nano_alloy");
  }
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:nanomachine_core"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_nanomachine_core", "era_10", "kubejs:nanomachine_core").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:nanomachine_core");
  }
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:nanomachine_fabricator"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_nanomachine_fabricator", "era_10", "kubejs:nanomachine_fabricator").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:nanomachine_fabricator");
  }
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:nanomachine_logic_matrix"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_nanomachine_logic_matrix", "era_10", "kubejs:nanomachine_logic_matrix").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:nanomachine_logic_matrix");
  }
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:nanomachine_swarm"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_nanomachine_swarm", "era_10", "kubejs:nanomachine_swarm").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:nanomachine_swarm");
  }
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:quantum_substrate"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_quantum_substrate", "era_10", "kubejs:quantum_substrate").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:quantum_substrate");
  }
  // Frozen Era 10 designed ID; register the restriction only if the actual item exists. This package does not create it.
  if (registry.containsKey(new RL("kubejs:reactive_nanite_catalyst"))) {
    AStages.addRestrictionForItem("astages/era_10/item_kubejs_reactive_nanite_catalyst", "era_10", "kubejs:reactive_nanite_catalyst").disableBlockInteraction();
  } else {
    global.Package2EraGatingStatus.pending.push("Unimplemented Era 10 item: kubejs:reactive_nanite_catalyst");
  }
  global.Package2EraGatingStatus.completed.push("era_10");
})();
