import { useEffect, useRef, useState } from 'react';

export default function RestTimer({ defaultSeconds = 60 }) {
  const [secondsLeft, setSecondsLeft] = useState(0);
  const [isRunning, setIsRunning] = useState(false);
  const intervalRef = useRef(null);

  useEffect(() => {
    if (!isRunning) {
      return undefined;
    }

    intervalRef.current = setInterval(() => {
      setSecondsLeft((current) => {
        if (current <= 1) {
          setIsRunning(false);
          return 0;
        }
        return current - 1;
      });
    }, 1000);

    return () => clearInterval(intervalRef.current);
  }, [isRunning]);

  function start(seconds = defaultSeconds) {
    setSecondsLeft(seconds);
    setIsRunning(true);
  }

  function pause() {
    setIsRunning(false);
  }

  function reset() {
    setIsRunning(false);
    setSecondsLeft(0);
  }

  const minutes = Math.floor(secondsLeft / 60);
  const seconds = secondsLeft % 60;

  return (
    <div className="flex items-center gap-3 rounded-2xl bg-black/5 px-4 py-3" role="timer" aria-live="polite">
      <span className="text-2xl font-black tabular-nums">
        {minutes}:{String(seconds).padStart(2, '0')}
      </span>
      <div className="flex gap-2">
        <button type="button" className="button-secondary py-1.5 text-xs" onClick={() => start()}>
          Start rest
        </button>
        {isRunning ? (
          <button type="button" className="button-secondary py-1.5 text-xs" onClick={pause}>
            Pause
          </button>
        ) : (
          <button type="button" className="button-secondary py-1.5 text-xs" onClick={reset} disabled={secondsLeft === 0}>
            Reset
          </button>
        )}
      </div>
    </div>
  );
}
