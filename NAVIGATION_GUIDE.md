### Project Navigation Guide

This document outlines the navigation implementation in the Koin app, focusing on best practices for handling authentication and logout flows in Jetpack Compose.

### 1. Implementation Overview

The app uses a single `NavHost` within `MainActivity.kt` to manage all navigation. The key navigation flows are:

*   **Authentication (`auth` route):** Handles user login and registration.
*   **Main App (`coin_list`, `profile` routes):** The main content of the app, accessible only after a user is authenticated.

### 2. The Logout Navigation Challenge

**Problem:**
Previously, attempting to log out from the `ProfileScreen` resulted in a navigation error: `Ignoring popBackStack to route auth as it was not found on the current back stack`.

**Root Cause:**
The logout logic was incorrectly trying to navigate *back* to the `auth` screen using `popBackStack`. However, after a successful login, the `auth` screen is (correctly) removed from the navigation history to prevent the user from accidentally returning to it. You can't pop back to a screen that isn't in the back stack.

### 3. Pitfalls to Avoid

*   **Don't `popBackStack` to a Cleared Destination:** Never assume a destination is on the back stack, especially in authentication flows where the login screen is typically cleared after the user signs in.
*   **Avoid Complex and Unclear Navigation Logic:** The original logout code was attempting multiple `popUpTo` operations that were redundant and logically flawed, making the intent difficult to understand and debug.

### 4. Best Practices and Implemented Solution

To fix the logout issue and ensure robust navigation, we implemented the following strategy in `MainActivity.kt`:

**The Fix:**
When the `onLogout` event is triggered, we now use this navigation logic:

```kotlin
navController.navigate("auth") {
    // Pop up to the start destination of the graph to clear the entire back stack.
    popUpTo(navController.graph.findStartDestination().id) {
        inclusive = true
    }
    // Avoid multiple copies of the same destination when re-navigating.
    launchSingleTop = true
}
```

**Why This Works:**

1.  **`navController.navigate("auth")`**: We perform a standard forward navigation to the `auth` route.
2.  **`popUpTo(navController.graph.findStartDestination().id)`**: This is the crucial part. It finds the very first screen of our navigation graph and clears all screens on top of it.
3.  **`inclusive = true`**: This ensures that the start destination itself is also removed, effectively wiping the entire navigation history.
4.  **`launchSingleTop = true`**: This is a standard best practice that prevents creating multiple instances of the `auth` screen if it were ever to be launched again while already on top.

This pattern provides a clean, predictable, and error-free way to handle user logout, resetting the app to its initial state.
