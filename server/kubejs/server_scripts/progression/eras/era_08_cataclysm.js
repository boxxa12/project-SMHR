// priority: -100
// Package 2 revision 2. Full item gating. No stage grants.
(function () {
  if (!global.ProgressionStages || !global.ProgressionStages.isValidStage("era_8")) throw new Error("[package2] Missing Package 1 stage: era_8");
  if (!global.Package2EraGatingStatus) throw new Error("[package2] Missing package2_validation.js");
  if (typeof AStages === "undefined") throw new Error("[package2] AStages unavailable");
  AStages.addRestrictionForMod("astages/era_8/mod_cataclysm", "era_8", "cataclysm")
    .disableBlockInteraction();
  AStages.addRestrictionForMod("astages/era_8/mod_cataclysm_dimension", "era_8", "cataclysm_dimension")
    .disableBlockInteraction();
  // Retains the enabled optional netherite bee product gate of the supplied Package 2. No unverified comb ID is invented.
  AStages.addRestrictionForItem("astages/era_8/item_mobees_netherite_bit", "era_8", "mobees:netherite_bit").disableBlockInteraction();
  global.Package2EraGatingStatus.completed.push("era_8");
})();
