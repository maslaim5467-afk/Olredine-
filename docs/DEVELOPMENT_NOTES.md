# OLREDINE — Development Notes

> Working notes for continuity across development sessions. This file records implementation state, design decisions, assumptions, and unresolved questions. It is not player-facing documentation.

Last updated: 2026-11-13

## Project direction

OLREDINE is intended to grow into a life, business, and economic simulation. The player begins by creating a person and then experiences a world in which nationality, residence, education, economic conditions, opportunities, restrictions, and later business decisions can have gameplay consequences.

The design should be data-driven and extensible. Avoid hard-coding country facts, economic values, or user-facing language into screen classes. Keep UI, game rules, static world definitions, localization, and mutable save state as separate concerns.

## Recent implementation

### Main menu and screen separation

- The main menu was extracted from `MainActivity` into `ui/MainMenuScreen.java`.
- A new `ui/NewGameScreen.java` was added.
- `MainActivity.java` now acts as a small screen coordinator:
  - main menu -> new-game screen
  - Android back action from new-game screen -> main menu
- The new-game screen uses a left navigation rail and a right content panel.
- Current sections: character, nationality, place of residence, education, economic status, family, and skills.
- The character section has name, gender, age, and one innate-trait selection.
- The nationality section is backed by the searchable 195-country XML list and was confirmed working by the user on-device.
- The residence section now has XML-backed country and location pickers. Its initial data covers 56 locations in 20 countries. The selected location supplies its settlement type (metropolis, medium city, or rural area).
- Education now has initial selectable fields: highest completed education level, current study status, study field/category, and academic performance. The no-formal-education choice hides field/performance selection. This is initial form capture only; downstream gameplay effects and durable save integration are not implemented.
- Economic status (section 5) now has a required initial selection with five broad levels: very difficult, difficult, ordinary, suitable, and favorable. The UI stores a stable internal ID for the chosen level; it does not ask for exact money, income, assets, or debt.
- Family (section 6) now has initial selectable fields for household/living arrangement, parent life status, family support, and family economic circumstances. Each choice stores a stable internal ID; all four groups are required before leaving the section. The family economic category is independent from the player's starting economic condition in section 5.
- Skills (section 7) remains a structural placeholder.
- The user confirmed that the residence APK test succeeded. The education UI change has now built successfully; further on-device behavior testing and design refinement remain.
- The current screen state is held in the screen object only. It is not yet persisted to SQLite or another save system.
- Main menu labels and new-game screen labels are Persian at present.

### Build pipeline

- `apk.sh` was changed to discover and compile all Java files below `src`, rather than compiling only `MainActivity.java`.
- This is required now that UI classes live in subpackages.
- The latest education-related build was reported successful by the user. This confirms compilation for that build, not full gameplay correctness or completion of the education system.

## Relevant repository / build context

- Repository: `maslaim5467-afk/Olredine-`
- Branch: `main`
- Android package: `com.example.mycgame`
- Java source root: `src/com/example/mycgame/`
- Current build workflow is a manual Termux shell script, not a Gradle project.
- The script expects `$HOME/android.jar`, `$HOME/r8.jar`, the Android build tools, and the existing signing keystore at `$HOME/AndroidKeys/my-release-key.jks`.
- Keep the existing keystore and alias intact. Do not replace signing credentials as part of routine UI work.
- The main menu background is loaded from the Android drawable resource named `menu_background`.

## Architecture decisions agreed so far

1. Keep `MainActivity` small. It should coordinate navigation and Activity lifecycle, not contain every screen or the simulation logic.
2. Put each substantial screen in its own class under `ui/`.
3. Static game/world definitions should be data files, not scattered Java constants.
4. XML is an accepted format for structured static game data and localization.
5. Localization must be extensible: adding a language should primarily mean adding a language file and registering/discovering it, not rewriting Java logic.
6. Keep localization separate from country/world facts. For example, a country has one stable ID and its displayed name is resolved through translation keys.
7. SQLite is the proposed store for mutable save-game state; it is not a replacement for static XML data. This is a design direction, not yet implemented.
8. Consider Android DataStore or a similarly small settings store for preferences, if/when the build setup makes that practical. Do not introduce dependencies casually into the current manual build pipeline.
9. Country selection is intended to have gameplay consequences, but nationality and residence must remain distinct concepts. A person's citizenship should not automatically be treated as the country where they live.
10. Country and city effects should eventually be represented through data and simulation rules (for example market access, cost of living, inflation, competition, taxation, and trade constraints), rather than simplistic permanent stereotypes. Values need sourcing, balancing, and a defined simulation time model.

## Deferred design issue: age and education consistency

- The current education form does not yet validate whether the selected education level and study status are plausible for the character's age.
- Example inconsistency: a 15-year-old character can currently be assigned a doctorate.
- This is a known design limitation, not a reason to block the current UI milestone.
- Revisit it when the character model and character/game-rule validation logic are designed.
- Define age-versus-education progression rules, expected durations, and valid study/completion states. Avoid relying only on one universal minimum-age table; education systems differ by country.
- Decide how explicitly supported exceptional paths (for example, unusually accelerated education) should work, if such paths are part of the game.
- Apply the rules in a shared validation layer, rather than scattering age checks across individual UI screens. The same rules should later govern in-game education progression.
- Until those rules are designed, treat the current education selections as preliminary form data and do not imply that all combinations are logically valid.

## Immediate next steps

1. Pull the latest `main` branch in Termux.
2. Install and test the latest APK on the target phone.
3. Verify:
   - app opens to the main menu;
   - tapping the new-game button opens the new screen;
   - the left navigation changes the right panel;
   - name, gender, and age survive section changes;
   - the back control and Android back action return to the main menu;
   - no clipping or unreadable text occurs in landscape orientation.
4. Adjust the visual proportions only after reviewing a real device screenshot.
5. Once navigation/layout is stable, extract reusable UI helpers if they reduce duplication; avoid premature framework-building.
6. Build and device-test the XML-backed residence section; verify country filtering, city search, type display, and required-field navigation.
7. Define a shared character data model and connect character, nationality, and residence values to it.
8. Refine education rules and define the fields and validation rules for family and skills. Include the deferred age/education consistency issue above when designing shared character validation; revisit the economic-status categories when the shared character/economy model is designed.
9. Design save-game schema and SQLite integration when the first complete character/world creation flow is understood.
10. Add a localization manager before spreading more hard-coded user-facing strings across screens.

## Known caveats / things not to assume

- Do not tell the user that a feature works on-device until the build and device test succeed.
- SVG files under `assets/ui/svg/` are design assets only; the current Android screens do not load them.
- Education has initial UI fields but its full logic, gameplay effects, and durable save integration are not implemented. Economic status captures one broad starting-condition category but has no economic calculations, character-model integration, gameplay effects, or durable save integration. Family and skills remain placeholders. Nationality and residence have initial data-backed implementations.
- The final "finish" action does not create a save yet.
- Character, nationality, and residence values are currently held in the screen object and are not durable across Activity/process recreation.
- The background image's fit and the panel proportions need validation on the actual landscape device.
- Do not expose or copy any sensitive MARG YOORNA code or credentials into project notes, examples, or public assets.

## Collaboration style

Work directly in the GitHub repository when the required tool access is available. Make small, reviewable commits. Preserve the user's local work: never use destructive Git cleanup/reset commands to resolve a pull conflict without first inspecting status and protecting local changes. Give the user the exact pull/build commands and wait for actual build output before diagnosing compilation success or failure.

## Residence implementation notes (2026-11-12)

- Static country and starter-location records are stored in `res/xml/world_data.xml`; the country list is no longer duplicated as a Java constant.
- Nationality uses the complete 195-entry country list.
- Residence intentionally lists only countries with at least one location currently defined in XML. This avoids offering a country for which the current build has no selectable location.
- The starter residence dataset contains 56 locations in 20 countries. Add more records to XML as the playable world expands; do not hard-code them in the screen.
- Each location has a stable ID, country code, Persian display name, and a game settlement category: `METROPOLIS`, `CITY`, or `VILLAGE`.
- The settlement category is derived from the selected location. It is not an independent user choice.
- The residence UI validates that both a country and a location are selected before moving forward.
- This implementation has been built and confirmed working by the user on-device.


## Economic status section implementation notes (2026-11-13)

- Section 5 now presents five mutually exclusive starting-condition choices: `VERY_DIFFICULT`, `DIFFICULT`, `ORDINARY`, `SUITABLE`, and `FAVORABLE`.
- Each choice has a short Persian explanation; the player is not shown exact currency or wealth amounts.
- The selected level is required before leaving section 5 and is held in the `NewGameScreen` object only.
- Employment and income are not selected in character creation. Income is intended to emerge later from gameplay activities such as employment, business, or investment.
- Family economic circumstances remain separate and belong to section 6.
- This is an initial UI milestone only. The categories have not yet been translated into starting cash, assets, liabilities, costs, or simulation rules; there is no durable save integration.


## Family section implementation notes (2026-11-13)

- Section 6 now captures four independent dimensions:
  - Household/living arrangement: living with both parents, one parent, a guardian, relatives, or independently/without a family guardian.
  - Parent life status: both alive, only father alive, only mother alive, neither alive, or unknown.
  - Family support: strong, moderate, limited, conflictual, or no effective support.
  - Family economic circumstances: very difficult, difficult, ordinary, suitable, or favorable.
- Every option is stored under a stable internal ID rather than its Persian display label.
- All four groups require a selection before the player can leave section 6.
- Household arrangement and parent life status are intentionally separate facts: for example, a parent may be alive without being part of the character's household. They are not forced into a simplistic one-to-one mapping.
- Family economic circumstances remain independent from the character's own starting economic status in section 5.
- This is initial UI data capture only. Family relationships, support events, obligations, starting resources, gameplay effects, and durable save integration are not implemented.
