# Contributing Guide

Tai lieu nay thong nhat cach lam viec Git/GitHub cho team SmartSpend.

## Branch strategy

- `main` la nhanh on dinh, chi merge code da review va chay test.
- Moi tinh nang tao mot branch rieng tu `main`.
- Khong commit truc tiep len `main`.

Quy uoc dat ten branch:

```text
feature/auth-security
feature/wallet-crud
feature/category-crud
feature/transaction
fix/wallet-not-found
docs/update-api-design
refactor/split-wallet-dto
```

## Daily workflow

Truoc khi bat dau lam moi ngay:

```bash
git checkout main
git pull origin main
git checkout <your-branch>
git merge main
```

Neu chua co branch:

```bash
git checkout main
git pull origin main
git checkout -b feature/your-feature-name
```

Sau khi lam xong:

```bash
git status
git add .
git commit -m "feat: add wallet create api"
git push -u origin feature/your-feature-name
```

## Commit message

Dung format ngan gon:

```text
feat: add wallet create api
fix: handle wallet not found
docs: update team rules
refactor: split wallet dto
test: add wallet service tests
chore: update config example
```

Loai commit thuong dung:

- `feat`: them tinh nang moi.
- `fix`: sua loi.
- `docs`: sua tai lieu.
- `refactor`: doi cau truc code, khong doi hanh vi.
- `test`: them hoac sua test.
- `chore`: viec cau hinh, build, maintenance.

## Pull request rules

- Moi pull request chi nen tap trung vao mot feature hoac mot bug.
- Nguoi con lai review truoc khi merge vao `main`.
- Khong merge neu project khong build/test duoc.
- Khong sua file shared neu chua bao voi nguoi con lai.

File shared can can than:

```text
backend/pom.xml
backend/src/main/resources/application.yml
backend/src/main/resources/application-dev.yml.example
backend/src/main/resources/db/migration/V1__init_schema.sql
docker-compose.yml
.env.example
README.md
docs/
```

## Migration rules

- Khong sua migration cu neu da duoc merge vao `main`.
- Migration moi phai tao file moi theo thu tu:

```text
V2__add_auth_indexes.sql
V3__add_wallet_constraints.sql
V4__create_chat_tables.sql
```

- Truoc khi tao migration moi, pull code moi nhat tu `main` de tranh trung version.

## Before merge checklist

Truoc khi tao hoac merge pull request:

```bash
git status
cd backend
mvn test
```

Checklist:

- [ ] Code dung branch rieng.
- [ ] Khong commit file secret nhu `.env` hoac `application-dev.yml`.
- [ ] API khong tra truc tiep JPA Entity.
- [ ] Migration moi dung version va ten ro rang.
- [ ] Test chay duoc local.
- [ ] Nguoi con lai da review.
