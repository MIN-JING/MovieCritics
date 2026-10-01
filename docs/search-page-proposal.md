# Search Page Proposal

## Goal

Turn Search from a passive result list into a decision-making entry point.

The current implementation already supports TMDB multi-search and basic UI states:

- `SearchScreen` renders the search box and result list.
- `SearchState` defines `INITIAL_RESULTS`, `SUGGESTIONS`, `NO_RESULTS`, and `RESULTS`.
- `SearchViewModel` calls `repository.getSearchMulti(query)`.

The next step is not "add more results". The next step is to help users decide faster:

- what to watch
- whether it matches their taste
- what action to take next

## Current Gaps

- The initial state is effectively empty.
- Suggestions are modeled in state but not implemented in product behavior.
- Results are flat and do not help users compare options quickly.
- Search items do not expose quick actions such as add to watchlist or mark as watched.
- Search behavior is mostly UI-driven and will become harder to maintain as features grow.
- Search does not yet leverage project-specific value such as reviews, scores, watchlist, and social data.

## Product Direction

Search should serve three jobs:

1. Query lookup
2. Content discovery
3. Action conversion

This means the page should support:

- direct keyword search
- exploratory browsing when the user does not know what to type
- fast actions without forcing a detail-page detour

## Proposed Experience

### 1. Initial State

When the search field is not focused and empty, show useful entry points instead of blank space.

- Trending searches
- Popular movies and series
- Recently viewed items
- Personalized rails such as "Because you liked..." when user data exists
- Curated topic shortcuts such as "Mind-bending", "Weekend picks", "High-rated drama"

Expected outcome:

- Search becomes a discovery page, not just a utility page.

### 2. Suggestion State

When the search field is focused and empty, show high-intent suggestions.

- Recent searches
- Popular search keywords
- Suggested entities from local history
- Quick chips for `Movie`, `TV`, `Person`

Expected outcome:

- Reduced typing
- Better first query quality

### 3. Result State

When results are returned, optimize for comparison and action.

- Group by media type: `Movies`, `TV`, `People`
- Show compact metadata on each card
- Keep cards visually scannable before users open detail

Recommended card fields:

- Poster or profile image
- Title or name
- Release year or first air year
- Media type
- TMDB score
- Short overview or known-for summary
- Current user state: `In Watchlist`, `Watched`, `Rated`

Expected outcome:

- Users can choose faster
- Less back-and-forth between search and detail

### 4. Quick Actions

Every movie or TV result should support at least one action directly in search.

- Add to watchlist
- Remove from watchlist
- Mark as watched
- Open detail

Optional later actions:

- Rate directly
- Share
- Add reminder

Expected outcome:

- Search becomes a conversion funnel, not just navigation

### 5. No Result State

Do not leave users at a dead end.

- Show "Try another keyword"
- Suggest similar keywords
- Offer fallback rails such as trending titles
- Show category shortcuts

Expected outcome:

- Reduced drop-off after failed searches

## Advanced Features Worth Building

These are the highest-value features for this specific project.

### A. Filter and Sort Layer

Add a lightweight filter bar above results.

- Media type: `All`, `Movie`, `TV`, `Person`
- Year range
- Minimum score
- Sort by popularity
- Sort by release date
- Sort by rating

Why this matters:

- TMDB multi-search is broad.
- Filtering turns raw API output into a usable search tool.

### B. Search History

Store recent queries locally.

- Last 10 to 20 searches
- Pin favorite searches
- Tap to rerun
- Long-press to delete

Why this matters:

- Easy implementation
- High daily usefulness

### C. Personalized Ranking

Re-rank results using local user context.

- Prefer genres the user watches often
- Boost items related to liked or reviewed content
- Surface items reviewed by followed users

Why this matters:

- This is where the product stops feeling generic.

### D. Explore Search

Support intent-driven entry points, not only exact keywords.

- "What should I watch tonight?"
- "Under 2 hours"
- "Highly rated but less mainstream"
- "Popular thrillers"

Why this matters:

- Good fit for a movie product
- Strong differentiation from standard TMDB wrappers

### E. Social Signals in Results

Blend social proof into search results when app data exists.

- Average app score
- Count of user reviews
- Followed users who watched or reviewed
- Your own status on that title

Why this matters:

- The app is not only a catalog browser.
- Social context can improve selection confidence.

## Information Architecture

Recommended search page sections:

- Search bar
- Suggestion or discovery area
- Filter row
- Result section tabs or grouped headers
- Result list
- Inline quick actions

Recommended state model:

- `Idle`
- `FocusedEmpty`
- `Loading`
- `Results`
- `NoResults`
- `Error`

This is simpler and easier to reason about than splitting result ownership between local Compose state and ViewModel state.

## UX Notes

- Debounce input around 300 to 500 ms.
- Do not fire network calls for blank queries.
- Preserve query when navigating to detail and coming back.
- Show loading feedback inside the result area, not only in logs.
- Make result rows clickable across the full row.
- Keep actions visible but secondary to the main title tap.

## Technical Proposal

### Phase 1: Stabilize Search Architecture

Refactor current search flow into one ViewModel-owned UI state.

Recommended changes:

- Move debounce and search trigger logic into `SearchViewModel`
- Replace duplicated local result state in `SearchScreen`
- Expose a single `SearchUiState`
- Add explicit events for query change, clear, retry, filter change, and item click

Target files:

- `app/src/main/java/com/jim/moviecritics/search/SearchScreen.kt`
- `app/src/main/java/com/jim/moviecritics/search/SearchState.kt`
- `app/src/main/java/com/jim/moviecritics/search/SearchViewModel.kt`

### Phase 2: Ship High-Impact Features

- Recent searches
- Suggestion panel
- Click-through to detail
- Media-type filter chips
- Better result cards
- Quick watchlist action

### Phase 3: Product Differentiation

- Personalized ranking
- Social metadata
- Explore search presets
- Paging for long result sets

## Suggested Roadmap

### Milestone 1

Make search feel complete.

- Result row click opens detail
- Recent searches
- Empty-state suggestions
- Loading and error UI
- Basic media-type chips

### Milestone 2

Improve decision speed.

- Grouped result sections
- Richer metadata on cards
- Watchlist quick action
- No-result fallback content

### Milestone 3

Add product differentiation.

- Personalized ranking
- Social proof in result cards
- Explore search presets

## Success Metrics

Track these after rollout:

- Search-to-detail click-through rate
- Search-to-watchlist conversion
- Average number of searches per session
- Zero-result abandonment rate
- Repeat usage of recent searches

## Recommended First Build

If only one iteration is funded, build this set:

- Recent searches
- Suggestion panel
- Filter chips for `All`, `Movie`, `TV`, `Person`
- Result row navigation
- Watchlist quick action
- No-result fallback

This is the best balance of user value, implementation cost, and product differentiation.
