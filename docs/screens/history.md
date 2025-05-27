# History Screen - Technical Specification

## 🎯 Overview
Data-heavy screen showing saved summaries. 90% of users have <20 items, but design must scale to 1000+ without performance issues.

## 📱 Layout Structure
```
┌─────────────────────────────┐
│ History          [Search] 🔍 │ 56dp
├─────────────────────────────┤
│ 127 summaries    [Filter] ▼ │ 48dp
├═════════════════════════════┤ Sticky header
│ TODAY                       │ 32dp
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ Contract Review Summary  │ │ 88dp height
│ │ "Key terms include..."   │ │ 2 lines max
│ │ 2:30 PM • 450→75 words  │ │ Metadata
│ └─────────────────────────┘ │
├═════════════════════════════┤
│ YESTERDAY                   │ Sticky
├─────────────────────────────┤
│ [Items...]                  │
└─────────────────────────────┘
```

## 🛠️ Implementation

### Core List Component
```kotlin
@Composable
fun HistoryScreen() {
    val historyItems by viewModel.historyItems.collectAsState()
    val groupedItems = remember(historyItems) {
        historyItems.groupByTimeframe()
    }
    
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedItems.forEach { (timeframe, items) ->
            stickyHeader(key = timeframe) {
                SectionHeader(
                    title = timeframe,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
            
            items(
                items = items,
                key = { it.id },
                contentType = { "summary_item" }
            ) { item ->
                SwipeableHistoryItem(
                    item = item,
                    onShare = { shareItem(it) },
                    onDelete = { deleteItem(it) },
                    onClick = { openSummary(it) }
                )
            }
        }
    }
}

fun List<HistoryItem>.groupByTimeframe(): List<Pair<String, List<HistoryItem>>> {
    val now = System.currentTimeMillis()
    val today = now.startOfDay()
    val yesterday = today - TimeUnit.DAYS.toMillis(1)
    val thisWeek = today - TimeUnit.DAYS.toMillis(7)
    
    return groupBy { item ->
        when {
            item.timestamp >= today -> "TODAY"
            item.timestamp >= yesterday -> "YESTERDAY"
            item.timestamp >= thisWeek -> "THIS WEEK"
            else -> "OLDER"
        }
    }.toList()
}
```

### Swipeable Item
```kotlin
@Composable
fun SwipeableHistoryItem(
    item: HistoryItem,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var swipeState by remember { mutableStateOf(SwipeState.Rest) }
    
    SwipeableRow(
        onSwipeLeft = { onDelete() },
        onSwipeRight = { onShare() },
        leftAction = SwipeAction(
            icon = Icons.Default.Delete,
            color = Color.Red,
            text = "Delete"
        ),
        rightAction = SwipeAction(
            icon = Icons.Default.Share,
            color = Color.Green,
            text = "Share"
        )
    ) {
        HistoryItemCard(
            item = item,
            onClick = onClick
        )
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("history_item_${item.id}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = item.preview,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.timestamp.formatRelativeTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${item.originalWords}→${item.summaryWords} words",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

### Multi-Select Mode
```kotlin
@Composable
fun MultiSelectHistoryScreen() {
    var selectionMode by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<String>()) }
    
    if (selectionMode) {
        ContextualActionBar(
            selectedCount = selectedItems.size,
            onDelete = { deleteItems(selectedItems) },
            onShare = { shareItems(selectedItems) },
            onExport = { exportItems(selectedItems) },
            onExit = { 
                selectionMode = false
                selectedItems = emptySet()
            }
        )
    }
    
    LazyColumn {
        items(historyItems) { item ->
            SelectableHistoryItem(
                item = item,
                isSelected = item.id in selectedItems,
                selectionMode = selectionMode,
                onLongPress = {
                    if (!selectionMode) {
                        selectionMode = true
                        selectedItems = setOf(item.id)
                        HapticFeedback.longPress()
                    }
                },
                onToggleSelection = {
                    selectedItems = if (item.id in selectedItems) {
                        selectedItems - item.id
                    } else {
                        selectedItems + item.id
                    }
                }
            )
        }
    }
}
```

### Performance Optimizations
```kotlin
object PerformanceConfig {
    const val PAGE_SIZE = 50
    const val PREFETCH_DISTANCE = 10
    const val MAX_ITEMS_IN_MEMORY = 200
    
    // Pagination
    @Composable
    fun PaginatedHistoryList() {
        val pager = Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = true
            )
        ) { HistoryPagingSource() }
        
        val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
        
        LazyColumn {
            items(lazyPagingItems) { item ->
                item?.let { HistoryItemCard(it) }
            }
        }
    }
}
```

## 🔍 Search & Filter
```kotlin
@Composable
fun SearchableHistoryScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var activeFilters by remember { mutableStateOf(setOf<Filter>()) }
    
    val filteredItems = remember(historyItems, searchQuery, activeFilters) {
        historyItems
            .filter { item ->
                if (searchQuery.isBlank()) true
                else item.searchableText.contains(searchQuery, ignoreCase = true)
            }
            .filter { item ->
                activeFilters.isEmpty() || activeFilters.any { it.matches(item) }
            }
    }
    
    Column {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search summaries..."
        )
        
        FilterChips(
            activeFilters = activeFilters,
            onFilterToggle = { filter ->
                activeFilters = if (filter in activeFilters) {
                    activeFilters - filter
                } else {
                    activeFilters + filter
                }
            }
        )
        
        HistoryList(items = filteredItems)
    }
}
```

## ⚠️ Edge Cases
| **Scenario** | **Solution** |
|--------------|--------------|
| **1000+ items** | Pagination + virtual scrolling |
| **Rapid swipes** | Debounce + action queue |
| **Storage full** | Auto-cleanup + user warning |
| **Search 10K items** | Async search + debounce |
| **Multi-select all** | Limit to 100 items |

---

**Reality Check**: 90% of users have <20 items. Don't over-engineer search until needed. Fast, simple list > feature-rich laggy one.
