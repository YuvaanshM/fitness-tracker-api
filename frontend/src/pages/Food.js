import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts';
import { parseMeal as parseMealWithAi } from '../api/ai';
import { getApiErrorMessage } from '../api/client';
import { searchFoods } from '../api/foods';
import { createMeal, createMealFromFood, getMealsByDate } from '../api/meals';
import { useAuth } from '../auth/AuthContext';

const CATEGORIES = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];
const MACRO_COLORS = { protein: '#258848', carbs: '#eab308', fats: '#dc2626' };

function toIsoDate(date) {
  return date.toISOString().slice(0, 10);
}

function useDebouncedValue(value, delayMs) {
  const [debounced, setDebounced] = useState(value);

  useEffect(() => {
    const timeout = setTimeout(() => setDebounced(value), delayMs);
    return () => clearTimeout(timeout);
  }, [value, delayMs]);

  return debounced;
}

function FoodSearch({ mealDate, onLogged }) {
  const [query, setQuery] = useState('');
  const [selectedFood, setSelectedFood] = useState(null);
  const [servings, setServings] = useState('1');
  const [category, setCategory] = useState('LUNCH');
  const debouncedQuery = useDebouncedValue(query, 350);

  const searchQuery = useQuery({
    queryKey: ['foods', 'search', debouncedQuery],
    queryFn: () => searchFoods(debouncedQuery),
    enabled: debouncedQuery.trim().length > 1,
  });

  const logFromFoodMutation = useMutation({
    mutationFn: createMealFromFood,
    onSuccess: () => {
      setSelectedFood(null);
      setServings('1');
      onLogged();
    },
  });

  function handleAddFood(event) {
    event.preventDefault();
    logFromFoodMutation.mutate({
      fdcId: selectedFood.fdcId,
      servings: Number(servings),
      mealDate,
      category,
    });
  }

  return (
    <div className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <p className="text-xs font-black uppercase tracking-[0.16em] text-black/45">USDA database</p>
      <h2 className="mt-2 text-2xl font-black">Search foods</h2>

      <label className="field-label mt-6 sr-only" htmlFor="food-search">Search foods</label>
      <input
        id="food-search"
        className="field-input mt-6"
        placeholder="Search for a food, e.g. chicken breast"
        value={query}
        onChange={(event) => setQuery(event.target.value)}
      />

      {searchQuery.isError && (
        <div className="error-banner mt-4" role="alert">Food search is unavailable right now.</div>
      )}

      {searchQuery.data?.length > 0 && (
        <ul className="mt-4 max-h-72 space-y-2 overflow-y-auto">
          {searchQuery.data.map((food) => (
            <li key={food.fdcId}>
              <button
                type="button"
                className="flex w-full items-center justify-between gap-4 rounded-xl border border-black/10 px-4 py-3 text-left hover:border-brand-500"
                onClick={() => setSelectedFood(food)}
              >
                <span>
                  <span className="block font-bold">{food.description}</span>
                  {food.brandName && <span className="block text-xs text-black/50">{food.brandName}</span>}
                </span>
                <span className="text-sm font-black text-brand-700">{food.caloriesPer100g ?? '—'} kcal/100g</span>
              </button>
            </li>
          ))}
        </ul>
      )}

      {debouncedQuery.trim().length > 1 && searchQuery.data?.length === 0 && !searchQuery.isFetching && (
        <p className="mt-4 text-sm text-black/50">No foods found for “{debouncedQuery}”.</p>
      )}

      {selectedFood && (
        <form className="mt-6 space-y-4 rounded-2xl bg-brand-50 p-5" onSubmit={handleAddFood}>
          <p className="font-black">{selectedFood.description}</p>
          {logFromFoodMutation.isError && (
            <div className="error-banner" role="alert">
              {getApiErrorMessage(logFromFoodMutation.error, 'We could not log that food.')}
            </div>
          )}
          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label className="field-label" htmlFor="servings">Servings (× 100 g)</label>
              <input
                id="servings"
                className="field-input"
                type="number"
                min="0.01"
                step="0.25"
                required
                value={servings}
                onChange={(event) => setServings(event.target.value)}
              />
            </div>
            <div>
              <label className="field-label" htmlFor="food-category">Category</label>
              <select
                id="food-category"
                className="field-input"
                value={category}
                onChange={(event) => setCategory(event.target.value)}
              >
                {CATEGORIES.map((option) => (
                  <option key={option} value={option}>{option}</option>
                ))}
              </select>
            </div>
          </div>
          <div className="flex gap-3">
            <button className="button-primary" type="submit" disabled={logFromFoodMutation.isPending}>
              {logFromFoodMutation.isPending ? 'Logging…' : 'Log this food'}
            </button>
            <button type="button" className="button-secondary" onClick={() => setSelectedFood(null)}>
              Cancel
            </button>
          </div>
        </form>
      )}
    </div>
  );
}

const EMPTY_MEAL_FORM = { name: '', category: 'LUNCH', calories: '', protein: '', carbs: '', fats: '', fiber: '' };

function ManualMealForm({ mealDate, onLogged, prefill }) {
  const [form, setForm] = useState(EMPTY_MEAL_FORM);

  useEffect(() => {
    if (prefill) {
      setForm((current) => ({ ...current, ...prefill }));
    }
  }, [prefill]);

  const createMealMutation = useMutation({
    mutationFn: createMeal,
    onSuccess: () => {
      setForm(EMPTY_MEAL_FORM);
      onLogged();
    },
  });

  function handleSubmit(event) {
    event.preventDefault();
    createMealMutation.mutate({
      name: form.name,
      mealDate,
      category: form.category,
      calories: Number(form.calories),
      protein: Number(form.protein),
      carbs: Number(form.carbs),
      fats: Number(form.fats),
      fiber: form.fiber === '' ? undefined : Number(form.fiber),
    });
  }

  function update(field) {
    return (event) => setForm((current) => ({ ...current, [field]: event.target.value }));
  }

  return (
    <div className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <p className="text-xs font-black uppercase tracking-[0.16em] text-black/45">Manual entry</p>
      <h2 className="mt-2 text-2xl font-black">Log a meal directly</h2>

      {createMealMutation.isError && (
        <div className="error-banner mt-4" role="alert">
          {getApiErrorMessage(createMealMutation.error, 'We could not log that meal.')}
        </div>
      )}

      <form className="mt-6 space-y-4" onSubmit={handleSubmit}>
        <div>
          <label className="field-label" htmlFor="meal-name">Meal name</label>
          <input id="meal-name" className="field-input" required value={form.name} onChange={update('name')} />
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <label className="field-label" htmlFor="meal-category">Category</label>
            <select id="meal-category" className="field-input" value={form.category} onChange={update('category')}>
              {CATEGORIES.map((option) => (
                <option key={option} value={option}>{option}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="field-label" htmlFor="meal-calories">Calories</label>
            <input id="meal-calories" className="field-input" type="number" min="0" required value={form.calories} onChange={update('calories')} />
          </div>
        </div>
        <div className="grid gap-4 sm:grid-cols-4">
          <div>
            <label className="field-label" htmlFor="meal-protein">Protein (g)</label>
            <input id="meal-protein" className="field-input" type="number" min="0" step="0.1" required value={form.protein} onChange={update('protein')} />
          </div>
          <div>
            <label className="field-label" htmlFor="meal-carbs">Carbs (g)</label>
            <input id="meal-carbs" className="field-input" type="number" min="0" step="0.1" required value={form.carbs} onChange={update('carbs')} />
          </div>
          <div>
            <label className="field-label" htmlFor="meal-fats">Fats (g)</label>
            <input id="meal-fats" className="field-input" type="number" min="0" step="0.1" required value={form.fats} onChange={update('fats')} />
          </div>
          <div>
            <label className="field-label" htmlFor="meal-fiber">Fiber (g)</label>
            <input id="meal-fiber" className="field-input" type="number" min="0" step="0.1" value={form.fiber} onChange={update('fiber')} />
          </div>
        </div>
        <button className="button-primary" type="submit" disabled={createMealMutation.isPending}>
          {createMealMutation.isPending ? 'Logging…' : 'Log meal'}
        </button>
      </form>
    </div>
  );
}

function AiMealAssistant({ onParsed }) {
  const [text, setText] = useState('');

  const parseMutation = useMutation({
    mutationFn: parseMealWithAi,
    onSuccess: (parsed) => {
      onParsed({
        name: parsed.name ?? '',
        calories: parsed.calories ?? '',
        protein: parsed.protein ?? '',
        carbs: parsed.carbs ?? '',
        fats: parsed.fats ?? '',
        fiber: parsed.fiber ?? '',
      });
    },
  });

  function handleSubmit(event) {
    event.preventDefault();
    if (!text.trim()) return;
    parseMutation.mutate(text.trim());
  }

  return (
    <div className="rounded-3xl border border-brand-200 bg-brand-50 p-6 shadow-card sm:p-8">
      <p className="text-xs font-black uppercase tracking-[0.16em] text-brand-700/70">Powered by Gemini</p>
      <h2 className="mt-2 text-2xl font-black">AI Meal Assistant</h2>
      <p className="mt-2 text-sm text-black/60">
        Describe what you ate in plain language and we'll estimate the nutrition for you to review.
      </p>

      {parseMutation.isError && (
        <div className="error-banner mt-4" role="alert">
          {getApiErrorMessage(parseMutation.error, 'We could not parse that meal.')}
        </div>
      )}

      <form className="mt-6 space-y-4" onSubmit={handleSubmit}>
        <div>
          <label className="field-label sr-only" htmlFor="ai-meal-text">Describe your meal</label>
          <textarea
            id="ai-meal-text"
            className="field-input min-h-24"
            placeholder="e.g. Had a chipotle bowl with double chicken, brown rice, black beans, and guacamole"
            value={text}
            onChange={(event) => setText(event.target.value)}
          />
        </div>
        <button className="button-primary" type="submit" disabled={parseMutation.isPending || !text.trim()}>
          {parseMutation.isPending ? 'Analyzing…' : 'Parse meal with AI'}
        </button>
      </form>

      {parseMutation.isSuccess && (
        <p className="mt-4 text-sm font-semibold text-brand-700">
          Estimate ready — review and confirm it below in "Log a meal directly".
        </p>
      )}
    </div>
  );
}

function MacroChart({ meals }) {
  const totals = meals.reduce(
    (acc, meal) => ({
      protein: acc.protein + Number(meal.protein ?? 0),
      carbs: acc.carbs + Number(meal.carbs ?? 0),
      fats: acc.fats + Number(meal.fats ?? 0),
    }),
    { protein: 0, carbs: 0, fats: 0 }
  );

  const data = [
    { name: 'Protein', value: totals.protein, color: MACRO_COLORS.protein },
    { name: 'Carbs', value: totals.carbs, color: MACRO_COLORS.carbs },
    { name: 'Fats', value: totals.fats, color: MACRO_COLORS.fats },
  ].filter((entry) => entry.value > 0);

  if (data.length === 0) {
    return <p className="text-sm text-black/50">Log a meal to see today's macro breakdown.</p>;
  }

  return (
    <div className="h-64">
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          <Pie data={data} dataKey="value" nameKey="name" innerRadius={50} outerRadius={80} paddingAngle={2}>
            {data.map((entry) => (
              <Cell key={entry.name} fill={entry.color} />
            ))}
          </Pie>
          <Tooltip formatter={(value) => `${value.toFixed(1)} g`} />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}

export default function Food() {
  const { user } = useAuth();
  const [mealDate, setMealDate] = useState(toIsoDate(new Date()));
  const [mealPrefill, setMealPrefill] = useState(null);
  const queryClient = useQueryClient();

  const mealsQuery = useQuery({
    queryKey: ['meals', user?.id, mealDate],
    queryFn: () => getMealsByDate(mealDate),
    enabled: Boolean(user?.id),
  });

  function refreshMeals() {
    queryClient.invalidateQueries({ queryKey: ['meals', user?.id, mealDate] });
    queryClient.invalidateQueries({ queryKey: ['meals', user?.id, 'summary', mealDate] });
  }

  const meals = mealsQuery.data ?? [];
  const mealsByCategory = useMemo(() => {
    const grouped = {};
    CATEGORIES.forEach((category) => {
      grouped[category] = meals.filter((meal) => meal.category === category);
    });
    grouped.UNCATEGORIZED = meals.filter((meal) => !meal.category);
    return grouped;
  }, [meals]);

  return (
    <div>
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-sm font-black uppercase tracking-[0.18em] text-brand-700">Nutrition</p>
          <h1 className="mt-2 text-3xl font-black tracking-tight sm:text-4xl">Food</h1>
        </div>
        <div>
          <label className="field-label" htmlFor="meal-date">Date</label>
          <input
            id="meal-date"
            className="field-input"
            type="date"
            value={mealDate}
            onChange={(event) => setMealDate(event.target.value)}
          />
        </div>
      </div>

      <section className="mt-8">
        <AiMealAssistant onParsed={setMealPrefill} />
      </section>

      <section className="mt-6 grid gap-6 lg:grid-cols-2">
        <FoodSearch mealDate={mealDate} onLogged={refreshMeals} />
        <ManualMealForm mealDate={mealDate} onLogged={refreshMeals} prefill={mealPrefill} />
      </section>

      <section className="mt-8 grid gap-6 lg:grid-cols-[1.4fr_0.6fr]">
        <article className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
          <h2 className="text-xl font-black">Meals for {mealDate}</h2>
          {mealsQuery.isError ? (
            <div className="error-banner mt-6" role="alert">Meals for this date are unavailable right now.</div>
          ) : meals.length === 0 ? (
            <p className="mt-6 text-sm text-black/55">No meals logged for this date yet.</p>
          ) : (
            <div className="mt-6 space-y-6">
              {[...CATEGORIES, 'UNCATEGORIZED'].map((category) =>
                mealsByCategory[category]?.length ? (
                  <div key={category}>
                    <p className="text-xs font-black uppercase tracking-[0.16em] text-black/45">{category}</p>
                    <ul className="mt-2 space-y-2">
                      {mealsByCategory[category].map((meal) => (
                        <li key={meal.id} className="flex items-center justify-between rounded-xl border border-black/10 px-4 py-3">
                          <span className="font-bold">{meal.name}</span>
                          <span className="text-sm text-black/55">{meal.calories} kcal</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                ) : null
              )}
            </div>
          )}
        </article>

        <article className="rounded-3xl bg-white p-6 shadow-card sm:p-8">
          <h2 className="text-xl font-black">Macro breakdown</h2>
          <div className="mt-6">
            <MacroChart meals={meals} />
          </div>
        </article>
      </section>
    </div>
  );
}
