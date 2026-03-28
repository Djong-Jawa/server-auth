# auth-server
This server auth is handling all the authentication and authorization for the microservices. 
It is responsible for issuing JWT tokens to clients and validating them for each request.

# How to work with this backend? 
New endpoint
├── Public?
│   ├── SecurityConfig  → add to permitAll()
│   └── JwtAuthFilter   → add to requestURI.equals() skip check
├── New domain record used with JdbcClient?
│   ├── reflect-config.json  → register the record
│   └── LiquibaseRuntimeHints → rh.registerType(...)
└── New XML/resource files?
└── LiquibaseRuntimeHints → hints.resources().registerPattern("...")