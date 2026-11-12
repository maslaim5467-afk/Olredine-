# OLREDINE — Development Notes

> Working notes for continuity across development sessions. This file records implementation state, design decisions, assumptions, and unresolved questions. It is not player-facing documentation.

Last updated: 2026-11-12

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
- The character section currently has basic name, gender, and age inputs.
- The other sections are structural placeholders; they are not yet backed by real game data.
- The current screen state is held in the screen object only. It is not yet persisted to SQLite or another save system.
- Main menu labels and new-game screen labels are Persian at present.

### Build pipeline

- `apk.sh` was changed to discover and compile all Java files below `src`, rather than compiling only `MainActivity.java`.
- This is required now that UI classes live in subpackages.
- The build was not executed or verified after these latest repository changes. A successful GitHub commit is not evidence of a successful APK build.

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

## Immediate next steps

1. Pull the latest `main` branch in Termux.
2. Run `bash apk.sh` and capture the complete output if it fails.
3. Install and test the APK on the target phone.
4. Verify:
   - app opens to the main menu;
   - tapping the new-game button opens the new screen;
   - the left navigation changes the right panel;
   - name, gender, and age survive section changes;
   - the back control and Android back action return to the main menu;
   - no clipping or unreadable text occurs in landscape orientation.
5. Adjust the visual proportions only after reviewing a real device screenshot.
6. Once navigation/layout is stable, extract reusable UI helpers if they reduce duplication; avoid premature framework-building.
7. Implement the nationality section as the first real data-backed section, after agreeing on its data schema and the distinction between nationality and residence.
8. Add a localization manager and XML language files before spreading hard-coded strings across more screens.
9. Design save-game schema and SQLite integration when the first complete character/world creation flow is understood.

## Known caveats / things not to assume

- Do not tell the user that the latest APK works until the new build and device test succeed.
- SVG files under `assets/ui/svg/` are design assets only; the current Android screens do not load them.
- The new-game sections other than character are placeholders.
- The final "finish" action does not create a save yet.
- The current character fields are not durable across Activity/process recreation.
- The background image's fit and the panel proportions need validation on the actual landscape device.
- Do not expose or copy any sensitive MARG YOORNA code or credentials into project notes, examples, or public assets.

## Collaboration style

Work directly in the GitHub repository when the required tool access is available. Make small, reviewable commits. Preserve the user's local work: never use destructive Git cleanup/reset commands to resolve a pull conflict without first inspecting status and protecting local changes. Give the user the exact pull/build commands and wait for actual build output before diagnosing compilation success or failure.
