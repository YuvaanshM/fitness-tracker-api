import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { getApiErrorMessage } from '../api/client';
import { updateProfile } from '../api/user';
import { useAuth } from '../auth/AuthContext';

function toFormState(user) {
  return {
    height: user?.height ?? '',
    weight: user?.weight ?? '',
    activityLevel: user?.activityLevel ?? 'MODERATELY_ACTIVE',
    goal: user?.goal ?? 'MAINTAIN',
    goalWeightChangePerWeek: user?.goalWeightChangePerWeek ?? 0,
    dateOfBirth: user?.dateOfBirth ?? '',
  };
}

function ProfileView({ user, onEdit }) {
  return (
    <article className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-xs font-black uppercase tracking-[0.16em] text-black/45">Account</p>
          <h2 className="mt-2 text-2xl font-black">{user?.username}</h2>
        </div>
        <button type="button" className="button-secondary" onClick={onEdit}>
          Edit profile
        </button>
      </div>

      <dl className="mt-8 grid gap-6 sm:grid-cols-2">
        <div>
          <dt className="text-xs font-bold uppercase tracking-wide text-black/45">Height</dt>
          <dd className="mt-1 text-lg font-black">{user?.height} cm</dd>
        </div>
        <div>
          <dt className="text-xs font-bold uppercase tracking-wide text-black/45">Weight</dt>
          <dd className="mt-1 text-lg font-black">{user?.weight} lb</dd>
        </div>
        <div>
          <dt className="text-xs font-bold uppercase tracking-wide text-black/45">Activity level</dt>
          <dd className="mt-1 text-lg font-black capitalize">{user?.activityLevel?.toLowerCase().replaceAll('_', ' ')}</dd>
        </div>
        <div>
          <dt className="text-xs font-bold uppercase tracking-wide text-black/45">Goal</dt>
          <dd className="mt-1 text-lg font-black capitalize">{user?.goal?.toLowerCase()}</dd>
        </div>
        <div>
          <dt className="text-xs font-bold uppercase tracking-wide text-black/45">Weekly change target</dt>
          <dd className="mt-1 text-lg font-black">{user?.goalWeightChangePerWeek} lb/week</dd>
        </div>
        <div>
          <dt className="text-xs font-bold uppercase tracking-wide text-black/45">Date of birth</dt>
          <dd className="mt-1 text-lg font-black">{user?.dateOfBirth}</dd>
        </div>
      </dl>
    </article>
  );
}

function ProfileEditForm({ user, onCancel, onSaved }) {
  const { refreshUser } = useAuth();
  const [form, setForm] = useState(toFormState(user));

  const updateMutation = useMutation({
    mutationFn: updateProfile,
    onSuccess: async () => {
      await refreshUser();
      onSaved();
    },
  });

  function update(field) {
    return (event) => setForm((current) => ({ ...current, [field]: event.target.value }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    updateMutation.mutate({
      height: Number(form.height),
      weight: Number(form.weight),
      activityLevel: form.activityLevel,
      goal: form.goal,
      goalWeightChangePerWeek: Number(form.goalWeightChangePerWeek),
      dateOfBirth: form.dateOfBirth,
    });
  }

  return (
    <article className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <p className="text-xs font-black uppercase tracking-[0.16em] text-black/45">Account</p>
      <h2 className="mt-2 text-2xl font-black">Edit your profile</h2>

      {updateMutation.isError && (
        <div className="error-banner mt-4" role="alert">
          {getApiErrorMessage(updateMutation.error, 'We could not save your profile.')}
        </div>
      )}

      <form className="mt-6 space-y-5" onSubmit={handleSubmit}>
        <div className="grid gap-5 sm:grid-cols-2">
          <div>
            <label className="field-label" htmlFor="profile-dob">Date of birth</label>
            <input
              className="field-input"
              id="profile-dob"
              type="date"
              required
              max={new Date().toISOString().split('T')[0]}
              value={form.dateOfBirth}
              onChange={update('dateOfBirth')}
            />
          </div>
          <div>
            <label className="field-label" htmlFor="profile-height">Height (cm)</label>
            <input
              className="field-input"
              id="profile-height"
              type="number"
              min="1"
              max="300"
              step="0.1"
              required
              value={form.height}
              onChange={update('height')}
            />
          </div>
          <div>
            <label className="field-label" htmlFor="profile-weight">Weight (lb)</label>
            <input
              className="field-input"
              id="profile-weight"
              type="number"
              min="1"
              max="1000"
              step="0.1"
              required
              value={form.weight}
              onChange={update('weight')}
            />
          </div>
          <div>
            <label className="field-label" htmlFor="profile-activity">Activity level</label>
            <select
              className="field-input"
              id="profile-activity"
              value={form.activityLevel}
              onChange={update('activityLevel')}
            >
              <option value="SEDENTARY">Sedentary</option>
              <option value="LIGHTLY_ACTIVE">Lightly active</option>
              <option value="MODERATELY_ACTIVE">Moderately active</option>
              <option value="VERY_ACTIVE">Very active</option>
              <option value="EXTRA_ACTIVE">Extra active</option>
            </select>
          </div>
          <div>
            <label className="field-label" htmlFor="profile-goal">Goal</label>
            <select className="field-input" id="profile-goal" value={form.goal} onChange={update('goal')}>
              <option value="LOSE">Lose weight</option>
              <option value="MAINTAIN">Maintain weight</option>
              <option value="GAIN">Gain weight</option>
            </select>
          </div>
          <div>
            <label className="field-label" htmlFor="profile-rate">Weekly change (lb)</label>
            <input
              className="field-input"
              id="profile-rate"
              type="number"
              min="-10"
              max="10"
              step="0.25"
              required
              value={form.goalWeightChangePerWeek}
              onChange={update('goalWeightChangePerWeek')}
            />
          </div>
        </div>

        <div className="flex gap-3">
          <button className="button-primary" type="submit" disabled={updateMutation.isPending} aria-busy={updateMutation.isPending}>
            {updateMutation.isPending ? 'Saving…' : 'Save changes'}
          </button>
          <button type="button" className="button-secondary" onClick={onCancel}>
            Cancel
          </button>
        </div>
      </form>
    </article>
  );
}

export default function Profile() {
  const { user } = useAuth();
  const [isEditing, setIsEditing] = useState(false);

  return (
    <div>
      <p className="text-sm font-black uppercase tracking-[0.18em] text-brand-700">Account</p>
      <h1 className="mt-2 text-3xl font-black tracking-tight sm:text-4xl">Profile</h1>

      <section className="mt-8">
        {isEditing ? (
          <ProfileEditForm user={user} onCancel={() => setIsEditing(false)} onSaved={() => setIsEditing(false)} />
        ) : (
          <ProfileView user={user} onEdit={() => setIsEditing(true)} />
        )}
      </section>
    </div>
  );
}
