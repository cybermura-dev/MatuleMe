# MatuleMe

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Installation](#installation)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Performance Optimizations](#performance-optimizations)
- [Contributing](#contributing)
- [Roadmap](#roadmap)
- [License](#license)

## Overview

**MatuleMe** is a modern Android non-commerce application built with Kotlin and Jetpack Compose. It aims to provide a smooth and intuitive shopping experience for sports footwear, featuring product browsing, cart management, a secure checkout process, robust user authentication, and order tracking.

The application is designed with Clean Architecture principles to ensure high testability, maintainability, and scalability. It utilizes Supabase as a backend service for authentication, database management, and storage.

### Target Audience
- Users looking for sports footwear
- Customers who appreciate a clean and responsive mobile shopping interface
- Users needing reliable order tracking and management features

## Features

### User Management

- **Authentication**: Secure email/password login, registration, and session management.
- **Password Reset**: Self-service flow for password recovery using email verification.
- **Profile Management**: Functionality for viewing and potentially updating user details (based on implemented features).

### Shopping Experience

- **Product Details**: View comprehensive information about individual products.
- **Cart Management**: Add products to cart, update quantities, and remove items.

### Shopping Cart & Checkout

- **Cart Summary**: View items in the cart with calculated totals.
- **Checkout Process**: Step-by-step process for placing an order.
- **Address Selection**: Choose from saved delivery addresses.
- **Payment Method Selection**: Select a payment method for the order (including display of selected card details).
- **Order Placement**: Finalize and place the order.
- **Order Details**: View details of a specific order, including items and status.
- **Order Actions**: Pay for pending orders, confirm order receipt, or potentially cancel orders (if allowed by status).

### User Interface

- **Intuitive Navigation**: Clear navigation flow between screens.
- **Responsive Design**: UI adapts to different screen sizes.
- **App Theme**: Consistent styling using custom defined `AppColors` and `AppTypography`.
- **Loading & Error States**: Visual feedback for loading processes and error messages (using `EventDialog`).
- **"About App" Screen**: Provides information about the application (name, version, description, developer).

## Technologies

### Development

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: Modern toolkit for building native Android UI.
- **Gradle Kotlin DSL**: For build configuration.
- **Kotlin Coroutines & Flow**: For asynchronous operations and reactive data streams.
- **Android Jetpack Libraries**: ViewModel, Navigation Component, etc.

### Backend Integration

- **Supabase**: Backend-as-a-Service for:
    - User Authentication (GoTrue)
    - PostgreSQL Database (PostgREST)
    - Storage (for assets like product images)
- **Ktor Client**: HTTP client for network requests to Supabase.
- **Kotlinx.serialization**: For JSON serialization and deserialization.

### Dependency Injection

- **Koin**: Lightweight dependency injection framework.

## Architecture

MatuleMe is structured around the principles of Clean Architecture:

1.  **Presentation Layer**: Handles the UI and presentation logic (Composeables, ViewModels). Depends on the Domain layer.
2.  **Domain Layer**: Contains the core business logic and entities (Use Cases, Repository Interfaces, Domain Models). Independent of other layers.
3.  **Data Layer**: Manages data sources (API clients, local storage) and implements repository interfaces (Repository Implementations, Data Transfer Objects - DTOs). Depends on external libraries and provides data to the Domain layer.

Dependencies flow inwards (Presentation -> Domain -> Data).

## Installation

### Prerequisites

- Android Studio (recent version recommended)
- JDK 11 or higher
- Android SDK
- Supabase project

### Setup

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/takeshikodev/MatuleMe.git
    cd MatuleMe
    ```
2.  **Configure Supabase:**
    - Ensure your Supabase project has the necessary tables (users, products, orders, items, addresses, payments, etc.).
    - Obtain your Supabase URL and Anon Key.
3.  **Add Supabase Credentials:**
    - Create a `local.properties` file in the root project directory if it doesn't exist.
    - Add your credentials (ensure these are not committed to Git if in `.gitignore`):
      ```properties
      supabase.url=YOUR_SUPABASE_URL
      supabase.key=YOUR_SUPABASE_ANON_KEY
      ```
4.  **Build the Project:**
    - Open the project in Android Studio.
    - Sync the project with Gradle files.
    - Build the project (`Build > Make Project`).
5.  **Run the App:**
    - Connect an Android device or start an emulator.
    - Run the application from Android Studio.

## Configuration

### Supabase Credentials

Supabase URL and Key are typically configured via `local.properties` or environment variables accessed at build time for security.

### Deep Links

Ensure your application is configured to handle deep links for features like email verification and password reset as required by Supabase Auth flows.

## Project Structure

```
matuleme/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ru/takeshiko/matuleme/
│   │   │   │       ├── data/          # Data layer (remote, local, repositories impl)
│   │   │   │       ├── domain/        # Domain layer (models, usecase, repositories interfaces)
│   │   │   │       ├── presentation/  # UI layer (screens, components, viewmodels)
│   │   │   │       ├── di/            # Koin modules
│   │   │   │       ├── util/          # Utility classes
│   │   │   │       └── MatuleMeApp.kt # Application entry point
│   │   │   ├── res/         # Android resources (drawable, values, etc.)
│   │   │   └── AndroidManifest.xml
│   │   └── ... (other source sets like test, androidTest)
│   ├── build.gradle.kts      # App module build script
├── build.gradle.kts          # Project build script
├── settings.gradle.kts       # Project settings
└── README.md                 # This file
```

## API Documentation

The application interacts with your Supabase project's API endpoints for Auth, Database (PostgREST), and Storage. Refer to your Supabase project's API documentation for specifics on available endpoints and schema.

## Performance Optimizations

- Image loading and caching (likely using Coil based on common practice in Compose projects).
- Efficient UI rendering with Jetpack Compose.
- Asynchronous data operations with Coroutines and Flow.

## Contributing

Contributions are welcome! If you'd like to contribute, please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Make your changes and commit them with clear messages.
4. Push your changes to your fork.
5. Open a Pull Request to the main repository.

Please adhere to the project's coding style and guidelines.

## Roadmap

- Social authentication integration
- Push notifications
- Further UI/UX enhancements

## License

This project is licensed under the MIT License - see the LICENSE file for details.
