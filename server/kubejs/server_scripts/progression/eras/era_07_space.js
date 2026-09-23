// priority: -100
// Package 2 revision 2. Full item gating. No stage grants.
(function () {
  if (!global.ProgressionStages || !global.ProgressionStages.isValidStage("era_7")) throw new Error("[package2] Missing Package 1 stage: era_7");
  if (!global.Package2EraGatingStatus) throw new Error("[package2] Missing package2_validation.js");
  if (typeof AStages === "undefined") throw new Error("[package2] AStages unavailable");
  AStages.addRestrictionForMod("astages/era_7/mod_ad_astra", "era_7", "ad_astra")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_7/mod_adtetra", "era_7", "adtetra")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_7/mod_planetsplus", "era_7", "planetsplus")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_7/mod_create_ad_astra_recipes", "era_7", "create_ad_astra_recipes")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_7/mod_crushedastra", "era_7", "crushedastra")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_7/mod_thermal_and_space", "era_7", "thermal_and_space")
    .disableBlockInteraction();
  if (typeof RecipeMachineStage === "undefined") throw new Error("[package2] Legacy RMS binding unavailable");
  RecipeMachineStage.addRecipes("create:milling", [
    "create:milling/venus_sandstone"
  ], "era_7");
  RecipeMachineStage.addRecipes("create:crushing", [
    "create:crushing/deepslate_calorite_ore",
    "create:crushing/deepslate_desh_ore",
    "create:crushing/deepslate_ice_shard_ore",
    "create:crushing/deepslate_ostrum_ore",
    "create:crushing/glacio_coal_ore",
    "create:crushing/glacio_copper_ore",
    "create:crushing/glacio_ice_shard_ore",
    "create:crushing/glacio_iron_ore",
    "create:crushing/glacio_lapis_ore",
    "create:crushing/mars_diamond_ore",
    "create:crushing/mars_ice_shard_ore",
    "create:crushing/mars_iron_ore",
    "create:crushing/mars_ostrum_ore",
    "create:crushing/mercury_iron_ore",
    "create:crushing/moon_cheese_ore",
    "create:crushing/moon_desh_ore",
    "create:crushing/moon_ice_shard_ore",
    "create:crushing/moon_iron_ore",
    "create:crushing/ores/deepslate_calorite_ore",
    "create:crushing/ores/deepslate_desh_ore",
    "create:crushing/ores/deepslate_ostrum_ore",
    "create:crushing/ores/mars_ostrum_ore",
    "create:crushing/ores/moon_desh_ore",
    "create:crushing/ores/venus_calorite_ore",
    "create:crushing/raw_materials/raw_calorite",
    "create:crushing/raw_materials/raw_calorite_block",
    "create:crushing/raw_materials/raw_desh",
    "create:crushing/raw_materials/raw_desh_block",
    "create:crushing/raw_materials/raw_ostrum",
    "create:crushing/raw_materials/raw_ostrum_block",
    "create:crushing/venus_calorite_ore",
    "create:crushing/venus_coal_ore",
    "create:crushing/venus_diamond_ore",
    "create:crushing/venus_gold_ore"
  ], "era_7");
  RecipeMachineStage.addRecipes("create:pressing", [
    "create:pressing/calorite_ingot",
    "create:pressing/desh_ingot",
    "create:pressing/ostrum_ingot",
    "create:pressing/steel_ingot"
  ], "era_7");
  RecipeMachineStage.addRecipes("create:mechanical_crafting", [
    "create_ad_astra_recipes:tier_1",
    "create_ad_astra_recipes:tier_2",
    "create_ad_astra_recipes:tier_3",
    "create_ad_astra_recipes:tier_4"
  ], "era_7");
  RecipeMachineStage.addRecipes("create:cutting", [
    "create:cutting/aeronos_caps",
    "create:cutting/glacian_log",
    "create:cutting/stripped_glacian_log",
    "create:cutting/strophar_caps"
  ], "era_7");
  RecipeMachineStage.addRecipes("create:splashing", [
    "create:splashing/crushed_calorite_ore",
    "create:splashing/crushed_desh_ore",
    "create:splashing/crushed_ostrum_ore"
  ], "era_7");
  RecipeMachineStage.addRecipes("thermal:press", [
    "thermal:machines/press/packing2x2/press_venus_sandstone_packing",
    "thermal:machines/press/press_calorite_ingot_to_plate",
    "thermal:machines/press/press_desh_ingot_to_plate",
    "thermal:machines/press/press_ostrum_ingot_to_plate",
    "thermal:machines/press/unpacking/press_venus_sandstone_unpacking"
  ], "era_7");
  RecipeMachineStage.addRecipes("thermal:pulverizer", [
    "thermal:machines/pulverizer/pulverizer_venus_sandstone",
    "thermal:machines/pulverizer/pulverizer_venus_sandstone_slab"
  ], "era_7");
  RecipeMachineStage.addRecipes("thermal:centrifuge", [
    "thermal_and_space:machine/centrifuge/centrifuge_mars_oil_sand",
    "thermal_and_space:machine/centrifuge/centrifuge_moon_oil_sand",
    "thermal_and_space:machine/centrifuge/centrifuge_venus_oil_sand"
  ], "era_7");
  RecipeMachineStage.addRecipes("thermal:sawmill", [
    "thermal:machines/sawmill/sawmill_aeronos_caps",
    "thermal:machines/sawmill/sawmill_glacian_logs",
    "thermal:machines/sawmill/sawmill_strophar_caps"
  ], "era_7");
  RecipeMachineStage.addRecipes("mekanism:crushing", [
    "mekanism:crushing/venus_sandstone_to_venus_sand"
  ], "era_7");
  RecipeMachineStage.addRecipes("mekanism:enriching", [
    "mekanism:enriching/ice_shard_or_to_ice_shards"
  ], "era_7");
  global.Package2EraGatingStatus.completed.push("era_7");
})();
