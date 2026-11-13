# OLREDINE — Development Notes

## Current checkpoint

**Date:** 2026-11-13  
**Branch:** `main`  
**Status:** Cross-section character-creation consistency rules implemented; Android build/device verification is still pending.

### Character creation: seven-section consistency review

The seven sections are:
1. Character
2. Nationality
3. Residence
4. Education
5. Personal economic status
6. Family
7. Skills

The review focuses on contradictions between selections, not merely validating each field independently.

### Implemented changes

- Centralized section validation in `CharacterCreationValidator.validateSection(...)` and added `validateAllSections(...)`.
- Character creation now checks all seven sections before completing/saving the character, and validates the current section when navigating forward.
- Education:
  - Added minimum-age checks for completed education levels and current/in-progress levels.
  - Current/in-progress education cannot be below the last completed level.
  - Same-level study remains allowed to avoid incorrectly blocking a second course/program at the same level.
  - `NOT_STUDYING` requires no current/in-progress level; `STUDYING`, `ON_BREAK`, and `DROPPED_OUT` require one.
  - Primary and lower-secondary levels only allow `UNDECLARED` as the education field.
- Family:
  - A two-parent household requires both parents to be alive.
  - A single-parent household is incompatible with both parents being deceased.
  - A single-parent household while both parents are alive remains allowed (e.g. separated parents).
- Personal economic status and family economic status remain independent; different values are not treated as contradictions.
- Skills remain independently selectable from formal education; skill IDs and the existing general-skill count rule are validated.

### Proposed gameplay age thresholds currently encoded

These are game rules, not universal education-system laws.

| Education level | Minimum age to have completed it |
|---|---:|
| No formal education | 12 (character start-age floor) |
| Primary | 10 |
| Lower secondary | 12 |
| Secondary | 15 |
| Vocational diploma | 16 |
| Short-cycle tertiary | 17 |
| Bachelor | 18 |
| Master | 20 |
| Doctorate | 23 |

Minimum ages currently encoded for an in-progress level:

| Education level | Minimum age to be studying it |
|---|---:|
| Primary | 6 |
| Lower secondary | 10 |
| Secondary | 13 |
| Vocational diploma | 14 |
| Short-cycle tertiary | 16 |
| Bachelor | 17 |
| Master | 19 |
| Doctorate | 21 |

### Files changed

- `src/com/example/mycgame/data/CharacterCreationValidator.java`
  - Latest verified blob SHA: `f498d63c8a2787b3581e7246cd36e97127832740`
- `src/com/example/mycgame/ui/NewGameScreen.java`
  - Latest verified blob SHA: `bda4ba320f440a4d84696a0cf2f30c3672e40726`

Both files were fetched again after their final updates and their blob SHAs matched the update results.

### Explicitly not treated as contradictions

- Nationality differing from country of residence.
- Personal economic status differing from family economic status.
- Skills being present without a matching formal qualification.
- Living independently at a young age (unusual, but not automatically impossible).
- Studying another program at the same education level as the last completed level.

### Verification status / next action

- GitHub updates and post-update file/SHA verification: completed.
- Android build: **not yet run for this checkpoint**.
- Device testing: **pending**.
- Do not describe this checkpoint as build-tested or runtime-verified until the user runs the build and tests it on Android.

Run from Termux:

```bash
git pull origin main
bash apk.sh
```

Suggested test cases:
- Age 12 + completed bachelor's degree → reject.
- Age 18 + completed bachelor's degree → accept.
- Two-parent household + only one parent alive → reject.
- Single-parent household + both parents alive → accept.
- Not studying + a current/in-progress level set → reject.
- Primary/lower-secondary + a specialized education field → unavailable/reject.
- Personal economic status differs from family economic status → accept.
