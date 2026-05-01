# Laptopshop

![Laptopshop cover](docs/screenshots/laptopshop-cover.svg)

Laptopshop is a full-stack Java Spring Boot e-commerce application for selling laptops. The UI is designed with a modern Vietnamese electronics-retail feel: red brand system, dense product cards, searchable catalog, polished cart/checkout flow, account area, and an admin dashboard for managing products, users, and orders.

Quick start for reviewers: [INSTALLATION.md](INSTALLATION.md)

Project story and scope: [docs/ABOUT.md](docs/ABOUT.md)

Release notes: [RELEASE_NOTES.md](RELEASE_NOTES.md)

Reviewer guide: [docs/REVIEWER_GUIDE.md](docs/REVIEWER_GUIDE.md)

Feature matrix: [docs/FEATURE_MATRIX.md](docs/FEATURE_MATRIX.md)

GitHub repo setup: [docs/GITHUB_REPO_SETUP.md](docs/GITHUB_REPO_SETUP.md)

Architecture notes: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

Testing guide: [docs/TESTING.md](docs/TESTING.md)

Deployment notes: [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)

Publishing checklist: [docs/PORTFOLIO_CHECKLIST.md](docs/PORTFOLIO_CHECKLIST.md)

Showcase script: [docs/SHOWCASE.md](docs/SHOWCASE.md)

## Screenshots

| Home | Catalog |
| --- | --- |
| ![Home](docs/screenshots/home.png) | ![Catalog](docs/screenshots/catalog.png) |

| Product detail | Cart |
| --- | --- |
| ![Product detail](docs/screenshots/product-detail.png) | ![Cart](docs/screenshots/cart.png) |

| Order history | Admin dashboard |
| --- | --- |
| ![Order history](docs/screenshots/order-history.png) | ![Admin dashboard](docs/screenshots/admin-dashboard.png) |

| About |
| --- |
| ![About](docs/screenshots/about.png) |

## Highlights

- Storefront with retail-style header, large search with live product suggestions, category navigation, promo hero, flash-sale area, product cards, and responsive footer.
- Product catalog with filters by brand, need, price range, search keyword, sorting, readable active filter chips, and pagination.
- Product detail page with pricing, promotion, warranty/support blocks, quantity control, add-to-cart API, and a real buy-now shortcut to checkout.
- Cart and checkout with stock validation, quantity validation, payment method, order creation, stock decrement, and sold count update.
- Account area with profile update, password change, address information, and order history.
- Admin dashboard for users, products, orders, backend search/filter on admin lists, real revenue/factory/low-stock metrics, CSV order export, product image upload, validated status flow, and consistent red Laptopshop branding.
- Professional About page with operating metrics, service process, warranty, return, terms, and privacy sections.
- Hardened backend behavior: JSON `401/403` for API failures, clear `404` pages, no password in `User.toString()`, transactional cart/order flows, cacheable static assets, sitemap/robots metadata, and `spring.jpa.open-in-view=false`.

## Release Status

Current release: **v1.0.0 Portfolio Showcase**.

This release is designed for GitHub, recruiter review, and local demo. It includes a complete storefront, customer flow, admin workflow, demo seed data, automated tests, Docker/Render assets, and full documentation.

Release details: [RELEASE_NOTES.md](RELEASE_NOTES.md)

Reviewer path: [docs/REVIEWER_GUIDE.md](docs/REVIEWER_GUIDE.md)

GitHub metadata/release setup: [docs/GITHUB_REPO_SETUP.md](docs/GITHUB_REPO_SETUP.md)

## Tech Stack

- Java 17
- Spring Boot 3.5.14
- Spring MVC, JSP, JSTL
- Spring Security
- Spring Data JPA, Hibernate
- MySQL for local/prod database
- H2 for tests
- Maven Wrapper
- Bootstrap Icons, custom CSS design system
- Docker and Docker Compose

## Demo Accounts

For the quickest review flow, running the app with the `local` profile will auto-create demo roles, accounts, and sample products on an empty database. You can also import `database/laptopshop.sql` if you want the fuller seed dataset.

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@laptopshop.dev` | `Admin@123` |
| Customer | `customer@laptopshop.dev` | `Customer@123` |

In `local` mode, the customer account also receives a sample cart and sample order history so reviewer flows are visible immediately after first run.

Admin area: `/admin`

Customer flows: `/products`, `/cart`, `/checkout`, `/order-history`, `/account`

## Configuration

The app uses Spring profiles and environment variables. Secrets are not hard-coded in `application.properties`.

Default profile:

```properties
SPRING_PROFILES_ACTIVE=local
```

Local profile file:

```text
src/main/resources/application-local.properties
```

Production profile file:

```text
src/main/resources/application-prod.properties
```

Create a local env file from the example:

```powershell
Copy-Item .env.example .env
```

Set these values for local development:

```powershell
$env:SPRING_PROFILES_ACTIVE = "local"
$env:MYSQL_HOST = "localhost"
$env:MYSQL_PORT = "3306"
$env:MYSQL_DATABASE = "laptopshop"
$env:MYSQL_USER = "root"
$env:MYSQL_PASSWORD = "your_mysql_password"
$env:APP_DEMO_SEED = "true"
$env:APP_BASE_URL = "http://localhost:8080"
```

For production, set:

```text
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/laptopshop
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
JPA_DDL_AUTO=validate
SPRING_SESSION_JDBC_INITIALIZE_SCHEMA=never
PORT=8080
APP_BASE_URL=https://your-live-demo.example
```

## Run Locally

Requirements:

- JDK 17+
- MySQL 8+
- Maven Wrapper from this repo

Option A: start with an empty `laptopshop` database and let the local profile seed the demo data automatically.

Option B: import the full SQL seed:

```powershell
mysql -u root -p < database/laptopshop.sql
```

Start the app:

```powershell
$env:SPRING_PROFILES_ACTIVE = "local"
$env:MYSQL_PASSWORD = "your_mysql_password"
.\mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8080
```

If port `8080` is occupied:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

## Run With Docker Compose

Start MySQL:

```powershell
docker compose up -d db-mysql
```

Import seed data:

```powershell
Get-Content database\laptopshop.sql | docker exec -i mysql-laptopshop mysql -uroot -plaptopshop_dev_password
```

Build and run the app:

```powershell
docker compose up --build app
```

Open:

```text
http://localhost:8080
```

The Docker Compose default password is only for local demo. Override it with `MYSQL_PASSWORD` in your shell or `.env` file.

Docker Compose also seeds demo data automatically through the `local` profile, so the customer cart and order history are ready after first startup.

## CI And Deployment

- GitHub Actions workflow: `.github/workflows/ci.yml`
- Docker image build: `docker build -t laptopshop .`
- Render blueprint: `render.yaml`
- Health check: `/actuator/health`
- Dependency monitoring: `.github/dependabot.yml`
- Repository hygiene: `CONTRIBUTING.md`, `SECURITY.md`, `.editorconfig`, `.gitattributes`

Recommended portfolio review path:

```text
/ -> /products -> /product/1 -> login as customer -> /cart -> /order-history -> login as admin -> /admin
```

## Test

Run all tests:

```powershell
.\mvnw.cmd test
```

Build the WAR and run tests:

```powershell
.\mvnw.cmd package
```

Current coverage includes:

- Register validator and strong password checks.
- Product service filtering, search, price ranges, and sorting.
- Product suggestion API for the header autocomplete.
- Cart add/update/remove, invalid quantity, stock validation.
- Place order flow, transactional stock/sold update, empty-cart handling.
- Public route E2E smoke through MockMvc.
- API unauthenticated `401 JSON` and protected page redirects.
- Authenticated customer flow: add to cart, cart page, checkout, order history.
- Admin dashboard metrics, backend list search/filter, order detail route, CSV export, and order status validation.

Detailed testing notes: [docs/TESTING.md](docs/TESTING.md)

## Useful Routes

| Area | Route |
| --- | --- |
| Home | `/` |
| Catalog | `/products` |
| Product detail | `/product/{id}` |
| Login | `/login` |
| Register | `/register` |
| Cart | `/cart` |
| Checkout | `/checkout` |
| Order history | `/order-history` |
| Account | `/account` |
| About | `/about` |
| Admin | `/admin` |
| Product suggestions API | `/api/products/suggest?name=mac` |
| Admin order CSV | `/admin/report/orders.csv` |

## Project Structure

```text
src/main/java/vn/hoidanit/laptopshop
  config/              Security and authentication config
  controller/          Client and admin controllers
  domain/              JPA entities and DTOs
  repository/          Spring Data repositories
  service/             Business logic
  service/specification Product filtering specs
  service/validator    Form validators

src/main/webapp
  WEB-INF/view         JSP pages
  resources/css        Design system and app styles
  resources/js         Shared UI helpers
  resources/client/js  Client storefront behavior

database/laptopshop.sql Seed schema and demo data
docs/screenshots         Portfolio screenshots
```

## Portfolio Notes

This project is suitable to present as a Spring MVC portfolio app because it demonstrates:

- Server-rendered MVC application structure.
- Authentication and role-based authorization.
- Real e-commerce business rules.
- Transactional order handling.
- Production-like configuration through profiles and environment variables.
- Automated tests beyond context loading.
- A cohesive UI/UX system across storefront, account, checkout, and admin.

## Maintainer Docs

- [RELEASE_NOTES.md](RELEASE_NOTES.md)
- [docs/ABOUT.md](docs/ABOUT.md)
- [docs/REVIEWER_GUIDE.md](docs/REVIEWER_GUIDE.md)
- [docs/FEATURE_MATRIX.md](docs/FEATURE_MATRIX.md)
- [docs/GITHUB_REPO_SETUP.md](docs/GITHUB_REPO_SETUP.md)
- [CONTRIBUTING.md](CONTRIBUTING.md)
- [SECURITY.md](SECURITY.md)
