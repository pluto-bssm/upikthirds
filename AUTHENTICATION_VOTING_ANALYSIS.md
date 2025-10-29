# Authentication and Voting System Analysis
## Spring Boot GraphQL Project Analysis Report

---

## 1. AUTHENTICATION SYSTEM

### 1.1 Overview
The project implements a **JWT-based OAuth2 authentication system** with refresh token rotation capability. Authentication is stateless and filter-based, integrated with Spring Security.

**Key Components:**
- JWT Token Management
- OAuth2 Integration  
- Refresh Token Rotation
- Security Context Management
- Role-based Authorization

### 1.2 JWT Token Infrastructure

#### File: `JWTUtil.java`
**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTUtil.java`

**Token Types:**
- **Access Token**: Short-lived (15 minutes / 900000ms)
  - Category: `"access"`
  - Contains: username, role, category
  - Expires: Configurable via `jwt.access-token-expiration-time`

- **Refresh Token**: Long-lived 
  - Category: `"refresh"`
  - Contains: username, role, category
  - Expires: Configurable via `jwt.refresh-token-expiration-time`

**Key Methods:**
```java
String createAccessToken(String username, String role)          // Creates access token
String createRefreshToken(String username, String role)         // Creates refresh token
String getUsername(String token)                                // Extracts username (even from expired)
String getRole(String token)                                    // Extracts role (even from expired)
String getCategory(String token)                                // Extracts category
Boolean isExpired(String token)                                 // Checks expiration
boolean validateToken(String token)                             // Full validation
```

**Security Properties:**
- Algorithm: HS256 (HMAC-SHA256)
- Signature Key: BASE64 decoded from `jwt.secret`
- Exception Handling: Comprehensive for ExpiredJwtException, MalformedJwtException, SignatureException

---

### 1.3 JWT Filter Implementation

#### File: `JWTFilter.java`
**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTFilter.java`

**Filter Chain Position:** Before `UsernamePasswordAuthenticationFilter`

**Skip Paths:**
```
Exact paths: /auth/reissue, /favicon.ico, /error, /api/my
Path prefixes: /oauth2/, /login/, /static/, /css/, /js/, /images/
```

**Token Extraction Priority:**
1. Bearer Token from Authorization header (prefix: "Bearer ")
2. Fallback to Authorization cookie

**Token Processing Flow:**
```
1. Extract Access Token from header/cookie
2. Check if token is expired
   → If expired: Try refresh token rotation
   → If valid: Validate and set authentication
3. Set SecurityContext with authentication
4. Continue filter chain
```

**Key Mechanisms:**

1. **Access Token Refresh**
   - When access token expires, JWTFilter automatically attempts refresh
   - Uses refresh token from cookies
   - Validates refresh token: category="refresh", not expired, exists in DB
   - Creates new access token with 15-minute expiration
   - Sets new token in response cookie

2. **SecurityContext Management**
   ```java
   // User extraction from token
   String username = jwtUtil.getUsername(token);
   String role = jwtUtil.getRole(token);
   String name = userRepository.findNameByUsername(username).orElse(username);
   
   // Create authentication principal
   UserDTO userDTO = UserDTO.builder()
       .username(username)
       .name(name)
       .role(role)
       .build();
   
   CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO, userRepository);
   Authentication authToken = new UsernamePasswordAuthenticationToken(
       customOAuth2User, null, customOAuth2User.getAuthorities());
   
   SecurityContextHolder.getContext().setAuthentication(authToken);
   ```

---

### 1.4 Security Context Utility

#### File: `SecurityUtil.java`
**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/util/SecurityUtil.java`

**Primary Purpose:** Centralized access to current authenticated user information

**Core Methods:**

```java
CustomOAuth2User getCurrentUser()
// Returns: Current authenticated user as CustomOAuth2User
// Throws: BusinessException if not authenticated

User getCurrentUserEntity()
// Returns: Full User entity from database
// Throws: BusinessException if not authenticated or user not found

UUID getCurrentUserId()
// Returns: Current user's UUID
// Throws: BusinessException if not authenticated

boolean isAuthenticated()
// Returns: true if user is authenticated, false otherwise
```

**Authentication Validation Logic:**
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
return authentication != null && authentication.isAuthenticated() && 
       !"anonymousUser".equals(authentication.getPrincipal());
```

---

### 1.5 Security Configuration

#### File: `SecurityConfig.java`
**Location:** `/src/main/java/pluto/upik/shared/config/SecurityConfig.java`

**Permitted Endpoints (No Authentication Required):**
```
/v3/api-docs/**, /swagger-ui/**, /swagger-ui.html
/oauth2/**, /login/**
/auth/**
/graphql, /graphiql (All GraphQL endpoints accessible without auth)
/static/**, /css/**, /js/**, /images/**
/favicon.ico, /error
```

**Security Chain Configuration:**
1. **CORS Configuration**
   - Allows all origins
   - Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
   - Allows credentials
   - Exposed headers: Authorization, Set-Cookie

2. **Stateless Session Policy**
   - No server-side session storage
   - Token-based authentication only

3. **Filter Chain**
   - JWT Filter added before UsernamePasswordAuthenticationFilter
   - OAuth2 Login with CustomOAuth2UserService
   - Custom success handler: CustomSuccessHandler

4. **Authorization**
   - Permitted patterns: `/graphql`, `/graphiql`, `/oauth2/**`, `/auth/**`
   - All other requests: Require authentication

5. **Exception Handling**
   - 403 Forbidden: Access denied
   - 401 Unauthorized: Login required

---

## 2. VOTING DOMAIN STRUCTURE

### 2.1 Vote Entity

#### File: `Vote.java`
**Location:** `/src/main/java/pluto/upik/domain/vote/data/model/Vote.java`

**Entity Structure:**
```java
@Entity
@Table(name = "vote")
public class Vote {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;                           // Vote ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User user;                         // Vote creator (User relation)
    
    @Column(columnDefinition = "TEXT")
    private String question;                   // Vote question
    
    private String category;                   // Vote category
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('OPEN', 'CLOSED')")
    private Status status;                     // Vote status (OPEN/CLOSED)
    
    @Column
    private LocalDate finishedAt;              // Vote end date
    
    @Column
    private Integer participantThreshold;      // Participant count threshold
    
    @Transient
    private boolean guideGenerated;            // Guide generation flag
    
    public enum Status {
        OPEN, CLOSED
    }
}
```

**User Relationship:**
- **Type:** ManyToOne (Multiple votes from one user)
- **Fetch Strategy:** LAZY
- **Foreign Key:** `creator_id` in vote table
- **Usage:** Access vote creator via `vote.getUser()`

---

### 2.2 Vote Repository

#### File: `VoteRepository.java`
**Location:** `/src/main/java/pluto/upik/domain/vote/repository/VoteRepository.java`

**Key Query Methods:**

```java
// Get votes by user
List<Vote> findByUserId(UUID userId)
    → Returns all votes created by user

@Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.finishedAt > :currentDate")
List<Vote> findActiveVotesByUserId(UUID userId, LocalDate currentDate)
    → Returns only active/ongoing votes for user

// Get votes by status
List<Vote> findByStatus(Vote.Status status)
    → Returns votes with specific status (OPEN/CLOSED)

// Pagination queries
@Query("SELECT v FROM Vote v ORDER BY v.finishedAt DESC")
Page<Vote> findAllByOrderByCreatedAtDesc(Pageable pageable)
    → Paginated list of all votes

@Query(value = "SELECT v.* FROM vote v 
       LEFT JOIN (SELECT vote_id, COUNT(*) as response_count FROM vote_response 
       GROUP BY vote_id) vr ON v.id = vr.vote_id 
       ORDER BY vr.response_count DESC NULLS LAST", nativeQuery = true)
Page<Vote> findAllOrderByParticipationRate(Pageable pageable)
    → Votes sorted by participation rate
```

**Critical for Feature:** `findByUserId(UUID userId)` is the method to retrieve user's created votes.

---

### 2.3 VoteResponse Entity

#### File: `VoteResponse.java`
**Location:** `/src/main/java/pluto/upik/domain/voteResponse/data/model/VoteResponse.java`

**Entity Structure:**
```java
@Entity
@Table(name = "vote_response")
public class VoteResponse {
    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue
    private UUID id;                          // Response ID
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                        // User who voted
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;                        // Vote being answered
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option selectedOption;            // Selected option
    
    @Column
    private LocalDate createdAt;              // Response creation date
}
```

**Relationships:**
- User: Who voted (ManyToOne)
- Vote: Which vote they participated in (ManyToOne)
- Option: Which option they selected (ManyToOne)

---

### 2.4 VoteResponse Repository

#### File: `VoteResponseRepository.java`
**Location:** `/src/main/java/pluto/upik/domain/voteResponse/repository/VoteResponseRepository.java`

**Key Query Methods:**

```java
List<VoteResponse> findByVoteId(UUID voteId)
    → Get all responses for a vote

@Query("SELECT vr FROM VoteResponse vr 
       WHERE vr.user.id = :userId AND vr.vote.id = :voteId")
Optional<VoteResponse> findByUserIdAndVoteId(UUID userId, UUID voteId)
    → Check if user voted on specific vote

@Query("SELECT COUNT(vr) FROM VoteResponse vr 
       WHERE vr.vote.id = :voteId")
Long countByVoteId(UUID voteId)
    → Total responses for a vote

@Query("SELECT COUNT(vr) FROM VoteResponse vr 
       WHERE vr.selectedOption.id = :optionId")
Long countByOptionId(UUID optionId)
    → Responses for specific option
```

---

### 2.5 User Entity

#### File: `User.java`
**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/entity/User.java`

**Structure:**
```java
@Entity
public class User {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    
    private String role;                      // User role
    private String username;                  // Unique username
    private String name;                      // Display name
    private String email;                     // Email address
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;          // Account creation date
    
    private double dollar;                    // Currency balance 1
    private double won;                       // Currency balance 2
    private long streakCount;                 // Streak counter
    
    @CreatedDate
    private LocalDateTime recentDate;         // Last activity date
}
```

---

## 3. GRAPHQL SCHEMA AND RESOLVERS

### 3.1 Vote GraphQL Schema

#### File: `schema-voting.graphqls`
**Location:** `/src/main/resources/graphql/schema-voting.graphqls`

**Schema Definition:**
```graphql
type VoteResponseMutation {
    createVoteResponse(input: CreateVoteResponseInput!): VoteResponsePayload!
}

type VoteResponseQuery {
    getVoteResponseCount(voteId: ID!): Int!
    getOptionResponseCount(optionId: ID!): Int!
    hasUserVoted(voteId: ID!): Boolean!
}

input CreateVoteResponseInput {
    voteId: ID!
    optionId: ID!
}

type VoteResponsePayload {
    id: ID!
    userId: ID!
    voteId: ID!
    optionId: ID!
    optionContent: String!
    voteTitle: String!
    createdAt: String!
}
```

---

### 3.2 Vote Resolvers

#### File: `VoteRootQueryResolver.java`
**Location:** `/src/main/java/pluto/upik/domain/vote/resolver/VoteRootQueryResolver.java`

**Root Query Entry Point:**
```java
@Controller
public class VoteRootQueryResolver {
    @SchemaMapping(typeName = "Query", field = "vote")
    public VoteQuery vote() {
        return new VoteQuery();
    }
}
```

---

#### File: `VoteMutationResolver.java`
**Location:** `/src/main/java/pluto/upik/domain/vote/resolver/VoteMutationResolver.java`

**Mutations:**
```java
@RequireAuth
@SchemaMapping(typeName = "VoteMutation", field = "createVote")
public VotePayload createVote(@Argument CreateVoteInput input) {
    UUID userId = securityUtil.getCurrentUserId();  // ← Get current user
    return voteApplication.createVote(input, userId);
}
```

**Key Pattern:** Uses `@RequireAuth` annotation and `securityUtil.getCurrentUserId()` to access authenticated user.

---

#### File: `VoteResponseQueryResolver.java`
**Location:** `/src/main/java/pluto/upik/domain/voteResponse/resolver/VoteResponseQueryResolver.java`

**Query Methods:**
```java
@SchemaMapping(typeName = "VoteResponseQuery", field = "getVoteResponseCount")
public Integer getVoteResponseCount(@Argument UUID voteId) {
    return voteResponseApplication.getVoteResponseCount(voteId).intValue();
}

@RequireAuth
@SchemaMapping(typeName = "VoteResponseQuery", field = "hasUserVoted")
public Boolean hasUserVoted(@Argument UUID voteId) {
    UUID userId = securityUtil.getCurrentUserId();  // ← Get current user ID
    return voteResponseApplication.hasUserVoted(userId, voteId);
}
```

---

### 3.3 IAM (Identity and Access Management) Resolver

#### File: `IamRootQueryResolver.java` & `IamQueryResolver.java`
**Locations:** 
- `/src/main/java/pluto/upik/domain/IAM/IamRootQueryResolver.java`
- `/src/main/java/pluto/upik/domain/IAM/IamQueryResolver.java`

**Root Query Entry:**
```java
@Controller
public class IamRootQueryResolver {
    @QueryMapping
    public IamQuery iam() {
        return new IamQuery();
    }
}
```

**Current User Query:**
```java
@SchemaMapping(typeName = "IamQuery")
public IamDTO getCurrentUser(IamQuery iamQuery) {
    UUID userId = securityUtil.getCurrentUserId();  // ← Get current user
    Optional<User> userOptional = userRepository.findById(userId);
    
    if (userOptional.isPresent()) {
        User user = userOptional.get();
        return IamDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }
    return null;
}
```

---

#### File: `iam.graphqls`
**Location:** `/src/main/resources/graphql/iam.graphqls`

**IAM Schema:**
```graphql
type IamDTO {
    id: ID
    username: String
    name: String
    email: String
    role: String
}

type IamQuery {
    getCurrentUser: IamDTO
}

extend type Query {
    iam: IamQuery
}
```

---

### 3.4 Guide Example Pattern (Reference)

#### File: `schema-guide.graphqls`
**Location:** `/src/main/resources/graphql/schema-guide.graphqls`

**User-Related Query in Mutation:**
```graphql
type GuideMutation {
    incrementGuideLike(id: ID!): Boolean!
    incrementGuideRevote(id: ID!, reason: String!): Boolean!
    getUserCreatedGuides: [Guide]!
}
```

**Resolver Implementation Pattern:**
```java
@RequireAuth
@SchemaMapping(typeName = "GuideMutation", field = "incrementGuideLike")
public boolean incrementGuideLike(GuideMutation parent, @Argument String id) {
    UUID userId = securityUtil.getCurrentUserId();  // ← Current user
    UUID guideId = UUID.fromString(id);
    
    return guideInteractionService.toggleLikeGuide(userId, guideId);
}
```

**Pattern for User-Created Items:**
- Use `@RequireAuth` annotation
- Call `securityUtil.getCurrentUserId()` to get authenticated user
- Pass user ID to service layer
- Service queries database with user ID filter

---

## 4. EXISTING USER QUERY PATTERNS

### 4.1 Pattern Summary

The project follows a consistent pattern for accessing authenticated user information:

**Pattern Template:**
```java
@RequireAuth  // Ensures user is authenticated
@SchemaMapping(typeName = "TypeName", field = "fieldName")
public ReturnType methodName(@Argument ArgumentType argument) {
    // Step 1: Get current user ID
    UUID userId = securityUtil.getCurrentUserId();
    
    // Step 2: Query repository with user ID filter
    List<Data> userItems = repository.findByUserId(userId);
    
    // Step 3: Transform to DTO and return
    return userItems.stream()
        .map(dto -> DTOMapper.toDTO(dto))
        .collect(Collectors.toList());
}
```

### 4.2 Authentication Principal Extraction

The project provides **two main mechanisms** for getting the current user:

**Mechanism 1: Direct ID Access**
```java
UUID userId = securityUtil.getCurrentUserId();
```
- Returns immediate user UUID
- Throws BusinessException if not authenticated
- Used for queries/mutations requiring just the ID

**Mechanism 2: Full User Entity Access**
```java
User user = securityUtil.getCurrentUserEntity();
```
- Returns full User entity with all fields
- Throws BusinessException if not authenticated
- Used when additional user information is needed

**Mechanism 3: Check Authentication Status**
```java
if (securityUtil.isAuthenticated()) {
    UUID userId = securityUtil.getCurrentUserId();
} else {
    // Handle unauthenticated case
}
```
- Returns boolean for conditional logic
- Used in queries that can work for both authenticated and anonymous users

### 4.3 Authorization Annotation

**Annotation:** `@RequireAuth`
**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/annotation/RequireAuth.java`
**Purpose:** Declarative authentication requirement at method level
**Behavior:** Throws exception if user is not authenticated
**Usage:** Applied to resolver methods that require authentication

---

## 5. SECURITY FLOW DIAGRAM

```
Request
  ↓
[JWTFilter] 
  ├─ Check if path should skip JWT validation
  ├─ Extract Token (Header → Cookie)
  ├─ Check if expired
  │  ├─ Yes → Try refresh token rotation
  │  │   ├─ Validate refresh token
  │  │   └─ Create new access token
  │  └─ No → Validate access token
  └─ Set SecurityContext with CustomOAuth2User
     
SecurityContext Set
  ↓
[GraphQL Resolver]
  ├─ Check @RequireAuth annotation
  └─ If required:
     └─ securityUtil.getCurrentUserId() → Extract from SecurityContext
     
SecurityContext
  ├─ Authentication principal: CustomOAuth2User
  ├─ UserDTO: username, name, role
  └─ Authorities: User roles
```

---

## 6. KEY CLASSES AND RESPONSIBILITIES

### 6.1 Authentication Layer

| Class | File | Responsibility |
|-------|------|-----------------|
| `JWTUtil` | shared/oauth2jwt/jwt/JWTUtil.java | Token creation, parsing, validation |
| `JWTFilter` | shared/oauth2jwt/jwt/JWTFilter.java | Request filtering, token extraction, auto-refresh |
| `SecurityUtil` | shared/oauth2jwt/util/SecurityUtil.java | Current user access, authentication checks |
| `SecurityConfig` | shared/config/SecurityConfig.java | Spring Security configuration, filter chain |
| `CustomOAuth2User` | shared/oauth2jwt/dto/CustomOAuth2User.java | Authentication principal (UserDetails equivalent) |

### 6.2 Voting Domain Layer

| Class | File | Responsibility |
|-------|------|-----------------|
| `Vote` | domain/vote/data/model/Vote.java | Vote entity, vote status management |
| `VoteResponse` | domain/voteResponse/data/model/VoteResponse.java | User vote response tracking |
| `VoteRepository` | domain/vote/repository/VoteRepository.java | Vote data access, user vote queries |
| `VoteResponseRepository` | domain/voteResponse/repository/VoteResponseRepository.java | Vote response data access |
| `VoteMutationResolver` | domain/vote/resolver/VoteMutationResolver.java | Create vote mutation, uses SecurityUtil |
| `VoteResponseQueryResolver` | domain/voteResponse/resolver/VoteResponseQueryResolver.java | Vote response queries, user vote checks |

### 6.3 Supporting Layer

| Class | File | Responsibility |
|-------|------|-----------------|
| `User` | shared/oauth2jwt/entity/User.java | User entity for authentication, vote creation |
| `IamQueryResolver` | domain/IAM/IamQueryResolver.java | Current user information query |

---

## 7. DATABASE RELATIONSHIPS

```
User (1) ──────────── (Many) Vote
         creator_id

User (1) ──────────── (Many) VoteResponse
         user_id

Vote (1) ──────────── (Many) VoteResponse
         vote_id

Option (1) ──────────── (Many) VoteResponse
          option_id
```

---

## 8. CRITICAL FINDINGS FOR FEATURE IMPLEMENTATION

### Finding 1: Current User Access Pattern
**Status:** ESTABLISHED
**Implementation Pattern:**
```java
@RequireAuth
@SchemaMapping(...)
public Data method() {
    UUID userId = securityUtil.getCurrentUserId();
    // ... use userId
}
```

### Finding 2: User Vote Query Support
**Status:** VERIFIED - Infrastructure exists
**Method Available:** `VoteRepository.findByUserId(UUID userId)`
**Result:** Returns list of votes created by user, NOT votes they participated in

### Finding 3: User Participation Tracking
**Status:** VERIFIED - VoteResponse table tracks user votes
**Structure:** 
- User participates in vote → Creates VoteResponse record
- To get votes a user participated in:
  ```
  SELECT DISTINCT vr.vote_id FROM vote_response vr 
  WHERE vr.user_id = ?
  ```

### Finding 4: Vote Creator vs Participant Distinction
**Status:** CRITICAL DISTINCTION
- **Vote Creator:** `Vote.user` → Who created the vote (one creator)
- **Vote Participant:** `VoteResponse.user` → Who participated in the vote (many participants)
- **Current Methods Available:**
  - `VoteRepository.findByUserId()` → Get votes user CREATED
  - NOT available: Get votes user PARTICIPATED in

### Finding 5: Security Implementation
**Status:** PRODUCTION-READY
- JWT tokens with HMAC-SHA256 signing
- Automatic refresh token rotation
- Stateless session management
- Comprehensive exception handling
- SecurityContext integration with Spring Security

---

## 9. ANNOTATIONS AND CUSTOM IMPLEMENTATIONS

### 9.1 @RequireAuth Annotation
Marks methods that require authentication. Enforced via aspect interceptor.

### 9.2 @SchemaMapping
Spring GraphQL annotation mapping resolver methods to GraphQL schema fields.

### 9.3 @AuthenticationPrincipal
**Status:** NOT USED in this codebase
**Alternative Used:** `securityUtil.getCurrentUserId()` pattern (more explicit)

---

## 10. SUMMARY FOR FEATURE IMPLEMENTATION

### What's Available for "getUserVotedOn" Feature:

**✅ YES - Directly Available:**
1. `securityUtil.getCurrentUserId()` - Get current user
2. `VoteResponseRepository.findByVoteId(UUID voteId)` - Get all responses for a vote
3. `@RequireAuth` annotation - Enforce authentication
4. `@SchemaMapping` - Create resolver method

**⚠️ NEEDS NEW QUERY:**
1. `VoteResponseRepository.findByUserId(UUID userId)` - Query votes user participated in
   - Current: Only checks if user voted on specific vote
   - Needed: Get ALL votes user voted on

**Required Database Query:**
```sql
SELECT DISTINCT v.* FROM vote v
INNER JOIN vote_response vr ON v.id = vr.vote_id
WHERE vr.user_id = ?
```

### Implementation Approach:

**Step 1:** Add method to VoteResponseRepository:
```java
@Query("SELECT DISTINCT v FROM Vote v " +
       "INNER JOIN VoteResponse vr ON v.id = vr.vote.id " +
       "WHERE vr.user.id = :userId")
List<Vote> findVotesByUserId(@Param("userId") UUID userId);
```

**Step 2:** Create resolver using existing pattern:
```java
@RequireAuth
@SchemaMapping(typeName = "VoteQuery", field = "getUserVotedOn")
public List<Vote> getUserVotedOn() {
    UUID userId = securityUtil.getCurrentUserId();
    return voteRepository.findVotesByUserId(userId);
}
```

**Step 3:** Add to GraphQL schema:
```graphql
type VoteQuery {
    # ... existing fields ...
    getUserVotedOn: [Vote]!
}
```

---

## 11. CODE REFERENCES

### Authentication Flow
- **Entry Point:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTFilter.java:48-81`
- **Token Validation:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTUtil.java:166-177`
- **Current User Access:** `/src/main/java/pluto/upik/shared/oauth2jwt/util/SecurityUtil.java:31-66`

### Voting Domain
- **Vote Entity:** `/src/main/java/pluto/upik/domain/vote/data/model/Vote.java:1-155`
- **Vote Repository:** `/src/main/java/pluto/upik/domain/vote/repository/VoteRepository.java:35-45`
- **Vote Response Entity:** `/src/main/java/pluto/upik/domain/voteResponse/data/model/VoteResponse.java:40-56`
- **Vote Response Repository:** `/src/main/java/pluto/upik/domain/voteResponse/repository/VoteResponseRepository.java:25-26`

### Existing User Query Example
- **Guide Resolver:** `/src/main/java/pluto/upik/domain/guide/resolver/GuideMutationResolver.java:40-43`
- **Vote Mutation Resolver:** `/src/main/java/pluto/upik/domain/vote/resolver/VoteMutationResolver.java:24-26`
- **IAM Query Resolver:** `/src/main/java/pluto/upik/domain/IAM/IamQueryResolver.java:28-44`

---

**Report Generated:** 2025-10-29
**Analysis Type:** Medium Thoroughness
**Codebase:** Spring Boot + GraphQL with JWT Authentication
