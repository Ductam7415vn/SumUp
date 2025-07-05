# **Navigation & State Management Deep-Dive**

## **Navigation Architecture**

### **Navigation Graph**
```kotlin
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Ocr : Screen("ocr")
    object Processing : Screen("processing")
    object Result : Screen("result")
    object Settings : Screen("settings")
    object History : Screen("history")
}
```

### **Navigation Flows**
- MainScreen → OcrScreen → MainScreen (with scanned text)
- MainScreen → ProcessingScreen → ResultScreen
- MainScreen → HistoryScreen/SettingsScreen → back
- ResultScreen → HistoryScreen → back

### **Advanced Features**
- **State Preservation**: UI state maintained during navigation
- **Type-Safe Arguments**: Proper data flow between screens
- **Back Stack Management**: Custom pop behavior
- **Saved State Handle**: Text passing between screens

---

## **State Management Patterns**

### **ViewModel Architecture**
Each screen implements sophisticated state management:
- **StateFlow**: Reactive state updates with lifecycle awareness
- **Computed Properties**: Derived state calculations
- **Event Handling**: User action processing
- **Navigation Coordination**: Screen transition management

### **Benefits**
- **Single Source of Truth**: Predictable state updates
- **Reactive UI**: Automatic UI updates with state changes
- **Centralized Error Handling**: Consistent error management
- **Loading States**: Professional loading patterns

### **State Examples**
- **MainUiState**: 9 properties with computed validation
- **OcrUiState**: Camera permissions + text recognition
- **ProcessingUiState**: Multi-stage progress tracking
