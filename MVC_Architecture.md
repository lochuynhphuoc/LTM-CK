# MVC Architecture Overview

## Layer Responsibilities

| Layer             | Location                                                                 | Responsibilities                                                                                                                                       |
| ----------------- | ------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Controller        | `src/main/java/com/ltm/controller/*.java`                              | Accept HTTP requests, validate input, invoke DAO/model logic, and redirect/forward to JSP views.                                                       |
| Model             | `src/main/java/com/ltm/model/*.java`                                   | Plain objects (`User`, `Task`) that carry domain data between controllers, DAOs, and views.                                                        |
| Data Access (DAO) | `src/main/java/com/ltm/dao/*.java`                                     | Encapsulate JDBC operations for users and tasks via `DatabaseConnection`, isolating persistence from business logic.                                 |
| View              | `src/main/webapp/*.jsp`                                                | Render HTML pages (`login.jsp`, `register.jsp`, `dashboard.jsp`, `taskList.jsp`, `db_update.jsp`) using attributes populated by controllers. |
| Infrastructure    | `src/main/java/com/ltm/listener`, `com/ltm/worker`, `com/ltm/util` | Bootstrap schema, manage task queues, and provide background processing to support controllers/DAOs.                                                   |

## Typical Request Flow

1. User submits a form (e.g., login) from a JSP page.
2. Servlet controller (e.g., `LoginServlet`) receives the request, validates input, and calls the appropriate DAO.
3. DAO interacts with the database via `DatabaseConnection`, maps results into model objects, and returns them.
4. Controller stores model data in request/session attributes and decides the next view.
5. JSP retrieves the provided attributes and renders the response HTML back to the user.

## Component Highlights

- **Controllers**: `LoginServlet`, `LogoutServlet`, `RegisterServlet`, `TaskServlet`.
- **Models**: `User`, `Task` POJOs.
- **DAOs**: `UserDAO`, `TaskDAO` powered by `DatabaseConnection`.
- **Listeners**: `AppContextListener`, `SchemaInitListener` ensure schema readiness at startup.
- **Background Workers**: `TaskQueue`, `WorkerThread` handle asynchronous job execution.
- **Views**: JSP files under `src/main/webapp` plus static corpus resources in `WEB-INF/corpus`.

## Task Processing Pipeline

```
TaskServlet (controller)
	|
	v
TaskDAO -> Persist new task in DB
	|
	v
TaskQueue -> Push task for async work
	|
	v
WorkerThread (background)
   - Poll task from queue
   - Execute long-running logic
   - Update task status back in DB
```

## Design Notes

- Controllers never access JDBC directly; all persistence is funneled through DAO classes for maintainability.
- Models remain free of servlet/JSP imports, keeping domain logic framework-agnostic.
- JSPs stay presentation-focused by relying on request attributes rather than embedding business logic.
- Initialization listeners and utility classes keep schema management concerns separate from request handling.
