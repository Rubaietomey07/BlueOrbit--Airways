<div align="center">

# ✈ BlueOrbit Airways
### Airline Management System

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Maven](https://img.shields.io/badge/Maven-3.9-red?style=for-the-badge&logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

A full-stack desktop Airline Management System built with Java 21, JavaFX 21,
SceneBuilder, and MySQL 8.0 — supporting Passenger, Staff, and Admin roles.

</div>

---

## 📸 Screenshots

| Login Screen | User Dashboard |
|---|---|
| ![Login](screenshots/login.png) | ![Dashboard](screenshots/dashboard.png) |

| Search Flight | Ticket Generated |
|---|---|
| ![Search](screenshots/search.png) | ![Ticket](screenshots/ticket.png) |

---

## ✨ Features

### 👤 Passenger (User)
- 🔍 Search flights by source, destination and date (next 20 days)
- 💺 Select seat class — Economy, Business, First Class
- 💰 View price breakdown with 15% tax
- 🏷️ Apply coupon codes or Employee ID for discounts
- 💳 Pay via Bank Account, Card, or Bkash
- 🎫 View generated ticket / boarding pass
- 📋 View booking history and cancel/delete bookings
- ✅ Verify any ticket by Booking ID / PNR

### 👨‍💼 Staff
- 👥 Add, update, search passengers
- 🔄 Change or upgrade passenger seat
- 🎫 Book tickets for passengers with **70% staff discount**

### 🔑 Admin
- ✈️ Full flight management (add, update, delete flights + auto seat generation)
- 👥 Employee management (add Staff/Admin accounts)
- 🏷️ Coupon management (create, update, delete coupons)
- 📊 Reports — Booking, Revenue, and Passenger reports with live stats
- 👤 User management — assign roles, activate/deactivate accounts

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 (LTS) |
| UI Framework | JavaFX 21 + FXML + SceneBuilder |
| Database | MySQL 8.0 |
| DB Connector | mysql-connector-j 8.3.0 |
| Build Tool | Apache Maven |
| Architecture | MVC (Model-View-Controller) |
| IDE | IntelliJ IDEA |

---

## 🗃️ Database Schema
**8 Tables:** `users`, `employees`, `flights`, `seats`,
`bookings`, `payments`, `payment_accounts`, `coupons`

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- JavaFX SDK 21
- MySQL 8.0+
- Maven 3.9+
- IntelliJ IDEA

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/BlueOrbit-Airways.git
cd BlueOrbit-Airways
```

**2. Set up the database**

Open MySQL Workbench and run the SQL schema:
```bash
mysql -u root -p < database/airline_db_schema.sql
```

**3. Configure database connection**

Create `src/main/java/com/example/airlinemanagementsystem/utils/DatabaseConnection.java`:
```java
public class DatabaseConnection {
    private static final String URL      = "jdbc:mysql://localhost:3306/airline_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "your_password_here"; // ← change this
    ...
}
```

**4. Run the application**

```bash
mvn javafx:run
```

Or run `MainApp.java` directly from IntelliJ.

---

## 🔐 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Staff | `staff1` | `staff123` |
| Passenger | `johndoe` | `user123` |

> **Note:** Staff and Admin accounts are created via the Admin → Employee Management module
> or directly in the database. Passengers self-register through the app.

---

## 📁 Project Structure
---

## 🤝 Contributing

Pull requests are welcome! For major changes please open an issue first.

---

## 📄 License

This project is licensed under the MIT License.

---

<div align="center">
Made with ❤️ using Java & JavaFX — BlueOrbit Airways
</div>
