# 🚀 BCBFixHub: An E-Store for Computer Parts and Peripherals 🛠️

A final project for Object-Oriented Programming (OOP) by **Team BCB: Instinctus. Gelidus. Callidus.**
<br>

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/JavaFX-079CD3?style=for-the-badge&logo=java&logoColor=white" alt="JavaFX">
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle">
  <img src="https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge" alt="Build Passing">
</p>

## 🎯 Project Overview

Finding reliable and affordable computer parts can be difficult. Official manufacturer channels are often expensive, while general online marketplaces lack a specific focus on components. This leads to user frustration, high costs, and unnecessary electronic waste.

**BCBFixHub** is a prototype e-commerce platform designed to solve this problem. It specializes in computer parts and peripherals, allowing users to browse, purchase, and manage orders efficiently. By centralizing access to these components, FixHub aims to streamline the repair process, reduce costs, and promote sustainable device maintenance and enhancement.

---

## ✨ Features & Functionality

The application provides a complete e-commerce experience with separate workflows for users and administrators.

### 👤 User Features

* **Secure Authentication:** Users can register for a new account and log in securely.
* **Dynamic Browsing:** Explore products by category or use the search bar to find items by name or device model.
* **Detailed Product Info:** View product specifications, pricing, stock availability, and compatibility information.
* **Shopping Cart:** A fully functional cart to add, remove, and update quantities of products.
* **Simple Checkout:** A streamlined process to confirm purchases and provide a delivery address.
* **Order History:** Users can view all their past orders and track their current fulfillment status (e.g., Pending, Shipped, Completed).

### ⚙️ Admin Dashboard

* **Secure Admin Login:** A separate login for administrators to access management functions.
* **Product Management (CRUD):** Admins can add new products, edit existing listings, or remove parts from the catalog.
* **Inventory Tracking:** Monitor stock levels for all items to maintain an accurate inventory and prevent overselling.
* **Order Fulfillment:** View all incoming user orders and update their status as they are processed.
* **Payment Monitoring:** Confirm and update the payment status for each order.

### 💻 General & Technical

* **Database Integration:** The JavaFX front-end is fully connected to a backend database to store and retrieve all application data.
* **Intuitive UI:** Built with JavaFX, using Table Views, forms, and navigation buttons for a smooth user experience.
* **Input Validation & Error Handling:** Prevents invalid data entry, ensures required fields are filled, and handles errors gracefully.

---

## 🛠️ Tech Stack

* **Core Language:** ☕ **Java**
* **UI Framework:** 🎨 **JavaFX**
* **Build Tool:** 📦 **Gradle**
* **Styling:** 💅 **CSS** (for JavaFX styling)
* **Database:** 🗄️ (e.g., MySQL, PostgreSQL - connected via JDBC)

---

## 🏃 How to Run

You can run this project directly from your IDE or by using the included Gradle wrapper.

### Prerequisites

* Java Development Kit (JDK) 11 or higher.
* Git.

### From the Command Line

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/aztigICE/BCBFixHub.git](https://github.com/aztigICE/BCBFixHub.git)
    ```

2.  **Navigate to the project directory:**
    ```sh
    cd BCBFixHub
    ```

3.  **Build the project:**
    * On macOS/Linux:
        ```sh
        ./gradlew build
        ```
    * On Windows:
        ```sh
        gradlew.bat build
        ```

4.  **Run the application:**
    * On macOS/Linux:
        ```sh
        ./gradlew run
        ```
    * On Windows:
        ```sh
        gradlew.bat run
        ```

### From an IDE (Recommended)

1.  Open your IDE (IntelliJ IDEA, Eclipse, etc.).
2.  Select "Open" or "Import".
3.  Choose the cloned `BCBFixHub` folder.
4.  Select "Import as a Gradle project".
5.  Let the IDE sync the dependencies.
6.  Find the `Main` class and run it.

---

## 👥 Contributors

This project was a collaborative effort by **Team BCB**:

| Avatar | Name | GitHub Handle |
| :---: | :---: | :---: |
| <img src="https://github.com/aztigICE.png?size=60" width="60" style="border-radius: 50%;"> | **Isiah Catan** | [@aztigICE](https://github.com/aztigICE) |
| <img src="https://github.com/Smileswawa13.png?size=60" width="60" style="border-radius: 50%;"> | **Neil Bermoy** | [@Smileswawa13](https://github.com/Smileswawa13) |
| <img src="https://github.com/TBuling.png?size=60" width="60" style="border-radius: 50%;"> | **Tristan Buling** | [@TBuling](https://github.com/TBuling) |
