 Java Spring MVC - Xây Dựng FullStack Website với Spring Boot

This is the **starter project** for the **Java Spring MVC - Xây Dựng FullStack Website với Spring Boot** 

- Đây là dự án đầu tiên làm bằng Java Spring boot được hướng dẫn bởi anh HoidanIT.


## Database SQL for quick testing

A ready-to-import SQL script is included at:

- `database/laptopshop.sql`

This script creates schema + seed data (`roles`, sample `products`) so anyone cloning this repo can run and test quickly.

### Import manually (local MySQL)

```bash
mysql -u root -p < database/laptopshop.sql
```

### Import with Docker MySQL from this repo

```bash
docker compose up -d db-mysql
docker exec -i mysql-laptopshop mysql -uroot -p172606 < database/laptopshop.sql
```

After import:

1. Start app (`mvn spring-boot:run` or `docker compose up -d`).
2. Open `http://localhost:8080`.
3. Register a new account at `/register` to login and test user flows.
