# Quick Summary: Authentication & Voting System Analysis

## Key Findings

### Authentication System
- **Type:** JWT-based OAuth2 with automatic refresh token rotation
- **Token Format:** HS256 signed, contains username/role/category
- **User Access:** Via `SecurityUtil` class - three methods available:
  - `getCurrentUserId()` - Returns UUID (throws exception if not authenticated)
  - `getCurrentUserEntity()` - Returns full User entity
  - `isAuthenticated()` - Boolean check

### Voting Structure
- **Vote Entity:** Represents a poll/question created by a user
  - `Vote.user` = Creator (ManyToOne relationship)
  - `Vote` table has columns: id, question, category, status, finishedAt, participantThreshold
  
- **VoteResponse Entity:** Tracks user participation in votes
  - `VoteResponse.user` = Who voted
  - `VoteResponse.vote` = Which vote they participated in
  - `VoteResponse.selectedOption` = Which option they chose

### Critical Distinction
- **Vote Creator:** `Vote.findByUserId()` ✅ Available
- **Vote Participant:** `VoteResponse.findByUserId()` ❌ NOT Available (needs to be created)

## Existing User Query Pattern

All resolver methods that access current user follow this pattern:

```java
@RequireAuth                                        // 1. Enforce authentication
@SchemaMapping(typeName = "Type", field = "field")  // 2. Map to GraphQL
public ReturnType method(@Argument InputType arg) { 
    UUID userId = securityUtil.getCurrentUserId(); // 3. Get user ID
    return repository.findByUserId(userId);         // 4. Query with user ID
}
```

Examples in codebase:
- `VoteMutationResolver.createVote()` - Gets current user to create vote
- `VoteResponseQueryResolver.hasUserVoted()` - Gets current user to check participation
- `GuideMutationResolver.incrementGuideLike()` - Gets current user to like guide

## For "getUserVotedOn" Feature

**Required Changes:**

1. **Add Repository Method:**
   ```java
   @Query("SELECT DISTINCT v FROM Vote v " +
          "INNER JOIN VoteResponse vr ON v.id = vr.vote.id " +
          "WHERE vr.user.id = :userId")
   List<Vote> findVotesByUserParticipation(@Param("userId") UUID userId);
   ```

2. **Create Resolver Method:**
   ```java
   @RequireAuth
   @SchemaMapping(typeName = "VoteQuery", field = "getUserVotedOn")
   public List<Vote> getUserVotedOn() {
       UUID userId = securityUtil.getCurrentUserId();
       return voteRepository.findVotesByUserParticipation(userId);
   }
   ```

3. **Update GraphQL Schema:**
   ```graphql
   type VoteQuery {
       # ... existing fields ...
       getUserVotedOn: [Vote]!
   }
   ```

## File Locations

### Authentication
- JWT Tokens: `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTUtil.java`
- Filter: `/src/main/java/pluto/upik/shared/oauth2jwt/jwt/JWTFilter.java`
- User Access: `/src/main/java/pluto/upik/shared/oauth2jwt/util/SecurityUtil.java`
- Security Config: `/src/main/java/pluto/upik/shared/config/SecurityConfig.java`

### Voting
- Vote Entity: `/src/main/java/pluto/upik/domain/vote/data/model/Vote.java`
- Vote Repository: `/src/main/java/pluto/upik/domain/vote/repository/VoteRepository.java`
- VoteResponse Entity: `/src/main/java/pluto/upik/domain/voteResponse/data/model/VoteResponse.java`
- VoteResponse Repository: `/src/main/java/pluto/upik/domain/voteResponse/repository/VoteResponseRepository.java`

### GraphQL Resolvers
- Vote Resolver: `/src/main/java/pluto/upik/domain/vote/resolver/VoteMutationResolver.java`
- Vote Response Resolver: `/src/main/java/pluto/upik/domain/voteResponse/resolver/VoteResponseQueryResolver.java`
- IAM Resolver: `/src/main/java/pluto/upik/domain/IAM/IamQueryResolver.java`

### GraphQL Schema
- Vote Schema: `/src/main/resources/graphql/schema-voting.graphqls`
- IAM Schema: `/src/main/resources/graphql/iam.graphqls`

## Security Notes

- **Stateless:** No server-side sessions, JWT tokens only
- **Auto-refresh:** Access tokens expire in 15 minutes, automatically refreshed via refresh token
- **GraphQL:** All GraphQL endpoints are publicly accessible (no auth required at endpoint level)
- **Authorization:** Enforced at resolver level via `@RequireAuth` annotation
- **CORS:** Allows all origins with credentials

## Next Steps

1. Review full analysis document: `AUTHENTICATION_VOTING_ANALYSIS.md`
2. Create `VoteResponseRepository.findVotesByUserParticipation()` method
3. Create resolver method following the established pattern
4. Update GraphQL schema with new query
5. Test with authenticated GraphQL query

---
Generated: 2025-10-29
