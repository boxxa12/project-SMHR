// progression/stage_backend.js
//
// Central wrapper around the AStages server-side API.
//
// AStages server-side signatures:
//   AStages.playerHasStage(stage, player)
//   AStages.getStagesFromPlayer(player)
//   AStages.addStageToPlayer(stage, player)
//   AStages.removeStageFromPlayer(stage, player)
//   AStages.removeAllStagesFromPlayer(player)

/**
 * Checks whether the player owns a progression stage.
 *
 * @param {Internal.ServerPlayer} player
 * @param {string} stage
 * @returns {boolean}
 */
function hasStage(player, stage) {
  if (!global.ProgressionStages.isValidStage(stage)) {
    throw new Error(
      `[progression] hasStage called with unknown stage "${stage}"`
    )
  }

  return AStages.playerHasStage(stage, player)
}

/**
 * Grants exactly one stage.
 *
 * @param {Internal.ServerPlayer} player
 * @param {string} stage
 */
function grantStage(player, stage) {
  if (!global.ProgressionStages.isValidStage(stage)) {
    throw new Error(
      `[progression] grantStage called with unknown stage "${stage}"`
    )
  }

  if (hasStage(player, stage)) {
    return
  }

  AStages.addStageToPlayer(stage, player)
}

/**
 * Grants the requested stage and every previous stage.
 *
 * Example:
 * era_5 -> era_1, era_2, era_3, era_4, era_5
 *
 * @param {Internal.ServerPlayer} player
 * @param {string} era
 */
function grantEraWithPrevious(player, era) {
  const cumulative = global.ProgressionStages.cumulativeListFor(era)

  for (const stage of cumulative) {
    if (!hasStage(player, stage)) {
      AStages.addStageToPlayer(stage, player)
    }
  }
}

/**
 * Admin/debug removal.
 *
 * If stage exists -> remove only that stage.
 * If stage is omitted -> remove all AStages stages from player.
 *
 * @param {Internal.ServerPlayer} player
 * @param {string} [stage]
 */
function adminRemoveStage(player, stage) {
  if (stage) {
    if (!global.ProgressionStages.isValidStage(stage)) {
      throw new Error(
        `[progression] adminRemoveStage called with unknown stage "${stage}"`
      )
    }

    AStages.removeStageFromPlayer(stage, player)
    return
  }

  AStages.removeAllStagesFromPlayer(player)
}

/**
 * Returns progression stages currently held by the player,
 * ordered according to STAGE_ORDER.
 *
 * AStages.getStagesFromPlayer(player) returns a Java List.
 *
 * @param {Internal.ServerPlayer} player
 * @returns {string[]}
 */
function listStages(player) {
  const held = AStages.getStagesFromPlayer(player)

  return global.ProgressionStages.STAGE_ORDER.filter(
    stage => held.contains(stage)
  )
}

global.ProgressionBackend = {
  hasStage: hasStage,
  grantStage: grantStage,
  grantEraWithPrevious: grantEraWithPrevious,
  adminRemoveStage: adminRemoveStage,
  listStages: listStages,
}

console.log(
  "[progression] stage_backend.js loaded — AStages backend ready"
)