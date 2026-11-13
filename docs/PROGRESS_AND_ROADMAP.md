# OLREDINE — Progress & Development Roadmap

> Living project log and forward-looking development plan. This document tracks what exists, what has been agreed, and what is planned. It is not a claim that planned or committed code has been tested on a device.

Last updated: 2026-11-13

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

Character, nationality, and residence have initial inputs. Education (section 4) has initial selectable fields for highest completed level, current study status, study field/category, and academic performance. The user has confirmed that the latest education-related APK build succeeded; further on-device behavior testing and design refinement remain. Economic status (section 5) now has a required selection among five broad starting-condition levels, with short descriptions and no exact monetary amounts. This is an initial UI capture only; the selected level is not yet connected to an economic model or durable save. Family (section 6) now has initial selectable fields for household arrangement, parent life status, family support, and family economic circumstances. Skills (section 7) remains a placeholder.

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

- A searchable list of 195 country entries, loaded from `res/xml/world_data.xml`.
- Country names are displayed in Persian.
- The list is sorted by Persian country name.
- Search accepts the displayed country name or its two-letter country code.
- One primary nationality can be selected.
- The selected country name and two-letter code are retained while navigating between sections.
- The player cannot move forward from nationality until a country is selected.
- Backward navigation remains available when the section is incomplete.

The country code is stored separately from the displayed Persian name. This is intended to give the game a stable identifier for later world-data integration.

**Important scope note:** the current list is a practical initial country list (193 UN member states plus the Holy See and Palestine). It is not yet a complete citizenship, territory, or international-recognition model. Special territories, disputed cases, multiple citizenships, and legal nationality rules are outside the current Alpha scope.

#### Residence section

Initial implementation added to the current UI:

- Residence country is independent of nationality.
- Residence country and location data are read from the static XML resource `res/xml/world_data.xml`.
- The residence-country picker currently exposes only countries that have at least one defined playable location.
- The initial location dataset contains 56 entries across 20 countries.
- Locations are searchable by their Persian display name.
- Each location has a stable ID, country code, Persian display name, and a settlement type: `METROPOLIS`, `CITY`, or `VILLAGE`.
- The displayed settlement type is derived from the selected location; it is not a separate player choice.
- Changing the residence country clears the previously selected location.
- The player cannot move forward from this section until both country and location are selected.

The initial location list is deliberately curated and limited. It is not a complete global city database. New countries and locations can be added to the XML without changing the screen's selection logic.

### Economic status section

The initial economic-status UI offers five mutually exclusive starting-condition choices:

| Internal ID | Display label | Initial meaning |
|---|---|---|
| `VERY_DIFFICULT` | بسیار دشوار | منابع اولیه بسیار محدود و فشار اقتصادی زیاد |
| `DIFFICULT` | دشوار | منابع محدود و شرایط مالی سخت |
| `ORDINARY` | معمولی | شرایط اقتصادی عادی |
| `SUITABLE` | مناسب | برخورداری از مقداری پشتوانه مالی |
| `FAVORABLE` | مطلوب | پشتوانه مالی نسبتاً خوب و شروع راحت‌تر |

The player does not enter exact amounts or choose a job/income source during character creation. The selection is required before leaving section 5 and is currently held only in the `NewGameScreen` object. The actual starting cash, assets, liabilities, income, and economic consequences have not been defined or implemented. Family circumstances remain separate in section 6.

### Family section

The initial family UI captures four required dimensions:

| Dimension | Options (display labels) | Internal storage |
|---|---|---|
| Household / living arrangement | Living with both parents; one parent; a guardian; relatives; independently / without a family guardian | Stable IDs |
| Parent life status | Both alive; only father alive; only mother alive; neither alive; unknown | Stable IDs |
| Family support | Strong; moderate; limited; conflictual; no effective support | Stable IDs |
| Family economic circumstances | Very difficult; difficult; ordinary; suitable; favorable | Stable IDs |

Household arrangement and parent life status are separate facts. A parent may be alive without living in the character's household, so these fields are not forced into a simplistic one-to-one mapping. Family economic circumstances are independent from the character's starting economic status in section 5.

All four groups are required before leaving section 6. This is initial UI capture only: family relationships, obligations, support events, starting resources, gameplay consequences, and durable save integration remain unimplemented.

### Current limitations

- Character, nationality, residence, education, and economic-status values are held in the screen object only; there is no durable save-game system yet.
- There is no complete character data model shared across screens yet.
- There is no playable world/gameplay screen after character creation.
- No event-and-consequence gameplay loop exists yet.
- Education captures initial form data but has no gameplay effects or durable save integration yet. Economic status captures one coarse starting category but is not connected to economic calculations or gameplay. Family captures four initial categories but has no relationship simulation, gameplay effects, or durable save integration. Skills remains a placeholder.
- The final action in character creation is still a placeholder.
- No claim of successful build or device testing should be made solely from a successful GitHub commit.

### Deferred design issue: age and education consistency

The current character-creation form does not yet enforce logical compatibility between character age and education level/status. For example, a 15-year-old character can currently be assigned a doctorate. This is a known deferred issue, not a blocker for the current UI milestone.

Revisit this when the shared character model and validation/game-rule layer are designed:
- Define plausible education progression, study durations, and completion/status combinations in relation to age.
- Account for differences between national education systems instead of applying an unqualified universal age table.
- Decide whether and how exceptional accelerated-education paths are supported.
- Centralize these rules so character creation and later in-game education progression use the same logic.

Until then, education selections are preliminary form data; not every possible combination should be treated as logically valid.

## 3. Alpha 0.1 target

The recommended first public milestone is a very early **Alpha 0.1**. Its purpose is to let an outside player experience the basic idea and provide useful feedback.

Alpha 0.1 should prioritize one complete, understandable loop over broad but shallow feature coverage:

**Create character → enter the world → encounter an event → make a decision → see a consequence → save progress → continue later.**

### Proposed Alpha 0.1 scope

#### A. Complete character creation

- Complete and validate all seven character-creation sections; refine education and economic-status rules, and design the family and skills sections.
- Finalize the fields and rules for each section without collecting data that has no planned gameplay use.
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

XML is used for structured static world reference data (currently countries and starter locations) and is an accepted option for language resources. The format should be selected based on the data's real needs; do not create a complex data framework prematurely.

### World simulation direction

Nationality and residence are distinct:

- Nationality describes a character's citizenship/national affiliation.
- Residence describes where the character currently lives.

In later versions, country and location data may influence legal conditions, employment, cost of living, market access, taxation, inflation, and migration. These effects must be based on explicit rules and balanced data, not simplistic assumptions about real populations.

## 6. Immediate next tasks

1. Review and device-test the residence section and its XML-backed country/location selection.
2. Define a shared character data model before adding many more independent form fields.
3. Connect the existing character, nationality, and residence UI to that model.
4. Refine education progression/validation rules and define the fields for family and skills; revisit the economic-status categories when the shared character and economy models are designed.
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

### 2026-11-12 — Initial residence section

- Moved the 195-country reference list out of Java and into `res/xml/world_data.xml`.
- Added an initial curated location dataset: 56 locations across 20 countries.
- Added searchable residence-country and location pickers.
- Kept residence country independent from nationality.
- Derived the settlement type (metropolis, medium city, or rural area) from the selected location's XML record.
- Added forward-navigation validation for residence country and location.
- This code has been committed, but the new residence flow has not yet been confirmed by a device test.


### 2026-11-13 — Deferred age/education validation issue

- Recorded that age and education level/status are not yet cross-validated (example: age 15 with a doctorate).
- Deferred implementation until the character model and shared validation/game-rule logic are completed.
- Future rules must consider progression and study duration, country-specific education systems, and any deliberately supported exceptional paths.
- The current successful APK build confirms compilation only; it does not mean this logical validation has been implemented.


### 2026-11-13 — Initial economic status section

- Replaced the section 5 placeholder with five mutually exclusive starting-condition choices: very difficult, difficult, ordinary, suitable, and favorable.
- Added short explanations while intentionally avoiding exact money/wealth amounts.
- Added required selection validation and stable internal IDs for the chosen category.
- Kept job selection and income-source selection out of character creation; those are intended to emerge through gameplay.
- Kept family economic circumstances separate in section 6.
- This commit implements initial UI capture only. It does not establish starting cash/assets/debt, economic calculations, persistent storage, or on-device test results.


### 2026-11-13 — Initial family section

- Replaced the section 6 placeholder with four required choice groups: household arrangement, parent life status, family support, and family economic circumstances.
- Stored choices using stable internal IDs rather than Persian display labels.
- Kept household arrangement separate from whether parents are alive, and kept family economic circumstances separate from the player's own starting economic status.
- Implemented required-field validation for all four groups.
- This is initial UI capture only; family simulation, gameplay effects, durable save integration, and device-test confirmation remain future work.
