# Jetpack Optimizer AI-Structured Architecture Guide

## SYSTEM_OVERVIEW
```
TARGET: Single-module Android application with MainActivity as sole entry point
KOTLIN_VERSION: 2.2.1
ARCHITECTURE: Clean Architecture with MVVM + Repository pattern
DI_FRAMEWORK: Hilt with KSP processing
UI_FRAMEWORK: Jetpack Compose
EXTERNAL_SDKS: [Supabase, Firebase, Moshi, AWS_Cognito, AWS_S3, AWS_APIGateway]
```

## COMPLIANCE_MATRIX

| CATEGORY | STATUS | REQUIREMENTS_MET |
|----------|--------|------------------|
| MODULE_STRUCTURE | PASS | Single module, MainActivity only |
| DI_KSP | PASS | Hilt + KSP configuration |
| OFFLINE_SYNC | PASS | Room + WorkManager + Repository |
| SHARED_VIEWMODEL | PASS | Lifecycle-aware shared instances |
| COMPOSE_STATE | PASS | Optimized state management |
| PERFORMANCE | PASS | Memory + Animation + Resource optimization |
| EXTERNAL_SDK_SECURITY | PASS | Secure integration patterns |
| ACCESSIBILITY | PASS | Semantic annotations + roles |
| ERROR_HANDLING | PASS | Result wrapper + Flow error handling |
| TESTING | PASS | Comprehensive test coverage |
| OBSERVABILITY | PASS | Logging + Crash reporting + Metrics |
| DEPLOYMENT | PASS | CI/CD + Build optimization |
| CODE_QUALITY | PASS | Lint + Detekt + Static analysis |

## MANDATORY_ARCHITECTURAL_RULES

### RULE_SET_01: STATE_MANAGEMENT
```
WHEN: Handling UI state that must survive configuration changes
THEN: USE rememberSaveable
EXAMPLES: [form_inputs, scroll_positions, dialog_states, user_preferences]

WHEN: Handling temporary UI state that can be lost
THEN: USE remember
EXAMPLES: [animation_states, temporary_flags, loading_indicators]

WHEN: Computing derived state from other state values
THEN: USE derivedStateOf wrapped in remember
PATTERN: remember { derivedStateOf { computation_based_on_state } }

WHEN: Collecting StateFlow/Flow in Compose
THEN: USE collectAsStateWithLifecycle
NEVER: collectAsState (lifecycle unaware)

WHEN: Converting async operations to state
THEN: USE produceState
AVOID: Manual state updates in composables
```

### RULE_SET_02: ANIMATION_PERFORMANCE
```
WHEN: Simple property animations [alpha, scale, offset]
THEN: USE tween
EXAMPLES: [list_item_animations, loading_indicators, fade_transitions]

WHEN: Physics-based interactions
THEN: USE spring
EXAMPLES: [drag_and_drop, gesture_responses, pull_to_refresh]

WHEN: Immediate state changes without animation
THEN: USE AnimationSpec.snap()

WHEN: Multi-step animations with precise timing
THEN: USE keyframes

WHEN: Content switching between different types
THEN: USE AnimatedContent with contentKey
```

### RULE_SET_03: COMPOSE_OPTIMIZATION
```
FOR: LazyColumn/LazyRow implementations
MANDATORY_PARAMETERS:
  - key = { item.unique_identifier }
  - contentType = { item.type_identifier }
  
WHEN: Custom drawing operations
THEN: USE Modifier.drawWithCache for size-dependent drawing
AVOID: Modifier.composed { } (performance impact)

FOR: Effect management
  - DisposableEffect: cleanup operations, external SDK connections
  - LaunchedEffect: coroutine operations with proper keys
  - SideEffect: non-compose state updates (use sparingly)
```

### RULE_SET_04: EXTERNAL_SDK_INTEGRATION

#### SUPABASE_RULES
```
CLIENT_CONFIGURATION:
  - SupabaseClient as @Singleton in Hilt module
  - RealtimeChannel cleanup in DisposableEffect MANDATORY
  
QUERY_OPTIMIZATION:
  - PostgrestFilterBuilder extensions for type safety
  - Flow.distinctUntilChanged() for session state
  
AUTHENTICATION:
  - SupabaseAuth.currentSession wrapped in Repository
  - Session state caching with StateFlow
```

#### FIREBASE_RULES
```
FIRESTORE_OPTIMIZATION:
  - enableNetwork()/disableNetwork() based on connectivity
  - ListenerRegistration.remove() in DisposableEffect MANDATORY
  
STORAGE_OPERATIONS:
  - putFile() with addOnProgressListener() for UI feedback
  - Error handling with specific FirebaseException types
  
AUTHENTICATION:
  - currentUser flow cached in Repository
  - Automatic token refresh implementation
```

#### MOSHI_CONFIGURATION
```
MANDATORY_ANNOTATIONS:
  - @JsonClass(generateAdapter = true) on ALL data classes
  - @Transient for non-serialized fields
  
BUILDER_SETUP:
  - Moshi.Builder().add(KotlinJsonAdapterFactory()) REQUIRED
  - Custom @ToJson/@FromJson adapters for complex types
```

#### AWS_SDK_RULES
```
COGNITO_INTEGRATION:
  - AWSCognitoIdentityProvider as singleton through Hilt
  - getCurrentUser() wrapped in Repository with Flow emission
  
S3_OPERATIONS:
  - S3TransferUtility with lifecycle management
  - Progress callbacks for upload/download operations
  
API_GATEWAY:
  - Generated clients cached in Repository layer
  - Automatic retry with exponential backoff
```

### RULE_SET_05: MEMORY_MANAGEMENT
```
REFERENCE_HANDLING:
  - WeakReference for ViewModels holding UI references
  - LruCache for image/data caching (no unlimited collections)
  
FLOW_OPTIMIZATION:
  - Flow.distinctUntilChanged() to prevent duplicate emissions
  - Flow.shareIn(SharingStarted.WhileSubscribed(5000)) for shared streams
  - Flow.stateIn(SharingStarted.Lazily) for Flow to StateFlow conversion
  
COROUTINE_MANAGEMENT:
  - SupervisorJob() in ViewModels MANDATORY
  - Dispatchers.IO.limitedParallelism(1) for sequential operations
  - Channel with appropriate capacity (avoid unlimited)
```

### RULE_SET_06: SECURITY_PATTERNS
```
API_KEY_MANAGEMENT:
  - BuildConfig fields for non-sensitive configuration
  - local.properties injection for development keys
  - AWS Parameter Store/Secrets Manager for production
  - Certificate pinning MANDATORY for production APIs
  
AUTHENTICATION_FLOW:
  - JWT token refresh logic implementation
  - BiometricPrompt for sensitive operations
  - EncryptedSharedPreferences for token storage
  - Automatic logout on session timeout
```

### RULE_SET_07: ERROR_HANDLING_PATTERNS
```
RESULT_WRAPPER:
  - sealed class Result<T> MANDATORY for API responses
  - Specific exception catching (avoid generic Exception)
  
FLOW_ERROR_HANDLING:
  - Flow.catch { } for stream error handling
  - runCatching { } for operations that might fail
  
CUSTOM_EXCEPTIONS:
  - Business logic specific exception classes
  - Proper error propagation through layers
```

### RULE_SET_08: ACCESSIBILITY_REQUIREMENTS
```
MANDATORY_COMPOSE_ACCESSIBILITY:
  - contentDescription for ALL interactive elements
  - semantics { } blocks for complex components
  - Modifier.semantics { merged = true } for grouped content
  - Role.Button, Role.Image explicit declarations
  - AccessibilityAction for custom interactions
```

## IMPLEMENTATION_SESSION_STRUCTURE

### SESSION_01: FOUNDATION_SETUP
```
DELIVERABLES:
  - Android project with single module configuration
  - build.gradle with KSP setup for Kotlin 2.2.1
  - External SDK dependencies configuration
  - MainActivity as sole activity
  - Application class with @HiltAndroidApp

VALIDATION_CRITERIA:
  - KSP processing functional
  - External SDKs properly initialized
  - Hilt dependency graph validates
```

### SESSION_02: DATA_LAYER_FOUNDATION
```
DELIVERABLES:
  - Room entities with proper annotations
  - DAO interfaces with Flow return types
  - Database class with version management
  - External SDK data source abstractions

VALIDATION_CRITERIA:
  - Database schema validation passes
  - DAO operations return proper Flow types
  - External SDK clients injectable
```

### SESSION_03: REPOSITORY_DATASTORE_SETUP
```
DELIVERABLES:
  - Repository interfaces with Result wrapper
  - Repository implementations with error handling
  - DataStore configuration for preferences
  - External SDK integration in repositories

VALIDATION_CRITERIA:
  - Repository methods return Result<T>
  - DataStore operations are type-safe
  - External SDK errors properly wrapped
```

### SESSION_04: DOMAIN_USECASE_LAYER
```
DELIVERABLES:
  - UseCase classes with single responsibility
  - Business logic encapsulation
  - Error transformation logic
  - External SDK business rules

VALIDATION_CRITERIA:
  - Each UseCase has single invoke function
  - Business rules properly implemented
  - Error handling consistent across use cases
```

### SESSION_05: VIEWMODEL_IMPLEMENTATION
```
DELIVERABLES:
  - ViewModels with @HiltViewModel annotation
  - StateFlow for UI state management
  - Proper lifecycle handling
  - Shared ViewModel configuration

VALIDATION_CRITERIA:
  - ViewModels survive configuration changes
  - State updates trigger UI recomposition
  - Memory leaks prevented with proper scoping
```

### SESSION_06: COMPOSE_UI_NAVIGATION
```
DELIVERABLES:
  - Composables with performance optimizations
  - Navigation graph with proper transitions
  - Accessibility implementations
  - External SDK UI integration

VALIDATION_CRITERIA:
  - UI performance meets 60fps target
  - Navigation transitions smooth
  - Accessibility requirements satisfied
  - External SDK operations reflect in UI
```

### SESSION_07: DEPENDENCY_INJECTION_MODULES
```
DELIVERABLES:
  - Hilt modules for all dependencies
  - External SDK client provisioning
  - Proper scope management
  - Configuration injection

VALIDATION_CRITERIA:
  - Dependency graph compiles without cycles
  - External SDK clients properly scoped
  - Configuration values injected correctly
```

### SESSION_08: BACKGROUND_TASKS_UTILITIES
```
DELIVERABLES:
  - WorkManager implementation
  - Utility classes for common operations
  - External SDK background sync
  - Performance monitoring setup

VALIDATION_CRITERIA:
  - Background tasks execute properly
  - External SDK sync operations functional
  - Performance metrics collection active
```

## VALIDATION_CHECKLIST

### PRE_IMPLEMENTATION_VALIDATION
```
□ Kotlin 2.2.1 properly configured
□ KSP processing enabled for all annotation processors
□ External SDK versions compatible
□ Single module structure confirmed
□ MainActivity as sole entry point validated
```

### POST_SESSION_VALIDATION
```
□ All mandatory patterns implemented
□ Performance requirements met
□ Security patterns enforced
□ Accessibility requirements satisfied
□ Error handling comprehensive
□ External SDK integration functional
□ Memory management optimized
□ Testing coverage adequate
```

## EXTERNAL_SDK_VERSION_CONSTRAINTS
```toml
[versions]
kotlin = "2.2.1"
supabase = "2.0.0+"
firebase-bom = "32.7.0+"
moshi = "1.15.0+"
aws-android-sdk = "2.77.0+"
compose-bom = "2024.02.00+"

[bundles]
external-apis = [
  "supabase-postgrest-kt",
  "supabase-auth-kt", 
  "firebase-firestore-ktx",
  "moshi-kotlin"
]
aws-suite = [
  "aws-android-sdk-cognito",
  "aws-android-sdk-s3",
  "aws-android-sdk-apigateway-core"
]
```

## AI_GENERATION_CONSTRAINTS
```
WHEN: Generating code for this architecture
THEN: MUST follow all RULE_SET patterns
AND: MUST use specified external SDK integration patterns
AND: MUST implement mandatory performance optimizations
AND: MUST include proper error handling
AND: MUST satisfy accessibility requirements

OUTPUT_FORMAT: No import statements in summaries
ARCHITECTURE_FOCUS: Single-module, single-activity, shared ViewModels
EXTERNAL_SDK_PRIORITY: Secure integration with proper lifecycle management
```