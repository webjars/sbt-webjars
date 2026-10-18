# AGENTS.md: sbt-webjars

## Agent tooling

- Follow the `zen-of-projects` Skill (extract with `./sbta extractSkillsJars` into the gitignored `.kiro/skills/`); this file records only project-specific facts and exceptions.
- MCP server `sbta` (this build's `./sbta --mcp`, configured in `.mcp.json` and `.kiro/settings/mcp.json`, approved in `.claude/settings.json`): use it for all sbt work: `compile`, `test` (`full` for all), `run`, `check` after editing Scala 3 sources, `glob-search` / `inspect` / `symbol-location` for symbols, `sbt-task` for any other command. Its tools are deferred in Claude Code: load them with ToolSearch (search `sbta`). If it's unavailable, say so and use `./sbta <command>`. Logs: `project/target/sbta/mcp.log`.
- Maintenance routine: `.factory/MAINTENANCE.md` (weekly), following the `zen-of-projects` Skill.

## Build & test

- sbt plugin, cross-built for sbt 1 (Scala 2.12) and sbt 2 (Scala 3). Full validation, the same as CI: `./sbta '+compile; +scripted'`.
- Use `+scripted`, not `scripted`: plain `scripted` only runs the sbt 1 test (`simple`); `no-webjars` and `sbt2-regen` pin sbt 2 and are skipped without the `+`.
