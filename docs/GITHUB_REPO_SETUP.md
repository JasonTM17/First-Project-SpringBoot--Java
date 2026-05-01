# GitHub Portfolio Setup

Use this checklist when publishing the recruiter-facing GitHub presentation for Laptopshop.

## Repository Identity

| Field | Value |
| --- | --- |
| Owner/repo | `JasonTM17/laptopshop-spring-boot-mvc` |
| Visibility | Public |
| Default branch | `master` |
| Website | Leave blank until a real live demo exists |

Repository description:

```text
Spring Boot MVC laptop e-commerce portfolio with retail storefront, checkout, admin dashboard, tests, Docker, and CI.
```

Topics:

```text
spring-boot spring-mvc spring-security jsp mysql ecommerce docker github-actions portfolio java h2-database jstl
```

## Branch Protection

Protect `master` with a light solo-portfolio rule set:

- Block force pushes and branch deletion.
- Require the `Test and package` status check.
- Do not require pull request review.
- Do not enforce the rule for admins.

This removes the unprotected-branch warning while still keeping the repo practical for a solo portfolio project.

## README And Visuals

- README first viewport should show the title, badges, one-line pitch, demo accounts, and quick reviewer path.
- Long docs belong in collapsed details or linked markdown files.
- Use `docs/screenshots/github-hero.png` as the README hero.
- Use `docs/screenshots/github-social-preview.jpg` as the GitHub social preview image. It is exported under 1 MB for GitHub's upload limit.
- Keep the Website field blank until a deployed app is available.

## Container Package

Publish the portfolio image to GitHub Container Registry so the GitHub sidebar does not show an empty Packages section.

| Field | Value |
| --- | --- |
| Workflow | `.github/workflows/container.yml` |
| Registry | GitHub Container Registry |
| Image | `ghcr.io/jasontm17/laptopshop-spring-boot-mvc` |
| Tags | `latest`, `v*`, `sha-*` |

Reviewer pull command:

```powershell
docker pull ghcr.io/jasontm17/laptopshop-spring-boot-mvc:latest
```

## Release

Create the public source release from the latest portfolio polish commit.

| Field | Value |
| --- | --- |
| Tag | `v1.0.1` |
| Title | `Laptopshop v1.0.1 Portfolio Polish` |
| Target | Latest `master` commit after README/docs/GitHub polish |
| Assets | GitHub source zip/tar only |

Release body:

```markdown
## Laptopshop v1.0.1 Portfolio Polish

This release polishes Laptopshop as a recruiter-first Spring Boot MVC portfolio: cleaner GitHub presentation, safer README hero, concise repo metadata, protected default branch, and updated source release.

### Highlights

- Retail-style Laptopshop storefront with catalog, product detail, cart, checkout, account, and About pages.
- Customer demo flow with seeded accounts, sample cart/order history, validation, and transactional checkout.
- Admin dashboard with real metrics, charts, low-stock activity, status validation, and CSV export.
- Production-minded setup with profiles, env vars, Docker, Render blueprint, health endpoint, sitemap, robots, cache headers, and CI.
- Recruiter-friendly README with quick review path, demo accounts, screenshots, docs map, and concise technical proof.
- GitHub Container Registry package published for `ghcr.io/jasontm17/laptopshop-spring-boot-mvc`.

### Verification

- `.\mvnw.cmd package`
- GitHub Actions: `Test and package`
- Browser smoke coverage: `/`, `/products`, `/product/1`, `/cart`, `/checkout`, `/login`, `/register`, `/about`, `/admin`

### Known Limits

- No live payment gateway.
- Wishlist is browser-local.
- Promotion/spec labels are view-layer derived from existing product data.
- Website field is intentionally blank until a real public demo is deployed.
```

## Verification Checklist

- Repo URL is `https://github.com/JasonTM17/laptopshop-spring-boot-mvc`.
- Sidebar About description is short enough to scan.
- Topics render in the intended recruiter-friendly order.
- Website field is blank.
- Branch protection warning is gone.
- Latest release is `Laptopshop v1.0.1 Portfolio Polish`.
- GitHub Packages shows the `laptopshop-spring-boot-mvc` container package.
- README hero is not clipped in GitHub dark or light mode.
- README screenshot gallery loads without broken images.
- Social preview image is uploaded in repository settings from `docs/screenshots/github-social-preview.jpg`.
- Open pull request count is `0`.
- Latest `master` Actions run is green.

## Optional CLI Reference

```powershell
gh repo edit JasonTM17/Laptopshop_SpringBoot_MVC `
  --rename laptopshop-spring-boot-mvc `
  --description "Spring Boot MVC laptop e-commerce portfolio with retail storefront, checkout, admin dashboard, tests, Docker, and CI." `
  --homepage ""

gh repo edit JasonTM17/laptopshop-spring-boot-mvc `
  --add-topic spring-boot `
  --add-topic spring-mvc `
  --add-topic spring-security `
  --add-topic jsp `
  --add-topic mysql `
  --add-topic ecommerce `
  --add-topic docker `
  --add-topic github-actions `
  --add-topic portfolio `
  --add-topic java `
  --add-topic h2-database `
  --add-topic jstl

gh release create v1.0.1 --title "Laptopshop v1.0.1 Portfolio Polish" --notes-file RELEASE_NOTES.md
```
