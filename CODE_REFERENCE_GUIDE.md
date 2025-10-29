# Code Reference Guide
## Authentication & Voting System - Code Locations and Examples

---

## 1. AUTHENTICATION COMPONENTS

### 1.1 Getting Current User ID (Most Common Pattern)

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/util/SecurityUtil.java:64-66`

```java
public UUID getCurrentUserId() {
    return getCurrentUserEntity().getId();
}
```

**Usage in Resolvers:**
```java
// Location: /src/main/java/pluto/upik/domain/vote/resolver/VoteMutationResolver.java:25
@RequireAuth
@SchemaMapping(typeName = "VoteMutation", field = "createVote")
public VotePayload createVote(@Argument CreateVoteInput input) {
    UUID userId = securityUtil.getCurrentUserId();  // ← Single line gets user ID
    return voteApplication.createVote(input, userId);
}
```

---

### 1.2 Checking if User is Authenticated

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/util/SecurityUtil.java:73-77`

```java
public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated() && 
           !"anonymousUser".equals(authentication.getPrincipal());
}
```

**Usage Example:**
```java
// Can be used in resolvers that support both authenticated and anonymous users
UUID userId = securityUtil.isAuthenticated() ? securityUtil.getCurrentUserId() : null;
List<VotePayload> votes = voteService.getAllVotes(userId);  // Returns different data based on auth
```

---

### 1.3 Getting Full User Entity

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/util/SecurityUtil.java:49-55`

```java
public User getCurrentUserEntity() {
    CustomOAuth2User oAuth2User = getCurrentUser();
    String username = oAuth2User.getUsername();
    
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new BusinessException("사용자 정보를 찾을 수 없습니다.", "USER_NOT_FOUND"));
}
```

**Usage in Resolvers:**
```java
// When you need full user information
@RequireAuth
public IamDTO getCurrentUser() {
    UUID userId = securityUtil.getCurrentUserId();
    User user = securityUtil.getCurrentUserEntity();  // Now you have access to all fields
    
    return IamDTO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .email(user.getEmail())
        .role(user.getRole())
        .build();
}
```

**Location Reference:** `/src/main/java/pluto/upik/domain/IAM/IamQueryResolver.java:28-44`

---

## 2. JWT TOKEN COMPONENTS

### 2.1 Token Creation

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTUtil.java:151-160`

```java
// Create Access Token (15 minutes)
public String createAccessToken(String username, String role) {
    return createJwt("access", username, role, accessTokenExpirationTime);
}

// Create Refresh Token (longer expiration)
public String createRefreshToken(String username, String role) {
    return createJwt("refresh", username, role, refreshTokenExpirationTime);
}
```

### 2.2 Token Validation

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTUtil.java:166-177`

```java
public boolean validateToken(String token) {
    try {
        getClaims(token);
        return true;
    } catch (ExpiredJwtException e) {
        log.debug("Token expired: {}", e.getMessage());
        return false;
    } catch (Exception e) {
        log.warn("Token validation failed: {}", e.getMessage());
        return false;
    }
}
```

### 2.3 Token Data Extraction

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTUtil.java:70-95`

```java
// Get username from token (even if expired)
public String getUsername(String token) {
    try {
        return getClaims(token).get("username", String.class);
    } catch (ExpiredJwtException e) {
        return e.getClaims().get("username", String.class);  // Can extract from expired token
    } catch (Exception e) {
        log.warn("Failed to extract username from token: {}", e.getMessage());
        return null;
    }
}

// Get role from token
public String getRole(String token) {
    try {
        return getClaims(token).get("role", String.class);
    } catch (ExpiredJwtException e) {
        return e.getClaims().get("role", String.class);
    } catch (Exception e) {
        log.warn("Failed to extract role from token: {}", e.getMessage());
        return null;
    }
}
```

---

## 3. REQUEST FILTERING

### 3.1 JWT Filter Flow

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTFilter.java:48-81`

```java
@Override
protected void doFilterInternal(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull FilterChain filterChain) throws ServletException, IOException {
    String requestURI = request.getRequestURI();

    // 1. Check if path should skip JWT validation
    if (shouldSkipFilter(requestURI)) {
        filterChain.doFilter(request, response);
        return;
    }

    // 2. Extract token from header or cookie
    String accessToken = extractAccessToken(request);
    if (accessToken == null) {
        filterChain.doFilter(request, response);
        return;
    }

    // 3. Process token
    try {
        if (jwtUtil.isExpired(accessToken)) {
            // Try refresh token rotation
            handleExpiredAccessToken(request, response);
        } else {
            validateAndSetAuthentication(accessToken);
        }
    } catch (Exception e) {
        log.warn("Error processing JWT for URI: {}. Error: {}", requestURI, e.getMessage());
    }
    
    setAuthentication(accessToken);
    filterChain.doFilter(request, response);
}
```

### 3.2 Automatic Token Refresh

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTFilter.java:105-141`

```java
private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
    extractCookieValue(request, "refreshToken")
            .filter(this::isValidRefreshToken)
            .ifPresent(refreshToken -> {
                try {
                    String newAccessToken = createNewAccessToken(refreshToken);
                    setAuthentication(newAccessToken);
                    setAccessTokenCookie(response, newAccessToken);
                    log.info("Access Token refreshed successfully for user: {}", 
                        jwtUtil.getUsername(refreshToken));
                } catch (Exception e) {
                    log.warn("Failed to refresh Access Token: {}", e.getMessage());
                }
            });
}
```

### 3.3 SecurityContext Setup

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTFilter.java:181-206`

```java
private void setAuthentication(String token) {
    String username = jwtUtil.getUsername(token);
    String role = jwtUtil.getRole(token);

    if (username == null || role == null || "ROLE_DELETED".equals(role)) {
        log.warn("Authentication failed for token. Username: {}, Role: {}", username, role);
        return;
    }

    // Get user display name from database
    String name = userRepository.findNameByUsername(username)
            .filter(n -> !n.trim().isEmpty())
            .orElse(username);

    // Create UserDTO
    UserDTO userDTO = UserDTO.builder()
            .username(username)
            .name(name)
            .role(role)
            .build();

    // Create authentication principal
    CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO, userRepository);
    Authentication authToken = new UsernamePasswordAuthenticationToken(
            customOAuth2User, null, customOAuth2User.getAuthorities());

    // Set in SecurityContext
    SecurityContextHolder.getContext().setAuthentication(authToken);
    log.debug("Authentication set for user: {}", username);
}
```

---

## 4. VOTING SYSTEM COMPONENTS

### 4.1 Vote Entity Structure

**Location:** `/src/main/java/pluto/upik/domain/vote/data/model/Vote.java`

```java
@Entity
@Table(name = "vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    // ← KEY: Creator relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String question;

    private String category;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('OPEN', 'CLOSED')")
    private Status status;

    @Column
    private LocalDate finishedAt;

    @Column
    private Integer participantThreshold;

    @Transient
    private boolean guideGenerated;

    public enum Status {
        OPEN, CLOSED
    }
}
```

### 4.2 VoteResponse Entity Structure

**Location:** `/src/main/java/pluto/upik/domain/voteResponse/data/model/VoteResponse.java:27-72`

```java
@Entity
@Table(name = "vote_response")
@Getter
public class VoteResponse {
    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue
    private UUID id;

    // ← KEY: Who voted (participant)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ← Which vote they participated in
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option selectedOption;

    @Column
    private LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
```

---

## 5. VOTE REPOSITORY QUERIES

### 5.1 Get Votes Created by User

**Location:** `/src/main/java/pluto/upik/domain/vote/repository/VoteRepository.java:35-45`

```java
// Get all votes created by user
List<Vote> findByUserId(UUID userId);

// Get active votes created by user
@Query("SELECT v FROM Vote v WHERE v.user.id = :userId AND v.finishedAt > :currentDate")
List<Vote> findActiveVotesByUserId(UUID userId, LocalDate currentDate);
```

### 5.2 Get All Votes with Participation Info

**Location:** `/src/main/java/pluto/upik/domain/vote/repository/VoteRepository.java:96-101`

```java
// Sorted by participation rate (most participated first)
@Query(value = "SELECT v.* FROM vote v " +
       "LEFT JOIN (SELECT vote_id, COUNT(*) as response_count FROM vote_response " +
       "GROUP BY vote_id) vr ON v.id = vr.vote_id " +
       "ORDER BY vr.response_count DESC NULLS LAST", 
       nativeQuery = true)
Page<Vote> findAllOrderByParticipationRate(Pageable pageable);
```

---

## 6. VOTERESPONSE REPOSITORY QUERIES

### 6.1 Check User Participation in Specific Vote

**Location:** `/src/main/java/pluto/upik/domain/voteResponse/repository/VoteResponseRepository.java:25-26`

```java
// Check if user already voted on specific vote
@Query("SELECT vr FROM VoteResponse vr WHERE vr.user.id = :userId AND vr.vote.id = :voteId")
Optional<VoteResponse> findByUserIdAndVoteId(@Param("userId") UUID userId, @Param("voteId") UUID voteId);
```

### 6.2 Count Responses for Vote

**Location:** `/src/main/java/pluto/upik/domain/voteResponse/repository/VoteResponseRepository.java:29-34`

```java
// Total responses for a vote
@Query("SELECT COUNT(vr) FROM VoteResponse vr WHERE vr.vote.id = :voteId")
Long countByVoteId(@Param("voteId") UUID voteId);

// Responses for specific option
@Query("SELECT COUNT(vr) FROM VoteResponse vr WHERE vr.selectedOption.id = :optionId")
Long countByOptionId(@Param("optionId") UUID optionId);
```

---

## 7. GRAPHQL RESOLVERS - AUTHENTICATION EXAMPLE

### 7.1 Vote Mutation Resolver

**Location:** `/src/main/java/pluto/upik/domain/vote/resolver/VoteMutationResolver.java`

```java
@Controller
@RequiredArgsConstructor
public class VoteMutationResolver {

    private final VoteApplication voteApplication;
    private final SecurityUtil securityUtil;

    @RequireAuth
    @SchemaMapping(typeName = "VoteMutation", field = "createVote")
    public VotePayload createVote(@Argument CreateVoteInput input) {
        UUID userId = securityUtil.getCurrentUserId();  // ← Get current user
        return voteApplication.createVote(input, userId);
    }
}
```

### 7.2 Vote Response Query Resolver

**Location:** `/src/main/java/pluto/upik/domain/voteResponse/resolver/VoteResponseQueryResolver.java`

```java
@Controller
@RequiredArgsConstructor
public class VoteResponseQueryResolver {

    private final VoteResponseApplication voteResponseApplication;
    private final SecurityUtil securityUtil;

    @SchemaMapping(typeName = "VoteResponseQuery", field = "getVoteResponseCount")
    public Integer getVoteResponseCount(@Argument UUID voteId) {
        // No authentication required - public query
        return voteResponseApplication.getVoteResponseCount(voteId).intValue();
    }

    @RequireAuth
    @SchemaMapping(typeName = "VoteResponseQuery", field = "hasUserVoted")
    public Boolean hasUserVoted(@Argument UUID voteId) {
        UUID userId = securityUtil.getCurrentUserId();  // ← Get current user
        return voteResponseApplication.hasUserVoted(userId, voteId);
    }
}
```

### 7.3 IAM Query Resolver (Current User Info)

**Location:** `/src/main/java/pluto/upik/domain/IAM/IamQueryResolver.java`

```java
@Controller
@RequiredArgsConstructor
public class IamQueryResolver {

    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;

    @SchemaMapping(typeName = "IamQuery")
    public IamDTO getCurrentUser(IamQuery iamQuery) {
        UUID userId = securityUtil.getCurrentUserId();
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
}
```

---

## 8. GRAPHQL SCHEMA DEFINITIONS

### 8.1 Vote Schema

**Location:** `/src/main/resources/graphql/schema-voting.graphqls`

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

### 8.2 IAM Schema

**Location:** `/src/main/resources/graphql/iam.graphqls`

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

## 9. SECURITY CONFIGURATION

**Location:** `/src/main/java/pluto/upik/shared/config/SecurityConfig.java:52-114`

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository) throws Exception {
    // 1. CORS
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

    // 2. Disable CSRF, Form Login, HTTP Basic
    http.csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

    // 3. Stateless session
    http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // 4. Authorization - Permit all patterns, require auth for others
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
            .anyRequest().authenticated()
    );

    // 5. OAuth2 Login
    http.oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(customSuccessHandler));

    // 6. JWT Filter - Added before UsernamePasswordAuthenticationFilter
    http.addFilterBefore(new JWTFilter(jwtUtil, userRepository, refreshTokenRepository), 
            UsernamePasswordAuthenticationFilter.class);

    // 7. Logout
    http.logout(logout -> logout.logoutUrl("/auth/logout").deleteCookies("refreshToken", "Authorization"));

    // 8. Exception handling
    http.exceptionHandling(exceptions -> exceptions
            .accessDeniedHandler((request, response, exception) -> {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Access denied\"}");
            })
            .authenticationEntryPoint((request, response, exception) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Login required\"}");
            })
    );

    return http.build();
}
```

---

## 10. AUTHORIZATION ANNOTATION

**Location:** `/src/main/java/pluto/upik/shared/oauth2jwt/annotation/RequireAuth.java`

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
    // Marks method as requiring authentication
}
```

**Used in resolvers to enforce authentication at method level before execution.**

---

## Summary Table

| Component | Location | Purpose |
|-----------|----------|---------|
| SecurityUtil | `shared/oauth2jwt/util/` | Get current user info |
| JWTUtil | `shared/oauth2jwt/jwt/` | Create/validate tokens |
| JWTFilter | `shared/oauth2jwt/jwt/` | Filter requests, set SecurityContext |
| SecurityConfig | `shared/config/` | Spring Security configuration |
| Vote | `domain/vote/data/model/` | Vote entity (creator) |
| VoteResponse | `domain/voteResponse/data/model/` | Vote response entity (participant) |
| VoteRepository | `domain/vote/repository/` | Vote queries |
| VoteResponseRepository | `domain/voteResponse/repository/` | Vote response queries |
| VoteMutationResolver | `domain/vote/resolver/` | Create votes with auth |
| VoteResponseQueryResolver | `domain/voteResponse/resolver/` | Query vote participation |
| IamQueryResolver | `domain/IAM/` | Get current user info |

---

Generated: 2025-10-29
