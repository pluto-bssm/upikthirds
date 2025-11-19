# OAuth2 Login Issues - Troubleshooting Guide

## Issue Summary

Two issues were identified from the error logs:

1. **Database Schema Mismatch** (Critical) - Prevents user creation
2. **Static Resource Not Found** (Informational) - Normal Spring Security behavior

---

## Issue 1: Database Schema Mismatch

### Error Details
```
ERROR: Data too long for column 'id' at row 1
SQL Error: 1406, SQLState: 22001
```

### Root Cause
The `user` table's `id` column has an incorrect data type that doesn't match the JPA entity definition.

**Expected (Entity)**: `UUID` → `BINARY(16)`
**Actual (Database)**: Likely `VARCHAR(n)` where n < 36

When OAuth2 creates a user with username like `google_114042602493578723257` (31 chars), the UUID generation in `@PrePersist` fails to insert because the column is too short.

### Impact
- OAuth2 login fails for all new users
- Transaction rollback with `UnexpectedRollbackException`
- Users cannot register through Google OAuth2

### Solution

#### Step 1: Apply Database Migration
Run the provided SQL migration script:

```bash
mysql -u root -p upik < fix_user_table_schema.sql
```

Or execute manually:
```sql
USE upik;
ALTER TABLE user MODIFY COLUMN id BINARY(16) NOT NULL;
```

#### Step 2: Verify Schema
```sql
DESCRIBE user;
```

Expected output:
```
Field: id
Type: binary(16)
Null: NO
Key: PRI
```

#### Step 3: Test OAuth2 Login
1. Clear browser cookies
2. Navigate to: `http://localhost:8080/oauth2/authorization/google`
3. Complete Google OAuth2 flow
4. Verify successful user creation in database:

```sql
SELECT HEX(id), username, email, role FROM user;
```

---

## Issue 2: Static Resource Not Found

### Error Details
```
NoResourceFoundException: No static resource login
Path: /login
```

### Analysis
This is **normal Spring Security OAuth2 behavior**, not an actual error.

**Why this happens**:
- Spring Security doesn't provide a default `/login` page
- The security config allows `/login/**` in `PERMIT_ALL_PATTERNS` (line 45)
- But no controller or static resource exists at `/login`

### Impact
- **No functional impact** - this is expected behavior
- OAuth2 flow works through `/oauth2/authorization/google`
- Users should not directly access `/login`

### Correct OAuth2 Flow

**Initiate Login**:
```
http://localhost:8080/oauth2/authorization/google
```

**Callback (configured in SecurityConfig)**:
- Success: Redirects to `${OAUTH2_SUCCESS_REDIRECT_URL}`
- Default: `http://localhost:3000/oauth2/callback`

### Optional: Add Custom Login Page

If you want a `/login` page, create a controller:

```java
@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/google";
    }
}
```

---

## Verification Steps

### 1. Check Database Schema
```sql
USE upik;
DESCRIBE user;
```

### 2. Check Application Logs
Look for successful user creation:
```
INFO  p.u.s.o.s.CustomOAuth2UserService - 신규 사용자 생성 - username: google_xxxxx, role: ROLE_XXX
```

### 3. Test End-to-End Flow
1. Access: `http://localhost:8080/oauth2/authorization/google`
2. Complete Google authentication
3. Verify redirect to frontend with JWT tokens
4. Check database for new user entry

### 4. Verify JWT Tokens
Check cookies/headers:
- `refreshToken` cookie should be set
- `Authorization` header should contain JWT

---

## Related Files

### Entity Definition
- `src/main/java/pluto/upik/shared/oauth2jwt/entity/User.java`
  - Line 20-22: `@Id @Column(columnDefinition = "BINARY(16)") private UUID id;`
  - Line 45-50: `@PrePersist` generates UUID

### OAuth2 Service
- `src/main/java/pluto/upik/shared/oauth2jwt/service/CustomOAuth2UserService.java`
  - Line 116-125: `createNewUser()` method
  - Uses `User.builder()` which triggers `@PrePersist`

### Security Configuration
- `src/main/java/pluto/upik/shared/config/SecurityConfig.java`
  - Line 43-49: `PERMIT_ALL_PATTERNS` including `/login/**`
  - Line 70-73: OAuth2 login configuration

---

## Prevention

### Future Schema Changes
To prevent schema mismatches:

1. **Use Flyway or Liquibase** for database migrations
2. **Enable JPA schema validation** in development:
   ```properties
   spring.jpa.hibernate.ddl-auto=validate
   ```
3. **Add schema tests** in integration tests

### Monitoring
Add database health checks:
```properties
management.endpoint.health.show-details=always
```

---

## Support

If issues persist after applying the fix:

1. Check application logs for detailed stack traces
2. Verify environment variables are correctly set
3. Ensure MariaDB driver version compatibility
4. Check that `@EnableJpaAuditing` is enabled for `@CreatedDate` fields

---

**Fix Applied**: 2025-11-12
**Status**: Ready for production deployment after database migration
