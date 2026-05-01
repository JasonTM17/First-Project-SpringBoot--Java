# Laptopshop Release Notes

## v1.0.1 Portfolio Polish - 2026-05-01

This release focuses on the GitHub portfolio presentation: a recruiter-first README, safer GitHub hero artwork, concise repository metadata, updated social-preview asset, protected default branch, and a fresh source release target.

### Highlights

- README first viewport redesigned around quick review: pitch, badges, demo accounts, reviewer route, quick start, screenshots, and collapsed deep docs.
- GitHub hero replaced with a shorter SVG that avoids clipped text in GitHub dark/light mode.
- New imagegen-based `docs/screenshots/github-social-preview.jpg` asset prepared for repository social preview under GitHub's 1 MB upload limit.
- Repository identity standardized as `laptopshop-spring-boot-mvc`.
- GitHub Container Registry publishing added for `ghcr.io/jasontm17/laptopshop-spring-boot-mvc`.
- Release/checklist docs updated for `v1.0.1 Portfolio Polish`.

### Verification

Validated locally for this release:

```powershell
.\mvnw.cmd package
git diff --check
```

GitHub verification targets:

- Latest `master` Actions run is green.
- GitHub Packages shows the GHCR container package.
- Branch protection is enabled for `master` with the `Test and package` status check.
- Open pull request count is `0`.
- Latest release is `Laptopshop v1.0.1 Portfolio Polish`.

### Known Limits

- Website remains blank until a real deployed demo exists.
- Social preview upload is a GitHub repository setting; the upload-ready image is tracked in `docs/screenshots/github-social-preview.jpg`.

## v1.0.0 Portfolio Showcase - 2026-04-30

This release packages Laptopshop as a complete Spring Boot MVC portfolio project: polished storefront, real customer checkout flow, admin operations, demo data, automated tests, Docker/Render deployment assets, and reviewer-friendly documentation.

### Release Goals

- Present a credible Vietnamese electronics-retail shopping experience inspired by dense retail UX patterns while keeping the Laptopshop brand original.
- Demonstrate business logic beyond CRUD: cart validation, order creation, stock/sold updates, authenticated customer flows, and admin status rules.
- Make the project easy for recruiters and reviewers to run in minutes with local demo seed data and documented demo accounts.
- Show production awareness through profiles, environment variables, Docker, CI, health checks, sitemap/robots metadata, and security-minded API behavior.

### Storefront

- Retail red header with benefit strip, category drawer, search bar, account/cart actions, and responsive mobile behavior.
- Homepage with category menu, promo carousel, deal shortcuts, featured product sections, and compact product cards.
- Catalog with search, brand/need/price filtering, active filter chips, sorting pills, pagination, and mobile filter drawer.
- Product detail with gallery, price/promo blocks, warranty/support information, specs, related products, add-to-cart, and buy-now shortcut.
- Browser-local wishlist with clear feedback that the state is saved on the current device.
- About page rewritten as a professional product/company profile with service promise, release status, reviewer checklist, process, warranty, return, terms, and privacy sections.

### Customer Flow

- Demo customer account with seeded cart and order history in the `local` profile.
- Cart quantity validation prevents zero, negative, and over-stock values.
- Checkout validates receiver information and blocks empty-cart orders.
- Order placement runs transactionally, creates order details, decrements stock, increments sold count, and clears the cart.
- Order history gives reviewers a visible post-purchase flow without manually creating data first.

### Admin Flow

- Dashboard uses backend metrics instead of random chart data.
- Revenue, factory distribution, low-stock products, and recent activity are loaded through `DashboardService`.
- Product, order, and user lists support backend search/filter with pagination-safe query strings.
- Order status changes are validated through centralized status constants.
- `GET /admin/report/orders.csv` exports order data for a real admin CTA.
- Admin UI is themed consistently with the Laptopshop red retail design system.

### Backend And Security

- Public product suggestion API: `GET /api/products/suggest?name=keyword`.
- API unauthenticated/forbidden responses return JSON `401/403` instead of HTML redirects.
- Missing resources return clear not-found behavior where relevant.
- Password changes use validated DTOs and consistent redirect messages.
- `User.toString()` avoids leaking password data.
- Static assets use cache-control rules, and metadata files are available at `/sitemap.xml`, `/robots.txt`, and `/site.webmanifest`.

### Documentation

- `README.MD` gives a full project overview, screenshots, routes, demo accounts, setup, tests, and portfolio notes.
- `INSTALLATION.md` provides the fastest local reviewer run path.
- `docs/ABOUT.md` explains the product story and engineering goals.
- `docs/FEATURE_MATRIX.md` maps user-facing features to backend implementation and tests.
- `docs/REVIEWER_GUIDE.md` gives a practical 10-minute review script.
- `docs/ARCHITECTURE.md`, `docs/TESTING.md`, `docs/DEPLOYMENT.md`, `docs/SHOWCASE.md`, and `docs/PORTFOLIO_CHECKLIST.md` support deeper evaluation.

### Verification

Validated for this release:

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

Browser smoke targets:

```text
/ -> /products -> /products?factory=APPLE -> /product/1
/login -> /cart -> /checkout -> /order-history
/about -> /admin
```

Additional smoke checks:

- Search autocomplete returns product suggestions.
- Buy-now adds product and routes to checkout.
- Wishlist toggles locally without console errors.
- Admin dashboard charts load with backend data.
- Admin quick search routes to `/admin/product?q=...`.
- `/api/products/suggest`, `/sitemap.xml`, and `/robots.txt` return `200`.

### Known Limits

- Payments are represented as checkout UI choices; there is no live payment gateway integration.
- Wishlist is intentionally browser-local for this release and does not sync to user accounts.
- Product specs, old prices, and promotion labels are rendered from existing product data and view-layer rules rather than a dedicated promotion/spec schema.
- The public demo seed is for portfolio review only; production environments should use sanitized accounts and protected database credentials.

### Recommended Next Release

- Account-synced wishlist table and wishlist management page.
- Payment provider adapter behind a service interface.
- Product specs/promotion schema for richer merchandising.
- Email notifications for order status changes.
- More browser-level automated tests for mobile breakpoints.
