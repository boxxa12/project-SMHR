// progression/stage_manifest.js
//
// SINGLE CENTRAL SOURCE OF TRUTH for stage IDs, order, and cumulative grant lists.
// No other script in this pack should hardcode a stage string ("era_5", etc.) —
// everything reads from STAGE_MANIFEST via the global `ProgressionStages` object
// defined at the bottom of this file.
//
// Pure JS data + helper functions. Zero dependency on AStages/FTB Quests API,
// so this file cannot break even if the mod-API assumptions in stage_backend.js
// turn out to need adjustment during runtime testing.
//
// era_0 is intentionally NOT included: per master_stage_gating_manifest_era0_10.md
// §Golden Rule / Era 0 table, era_0 is baseline-availability with "no stage
// required" — there is nothing to grant or check for era_0.

/**
 * Ordered, cumulative progression ladder. Index order IS grant order.
 * server_complete is modeled as the element AFTER era_10, per Package-1 brief
 * item 2 ("server_complete is additional to era_10") — granting server_complete
 * cumulatively includes era_10 and everything below it, which satisfies that
 * requirement without needing a separate code path.
 */
const STAGE_ORDER = [
  "era_1",
  "era_2",
  "era_3",
  "era_4",
  "era_5",
  "era_6",
  "era_7",
  "era_8",
  "era_9",
  "era_10",
  "server_complete",
]

/**
 * @param {string} stage
 * @returns {number} index in STAGE_ORDER, or -1 if unknown
 */
function indexOfStage(stage) {
  return STAGE_ORDER.indexOf(stage)
}

/**
 * @param {string} stage
 * @returns {string[]} the stage itself plus every stage before it, in order.
 *          e.g. cumulativeListFor("era_5") -> ["era_1","era_2","era_3","era_4","era_5"]
 *          cumulativeListFor("server_complete") -> era_1..era_10 + server_complete
 * @throws if stage is not in STAGE_ORDER (fail loud — never silently no-op on a typo)
 */
function cumulativeListFor(stage) {
  const idx = indexOfStage(stage)
  if (idx === -1) {
    throw new Error(`[progression] Unknown stage id "${stage}". Valid stages: ${STAGE_ORDER.join(", ")}`)
  }
  return STAGE_ORDER.slice(0, idx + 1)
}

/**
 * @param {string} stage
 * @returns {string|null} the previous stage in the ladder, or null if `stage`
 *          is the first stage (era_1) or unknown.
 */
function previousStage(stage) {
  const idx = indexOfStage(stage)
  if (idx <= 0) return null
  return STAGE_ORDER[idx - 1]
}

/**
 * @param {string} stage
 * @returns {string|null} the next stage in the ladder, or null if `stage` is
 *          the last stage (server_complete) or unknown.
 */
function nextStage(stage) {
  const idx = indexOfStage(stage)
  if (idx === -1 || idx === STAGE_ORDER.length - 1) return null
  return STAGE_ORDER[idx + 1]
}

/**
 * @param {string} stage
 * @returns {boolean}
 */
function isValidStage(stage) {
  return indexOfStage(stage) !== -1
}

// Exposed globally (KubeJS server_scripts share a global scope across files
// loaded in the same load order; `progression/` is alphabetically before most
// other folders, and this file is alphabetically first inside it, so it loads
// before stage_backend.js and stage_debug_commands.js in the same directory).
// RUNTIME TEST REQUIRED: confirm actual load order in latest.log; if KubeJS
// does not guarantee this ordering on this Arclight build, wrap the three
// progression scripts' contents in an explicit require-style single file
// instead of relying on load order.
global.ProgressionStages = {
  STAGE_ORDER: STAGE_ORDER,
  indexOfStage: indexOfStage,
  cumulativeListFor: cumulativeListFor,
  previousStage: previousStage,
  nextStage: nextStage,
  isValidStage: isValidStage,
}

console.log(`[progression] stage_manifest.js loaded — ${STAGE_ORDER.length} stages registered: ${STAGE_ORDER.join(", ")}`)
