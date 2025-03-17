# Pokemon TCG Web App

## Overview
This is a web application that integrates with the Pokémon TCG API to allow users to search for cards, manage personal binders, and conduct trades. Authentication is required for actions beyond searching, such as creating binders, adding cards, and trading.

## Features
- **Search for Cards**: Available to all users without authentication.
- **User Authentication**: Required for creating binders and managing cards.
- **Binders**: Users can create, name, and manage their personal card collections.
- **Trading System**: Users can sell or swap cards between binders.
- **Pricing System**: Card prices are fetched from the Pokémon TCG API, defaulting to `0.00` if unavailable.
- **Admin Features**: Admins can delete any user or binder.

## Database Schema
### Tables:
#### Users
- `id` (Primary Key)
- `username`
- `hashedPassword`
- `role`
- `money` (Starting at `$100.00`)

#### Binders
- `binderId` (Primary Key)
- `name`
- `userId` (Foreign Key referencing Users)

#### Trades
- `tradeId` (Primary Key)
- `userId` (Foreign Key referencing Users)
- `cardId`
- `price`

## Key Classes & Interfaces
### Models
- `User`
- `Card`
- `Binder`

### Services
- `PokemonApiService` (Implements `PokemonServiceInterface`)

### Data Access Objects (DAOs)
- `BinderDao`
- `CardDao`
- `UserDao`

## API Integration
The Pokémon TCG API is used to fetch real-time card details. Only essential information (image, Pokedex number, name, price) is stored in binders to minimize database size.

## Future Enhancements
- **Pagination**: To improve user experience when browsing large sets of cards.
- **Frontend Development**: A user-friendly UI for managing binders and trades.
- **Improved Trade System**: Possibly allowing auctions or direct swaps.

## Setup Instructions
1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-repo/pokemon-tcg-webapp.git
   ```
2. **Configure the PostgreSQL database** `m2_final_project`.
3. **Run the backend server.**
4. **Access the API and start testing the features.**

## Technologies Used
- **Java**
- **Spring Boot**
- **PostgreSQL**
- **Pokémon TCG API**
- **Vue.js** *(Planned for frontend)*

## Author
Developed by **Jordan Opst** as a backend design final project.

