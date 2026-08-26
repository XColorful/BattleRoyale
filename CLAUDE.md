These rules apply to every task in this project unless explicitly overridden.
Bias: caution over speed on non-trivial work. Use judgment on trivial tasks.

## Project Context

### Documentation

Start from `docs/architecture/Home.md`.
Its structure mirrors the codebase and routes to detailed API documentation under `docs/api`, which follows the same package structure.
Use documentation and existing implementations as the primary source of design intent.

### Tooling

Assume an IntelliJ IDEA MCP server is available; pass the worktree root as `projectPath` to select the matching IDEA window.
Use `search_symbol` with `include_external: true` to locate classes, then use `read_file` to inspect their implementations.
Prefer PSI-based code navigation over plain text search.
Inspect Minecraft or platform source code through IDEA MCP when API usage is unclear.

### Compatibility

The project targets multiple Minecraft versions through separate branches.
Project compatibility utilities are part of the architecture, not conveniences.
Prefer project wrappers over direct platform APIs whenever available.
Do not bypass compatibility utilities unless no equivalent abstraction exists.
Report newly introduced platform API usage and let me decide whether it requires a compatibility wrapper.

### Version Control

Each branch has its own dedicated git worktree and IDEA project.
Never switch branches within a worktree or modify git configuration/worktree state.
Use other IDEA projects through the IDEA MCP server for cross-version inspection when needed.

Unless explicitly requested, only modify the current task's branch.
Do not push unless explicitly requested.
If commit permission is not given by me, stop after changes and notify me for review.

## Rule 1 — Understand Before Coding

Read documentation, existing implementations, exports, callers, and shared utilities before changing code.
Assume existing designs are intentional until proven otherwise.
State assumptions explicitly. If intent is unclear, ask rather than guess.
Present multiple interpretations when ambiguity exists.

## Rule 2 — Simplicity First

Minimum code that solves the problem. Nothing speculative.
No features beyond what was asked. No abstractions for single-use code.
Test: would a senior engineer say this is overcomplicated? If yes, simplify.

## Rule 3 — Surgical Changes

Touch only what you must. Clean up only your own mess.
Don't "improve" adjacent code, comments, or formatting.
Don't refactor what isn't broken.
If a refactor changes architectural intent rather than implementation, ask first.
Prefer extending existing structures and patterns before introducing new ones.

## Rule 4 — Goal-Driven Execution

Define success criteria. Loop until verified.
Don't follow steps. Define success and iterate.
Strong success criteria let you loop independently.

## Rule 5 — Use AI only for judgment calls

Use me for: classification, drafting, summarization, extraction.
Do NOT use me for: routing, retries, deterministic transforms.
If code can answer, code answers.

## Rule 6 — Manage Context and Checkpoints

Keep context relevant to the current task.
After significant steps, summarize what was done, verified, and remaining.
If context is lost or state cannot be described, stop and restate.

## Rule 7 — Follow Existing Patterns

Match established project conventions, even if personal preference differs.
If patterns conflict, prefer the newer or more tested one.
Explain conflicts instead of silently blending approaches.
If a convention is harmful, surface it rather than forking silently.

## Rule 8 — Tests verify intent, not just behavior

Tests must encode WHY behavior matters, not just WHAT it does.
A test that can't fail when business logic changes is wrong.

## Rule 10 — Fail loud

"Completed" is wrong if anything was skipped silently.
"Tests pass" is wrong if any were skipped.
Default to surfacing uncertainty, not hiding it.