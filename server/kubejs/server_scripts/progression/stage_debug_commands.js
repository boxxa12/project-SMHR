// progression/stage_debug_commands.js
//
// Development/admin commands for testing progression.
//
// Commands:
// /stagedebug inspect <player>
// /stagedebug grant <player> <era>
// /stagedebug removeone <player> <stage>
// /stagedebug resetall <player>

ServerEvents.commandRegistry(event => {
  const { commands: Commands, arguments: Arguments } = event

  const OP_LEVEL_2 = source => source.hasPermission(2)

  event.register(
    Commands.literal("stagedebug")
      .requires(OP_LEVEL_2)

      // /stagedebug inspect <player>
      .then(
        Commands.literal("inspect")
          .then(
            Commands.argument(
              "target",
              Arguments.PLAYER.create(event)
            ).executes(ctx => {
              const player =
                Arguments.PLAYER.getResult(ctx, "target")

              const stages =
                global.ProgressionBackend.listStages(player)

              const message =
                `[stagedebug] ${player.name.string} holds: ${
                  stages.length
                    ? stages.join(", ")
                    : "(none)"
                }`

              ctx.source.sendSuccess(
                Text.of(message),
                false
              )

              return 1
            })
          )
      )

      // /stagedebug grant <player> <era>
      .then(
        Commands.literal("grant")
          .then(
            Commands.argument(
              "target",
              Arguments.PLAYER.create(event)
            ).then(
              Commands.argument(
                "era",
                Arguments.STRING.create(event)
              ).executes(ctx => {
                const player =
                  Arguments.PLAYER.getResult(ctx, "target")

                const era =
                  Arguments.STRING.getResult(ctx, "era")

                if (!global.ProgressionStages.isValidStage(era)) {
                  ctx.source.sendFailure(
                    Text.of(
                      `[stagedebug] Unknown stage "${era}". Valid: ${global.ProgressionStages.STAGE_ORDER.join(", ")}`
                    )
                  )

                  return 0
                }

                global.ProgressionBackend.grantEraWithPrevious(
                  player,
                  era
                )

                ctx.source.sendSuccess(
                  Text.of(
                    `[stagedebug] Granted ${player.name.string} cumulative stages through ${era}`
                  ),
                  true
                )

                return 1
              })
            )
          )
      )

      // /stagedebug removeone <player> <stage>
      .then(
        Commands.literal("removeone")
          .then(
            Commands.argument(
              "target",
              Arguments.PLAYER.create(event)
            ).then(
              Commands.argument(
                "stage",
                Arguments.STRING.create(event)
              ).executes(ctx => {
                const player =
                  Arguments.PLAYER.getResult(ctx, "target")

                const stage =
                  Arguments.STRING.getResult(ctx, "stage")

                if (!global.ProgressionStages.isValidStage(stage)) {
                  ctx.source.sendFailure(
                    Text.of(
                      `[stagedebug] Unknown stage "${stage}"`
                    )
                  )

                  return 0
                }

                global.ProgressionBackend.adminRemoveStage(
                  player,
                  stage
                )

                ctx.source.sendSuccess(
                  Text.of(
                    `[stagedebug] Removed ${stage} from ${player.name.string}`
                  ),
                  true
                )

                return 1
              })
            )
          )
      )

      // /stagedebug resetall <player>
      .then(
        Commands.literal("resetall")
          .then(
            Commands.argument(
              "target",
              Arguments.PLAYER.create(event)
            ).executes(ctx => {
              const player =
                Arguments.PLAYER.getResult(ctx, "target")

              global.ProgressionBackend.adminRemoveStage(
                player,
                undefined
              )

              ctx.source.sendSuccess(
                Text.of(
                  `[stagedebug] Reset ALL stages for ${player.name.string}`
                ),
                true
              )

              return 1
            })
          )
      )
  )
})

console.log(
  "[progression] stage_debug_commands.js loaded — /stagedebug registered"
)