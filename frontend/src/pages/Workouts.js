import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { getWorkoutSuggestion } from '../api/ai';
import { getApiErrorMessage } from '../api/client';
import { getRoutines } from '../api/routines';
import { finishWorkout, getWorkouts, logSet, startWorkout } from '../api/workouts';
import { useAuth } from '../auth/AuthContext';
import RestTimer from '../components/RestTimer';

function AiWorkoutSuggestion() {
  const suggestionMutation = useMutation({ mutationFn: getWorkoutSuggestion });

  return (
    <div className="mt-6 rounded-3xl border border-brand-200 bg-brand-50 p-6 shadow-card sm:p-8">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-xs font-black uppercase tracking-[0.16em] text-brand-700/70">Powered by Gemini</p>
          <h2 className="mt-2 text-2xl font-black">Suggest my next workout</h2>
        </div>
        <button
          type="button"
          className="button-primary"
          onClick={() => suggestionMutation.mutate()}
          disabled={suggestionMutation.isPending}
          aria-busy={suggestionMutation.isPending}
        >
          {suggestionMutation.isPending ? 'Thinking…' : 'Suggest next workout'}
        </button>
      </div>

      {suggestionMutation.isError && (
        <div className="error-banner mt-6" role="alert">
          {getApiErrorMessage(suggestionMutation.error, 'We could not generate a suggestion right now.')}
        </div>
      )}

      {suggestionMutation.data?.content && (
        <p className="mt-6 whitespace-pre-line text-sm text-black/70">{suggestionMutation.data.content}</p>
      )}
    </div>
  );
}

function toIsoDate(date) {
  return date.toISOString().slice(0, 10);
}

function formatDateTime(value) {
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  }).format(new Date(value));
}

function RoutineDayPicker({ routines, selectedRoutineId, selectedDayId, onSelectRoutine, onSelectDay, onStart, isStarting }) {
  const selectedRoutine = routines.find((routine) => String(routine.id) === String(selectedRoutineId));

  return (
    <div className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <p className="text-xs font-black uppercase tracking-[0.16em] text-black/45">Start a session</p>
      <h2 className="mt-2 text-2xl font-black">Pick a routine day</h2>

      {routines.length === 0 ? (
        <p className="mt-6 text-sm text-black/55">
          You don't have any routines yet. Create one via the API to start logging structured workouts.
        </p>
      ) : (
        <div className="mt-6 grid gap-4 sm:grid-cols-2">
          <div>
            <label className="field-label" htmlFor="routine-select">Routine</label>
            <select
              id="routine-select"
              className="field-input"
              value={selectedRoutineId}
              onChange={(event) => onSelectRoutine(event.target.value)}
            >
              <option value="">Select a routine</option>
              {routines.map((routine) => (
                <option key={routine.id} value={routine.id}>{routine.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="field-label" htmlFor="day-select">Day</label>
            <select
              id="day-select"
              className="field-input"
              value={selectedDayId}
              onChange={(event) => onSelectDay(event.target.value)}
              disabled={!selectedRoutine}
            >
              <option value="">Select a day</option>
              {selectedRoutine?.days?.map((day) => (
                <option key={day.id} value={day.id}>{day.name}</option>
              ))}
            </select>
          </div>
        </div>
      )}

      <button
        type="button"
        className="button-primary mt-6 w-full"
        disabled={!selectedDayId || isStarting}
        aria-busy={isStarting}
        onClick={onStart}
      >
        {isStarting ? 'Starting…' : 'Start workout'}
      </button>
    </div>
  );
}

function SetLoggingForm({ exercise, setNumber, onLogSet, isLogging }) {
  const [reps, setReps] = useState('');
  const [weightLbs, setWeightLbs] = useState('');

  function handleSubmit(event) {
    event.preventDefault();
    onLogSet(exercise, setNumber, { reps: Number(reps), weightLbs: Number(weightLbs) });
    setReps('');
    setWeightLbs('');
  }

  return (
    <form className="flex flex-wrap items-end gap-3" onSubmit={handleSubmit}>
      <div>
        <label className="field-label" htmlFor={`reps-${exercise.exerciseId}-${setNumber}`}>Reps</label>
        <input
          id={`reps-${exercise.exerciseId}-${setNumber}`}
          className="field-input w-24"
          type="number"
          min="0"
          required
          value={reps}
          onChange={(event) => setReps(event.target.value)}
        />
      </div>
      <div>
        <label className="field-label" htmlFor={`weight-${exercise.exerciseId}-${setNumber}`}>Weight (lb)</label>
        <input
          id={`weight-${exercise.exerciseId}-${setNumber}`}
          className="field-input w-28"
          type="number"
          min="0"
          step="0.5"
          required
          value={weightLbs}
          onChange={(event) => setWeightLbs(event.target.value)}
        />
      </div>
      <button className="button-secondary" type="submit" disabled={isLogging} aria-busy={isLogging}>
        Log set {setNumber}
      </button>
    </form>
  );
}

function ActiveSession({ session, routineDay, onFinish, isFinishing }) {
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const [restSeconds, setRestSeconds] = useState(60);

  const logSetMutation = useMutation({
    mutationFn: ({ setNumber, values, exercise }) =>
      logSet(session.id, {
        exerciseId: exercise.exerciseId,
        setNumber,
        reps: values.reps,
        weightLbs: values.weightLbs,
        restSeconds: exercise.restSeconds,
        completed: true,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workouts', user?.id] });
    },
  });

  const setsByExercise = useMemo(() => {
    const grouped = {};
    (session.sets ?? []).forEach((set) => {
      grouped[set.exerciseId] = grouped[set.exerciseId] ?? [];
      grouped[set.exerciseId].push(set);
    });
    return grouped;
  }, [session.sets]);

  function handleLogSet(exercise, setNumber, values) {
    logSetMutation.mutate(
      { setNumber, values, exercise },
      {
        onSuccess: () => {
          if (exercise.restSeconds) {
            setRestSeconds(exercise.restSeconds);
          }
        },
      }
    );
  }

  return (
    <div className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-xs font-black uppercase tracking-[0.16em] text-brand-700">In progress</p>
          <h2 className="mt-2 text-2xl font-black">{routineDay?.name ?? 'Workout session'}</h2>
        </div>
        <button
          type="button"
          className="button-primary"
          onClick={onFinish}
          disabled={isFinishing}
          aria-busy={isFinishing}
        >
          {isFinishing ? 'Finishing…' : 'Finish workout'}
        </button>
      </div>

      <div className="mt-6">
        <RestTimer defaultSeconds={restSeconds} />
      </div>

      {logSetMutation.isError && (
        <div className="error-banner mt-6" role="alert">
          {getApiErrorMessage(logSetMutation.error, 'We could not log that set.')}
        </div>
      )}

      <div className="mt-8 space-y-6">
        {routineDay?.exercises?.map((exercise) => {
          const loggedSets = setsByExercise[exercise.exerciseId] ?? [];
          const nextSetNumber = loggedSets.length + 1;

          return (
            <div key={exercise.id} className="rounded-2xl border border-black/10 p-5">
              <div className="flex items-baseline justify-between">
                <h3 className="text-lg font-black">{exercise.exerciseName}</h3>
                <p className="text-xs font-bold text-black/45">
                  Target {exercise.targetSets ?? '—'} × {exercise.targetReps ?? '—'}
                </p>
              </div>

              {loggedSets.length > 0 && (
                <ul className="mt-3 space-y-1 text-sm text-black/60">
                  {loggedSets.map((set) => (
                    <li key={set.id}>
                      Set {set.setNumber}: {set.reps} reps @ {set.weightLbs} lb
                    </li>
                  ))}
                </ul>
              )}

              <div className="mt-4">
                <SetLoggingForm
                  exercise={exercise}
                  setNumber={nextSetNumber}
                  onLogSet={handleLogSet}
                  isLogging={logSetMutation.isPending}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function WorkoutHistory({ workouts, isError }) {
  const [expandedId, setExpandedId] = useState(null);

  if (isError) {
    return <div className="error-banner" role="alert">Workout history is unavailable right now.</div>;
  }

  if (!workouts.length) {
    return <p className="text-sm text-black/55">No past workouts yet. Start your first session above.</p>;
  }

  return (
    <ul className="space-y-3">
      {workouts.map((workout) => {
        const isExpanded = expandedId === workout.id;
        return (
          <li key={workout.id} className="rounded-2xl border border-black/10 bg-white">
            <button
              type="button"
              className="flex w-full items-center justify-between gap-4 p-5 text-left"
              onClick={() => setExpandedId(isExpanded ? null : workout.id)}
              aria-expanded={isExpanded}
            >
              <div>
                <p className="font-black">{workout.routineDayName ?? 'Freeform session'}</p>
                <p className="text-sm text-black/50">{formatDateTime(workout.startedAt)}</p>
              </div>
              <span
                className={`rounded-full px-3 py-1 text-xs font-black ${
                  workout.endedAt ? 'bg-brand-100 text-brand-700' : 'bg-amber-100 text-amber-900'
                }`}
              >
                {workout.endedAt ? 'Completed' : 'In progress'}
              </span>
            </button>
            {isExpanded && (
              <div className="border-t border-black/10 p-5">
                {workout.sets?.length ? (
                  <ul className="space-y-1 text-sm text-black/65">
                    {workout.sets.map((set) => (
                      <li key={set.id}>
                        {set.exerciseName}: Set {set.setNumber} — {set.reps} reps @ {set.weightLbs} lb
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-black/50">No sets logged for this session.</p>
                )}
              </div>
            )}
          </li>
        );
      })}
    </ul>
  );
}

export default function Workouts() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [session, setSession] = useState(null);
  const [selectedRoutineId, setSelectedRoutineId] = useState('');
  const [selectedDayId, setSelectedDayId] = useState('');

  const today = toIsoDate(new Date());
  const thirtyDaysAgo = toIsoDate(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000));

  const routinesQuery = useQuery({
    queryKey: ['routines', user?.id],
    queryFn: getRoutines,
    enabled: Boolean(user?.id),
  });

  const workoutsQuery = useQuery({
    queryKey: ['workouts', user?.id, thirtyDaysAgo, today],
    queryFn: () => getWorkouts({ from: thirtyDaysAgo, to: today }),
    enabled: Boolean(user?.id),
  });

  const startMutation = useMutation({
    mutationFn: startWorkout,
    onSuccess: (created) => {
      setSession(created);
      queryClient.invalidateQueries({ queryKey: ['workouts', user?.id] });
    },
  });

  const finishMutation = useMutation({
    mutationFn: () => finishWorkout(session.id, { endedAt: new Date().toISOString() }),
    onSuccess: () => {
      setSession(null);
      setSelectedRoutineId('');
      setSelectedDayId('');
      queryClient.invalidateQueries({ queryKey: ['workouts', user?.id] });
    },
  });

  const routines = routinesQuery.data ?? [];
  const selectedRoutine = routines.find((routine) => String(routine.id) === String(selectedRoutineId));
  const routineDay = session
    ? routines.flatMap((routine) => routine.days ?? []).find((day) => String(day.id) === String(session.routineDayId))
    : selectedRoutine?.days?.find((day) => String(day.id) === String(selectedDayId));

  function handleStart() {
    startMutation.mutate({ routineDayId: Number(selectedDayId) });
  }

  return (
    <div>
      <p className="text-sm font-black uppercase tracking-[0.18em] text-brand-700">Training</p>
      <h1 className="mt-2 text-3xl font-black tracking-tight sm:text-4xl">Workouts</h1>

      {startMutation.isError && (
        <div className="error-banner mt-6" role="alert">
          {getApiErrorMessage(startMutation.error, 'We could not start that workout.')}
        </div>
      )}
      {finishMutation.isError && (
        <div className="error-banner mt-6" role="alert">
          {getApiErrorMessage(finishMutation.error, 'We could not finish that workout.')}
        </div>
      )}

      <section className="mt-8">
        {session ? (
          <ActiveSession
            session={session}
            routineDay={routineDay}
            onFinish={() => finishMutation.mutate()}
            isFinishing={finishMutation.isPending}
          />
        ) : (
          <RoutineDayPicker
            routines={routines}
            selectedRoutineId={selectedRoutineId}
            selectedDayId={selectedDayId}
            onSelectRoutine={(value) => {
              setSelectedRoutineId(value);
              setSelectedDayId('');
            }}
            onSelectDay={setSelectedDayId}
            onStart={handleStart}
            isStarting={startMutation.isPending}
          />
        )}
        {!session && <AiWorkoutSuggestion />}
      </section>

      <section className="mt-10">
        <h2 className="text-xl font-black">History</h2>
        <div className="mt-4">
          <WorkoutHistory workouts={workoutsQuery.data ?? []} isError={workoutsQuery.isError} />
        </div>
      </section>
    </div>
  );
}
