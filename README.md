# ClientManagementSystem
ClientManagementSystem is a Maven Multi Module project written in Java 12, that enables to manage data about clients shopping.
It allows user to:
* Find user which spent most money on shopping
* Find user which spent most money on shopping in each product category
* Show juxtaposition with most popular product category in each group age
* Show juxtaposition with average product price in each product category
* Find most expensive product in each category
* Find least expensive product in each category
* For each product category, find customer that have bought the most products from this category
* Check if each client is able to pay for their shopping. The result is presented in juxtaposition with client and debt they have to pay.

## Installation

* From _ClientManagementSystem_ module: 
```bash
    mvn clean install
``` 
* From _main_ module
```bash
    mvn clean compile assembly::single
```

## Usage

* From _main/target_ 
```bash
    java --enable-preview -cp main-1.0-SNAPSHOT-jar-with-dependencies.jar stefanowicz.kacper.main.App
```

Please make sure that _files_ folder is located in the same place as _main-1.0-SNAPSHOT-jar-with-dependencies.jar_. 