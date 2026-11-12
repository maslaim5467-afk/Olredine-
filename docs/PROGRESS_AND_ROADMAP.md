# OLREDINE — Progress & Development Roadmap

> Living project log and forward-looking development plan. This document tracks what exists, what has been agreed, and what is planned. It is not a claim that planned or committed code has been tested on a device.

Last updated: 2026-11-12

## 1. Product vision

OLREDINE is a life simulation, business simulation, and economic strategy game.

The player begins as an ordinary person and shapes their economic and personal future through decisions and experiences. The long-term journey may lead from education, work, and everyday financial choices to entrepreneurship, investment, and ownership of businesses.

The central product idea is **to simulate the economic journey of a person**, rather than starting the player as the manager of an established company.

### Design principles

- Player decisions should produce understandable consequences.
- Personal circumstances and the world should influence available opportunities.
- Life systems and economic systems should connect, rather than feel like unrelated menus.
- World data and game rules should be extensible and data-driven.
- Early releases should contain a small, genuine gameplay loop instead of a large collection of empty screens.
- Build only the architecture that current gameplay needs, while keeping clear boundaries for future expansion.

## 2. Current development status

### Android application foundation

- Android application package: `com.example.mycgame`.
- Current interface is written in Java and built programmatically.
- `MainActivity` coordinates screen navigation.
- Main menu and new-game flow are separated into UI classes.
- The project currently uses a manual Termux build script (`apk.sh`), not Gradle.
- The native C library is currently a minimal stub. Gameplay does not yet call it through JNI.

### New-game / character creation flow

The new-game screen contains seven sections:

1. Character
2. Nationality
3. Place of residence
4. Education
5. Economic status
6. Family
7. Skills

The first two sections have initial working inputs. Sections 3–7 are still placeholders.

#### Character section

Implemented in the current UI:

- Character name input.
- Gender selection.
- Age input, restricted to 12–30 inclusive.
- Exactly one innate trait must be selected before leaving this section.

The eight available innate traits are:

| Trait | Initial tendency |
|---|---|
| Disciplined | Order and planning |
| Ambitious | Progress and goal achievement |
| Analytical | Examining and analyzing problems |
| Sociable | Communication and interaction |
| Risk-taking | Willingness to accept risk |
| Patient | Waiting for long-term results |
| Creative | New ideas and solutions |
| Cautious | Considering risks before acting |

An innate trait is an initial tendency, not a permanent limit or a complete personality definition. Other traits are intended to emerge through behavior, decisions, and life experiences. The acquired-trait system and gameplay effects have not yet been implemented.

#### Nationality section

Implemented in the current UI:

- A searchable list of 195 country entries.
- Country names are displayed in Persian.
- The list is sorted by Persian country name.
- Search accepts the displayed country name or its two-letter country code.
- One primary nationality can be selected.
- The selected country name and two-letter code are retained while navigating between sections.
- The player cannot move forward from nationality until a country is selected.
- Backward navigation remains available when the section is incomplete.

The country code is stored separately from the displayed Persian name. This is intended to give the game a stable identifier for later world-data integration.

**Important scope note:** the current list is a practical initial country list (193 UN member states plus the Holy See and Palestine). It is not yet a complete citizenship, territory, or international-recognition model. Special territories, disputed cases, multiple citizenships, and legal nationality rules are outside the current Alpha scope.

### Current limitations

- Character and nationality values are held in the screen object only.
- There is no durable save-game system yet.
- There is no complete character data model shared across screens yet.
- There is no playable world/gameplay screen after character creation.
- No event-and-consequence gameplay loop exists yet.
- Sections 3–7 do not yet collect real data.
- The final action in character creation is still a placeholder.
- No claim of successful build or device testing should be made solely from a successful GitHub commit.

## 3. Alpha 0.1 target

The recommended first public milestone is a very early **Alpha 0.1**. Its purpose is to let an outside player experience the basic idea and provide useful feedback.

Alpha 0.1 should prioritize one complete, understandable loop over broad but shallow feature coverage:

**Create character → enter the world → encounter an event → make a decision → see a consequence → save progress → continue later.**

### Proposed Alpha 0.1 scope

#### A. Complete character creation

- Finish the six remaining sections: nationality is now implemented, leaving residence, education, economic status, family, and skills.
- Define the actual fields and rules for each section before adding UI controls.
- Keep nationality separate from residence.
- Validate required fields and avoid collecting information that has no planned use.
- Preserve the character's data when moving between sections.

#### B. Establish a coherent character data model

- Introduce a dedicated character model rather than treating UI fields as the source of truth.
- Use stable IDs/codes for structured choices (for example, country codes), not translated display strings.
- Keep display/localization text separate from stored game values.
- Make the model extensible so future attributes can be added without coupling game rules to screen classes.

#### C. Implement durable save and continue

- Save the completed character and game state locally.
- Restore the game after the app is closed and reopened.
- Provide a clear new-game / continue-game flow.
- Keep static world definitions separate from mutable player save data.
- SQLite is the current proposed option for structured save-game state; implementation and schema design are still pending.

#### D. Add the first playable world and event loop

- Move the player from character creation into an initial world/game screen.
- Present a small number of meaningful situations.
- Let the player make choices.
- Show visible consequences in the player's state (for example, money, time, or a relevant personal condition), using simple and clearly explained rules.
- Persist the consequences so continuing the game has meaning.

The first event set should be deliberately small. It should prove that decisions change the state of the simulation, not attempt to simulate an entire economy.

#### E. Test and package

- Build the APK from the latest main branch.
- Test on a real Android device in landscape orientation.
- Verify navigation, input validation, save/restore, event choices, and state persistence.
- Fix release-blocking crashes and data-loss issues.
- Label the release clearly as an early alpha and document known limitations.

## 4. Development sequence

The following sequence is a working plan. It may be adjusted when implementation or testing reveals a better order.

| Stage | Work | Exit condition |
|---|---|---|
| 1 | Finish character-creation design and fields | All seven sections have agreed data requirements |
| 2 | Implement character model and connect the form | UI edits one coherent character object |
| 3 | Implement local save/restore | Character and game state survive app restart |
| 4 | Build initial world/game screen | A completed character can enter the game |
| 5 | Implement first events and consequences | At least one decision changes persistent game state |
| 6 | Test and prepare Alpha 0.1 | Core loop works reliably on the target device |

## 5. Architecture direction

### Current responsibilities

- **Java / Android:** UI, input, navigation, lifecycle, local storage integration, and ordinary gameplay orchestration.
- **C / JNI:** reserved for simulation calculations where profiling demonstrates a meaningful benefit, such as large-scale economic calculations or performance-sensitive simulation workloads.

The current C library is not yet a gameplay engine. Do not move ordinary UI or simple game rules into C just because a native library is present.

### Data boundaries

Keep these concerns separate as the project grows:

1. UI screens and user interaction.
2. Character and mutable save-game state.
3. Static world definitions (countries, locations, and other reference data).
4. Game rules and event resolution.
5. Localization and translated display strings.

XML is an accepted option for structured static data and language resources. The format should be selected based on the data's real needs; do not create a complex data framework prematurely.

### World simulation direction

Nationality and residence are distinct:

- Nationality describes a character's citizenship/national affiliation.
- Residence describes where the character currently lives.

In later versions, country and location data may influence legal conditions, employment, cost of living, market access, taxation, inflation, and migration. These effects must be based on explicit rules and balanced data, not simplistic assumptions about real populations.

## 6. Immediate next tasks

1. Design the residence section, including the distinction between country of residence and city/location.
2. Define a shared character data model before adding many more independent form fields.
3. Connect the existing character and nationality UI to that model.
4. Decide and document the fields for education, economic status, family, and skills.
5. Implement local save/restore once the first character schema is stable.
6. Design the smallest playable opening scenario and its event consequences.

## 7. Release philosophy

- Do not call a screen a gameplay feature until it affects or represents actual game state.
- Do not describe a feature as tested until it has been built and exercised on a device.
- Prefer a narrow, functioning loop over many unfinished systems.
- Keep Alpha feedback focused on clarity, bugs, usability, and whether the first decisions feel meaningful.
- Expand economic depth after the initial life-and-decision loop is understandable and stable.

## 8. Change log

### 2026-11-12 — Character creation and nationality

- Added eight selectable innate traits to the character section.
- Added age validation (12–30 inclusive).
- Added a searchable Persian country picker with 195 country entries and two-letter codes.
- Added a required primary-nationality check before moving forward.
- Kept nationality selection separate from the future residence section.
- Confirmed by the developer/user that the nationality flow worked in an APK test.
- Durable save data, the remaining character sections, and playable events remain future work.

---

This document is a living development record. Update the implementation status only after inspecting the repository and distinguishing committed code from features actually built and tested.
