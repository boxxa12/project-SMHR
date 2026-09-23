// priority: -100
// Package 2 revision 2. Full item gating. No stage grants.
(function () {
  if (!global.ProgressionStages || !global.ProgressionStages.isValidStage("era_5")) throw new Error("[package2] Missing Package 1 stage: era_5");
  if (!global.Package2EraGatingStatus) throw new Error("[package2] Missing package2_validation.js");
  if (typeof AStages === "undefined") throw new Error("[package2] AStages unavailable");
  AStages.addRestrictionForMod("astages/era_5/mod_ae2", "era_5", "ae2")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_5/mod_advanced_ae", "era_5", "advanced_ae")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_5/mod_expatternprovider", "era_5", "expatternprovider")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_5/mod_extendedae_plus", "era_5", "extendedae_plus")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_5/mod_appmek", "era_5", "appmek")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_5/mod_ae2craftpriority", "era_5", "ae2craftpriority")
    .disableBlockInteraction();
  // Future AE2 bee product; does not open with the rest of Forestry in Era 4.
  AStages.addRestrictionForItem("astages/era_5/item_mobees_certus_bit", "era_5", "mobees:certus_bit").disableBlockInteraction();
  // Future AE2 bee product; does not open with the rest of Forestry in Era 4.
  AStages.addRestrictionForItem("astages/era_5/item_mobees_bee_comb_certus", "era_5", "mobees:bee_comb_certus").disableBlockInteraction();
  if (typeof RecipeMachineStage === "undefined") throw new Error("[package2] Legacy RMS binding unavailable");
  RecipeMachineStage.addRecipes("create:milling", [
    "create:milling/compat/ae2/certus_quartz",
    "create:milling/compat/ae2/ender_pearl",
    "create:milling/compat/ae2/fluix_crystal",
    "create:milling/compat/ae2/sky_stone_block"
  ], "era_5");
  RecipeMachineStage.addRecipes("create:mixing", [
    "create:mixing/compat/ae2/fluix_crystal"
  ], "era_5");
  RecipeMachineStage.addRecipes("powah:energizing", [
    "powah:energizing/compat/certus_quartz_to_charged_certus_quartz"
  ], "era_5");
  RecipeMachineStage.addRecipes("mekanism:crushing", [
    "advanced_ae:quantum_infused_dust_crushed",
    "mekanism:compat/ae2/certus_crystal_to_dust",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/block_to_chiseled_block",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/chiseled_block_to_pillar",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/chiseled_slab_to_pillar_slab",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/chiseled_stairs_to_pillar_stairs",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/chiseled_wall_to_pillar_wall",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/pillar_slab_to_slab",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/pillar_stairs_to_stairs",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/pillar_to_block",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/pillar_wall_to_wall",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/slab_to_chiseled_slab",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/stairs_to_chiseled_stairs",
    "mekanism:compat/ae2/decorative/certus_quartz/crushing/wall_to_chiseled_wall",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/brick_slab_to_smooth_slab",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/brick_stairs_to_smooth_stairs",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/brick_to_smooth",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/brick_wall_to_smooth_wall",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/small_brick_slab_to_brick_slab",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/small_brick_stairs_to_brick_stairs",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/small_brick_to_brick",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/small_brick_wall_to_brick_wall",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/smooth_chest_to_chest",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/smooth_slab_to_slab",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/smooth_stairs_to_stairs",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/smooth_to_stone",
    "mekanism:compat/ae2/decorative/sky_stone/crushing/smooth_wall_to_wall",
    "mekanism:compat/ae2/fluix_crystal_to_dust",
    "mekanism:compat/ae2/sky_stone_to_dust"
  ], "era_5");
  RecipeMachineStage.addRecipes("mekanism:enriching", [
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/block_to_pillar",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/chiseled_block_to_block",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/chiseled_slab_to_slab",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/chiseled_stairs_to_stairs",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/chiseled_wall_to_wall",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/pillar_slab_to_chiseled_slab",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/pillar_stairs_to_chiseled_stairs",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/pillar_to_chiseled_block",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/pillar_wall_to_chiseled_wall",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/slab_to_pillar_slab",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/stairs_to_pillar_stairs",
    "mekanism:compat/ae2/decorative/certus_quartz/enriching/wall_to_pillar_wall",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/brick_slab_to_small_brick_slab",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/brick_stairs_to_small_brick_stairs",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/brick_to_small_brick",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/brick_wall_to_small_brick_wall",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/chest_to_smooth_chest",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/slab_to_smooth_slab",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/smooth_slab_to_brick_slab",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/smooth_stairs_to_brick_stairs",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/smooth_to_brick",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/smooth_wall_to_brick_wall",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/stairs_to_smooth_stairs",
    "mekanism:compat/ae2/decorative/sky_stone/enriching/wall_to_smooth_wall",
    "mekanism:compat/ae2/sky_stone_dust_to_sky_stone"
  ], "era_5");
  RecipeMachineStage.addRecipes("ae2:charger", [
    "ae2:charger/charged_certus_quartz_crystal",
    "ae2:charger/guide",
    "ae2:charger/meteorite_compass"
  ], "era_5");
  RecipeMachineStage.addRecipes("ae2:inscriber", [
    "advanced_ae:quantum_infused_dust",
    "advanced_ae:quantum_processor",
    "advanced_ae:quantum_processor_press",
    "advanced_ae:quantum_processor_press_from_iron",
    "advanced_ae:quantum_processor_print",
    "ae2:inscriber/calculation_processor",
    "ae2:inscriber/calculation_processor_press",
    "ae2:inscriber/calculation_processor_print",
    "ae2:inscriber/certus_quartz_dust",
    "ae2:inscriber/ender_dust",
    "ae2:inscriber/engineering_processor",
    "ae2:inscriber/engineering_processor_press",
    "ae2:inscriber/engineering_processor_print",
    "ae2:inscriber/fluix_dust",
    "ae2:inscriber/logic_processor",
    "ae2:inscriber/logic_processor_press",
    "ae2:inscriber/logic_processor_print",
    "ae2:inscriber/silicon_press",
    "ae2:inscriber/silicon_print",
    "ae2:inscriber/sky_stone_dust"
  ], "era_5");
  global.Package2EraGatingStatus.completed.push("era_5");
})();
