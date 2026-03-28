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

## Public endpoint
1. Create a new endpoint in the controller.
2. Add the endpoint to the SecurityConfig class to allow public access.
3. Add the endpoint to the JwtAuthFilter class to skip JWT validation for that endpoint.

## Adding new domain record
1. Create a new record in the domain package.
2. Add the record to the reflect-config.json file.
3. Add the record to the LiquibaseRuntimeHints class.