# 🃏 Pokémon TCG Binder-Building Web App 🃏

![Example Postman View With Requests](docs/screenshots/Postman%20View.png)

## 📖 Overview
A web application that integrates with the [Pokémon TCG API](https://pokemontcg.io/) to allow users to **search for cards, manage personal binders, and conduct trades**.

With this app, Pokémon Trading Card Game (TCG) collectors can **organize collections, track card values, and engage in virtual trading**.
Users can create multiple binders to categorize their cards for deck-building, investment tracking, or general collection management.
The marketplace feature allows authenticated users to buy and sell cards using an **in-app currency system**.

### 🔐 Authentication & Security
Authentication is required for actions beyond searching, such as creating binders and buying cards.
- **User authentication is handled via JWT tokens.**
- Tokens are **securely generated and stored** on the client side after login.
- **Spring Security** ensures that protected endpoints require authentication.

## 🚀 Live Deployment (Koyeb)
The backend is **deployed on Koyeb**, making it accessible for API calls **without running locally**.  
Use the Postman and API documentation in the project to test the backend.

**Base API URL:**
```plaintext
https://sparkling-vivi-angelo-j-code-a0aad5ff.koyeb.app/
```

[The Frontend is Coming!](https://sparkling-vivi-angelo-j-code-a0aad5ff.koyeb.app/index.html)

## ⚡ Features
- **Search for Cards 🔍**: Available to all users without authentication.
- **User Authentication 🔒**: Required for creating binders and managing cards.
- **Binders 📂**: Users can create, name, and manage their personal card collections.
- **Database Storage 🗄️**: Deserializes JSON results and extracts essential fields when storing cards in binders.
- **Trading System 💰**: Users can sell or buy cards within their binders.
- **Pricing System 🏷️**: Card prices are fetched from the Pokémon TCG API or manually entered.
- **Admin Features 👀**: Admins can delete any user or binder, as well as access any of the other normal user functions.

## 💾 Database ERD
![Database ERD](database/PokemonTcgWebAppErd.png)

## 🎯 Key Classes & Interfaces
### 📦 Models
- `User` 👤
- `Card` 🎴
- `Binder` 📂

### 🎮 Controllers ###
- `UserController` 👥
- `AuthenticationController` 🔑
- `BinderController` 📂
- `CardController` 🎴

### 🔧 Services
- `PokemonApiService` 🌐 (Implements `PokemonServiceInterface`)

### 📊 Data Access Objects (DAOs)
- `BinderDao` 📂
- `CardDao` 🎴
- `UserDao` 👤

### 📜 Data Transfer Objects (DTOs)
- `CardDto` 🎴
- `BinderCardDto` 📂
- `CardmarketDto` 🏷️
- `TcgplayerDto` 🏷️
- `BuyCardRequestDto` 🛒

### 🛠️ Utils
- `CardMapper` 🗺️

![Example Binder View in JSON](docs/screenshots/Binder%20View.png)

## 🌐 API Integration
This app integrates with the Pokémon TCG API to retrieve real-time card data while keeping the 
database lightweight by storing only essential attributes (ID, name, image URLs, price).

## 🗺️ Future Enhancements
- **Test Suite Updates**: Ensure unit and integration tests align with recent code changes.
- **Pagination Improvements**: Enhance user experience when browsing large sets of cards.
- **Frontend Development**: A user-friendly UI for managing binders and trades.
- **Improved Market System**: Possibly allowing direct trade between users.

## 🛠️ Technologies Used
- **Java** ☕
- **Spring Boot** 🌱
- **PostgreSQL** 🐘
- **Pokémon TCG API** 🌐
- **Vue.js** 🖥️ *(Planned for frontend)*

## Author
Developed by **Jordan Opst** as a backend design final project.

