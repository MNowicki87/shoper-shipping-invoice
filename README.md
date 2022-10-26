Shoper Shipping Invoice Demo
============================

![shoper-shipping-invoice](https://socialify.git.ci/MNowicki87/shoper-shipping-invoice/image?description=1&font=Rokkitt&language=1&name=1&owner=1&pattern=Signal&theme=Light)

Application started as an educational project, soon to realize that I need it as an internal tool to issue invoices.
It communicates with a [Shoper](https://shoper.pl) (e-commerce SaaS) via REST API to fetch data about orders, ordered products, clients etc.
It allows importing bank statement of incoming transfers as csv and bind these payments to customer orders manually or
with a consecutive import of a csv file from payment-processing service provider ([BlueMedia](https://bm.pl)). Having information about orders and payments together it can issue an invoice, which then can be downloaded as individual PDF or in bulk (zipped).

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
![shoper-shipping-invoice-29 13](https://user-images.githubusercontent.com/55910255/197997297-eb66f0dc-3808-46f7-ae76-44b30eb851c0.png)
![shoper-shipping-invoice-29 24](https://user-images.githubusercontent.com/55910255/197997353-fd012be5-d861-4dc7-ae32-26ca9e902da5.png)
![shoper-shipping-invoice-31 43](https://user-images.githubusercontent.com/55910255/197997414-1ab02fd1-6141-449a-978f-a313cf4ae32d.png)
![shoper-shipping-invoice-32 22](https://user-images.githubusercontent.com/55910255/197997442-ec1ef483-e73e-432e-8ea3-9265dc98eb60.png)
![shoper-shipping-invoice-32 46](https://user-images.githubusercontent.com/55910255/197997457-8e80b4fa-3be2-42bf-b14a-43cb4d43781a.png)
![shoper-shipping-invoice-32 55](https://user-images.githubusercontent.com/55910255/197997464-e5910457-116d-4017-8725-5fdf8aa69fdc.png)
![shoper-shipping-invoice-33 10](https://user-images.githubusercontent.com/55910255/197997478-930fe5fc-e543-41da-a9cd-56d240ee8f90.png)



🧐 Features
-----------

Here's some of the project's features:
* Display basic sales statistics
* Import payment details from bank statements
* Import payment details from payment processing platform (BlueMedia)
* Issue invoices for delivery services based on processed payments and orders data
* Send issued invoices by e-mail
* Communicate with [Shoper SaaS via REST API](https://developers.shoper.pl/developers/api/getting-started)
* Listen for [webhooks about new orders from Shoper Saas](https://developers.shoper.pl/developers/webhooks/introduction)

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
