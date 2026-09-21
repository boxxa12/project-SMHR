// priority: 100
// Package 2 revision 1: initialize before era files, validate after full startup.
// Read-only diagnostics: never grants stages, changes recipes, or shuts down the server.
global.Package2EraGatingStatus = { completed: [], validation: "NOT_RUN" }

ServerEvents.loaded(event => {
  const expected = __MANIFEST__
  const status = global.Package2EraGatingStatus
  const failures = []
  function fail(message) { failures.push(message) }

  try {
    const ResourceLocation = Java.loadClass("net.minecraft.resources.ResourceLocation")
    const Registries = Java.loadClass("net.minecraftforge.registries.ForgeRegistries")
    const StageContainer = Java.loadClass("net.sdm.recipemachinestage.api.stage.StageContainer")
    const recipes = event.server.getRecipeManager()

    for (let era = 1; era <= 9; era++) {
      const stage = "era_" + era
      if (status.completed.indexOf(stage) === -1) fail("Era script did not finish: " + stage)
      if (!global.ProgressionStages || !global.ProgressionStages.isValidStage(stage)) {
        fail("Stage missing from Package 1 manifest: " + stage)
      }
    }
    expected.items.forEach(entry => {
      if (!Registries.ITEMS.containsKey(new ResourceLocation(entry.item))) {
        fail("Unknown item: " + entry.item + " [" + entry.stage + "]")
      }
    })
    expected.recipes.forEach(group => {
      const type = Registries.RECIPE_TYPES.getValue(new ResourceLocation(group.type))
      if (type == null) {
        fail("Unknown RecipeType: " + group.type + " [" + group.stage + "]")
      }
      group.ids.forEach(id => {
        const location = new ResourceLocation(id)
        const found = recipes.byKey(location)
        if (!found.isPresent()) {
          fail("Unknown recipe: " + id + " [expected " + group.type + "]")
          return
        }
        const actualType = String(Registries.RECIPE_TYPES.getKey(found.get().getType()))
        if (actualType !== group.type) {
          fail("Wrong RecipeType: " + id + ": expected " + group.type + ", actual " + actualType)
        }
        if (group.layer === "rms" && type != null) {
          const registered = StageContainer.getRecipeData(type, location)
          if (registered == null || String(registered.stage) !== group.stage) {
            fail("Missing/wrong RMS registration: " + id + " [expected " + group.stage + "]")
          }
        }
      })
    })
  } catch (error) {
    fail("Validator exception (validation incomplete): " + String(error))
  }

  status.validation = failures.length ? "FAILED" : "DATA_CHECK_PASSED"
  status.failures = failures
  if (failures.length) {
    console.error("[package2] DATA CHECK FAILED: " + failures.length + " issue(s); DO NOT approve this build for production.")
    failures.slice(0, 40).forEach(message => console.error("[package2] " + message))
    if (failures.length > 40) console.error("[package2] First 40 shown; full list is in global.Package2EraGatingStatus.failures.")
  } else {
    console.log("[package2] DATA CHECK PASSED: 118 items, 9348 recipe IDs/types, 9346 RMS entries; multiplayer enforcement NOT VERIFIED.")
  }
})
