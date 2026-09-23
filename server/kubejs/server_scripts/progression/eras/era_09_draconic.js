// priority: -100
// Package 2 revision 2. Full item gating. No stage grants.
(function () {
  if (!global.ProgressionStages || !global.ProgressionStages.isValidStage("era_9")) throw new Error("[package2] Missing Package 1 stage: era_9");
  if (!global.Package2EraGatingStatus) throw new Error("[package2] Missing package2_validation.js");
  if (typeof AStages === "undefined") throw new Error("[package2] AStages unavailable");
  AStages.addRestrictionForMod("astages/era_9/mod_draconicevolution", "era_9", "draconicevolution")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_9/mod_brandonscore", "era_9", "brandonscore")
    .disableBlockInteraction();
  if (typeof RecipeMachineStage === "undefined") throw new Error("[package2] Legacy RMS binding unavailable");
  RecipeMachineStage.addRecipes("occultism:miner", [
    "occultism:miner/ores/draconium_ore"
  ], "era_9");
  RecipeMachineStage.addRecipes("draconicevolution:fusion_crafting", [
    "draconicevolution:awakened_draconium_block",
    "draconicevolution:components/awakened_core",
    "draconicevolution:components/chaotic_core",
    "draconicevolution:machines/awakened_crafting_injector",
    "draconicevolution:machines/chaotic_crafting_injector",
    "draconicevolution:machines/draconic_relay_crystal",
    "draconicevolution:machines/draconium_chest",
    "draconicevolution:machines/reactor_core",
    "draconicevolution:machines/reactor_injector",
    "draconicevolution:machines/reactor_stabilizer",
    "draconicevolution:machines/wyvern_crafting_injector",
    "draconicevolution:tools/advanced_dislocator",
    "draconicevolution:tools/chaotic_axe",
    "draconicevolution:tools/chaotic_bow",
    "draconicevolution:tools/chaotic_capacitor",
    "draconicevolution:tools/chaotic_chestpiece",
    "draconicevolution:tools/chaotic_hoe",
    "draconicevolution:tools/chaotic_pickaxe",
    "draconicevolution:tools/chaotic_shovel",
    "draconicevolution:tools/chaotic_staff",
    "draconicevolution:tools/chaotic_staff_alt",
    "draconicevolution:tools/chaotic_sword",
    "draconicevolution:tools/draconic_axe",
    "draconicevolution:tools/draconic_bow",
    "draconicevolution:tools/draconic_capacitor",
    "draconicevolution:tools/draconic_chestpiece",
    "draconicevolution:tools/draconic_hoe",
    "draconicevolution:tools/draconic_pickaxe",
    "draconicevolution:tools/draconic_shovel",
    "draconicevolution:tools/draconic_staff",
    "draconicevolution:tools/draconic_sword",
    "draconicevolution:tools/wyvern_axe",
    "draconicevolution:tools/wyvern_bow",
    "draconicevolution:tools/wyvern_capacitor",
    "draconicevolution:tools/wyvern_chestpiece",
    "draconicevolution:tools/wyvern_hoe",
    "draconicevolution:tools/wyvern_pickaxe",
    "draconicevolution:tools/wyvern_shovel",
    "draconicevolution:tools/wyvern_sword"
  ], "era_9");
  global.Package2EraGatingStatus.completed.push("era_9");
})();
