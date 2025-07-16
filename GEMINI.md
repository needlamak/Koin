## Gemini Added Memories
- The project uses a caching mechanism (true_ssot_crypto_app.md) for data loading in CoinDetailViewModel and CoinListViewModel to improve performance and address past data loading issues.
- The project uses a caching mechanism (true_ssot_crypto_app.md) for data loading in CoinDetailViewModel and CoinListViewModel to improve performance and address past data loading issues.

## Recent Progress

This section summarizes the recent changes and additions made to the project:

### UI Enhancements: Coin Images and Indicators

*   **Coin Images in Bottom Sheets:** Implemented the display of coin images and symbols in `BuyBottomSheet.kt` and `BuySuccessBottomSheet.kt`. This involved updating `BuyTransactionDetails.kt` and `CoinListViewModel.kt` to correctly pass the necessary image and symbol data.
*   **Coin Images in Transaction History:** Enhanced the transaction history views by adding coin images and symbols. This required:
    *   Adding a `coinImage` field to the `Transaction` data class (`app/src/main/java/com/koin/domain/model/Transaction.kt`).
    *   Modifying `TransactionRepositoryImpl.kt` to fetch and include the `coinImage` when populating `Transaction` objects.
    *   Updating `TransactionCard.kt` and `TransactionDetailScreen.kt` to display the coin image with a fallback to the coin symbol if the image is unavailable.
*   **Gain/Loss Indicators:** Introduced a new reusable `ChangeIndicator` composable (`app/src/main/java/com/koin/components/ChangeIndicator.kt`). This component displays a small, filled triangle (green and pointing up for gains, red and pointing down for losses) next to relevant percentage or PnL figures. These indicators have been integrated into:
    *   The `BalanceHeader` in `PortfolioScreen.kt` to show overall portfolio performance.
    *   `PortfolioDetailScreen.kt` to indicate the performance of individual coins within the portfolio.
    *   `CoinDetailScreen.kt` to display the 24-hour price change direction.

### New Feature: Total Balance Screen

*   **Dedicated Total Balance Screen:** Created a new `TotalBalanceScreen.kt` (`app/src/main/java/com/koin/ui/totalbalance/TotalBalanceScreen.kt`) to provide a centralized view of the user's total balance.
*   **Data and UI Components:** The new screen includes:
    *   A display for the total balance value.
    *   A `LazyRow` to show avatars of recent transaction users (currently using dummy data for avatars).
    *   A section for recent transactions, utilizing `TransactionCard` components.
    *   A "View All" pill that navigates to the `TransactionHistoryScreen.kt`.
*   **ViewModel for Total Balance:** Developed `TotalBalanceViewModel.kt` to manage the data for the `TotalBalanceScreen`, fetching total balance from `PortfolioRepository` and recent transactions from `TransactionRepository`.
*   **Navigation Integration:** The new screen's route (`TotalBalance`) was defined in `app/src/main/java/com/koin/navigation/Screen.kt` and integrated into the main `NavGraph.kt`. A navigation card was added to `ProfileScreen.kt` to allow users to access the `TotalBalanceScreen`.

### Performance Optimization

*   **CoinListScreen Scrolling Improvement:** Addressed reported scrolling choppiness in `CoinListScreen.kt` by implementing debouncing for search queries within `CoinListViewModel.kt`. This significantly reduces the frequency of filtering and sorting operations, leading to a smoother user experience.
*   **ViewModel Refactoring:** Streamlined the data loading logic in `CoinListViewModel.kt` by removing the redundant `loadCoins()` function, ensuring that data flow is efficiently managed through the `combine` operator in the `init` block.
