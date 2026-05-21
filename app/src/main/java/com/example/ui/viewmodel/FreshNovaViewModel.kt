package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.db.FoodDatabase
import com.example.data.model.FoodItem
import com.example.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Dashboard : Screen()
}

sealed class BottomTab {
    object Home : BottomTab()
    object Scan : BottomTab()
    object Inventory : BottomTab()
    object Insights : BottomTab()
    object Profile : BottomTab()
}

sealed class AIRecommendationsState {
    object Idle : AIRecommendationsState()
    object Loading : AIRecommendationsState()
    data class Success(val recipes: String) : AIRecommendationsState()
    data class Error(val message: String) : AIRecommendationsState()
}

data class UserProfile(
    val name: String = "Rahul Sharma",
    val email: String = "rahul.sharma@email.com",
    val statusBadge: String = "Eco Saver",
    val itemsSaved: Int = 32,
    val moneySaved: Double = 2356.0,
    val dayStreak: Int = 12
)

data class ScannedBill(
    val storeName: String,
    val dateText: String,
    val billNumber: String,
    val items: List<FoodItem>
)

class FreshNovaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = FoodDatabase.getDatabase(application)
    private val repository = FoodRepository(database.foodDao())

    // App Navigation UI States
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow<BottomTab>(BottomTab.Home)
    val currentTab: StateFlow<BottomTab> = _currentTab.asStateFlow()

    // Persistent Room Flows
    val activeInventory: StateFlow<List<FoodItem>> = repository.activeItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historicalConsumed: StateFlow<List<FoodItem>> = repository.consumedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historicalWasted: StateFlow<List<FoodItem>> = repository.wastedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active User Profile Info (Local state that adapts upon consuming/wasting)
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Interactive Sorting Configs
    private val _inventorySortBy = MutableStateFlow("Expiry Date") // "Expiry Date", "Name", "Category"
    val inventorySortBy: StateFlow<String> = _inventorySortBy.asStateFlow()

    // Chef Nova-AI Recipes Panel State
    private val _aiState = MutableStateFlow<AIRecommendationsState>(AIRecommendationsState.Idle)
    val aiState: StateFlow<AIRecommendationsState> = _aiState.asStateFlow()

    // simulated Scanner Overlay view states
    private val _scannedBill = MutableStateFlow<ScannedBill?>(null)
    val scannedBill: StateFlow<ScannedBill?> = _scannedBill.asStateFlow()

    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn.asStateFlow()

    private val _showScannerOverlay = MutableStateFlow(false)
    val showScannerOverlay: StateFlow<Boolean> = _showScannerOverlay.asStateFlow()

    // App Preferences (Preferences Screen)
    val languages = listOf("English", "हिन्दी (Hindi)", "Español (Spanish)")
    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(true)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    init {
        // Run database populator on instantiation immediately
        viewModelScope.launch {
            repository.checkAndAddSampleData()
            // Simulating a fast sleek splash timer
            kotlinx.coroutines.delay(1800)
            _currentScreen.value = Screen.Login
        }
    }

    // Auth Flows
    fun login(email: String, passcode: String) {
        viewModelScope.launch {
            _currentScreen.value = Screen.Dashboard
            _currentTab.value = BottomTab.Home
        }
    }

    fun logout() {
        _currentScreen.value = Screen.Login
    }

    // Storage updates (Reactively modifies profile counters to show smart impact!)
    fun markAsConsumed(item: FoodItem) {
        viewModelScope.launch {
            repository.consumeItem(item)
            val currentSavings = _userProfile.value.moneySaved + item.estimatedCost
            val currentCount = _userProfile.value.itemsSaved + 1
            _userProfile.value = _userProfile.value.copy(
                moneySaved = currentSavings,
                itemsSaved = currentCount
            )
        }
    }

    fun markAsWasted(item: FoodItem) {
        viewModelScope.launch {
            repository.wasteItem(item)
        }
    }

    fun deleteItem(item: FoodItem) {
        viewModelScope.launch {
            repository.deleteById(item.id)
        }
    }

    fun addCustomItem(name: String, quantity: String, category: String, shelfLifeDays: Int, location: String, estimatedCost: Double) {
        viewModelScope.launch {
            val expiryMillis = System.currentTimeMillis() + (shelfLifeDays * 24L * 60L * 60L * 1000L)
            repository.insert(
                FoodItem(
                    name = name,
                    quantity = quantity,
                    category = category,
                    expiryDateMillis = expiryMillis,
                    storageLocation = location,
                    estimatedCost = if (estimatedCost > 0.0) estimatedCost else 40.0
                )
            )
        }
    }

    // Set sorting criterion
    fun setInventorySortBy(criteria: String) {
        _inventorySortBy.value = criteria
    }

    fun setBottomTab(tab: BottomTab) {
        _currentTab.value = tab
    }

    // Scanner actions and simulations
    fun toggleFlashlight() {
        _isFlashlightOn.value = !_isFlashlightOn.value
    }

    fun startQRScanSimulation() {
        _showScannerOverlay.value = true
        _scannedBill.value = null
        viewModelScope.launch {
            // Wait 1.5s for green scan bar matching modern scanner feel
            kotlinx.coroutines.delay(1200)
            // Generate parsed receipt content dynamically based on current time
            val now = System.currentTimeMillis()
            val day = 24 * 60 * 60 * 1000L
            val simulatedItems = listOf(
                FoodItem(
                    name = "Milk",
                    quantity = "1 Litre",
                    category = "Dairy",
                    expiryDateMillis = now + day, // 1 day left
                    storageLocation = "Refrigerated",
                    estimatedCost = 57.0
                ),
                FoodItem(
                    name = "Bread",
                    quantity = "Brown Bread",
                    category = "Bakery",
                    expiryDateMillis = now, // Today
                    storageLocation = "Pantry",
                    estimatedCost = 50.0
                ),
                FoodItem(
                    name = "Yogurt",
                    quantity = "400 g",
                    category = "Dairy",
                    expiryDateMillis = now + 2 * day, // 2 days left
                    storageLocation = "Refrigerated",
                    estimatedCost = 80.0
                ),
                FoodItem(
                    name = "Eggs",
                    quantity = "6 pcs",
                    category = "Dairy",
                    expiryDateMillis = now + 7 * day, // 7 days left
                    storageLocation = "Refrigerated",
                    estimatedCost = 45.0
                ),
                FoodItem(
                    name = "Cheese",
                    quantity = "200 g",
                    category = "Dairy",
                    expiryDateMillis = now + 10 * day, // 10 days left
                    storageLocation = "Refrigerated",
                    estimatedCost = 150.0
                )
            )
            _scannedBill.value = ScannedBill(
                storeName = "GREEN MART",
                dateText = "Date: 05 May 2025",
                billNumber = "Bill No: GM124587",
                items = simulatedItems
            )
        }
    }

    fun closeQRScan() {
        _showScannerOverlay.value = false
        _scannedBill.value = null
    }

    fun saveScannedBillToInventory() {
        val bill = _scannedBill.value
        if (bill != null) {
            viewModelScope.launch {
                repository.insertAll(bill.items)
                closeQRScan()
                // Auto route back to primary list tab to view saved items immediately!
                _currentTab.value = BottomTab.Inventory
            }
        }
    }

    // Chef Nova-AI trigger
    fun loadAIFoodRecommendations() {
        _aiState.value = AIRecommendationsState.Loading
        viewModelScope.launch {
            try {
                val currentPantry = activeInventory.value
                val suggestions = GeminiClient.getRecipeRecommendations(currentPantry)
                _aiState.value = AIRecommendationsState.Success(suggestions)
            } catch (e: Exception) {
                _aiState.value = AIRecommendationsState.Error(e.message ?: "Failed loading kitchen recommendations.")
            }
        }
    }

    // Profile preferences setters
    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun toggleNotifications() {
        _isNotificationEnabled.value = !_isNotificationEnabled.value
    }
}
