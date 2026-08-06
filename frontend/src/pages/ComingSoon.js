const copy = {
  workouts: {
    eyebrow: 'Training',
    title: 'Build and log every workout',
    description: 'Your routines, live set tracking, workout history, and rest timer will live here.',
  },
  food: {
    eyebrow: 'Nutrition',
    title: 'Make every meal count',
    description: 'Search foods, log meals, and compare daily macros with your personal targets.',
  },
  insights: {
    eyebrow: 'Progress',
    title: 'Turn consistency into insight',
    description: 'Follow your weight, strength, and nutrition trends over time.',
  },
  profile: {
    eyebrow: 'Account',
    title: 'Your profile, your targets',
    description: 'Manage your personal information and the goals that shape your recommendations.',
  },
};

export default function ComingSoon({ page }) {
  const content = copy[page];

  return (
    <section>
      <p className="text-sm font-black uppercase tracking-[0.18em] text-brand-700">{content.eyebrow}</p>
      <h1 className="mt-2 max-w-2xl text-3xl font-black tracking-tight sm:text-4xl">{content.title}</h1>
      <p className="mt-4 max-w-2xl text-lg leading-8 text-black/60">{content.description}</p>
      <div className="mt-10 grid min-h-80 place-items-center rounded-3xl border border-dashed border-brand-500/30 bg-white p-8 shadow-card">
        <div className="max-w-md text-center">
          <span className="mx-auto grid h-14 w-14 place-items-center rounded-2xl bg-brand-100 text-2xl" aria-hidden="true">↗</span>
          <h2 className="mt-5 text-xl font-black">Foundation ready</h2>
          <p className="mt-2 text-sm leading-6 text-black/55">
            This page is routed and protected. Its full feature flow is the next implementation phase.
          </p>
        </div>
      </div>
    </section>
  );
}
