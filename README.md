# Calmora App

## Leaderboard & App Usage Tracking

### Overview
The app now includes a leaderboard feature that displays:
- A bar chart showing daily app usage for the current week only
- A list of the top three users with their current week usage time

### Implementation Details

#### App Usage Tracking
- The app tracks usage time using the `AppUsageTracker` class
- Time tracking starts when the app comes to the foreground and stops when it goes to the background
- Usage data is stored in Firestore under the "Users" collection with the following fields:
  - `totalAppUsage`: Total minutes of app usage (lifetime)
  - `day_1` to `day_7`: Minutes used on each day of the week (1=Sunday, 7=Saturday)
  - `currentWeekUsage`: Total minutes used in the current week
  - `currentWeekId`: Identifier for the current week (format: "week_YEAR_WEEKNUMBER")

#### Cloud Functions
- A scheduled cloud function runs every Monday at 00:01 UTC
- This function resets all daily counters for the new week
- It also updates the current week identifier to help with data segregation
- The reset ensures that only current week data is shown in the leaderboard

#### LeaderBoard UI
- The bar chart displays the current week's daily usage organized by day of week
- The leaderboard shows the top three users based on their current week usage
- Only users with data from the current week are included in the rankings
- Usage time is displayed in hours and minutes format

### Usage
- No user action is required to track app usage
- The leaderboard automatically displays real-time data from the current week when viewed

### Testing
- A manual trigger function is available at `/manualTriggerWeeklyReset` for testing weekly reset
