## 🧠 Cursor Codegen Rules for This Android Project

### 📘 Project Overview
- Single-activity, Jetpack Compose app
- Clean Architecture with feature-based packaging
- Hilt for DI, Room for local DB, DataStore for preferences, WorkManager for background tasks
- Coroutines + Flow for reactive async
- KSP (not KAPT) is used for annotation processing
- Cursor must prioritize maintainability, testability, and minimal manual intervention

---

## 🔍 Global Codegen Expectations

- 🧠 Always check the **latest Android and Jetpack documentation** for implementation before generating code
- 🔍 **Scan the entire codebase**, including `src`, for existing dependencies, types, and architecture alignment
- 🧪 Prioritize **testable code** — all business logic should be isolated in ViewModels, UseCases, or Repositories
- ⚙️ Avoid unnecessary singletons, ensure dependency scoping is clean and aligned with lifecycle
- ⛔ Never use `kapt`. Use **KSP** for all annotation processing
- 💡 Avoid hardcoded behavior — follow DI and reusable design patterns

---

## 🗂️ Project Structure Guidelines

- Feature-based folder structure
- Each feature must contain: `data`, `domain`, `presentation`
- Shared logic, styles, and utilities must reside in `common/` or `core/`

---

## 🧱 Clean Architecture Implementation Plan

### Setup:
- Initialize Gradle project with required dependencies for offline-first architecture
- Create proper DI entry points (`Application`, `MainActivity`) and enable splash screen handling via system API
- Establish `di`, `data`, `domain`, `presentation`, and `common` packages per feature

### Data Layer:
- Define Room entities, DAOs, database classes in their appropriate packages
- Build PreferenceDataStore classes for key-value persistence
- Build Repository interfaces and their implementations to wrap data sources

### Domain Layer:
- Use case classes should be grouped per feature and provide single-responsibility methods
- Ensure all business logic is encapsulated here and separated from data and UI concerns

### Presentation Layer:
- ViewModels must be scoped appropriately (`@HiltViewModel`) and interact with UseCases only
- `UiState` classes should represent screen states and be driven from Flow or StateFlow
- Composables must observe ViewModels using best practices and emit screen sections incrementally
- Navigation should be feature-based and declarative

---

## 📦 Dependency Injection

- Split modules logically (DatabaseModule, RepositoryModule, UseCaseModule, WorkerModule, etc.)
- Modules must not violate the boundaries of Clean Architecture (i.e., UI should not depend on data directly)
- Avoid overcomplicating DI: keep scope usage minimal and clear (e.g., ViewModelScoped where possible)

---

## 📊 Tooling Expectations

- WorkerManager for deferred work only — avoid excessive background logic where unnecessary
- Leverage NetworkMonitor to support offline-first behaviors
- Use Coil (via AsyncImage) for optimized image loading and caching
- If the app becomes scroll-heavy or pagination is needed, adopt Paging3

---

## 🔁 Splash Screen Logic

- Splash should only exit after a decision is made regarding navigation (auth vs onboarding vs home)
- Logic must reside in ViewModel or equivalent controller, not directly inside the Composable
- AppNavGraph should handle the first screen based on exposed splash state

---

## ✅ Testing & Maintenance Strategy

- All generated code must be testable and favor interface abstraction over concrete implementations
- Inject dispatchers and repositories to support test isolation
- Avoid logic in Composables — all state decisions should be exposed and tested from ViewModel
- Use fakes or mocks for DAOs, repositories, DataStore, etc. during unit testing
- Avoid creating unnecessary ViewModels or modules — reduce duplication and layering where not essential

---

## 🧩 Feature Expansion

- When adding a new feature, reuse existing navigation and architecture structure
- Scaffold all layers (data, domain, presentation) and ensure DI is respected
- Ensure new Composables use local ViewModel and are isolated from unrelated logic
- Avoid cross-feature leaks or tightly coupled ViewModels

---

## 🚨 Caution for Cursor Codegen

- Validate correctness of modules and Hilt bindings — these are common areas of tool failure
- Avoid generating multiple ViewModels or overlapping state classes
- Confirm that Composables are hooked to the right ViewModels and not duplicating business logic
- Manual intervention may be needed, but output should always be reviewable, aligned, and minimal to fix

---

## ♻️ Maintenance & Scalability

- Use shared UI loading states (Resource wrapper, loading shimmers) instead of custom logic per screen
- Allow dynamic feature registration if scaling large (optional)
- Always use centralized logging, feature flags, and baseline performance tools
- Maintain consistent folder and naming conventions to avoid confusion and tooling bugs

