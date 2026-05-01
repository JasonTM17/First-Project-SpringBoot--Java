# Portfolio Publishing Checklist

Use this when preparing Laptopshop for GitHub, LinkedIn, or a live demo.

## Repository

- Add screenshots from `docs/screenshots` to the README preview.
- Keep `.env` out of Git and publish only `.env.example`.
- Set repository topics from `docs/GITHUB_REPO_SETUP.md`.
- Confirm GitHub Actions passes on the default branch.
- Confirm Dependabot is enabled for Maven and GitHub Actions.
- Pin the demo accounts in the README and installation guide.
- Link `RELEASE_NOTES.md`, `docs/ABOUT.md`, `docs/REVIEWER_GUIDE.md`, `docs/FEATURE_MATRIX.md`, and `docs/GITHUB_REPO_SETUP.md` from the README.
- Fill GitHub About with the short description from `docs/GITHUB_REPO_SETUP.md`.
- Publish the `v1.0.1` source release using `docs/GITHUB_REPO_SETUP.md`.
- Confirm the GHCR package is published from `.github/workflows/container.yml`.
- Protect `master` with the lightweight status-check rule from `docs/GITHUB_REPO_SETUP.md`.
- Link a live demo only after production env vars are configured.

## Local Review

```powershell
.\mvnw.cmd package
docker compose config
docker build -t laptopshop .
```

Open:

```text
http://localhost:8081
```

Smoke path:

```text
/ -> /products?factory=APPLE -> /product/1 -> /cart -> /order-history -> /admin
```

Health and metadata:

```powershell
curl.exe http://localhost:8081/actuator/health
curl.exe http://localhost:8081/actuator/info
curl.exe http://localhost:8081/robots.txt
```

## Demo Accounts

- Customer: `customer@laptopshop.dev` / `Customer@123`
- Admin: `admin@laptopshop.dev` / `Admin@123`

## Render

- Create a MySQL-compatible database.
- Deploy with `render.yaml` or a Docker web service.
- Set the protected variables from `docs/DEPLOYMENT.md`.
- Keep `SPRING_PROFILES_ACTIVE=prod`.
- Use `/actuator/health` as the health check.

## Final Presentation

- Use `docs/screenshots/github-social-preview.jpg` as the social preview image.
- Lead with the customer workflow and admin dashboard.
- Open `/about` to show the product story, service policies, release status, and reviewer checklist.
- Reference `RELEASE_NOTES.md` to make the release scope and known limits explicit.
- Reference `docs/GITHUB_REPO_SETUP.md` if the reviewer asks how the GitHub About/topics/release were prepared.
- Mention that the local profile auto-seeds reviewer-ready data.
- Mention that business logic is covered by automated tests, not only context-load tests.
- Keep the final demo story under five minutes: problem, customer flow, admin flow, tests/deploy readiness.
