# Getting Started

### JWT secret

The application loads `JWT_SECRET` from a `.env` file in the backend directory or
from the process environment. Create a local `.env` with a randomly generated
secret of at least 32 bytes before starting the application. In PowerShell, from
the backend directory:

```powershell
$bytes = New-Object byte[] 32
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
"JWT_SECRET=$([Convert]::ToBase64String($bytes))" | Set-Content -Encoding Ascii .env
$rng.Dispose()
```

`.env` is ignored by Git. Set `JWT_SECRET` through your deployment environment
instead of using a local `.env` in production.

### Database configuration

The local defaults target `sonido_vivo` on `localhost:3306` with user `root`.
Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in the backend `.env` file or
deployment environment to use other credentials. Never commit production
credentials. SQL logging is disabled by default; enable it locally with
`JPA_SHOW_SQL=true` when diagnosing database queries.

Existing databases created with the previous order mappings may contain
duplicate, required columns that the current application no longer writes.
Apply `src/main/resources/db/manual/allow-purchases-with-legacy-columns.sql`
once to that database to allow new purchases; the migration preserves existing
column values.

### Authentication and roles

The authentication endpoints are:

- `POST /api/v1/auth/refresh` with `refreshToken` rotates the refresh token and
  returns a new access/refresh token pair. Reusing the old refresh token fails.
- `POST /api/v1/auth/logout` with `refreshToken` revokes that refresh token.
- `PUT /api/v1/auth/profile` updates the authenticated user's personal data.
  It returns a replacement access/refresh token pair and revokes that user's
  previous refresh tokens.

Refresh tokens expire after seven days by default. Only a SHA-256 hash of each
refresh token is persisted.

- `POST /api/v1/auth/register` with `nombre`, `apellido`, `correo`, `password`,
  `fechaNacimiento` (`YYYY-MM-DD`), `direccion`, `region`, and `genero`.
- `POST /api/v1/auth/login` with `correo` and `password`. A successful response
  includes a Bearer JWT, user ID, name, email, and role.

Public registration always creates a `CLIENTE`. Product and category reads are
public; creating, editing, and deleting them requires `ADMINISTRADOR`. Updating
stock through `PATCH /api/v1/productos/{id}/stock` requires `VENDEDOR`.
There are no default test accounts. To provision a first administrator, register
the account and promote it explicitly in MySQL:

```sql
UPDATE usuarios
SET rol = 'ADMINISTRADOR'
WHERE email = 'admin@example.com';
```

Use the same process with `VENDEDOR` for a seller. Do not expose role assignment
in public registration.

Customers and sellers can create an order with `POST /api/v1/pedidos`, sending
an `items` array containing `productoId` and `cantidad`. The backend calculates
the total, checks and locks stock, and persists the order and item price
snapshots in one transaction. Customers and sellers can retrieve only their own
orders from `GET /api/v1/pedidos/mis-pedidos`; administrators can list all orders from
`GET /api/v1/pedidos`. Administrator order responses include the customer name
and email for order management.

Administrators can advance a valid order state with
`PATCH /api/v1/pedidos/{id}/estado`, sending `{"estado":"PREPARANDO"}` (valid
transitions are PENDIENTE -> PREPARANDO/CANCELADO, PREPARANDO ->
ENVIADO/CANCELADO, and ENVIADO -> ENTREGADO). Cancelling restores reserved
stock.

Administrators can manage accounts using `GET/POST /api/v1/admin/usuarios`,
`PUT/DELETE /api/v1/admin/usuarios/{id}`. The request role is restricted by the
backend enum and public registration continues to create clients only. A user
with orders cannot be deleted to preserve order history.

`GET /api/v1/admin/reportes?periodo=SEMANA|MES|ANIO` returns operation count,
sales total, units sold, and up to five best-selling products for the selected
period. Cancelled orders are excluded.

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.2/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.2/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.1.2/reference/web/servlet.html)
* [SpringDoc OpenAPI](https://springdoc.org/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.1.2/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/4.1.2/reference/io/validation.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi-demos/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.
