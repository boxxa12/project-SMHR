// priority: -100
// Package 2 revision 2. Full item gating. No stage grants.
(function () {
  if (!global.ProgressionStages || !global.ProgressionStages.isValidStage("era_4")) throw new Error("[package2] Missing Package 1 stage: era_4");
  if (!global.Package2EraGatingStatus) throw new Error("[package2] Missing package2_validation.js");
  if (typeof AStages === "undefined") throw new Error("[package2] AStages unavailable");
  AStages.addRestrictionForMod("astages/era_4/mod_forestry", "era_4", "forestry")
    .ignoreItems("forestry:gear_copper")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_4/mod_mobees", "era_4", "mobees")
    .ignoreItems("mobees:certus_bit", "mobees:bee_comb_certus", "mobees:netherite_bit")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_4/mod_beemastery", "era_4", "beemastery")
    .disableBlockInteraction();
  if (typeof RecipeMachineStage === "undefined") throw new Error("[package2] Legacy RMS binding unavailable");
  RecipeMachineStage.addRecipes("thermal:pulverizer", [
    "thermal:machines/pulverizer/draconic_block",
    "thermal:machines/pulverizer/legendary_block",
    "thermal:machines/pulverizer/mutated_iron_block",
    "thermal:machines/pulverizer/reinforced_block"
  ], "era_4");
  RecipeMachineStage.addRecipes("thermal:smelter", [
    "thermal:machines/smelter/reinforced_ingot_from_draconic"
  ], "era_4");
  RecipeMachineStage.addRecipes("thermal:centrifuge", [
    "thermal:machines/centrifuge/draconic_comb",
    "thermal:machines/centrifuge/legendary_comb",
    "thermal:machines/centrifuge/mutated_comb",
    "thermal:machines/centrifuge/reinforced_comb",
    "thermal:machines/centrifuge/witheria_comb"
  ], "era_4");
  global.Package2EraGatingStatus.completed.push("era_4");
})();
