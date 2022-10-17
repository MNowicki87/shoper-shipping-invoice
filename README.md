Shoper Shipping Invoice Demo
============================

![project-image](https://socialify.git.ci/MNowicki87/shoper-shipping-invoice?description=1&font=Rokkitt&logo=https%3A%2F%2Favatars.githubusercontent.com%2Fu%2F55910255&name=1&owner=1&pattern=Signal&theme=Light)

Application started as an educational project, soon to realize that I need it for as an internal tool to issue invoices.
It communicates with a Shoper (e-commerce SaaS) via REST API to fetch data about orders, ordered products, clients etc.
It allows to import bank statement of incoming transfers as csv and bind said payments to customer orders manually or
with a consecutive import of a csv file from payment-processing service provider (BlueMedia). Having information about
orders and payments together it can issue an invoice, which can be then be downloaded as individual PDFs or in bulk (
zipped).

At the beginning of this project I've made quite an unfortunate call and decided to use [vaadin](https://vaadin.com/),
which at the time, seemed like a perfect tool to easily build frontend. That being said I'm aware of several flaws in
the architecture, which I plan to successfully eliminate. Although, since the application is used only as an internal
tool and by me alone - I took 'working is better than perfect' approach.

[![](https://img.shields.io/badge/SpringBoot-2.3.1-green)](#)
[![](https://img.shields.io/badge/Java-11-lightblue)](#)
[![](https://img.shields.io/badge/Vaadin-14.8.3-blue)](#)
[![](https://img.shields.io/badge/H2-in--mem(dev)-orange)](#)
[![](https://img.shields.io/badge/CSS-3-navy)](#)
[![](https://img.shields.io/badge/MySQL-5.7(prod)-lightblue)](#)

🚀 Demo
-------

Coming soon…

Project Screenshots:
--------------------

![project-screenshot](asdf)

🧐 Features
-----------

Here're some of the project's features:
* Display basic sales statistics
* Import payment details from bank statements
* Import payment details from payment processing platform (BlueMedia)
* Issue invoices for delivery services based on processed payments and orders data
* Send issued invoices by e-mail
* Communicate with Shoper SaaS via REST API
* Listen for webhooks about new orders from Shoper Saas

🛠️ Installation Steps:
-----------------------

Coming soon…

💻 Built with
-------------

Technologies used in the project:
* Java 11
* Spring Boot 2.3.1
* Vaadin 14.8.3
* Liquibase
* Docker
