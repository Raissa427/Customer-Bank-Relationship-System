
# Customer-Bank Relationship System

## Description

This project is a simple Java application that models a Customer–Bank Relationship using Object-Oriented Programming (OOP) principles. It demonstrates how customers interact with bank accounts and how different types of accounts behave.

The system is designed to reflect real-world banking structure using classes, objects, and relationships.

---

## Features

* Create customers with associated accounts
* Support different account types:

  * Savings Account
  * Current Account
* Perform basic operations:

  * Deposit money
  * Withdraw money
  * Check balance

---

## System Design

### Classes Used

* **Account (Abstract Class)**
  Represents a general bank account. It defines common properties and methods such as balance, deposit, and withdraw.

* **SavingsAccount (Subclass)**
  Inherits from Account and allows withdrawals only if sufficient balance is available.

* **CurrentAccount (Subclass)**
  Inherits from Account and allows overdraft (withdraw even if balance is low).

* **Customer**
  Represents a bank customer. Each customer is associated with one account.

* **Bank (Main Class)**
  Contains the main method used to test the system and demonstrate functionality.

---

## OOP Concepts Demonstrated

* **Encapsulation**
  Customer data is protected using private variables and accessed through methods.

* **Abstraction**
  The Account class is abstract and defines common behavior for all account types.

* **Inheritance**
  SavingsAccount and CurrentAccount inherit from the Account class.

* **Polymorphism**
  The deposit and withdraw methods behave differently depending on the account type.

---

## How to Run the Project

1. Open terminal in the project folder
2. Compile the Java files:

   ```
   javac *.java
   ```
3. Run the program:

   ```
   java Bank
   ```

---

## Example Output

The program performs operations such as deposit and withdrawal, then displays customer information and account balance.

---

## Author

Raissa Numubyeyi Irumva
