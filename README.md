# Universal Android Recipe App

A comprehensive Android recipe application built with modern Android development practices, featuring clean architecture, orientation-aware layouts, accessibility support, and comprehensive testing. The app demonstrates clean architecture principles with MVVM pattern, dependency injection, and robust state management.

## Screenshots

### Phone
**Recipe List Screen**
| Portrait | Landscape | Large font |
| --- | --- | --- |
| <img width="270" height="550" alt="Screenshot_20250820_232021" src="https://github.com/user-attachments/assets/6effc33b-c419-43b8-af2d-76d7f58e5d7d" /> | <img width="550" height="270" alt="Screenshot_20250820_232237" src="https://github.com/user-attachments/assets/5d749c9a-8852-4e7f-9ae4-5af360e67311" /> | <img width="270" height="550" alt="Screenshot_20250820_232532" src="https://github.com/user-attachments/assets/8d435aec-e0c8-4a35-a322-10f1555505d6" /> |

**Recipe Detail Screen**

<img width="270" height="550" alt="Screenshot_20250820_233254" src="https://github.com/user-attachments/assets/44d25fc6-fe03-43ec-8f5a-28316eea8f90" />

### Tablet
**Recipe List Screen**
| Portrait | Landscape | Large font |
| --- | --- | --- |
| <img width="400" height="640" alt="Screenshot_20250820_232702" src="https://github.com/user-attachments/assets/f915e7f4-d7bf-46c1-ae83-8a285f3686d2" /> | <img width="640" height="400" alt="Screenshot_20250820_232808" src="https://github.com/user-attachments/assets/a564791a-ba97-4484-a894-ee2b68973a61" /> | <img width="400" height="640" alt="Screenshot_20250820_232924" src="https://github.com/user-attachments/assets/318dfbc4-d889-4a83-85cb-c992c0c22ec4" /> |

**Recipe Detail Screen**

<img width="400" height="640" alt="Screenshot_20250820_233040" src="https://github.com/user-attachments/assets/9b595f48-7fd4-4b54-9752-54321468bb88" />

## Features

### Core Functionality
- **Recipe Browsing**: View a collection of recipes with images, titles, and descriptions
- **Recipe Details**: Detailed view showing ingredients, cooking times, and serving sizes
- **Search & Filter**: Search recipes by title, description, or ingredients
- **Sorting**: Sort recipes by cooking time (ascending/descending) or serving size (ascending/descending)
- **Orientation Support**: Different layouts for portrait and landscape orientations
- **State Management**: Proper state handling with lifecycle-aware state collection

### Technical Features
- **Clean Architecture**: Clear separation of concerns with domain, data, and presentation layers
- **MVVM Architecture**: Model-View-ViewModel pattern with reactive state management using StateFlow
- **Jetpack Compose**: Modern declarative UI framework
- **Navigation Component**: Type-safe navigation between screens using recipe indices
- **Coroutines & Flow**: Asynchronous programming with reactive state management
- **Dependency Injection**: Hilt for dependency management
- **Unit Testing**: Comprehensive test coverage for business logic
- **Automation Testing**: UI testing with Compose Testing
- **Accessibility**: Screen reader support and proper content descriptions

### Algorithm Implementation
The app includes a unit-testable algorithm (`RecipeAlgorithm`) that provides:
- Recipe sorting by total cooking time (prep + cooking time)
- Recipe sorting by serving size
- Recipe filtering by serving range
- Efficient sorting algorithms with proper null handling

## Architecture

### Clean Architecture Layers

The app follows clean architecture principles with three main layers:

#### Domain Layer (`domain/`)
- **Recipe.kt**: Core domain entities with nested classes for details and ingredients
- **RecipeRepository.kt**: Repository interface defining data access contracts

#### Data Layer (`data/`)
- **RecipeRepositoryImpl.kt**: Repository implementation with local JSON fallback and API integration
- **RecipeListResponse.kt**: API response data classes
- **remote/RecipeApiService.kt**: Retrofit interface for API calls
- **mapper/RecipeMappers.kt**: Data transformation utilities between API and domain models

#### Presentation Layer (`presentation/`)
- **RecipeViewModel.kt**: ViewModel managing UI state with proper state flow management
- **ui/screens/**: Screen composables for recipe list and detail views
- **ui/components/**: Reusable UI components
- **ui/theme/**: App theming and styling
- **navigation/**: Navigation configuration using Compose Navigation

### Project Structure
```
app/src/main/java/com/example/universal_android_app/
├── algorithm/           # Business logic algorithms
│   └── RecipeAlgorithm.kt
├── data/               # Data layer implementation
│   ├── mapper/
│   │   └── RecipeMappers.kt
│   ├── remote/
│   │   └── RecipeApiService.kt
│   ├── RecipeListResponse.kt
│   └── RecipeRepositoryImpl.kt
├── di/                 # Dependency injection modules
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
├── domain/             # Domain layer (entities and interfaces)
│   ├── Recipe.kt
│   └── RecipeRepository.kt
├── presentation/       # Presentation layer
│   ├── navigation/
│   │   └── RecipeNavigation.kt
│   ├── ui/
│   │   ├── components/     # Reusable UI components
│   │   │   ├── AppScaffold.kt
│   │   │   ├── CommonComponents.kt
│   │   │   ├── RecipeDetailsCard.kt
│   │   │   ├── RecipeImage.kt
│   │   │   └── SearchBar.kt
│   │   ├── screens/        # Screen composables
│   │   │   ├── RecipeDetailScreen.kt
│   │   │   └── RecipeListScreen.kt
│   │   └── theme/          # App theming
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   └── RecipeViewModel.kt
├── MainActivity.kt     # Main activity
└── UniversalApplication.kt
```

### Key Components

#### Domain Layer
- **Recipe.kt**: Core domain entities with proper data classes for recipe details and ingredients
- **RecipeRepository.kt**: Repository interface defining data access contracts

#### Data Layer
- **RecipeRepositoryImpl.kt**: Repository implementation with local JSON fallback and API integration
- **RecipeApiService.kt**: Retrofit interface for API calls
- **RecipeListResponse.kt**: API response data classes
- **RecipeMappers.kt**: Data transformation utilities between API and domain models

#### Business Logic
- **RecipeAlgorithm.kt**: Core algorithm for recipe sorting and filtering with interface for testability
- **RecipeViewModel.kt**: ViewModel managing UI state with proper state flow management

#### UI Layer
- **RecipeListScreen.kt**: Main recipe browsing screen with search and sort controls
- **RecipeDetailScreen.kt**: Detailed recipe view with ingredients list
- **CommonComponents.kt**: Reusable UI components including loading spinners and error messages
- **RecipeImage.kt**: Accessible image loading component
- **SearchBar.kt**: Search functionality component
- **AppScaffold.kt**: Main app scaffold with navigation setup

#### Navigation
- **RecipeNavigation.kt**: Navigation configuration using Compose Navigation

## Accessibility Features

The app demonstrates strong accessibility understanding through:

1. **Content Descriptions**: All images have proper alt text
2. **Semantic Structure**: Proper heading hierarchy and content organization
3. **Touch Targets**: Adequate size for interactive elements
4. **Color Contrast**: High contrast text and backgrounds
5. **Screen Reader Support**: Proper labeling for assistive technologies
6. **Navigation**: Clear navigation patterns with back buttons
7. **Test Tags**: Proper test tags for UI testing

## Orientation Support

The app adapts to different screen orientations:

- **Portrait Mode**: Single-column list layout optimized for vertical viewing
- **Landscape Mode**: Two-column grid layout for better space utilization
- **Dynamic Layout**: Automatic switching based on device orientation using `LocalConfiguration`

## Testing

### Unit Tests
- **RecipeAlgorithmImplTest.kt**: Tests for all algorithm methods
- **RecipeViewModelTest.kt**: Tests for ViewModel state management
- **RecipeRepositoryTest.kt**: Tests for repository operations
- **Coverage**: Tests for sorting, filtering, calculations, and edge cases

### Automation Tests
- **RecipeUITest.kt**: UI testing with Compose Testing
- **Test Coverage**: Navigation, search, sorting, accessibility, orientation changes

### Test Utilities
- **MainDispatcherRule.kt**: Coroutine test dispatcher management

## Dependencies

### Core Libraries
- **Jetpack Compose**: UI framework
- **Navigation Compose**: Navigation component
- **ViewModel**: State management
- **Coroutines**: Asynchronous programming
- **Retrofit**: Network requests
- **Coil**: Image loading
- **Gson**: JSON parsing
- **Hilt**: Dependency injection

### Testing Libraries
- **JUnit**: Unit testing
- **MockK**: Mocking framework
- **Compose Testing**: Compose UI testing

## Setup and Running

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+
- Kotlin 1.9+

### Building the App
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or device

### Running Tests
```bash
# Unit tests
./gradlew :app:testDebugUnitTest

# Instrumented tests
./gradlew :app:connectedAndroidTest
```

## JSON Data Structure

The app consumes JSON data from `assets/recipes.json` with the following structure:
```json
{
  "recipes": [
    {
      "dynamicTitle": "Recipe Title",
      "dynamicDescription": "Recipe description",
      "dynamicThumbnail": "/path/to/image.jpg",
      "dynamicThumbnailAlt": "Accessibility description",
      "recipeDetails": {
        "amountLabel": "Serves",
        "amountNumber": 4,
        "prepLabel": "Prep",
        "prepTime": "15m",
        "cookingLabel": "Cooking",
        "cookingTime": "30m",
        "cookTimeAsMinutes": 30,
        "prepTimeAsMinutes": 15
      },
      "ingredients": [
        {
          "ingredient": "Ingredient description"
        }
      ]
    }
  ]
}
```
