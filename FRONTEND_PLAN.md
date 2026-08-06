# Fitness Tracker Frontend Plan

The `frontend/` application is a React SPA backed by the Spring API.

## Stack

- React Router v6
- TanStack Query and Axios
- Tailwind CSS
- Recharts
- JWT authentication (currently persisted in `localStorage`; move to an HttpOnly cookie when the backend supports it)

## Build phases

1. **Foundation** — tooling, environment-based API client, auth context, protected routes, public home/login/register pages, and responsive app shell. **Complete**
2. **Dashboard** — daily nutrition summary, remaining calories, weekly calorie chart, and recent workout status.
3. **Workouts** — history, routine/day selection, live set logging, and rest timer.
4. **Food** — USDA search, meal logging, meal categories, and macro charts.
5. **Insights** — weight, nutrition, and strength trends plus BMR/TDEE.
6. **Profile** — editable profile and goal settings.
7. **Polish** — responsive/accessibility review and full React Testing Library coverage.

## Backend dependencies

- Meal categories (`BREAKFAST`, `LUNCH`, `DINNER`, `SNACK`)
- Weight, nutrition, strength, and step progress endpoints
- Editable profile fields and `PUT /api/user/me`

The first frontend slice uses the currently available auth, profile, and metrics endpoints. Later phases should be connected as their backend endpoints become available.
