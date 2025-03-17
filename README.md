# Pokémon TCG Binder-Building Web App

![Example Binder View in JSON](docs/screenshots/Binder%20View.png)

## Overview
This is a web application that integrates with the Pokémon TCG API to allow users to search for cards, manage personal binders, and conduct trades. Authentication is required for actions beyond searching, such as creating binders, adding cards, and trading.

## Features
- **Search for Cards**: Available to all users without authentication.
- **User Authentication**: Required for creating binders and managing cards.
- **Binders**: Users can create, name, and manage their personal card collections.
- **Database Storage**: Deserializes JSON results and extracts essential fields when storing cards in binders.
- **Trading System**: Users can sell or buy cards using their binders.
- **Pricing System**: Card prices are fetched from the Pokémon TCG API or manually entered.
- **Admin Features**: Admins can delete any user or binder, as well as access any of the other normal user functions.

## Database Schema
### Tables:
#### users
- `id` (Primary Key)
- `username`
- `password_hash`
- `role`
- `money` (Defaults to `$100.00`)

#### binder
- `binderId` (Primary Key)
- `name`
- `userId` (Foreign Key referencing Users, on delete cascade)

#### binder_cards
- `id` (Unique ID for each entry)
- `binder_id` INTEGER (Foreign Key referencing binder, on delete cascade)
- `card_id`
- `name`
- `small_image_url`
- `large_image_url`
- `price`
- `quantity`

## Key Classes & Interfaces
### Models
- `User`
- `Card`
- `Binder`

### Controllers ###
- `UserController`
- `AuthenticationController`
- `BinderController`
- `CardController`

### Services
- `PokemonApiService` (Implements `PokemonServiceInterface`)

### Data Access Objects (DAOs)
- `BinderDao`
- `CardDao`
- `UserDao`

### Data Transfer Objects (DTOs)
- `CardDto`
- `BinderCardDto`
- `CardmarketDto`
- `TcgplayerDto`
- `BuyCardRequestDto`

### Other
- `CardMapper`

## API Integration
The Pokémon TCG API is used to fetch real-time card details. Only essential information (image, Pokédex number, name, price) is stored in binders to minimize database size.

## Future Enhancements
- **Repaired Testing**: To bring test classes up-to-speed with recent modifications.
- **Pagination Improvements**: To improve user experience when browsing large sets of cards.
- **Frontend Development**: A user-friendly UI for managing binders and trades.
- **Improved Trade System**: Possibly allowing auctions or direct swaps.

## Setup Instructions
1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-repo/pokemon-tcg-webapp.git
   ```
2. **Configure the PostgreSQL database:**
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

